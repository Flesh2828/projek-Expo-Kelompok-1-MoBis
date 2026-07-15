package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.Pesanan;
import model.PiutangRow;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

public class OwnerPiutangController {

    // --- Root (dipakai untuk deteksi kapan halaman ini ditutup) ---
    @FXML private VBox rootPiutang;

    // --- Label Ringkasan ---
    @FXML private Label lblTotalPiutang;
    @FXML private Label lblMelewatiJT;
    @FXML private Label lblAkanJT;

    // --- Tabel ---
    @FXML private TableView<PiutangRow> tablePiutang;
    @FXML private TableColumn<PiutangRow, String> colPelanggan;
    @FXML private TableColumn<PiutangRow, String> colIdPesanan;
    @FXML private TableColumn<PiutangRow, String> colTotalTagihan;
    @FXML private TableColumn<PiutangRow, String> colDpDibayar;
    @FXML private TableColumn<PiutangRow, String> colSisaTagihan;
    @FXML private TableColumn<PiutangRow, String> colJatuhTempo;
    @FXML private TableColumn<PiutangRow, String> colStatus;

    private static final Locale ID     = new Locale("id", "ID");
    private static final NumberFormat RUPIAH = NumberFormat.getInstance(ID);

    /** Interval auto-refresh data real-time dari pesanan.xml (Admin & Pelanggan). */
    private static final Duration INTERVAL_REFRESH = Duration.seconds(3);
    private Timeline autoRefreshTimeline;

    @FXML
    public void initialize() {
        // Bind kolom ke property model
        colPelanggan   .setCellValueFactory(new PropertyValueFactory<>("pelanggan"));
        colIdPesanan   .setCellValueFactory(new PropertyValueFactory<>("idPesanan"));
        colTotalTagihan.setCellValueFactory(new PropertyValueFactory<>("totalTagihan"));
        colDpDibayar   .setCellValueFactory(new PropertyValueFactory<>("dpDibayar"));
        colSisaTagihan .setCellValueFactory(new PropertyValueFactory<>("sisaTagihan"));
        colJatuhTempo  .setCellValueFactory(new PropertyValueFactory<>("jatuhTempo"));
        colStatus      .setCellValueFactory(new PropertyValueFactory<>("status"));

        // --- Styling tampilan tabel (warna teks per kolom) ---
        terapkanGayaTeks(colPelanggan,    "-fx-font-weight: 600; -fx-text-fill: #2C1810;");
        terapkanGayaTeks(colIdPesanan,    "-fx-font-weight: 500; -fx-text-fill: #9B8776;");
        terapkanGayaTeks(colTotalTagihan, "-fx-font-weight: 500; -fx-text-fill: #46332D;");
        terapkanGayaTeks(colDpDibayar,    "-fx-font-weight: 600; -fx-text-fill: #10825F;");
        terapkanGayaTeks(colJatuhTempo,   "-fx-font-weight: 500; -fx-text-fill: #8A7566;");

        // Kolom SISA TAGIHAN — merah tebal khusus baris yang sudah lewat jatuh tempo
        colSisaTagihan.setCellFactory(col -> new TableCell<PiutangRow, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    PiutangRow row = ambilBarisSaatIni(this);
                    boolean terlambat = row != null && "MERAH".equals(row.getKategori());
                    setStyle(terlambat
                            ? "-fx-font-weight: 700; -fx-text-fill: #C10007;"
                            : "-fx-font-weight: 600; -fx-text-fill: #2C1810;");
                }
            }
        });

        // Kolom STATUS — badge/pill berwarna sesuai urgensi
        colStatus.setCellFactory(col -> new TableCell<PiutangRow, String>() {
            private final Label badge = new Label();
            {
                badge.getStyleClass().add("badge-piutang");
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    PiutangRow row = ambilBarisSaatIni(this);
                    String kategori = (row != null) ? row.getKategori() : "HIJAU";
                    badge.getStyleClass().removeAll("badge-merah", "badge-kuning", "badge-hijau");
                    switch (kategori) {
                        case "MERAH":
                            badge.getStyleClass().add("badge-merah");
                            break;
                        case "KUNING":
                            badge.getStyleClass().add("badge-kuning");
                            break;
                        default:
                            badge.getStyleClass().add("badge-hijau");
                            break;
                    }
                    badge.setText(item);
                    setGraphic(badge);
                }
            }
        });

        // Tint latar baris (merah/kuning muda) sesuai urgensi jatuh tempo
        tablePiutang.setRowFactory(tv -> new TableRow<PiutangRow>() {
            @Override
            protected void updateItem(PiutangRow item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("row-merah", "row-kuning", "row-hijau");
                if (!empty && item != null) {
                    switch (item.getKategori()) {
                        case "MERAH":
                            getStyleClass().add("row-merah");
                            break;
                        case "KUNING":
                            getStyleClass().add("row-kuning");
                            break;
                        default:
                            getStyleClass().add("row-hijau");
                            break;
                    }
                }
            }
        });

        muatDataPiutang();
        mulaiAutoRefresh();
        System.out.println("Halaman Piutang Pelanggan diinisialisasi.");
    }

    /** Refresh data secara berkala selama halaman ini masih tampil di layar. */
    private void mulaiAutoRefresh() {
        autoRefreshTimeline = new Timeline(new KeyFrame(INTERVAL_REFRESH, e -> muatDataPiutang()));
        autoRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        autoRefreshTimeline.play();

        if (rootPiutang != null) {
            rootPiutang.parentProperty().addListener((obs, oldParent, newParent) -> {
                if (newParent == null) {
                    autoRefreshTimeline.stop();
                }
            });
        }
    }

    private void muatDataPiutang() {
        List<Pesanan> semuaPesanan = Pesanan.getAllPesanan();

        ObservableList<PiutangRow> dataPiutang = FXCollections.observableArrayList();

        double totalPiutang = 0.0;
        int melewatiJT = 0;
        int akanJT     = 0;

        LocalDate hari = LocalDate.now();

        for (Pesanan p : semuaPesanan) {
            // PENTING: data status pembayaran di pesanan.xml bervariasi teksnya
            // ("Belum Bayar", "Belum bayar", "Belum Lunas", dst — semuanya berarti
            // pesanan tsb masih piutang / belum lunas). Sebelumnya kode ini hanya
            // mencocokkan string persis "Belum Lunas" sehingga data seperti
            // "Belum Bayar"/"Belum bayar" tidak pernah cocok dan baris piutang
            // tidak pernah muncul di tabel. Sekarang: anggap piutang jika status
            // MENGANDUNG kata "belum" (huruf besar/kecil diabaikan), konsisten
            // dengan logika yang sudah dipakai di AdminRingkasanController.
            String statusBayar = p.getStatusPembayaran();
            if (statusBayar == null || !statusBayar.toLowerCase().contains("belum")) continue;

            double tagihan = p.getTotalHarga();
            totalPiutang += tagihan;

            // Tentukan status berdasarkan tanggal jatuh tempo
            String tglStr   = p.getTanggalPengiriman();
            LocalDate tglJT = parseTanggal(tglStr);
            String statusLabel;
            String kategori; // MERAH | KUNING | HIJAU — dipakai untuk styling tabel saja

            if (tglJT != null) {
                long selisih = hari.toEpochDay() - tglJT.toEpochDay(); // positif = sudah lewat

                // --- Hitungan kartu ringkasan (LOGIKA ASLI, tidak diubah) ---
                if (selisih > 7) {
                    melewatiJT++;
                } else if (selisih >= -7) {
                    akanJT++;
                }

                // --- Label & kategori untuk tampilan tabel ---
                if (selisih > 7) {
                    statusLabel = "Lewat " + selisih + " hari";
                    kategori = "MERAH";
                } else if (selisih > 0) {
                    statusLabel = "Lewat " + selisih + " hari";
                    kategori = "KUNING";
                } else if (selisih == 0) {
                    statusLabel = "Jatuh tempo hari ini";
                    kategori = "KUNING";
                } else if (selisih >= -7) {
                    long sisaHari = -selisih;
                    statusLabel = sisaHari + " hari lagi";
                    kategori = sisaHari <= 3 ? "KUNING" : "HIJAU";
                } else {
                    statusLabel = "Belum jatuh tempo";
                    kategori = "HIJAU";
                }
            } else {
                statusLabel = "Belum jatuh tempo";
                kategori = "HIJAU";
            }

            dataPiutang.add(new PiutangRow(
                    p.getUsernamePelanggan(),
                    p.getIdPesanan(),
                    "Rp " + formatRp(tagihan),
                    "Rp 0",                     // DP — sistem saat ini tidak menyimpan DP terpisah
                    "Rp " + formatRp(tagihan),  // Sisa = total (belum ada pembayaran parsial)
                    tglStr,
                    statusLabel,
                    kategori
            ));
        }

        tablePiutang.setItems(dataPiutang);
        tablePiutang.refresh();

        // Update label ringkasan
        if (lblTotalPiutang != null) lblTotalPiutang.setText("Rp " + formatRp(totalPiutang));
        if (lblMelewatiJT   != null) lblMelewatiJT  .setText(String.valueOf(melewatiJT));
        if (lblAkanJT       != null) lblAkanJT       .setText(String.valueOf(akanJT));
    }

    // ======================================================
    //  Utilitas
    // ======================================================

    /** Memasang cell factory sederhana yang mewarnai teks kolom sesuai desain kartu Daftar Piutang. */
    private void terapkanGayaTeks(TableColumn<PiutangRow, String> kolom, String gayaCss) {
        if (kolom == null) return;
        kolom.setCellFactory(col -> new TableCell<PiutangRow, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle(gayaCss);
                }
            }
        });
    }

    /**
     * Mengambil data PiutangRow untuk baris tempat sel ini berada, berdasarkan index
     * langsung dari TableView.getItems(). Sengaja TIDAK memakai cell.getTableRow().getItem(),
     * karena pada TableView yang tervirtualisasi nilainya bisa telat/kosong (null) sesaat
     * saat sel baru dibuat/di-scroll — itulah yang tadinya membuat semua baris "jatuh"
     * ke warna hijau (default) walau datanya sebenarnya merah/kuning.
     */
    private PiutangRow ambilBarisSaatIni(TableCell<PiutangRow, ?> cell) {
        int index = cell.getIndex();
        if (index < 0 || index >= cell.getTableView().getItems().size()) return null;
        return cell.getTableView().getItems().get(index);
    }

    private String formatRp(double angka) {
        return RUPIAH.format((long) angka);
    }

    /**
     * Mencoba mem-parse berbagai format tanggal yang ada di pesanan.xml:
     * "25 Jun 2026", "2 Agustus 2026", "2026-07-11", "8 Juli 2026", dll.
     */
    private LocalDate parseTanggal(String tanggal) {
        if (tanggal == null || tanggal.trim().isEmpty()) return null;
        tanggal = tanggal.trim();

        // Format ISO
        try { return LocalDate.parse(tanggal, DateTimeFormatter.ISO_LOCAL_DATE); }
        catch (DateTimeParseException ignored) {}

        // Format "DD Bulan YYYY" (nama bulan Indonesia panjang & singkat)
        String[] bagian = tanggal.split("\\s+");
        if (bagian.length == 3) {
            int     hari  = Integer.parseInt(bagian[0]);
            int     bulan = namaBulanKeAngka(bagian[1]);
            int     tahun = Integer.parseInt(bagian[2]);
            if (bulan > 0) {
                try { return LocalDate.of(tahun, bulan, hari); }
                catch (Exception ignored) {}
            }
        }
        return null;
    }

    private int namaBulanKeAngka(String nama) {
        switch (nama.toLowerCase()) {
            case "jan": case "januari":   return 1;
            case "feb": case "februari":  return 2;
            case "mar": case "maret":     return 3;
            case "apr": case "april":     return 4;
            case "mei":                   return 5;
            case "jun": case "juni":      return 6;
            case "jul": case "juli":      return 7;
            case "agu": case "agustus":   return 8;
            case "sep": case "september": return 9;
            case "okt": case "oktober":   return 10;
            case "nov": case "november":  return 11;
            case "des": case "desember":  return 12;
            default: return -1;
        }
    }
}