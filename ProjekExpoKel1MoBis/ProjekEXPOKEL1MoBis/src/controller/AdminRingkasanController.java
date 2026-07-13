package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import model.Menu;
import model.Pesanan;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class AdminRingkasanController {

    // --- Root (dipakai untuk deteksi kapan halaman ini ditutup) ---
    @FXML private ScrollPane rootRingkasan;

    // --- Kartu Statistik ---
    @FXML private Text txtSubJudul;
    @FXML private Text txtTotalPesanan;
    @FXML private Text txtPesananHariIni;
    @FXML private Text txtPesananHariIniTanggal;
    @FXML private Text txtPendapatanBulanIni;
    @FXML private Text txtTrendPendapatan;
    @FXML private Text txtMenuAktif;
    @FXML private Text txtMenuAktifSub;
    // --- Label ---
    @FXML private Label lblTotalPesanan;
    @FXML private Label lblPesananHariIni;
    @FXML private Label lblPendapatan;

    @FXML private Text txtJumlahPesananTerbaru;

    @FXML private TableView<Pesanan> tablePesananTerbaru;
    @FXML private TableColumn<Pesanan, String> colId;
    @FXML private TableColumn<Pesanan, String> colPelanggan;
    @FXML private TableColumn<Pesanan, String> colMenu;
    @FXML private TableColumn<Pesanan, String> colQty;
    @FXML private TableColumn<Pesanan, String> colTglKirim;
    @FXML private TableColumn<Pesanan, String> colStatus;
    @FXML private TableColumn<Pesanan, String> colPembayaran;

    @FXML private VBox boxAlertKapasitas;
    @FXML private ProgressBar progressKapasitas;
    @FXML private Text txtKapasitasPersen;
    @FXML private Text txtKapasitasPorsi;

    private static final Locale ID_LOCALE = new Locale("id", "ID");
    private static final NumberFormat RUPIAH = NumberFormat.getInstance(ID_LOCALE);
    private static final String[] BULAN_SINGKAT = {
        "Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Agu", "Sep", "Okt", "Nov", "Des"
    };
    private static final String[] HARI_INDO = {
        "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"
    };

    /** Batas kapasitas produksi (porsi), dipakai untuk progress bar & alert. */
    private static final int KAPASITAS_MAKS_PORSI = 500;

    /** Interval auto-refresh data real-time dari pesanan.xml & menu.xml (sinkron dgn Pelanggan). */
    private static final Duration INTERVAL_REFRESH = Duration.seconds(3);
    private Timeline autoRefreshTimeline;

    private final ObservableList<Pesanan> dataPesanan = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1. Hubungkan kolom TableView dengan property yang ada di model Pesanan.java
        colId.setCellValueFactory(new PropertyValueFactory<>("idPesanan"));
        colPelanggan.setCellValueFactory(new PropertyValueFactory<>("usernamePelanggan")); // Nanti bisa disesuaikan property-nya di model
        colMenu.setCellValueFactory(new PropertyValueFactory<>("namaMenu"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("jumlahPorsi"));
        colTglKirim.setCellValueFactory(new PropertyValueFactory<>("tanggalPengiriman"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("statusPesanan"));
        colPembayaran.setCellValueFactory(new PropertyValueFactory<>("statusPembayaran"));

        colStatus.setCellFactory(col -> badgeCell(this::gayaStatus));
        colPembayaran.setCellFactory(col -> badgeCell(this::gayaPembayaran));

        // 2. Tanggal & sapaan header
        aturHeaderTanggal();

        // 3. Ambil data gabungan langsung dari data/pesanan.xml & data/menu.xml
        muatSemuaData();
        mulaiAutoRefresh();
    }

    private void aturHeaderTanggal() {
        if (txtSubJudul != null) {
            LocalDate hariIni = LocalDate.now();
            String namaHari = HARI_INDO[hariIni.getDayOfWeek().getValue() - 1];
            String tanggal = hariIni.getDayOfMonth() + " " + BULAN_SINGKAT[hariIni.getMonthValue() - 1] + " " + hariIni.getYear();
            txtSubJudul.setText(namaHari + ", " + tanggal + " \u00B7 Selamat datang, Bambang");
        }
    }

    /** Refresh data secara berkala selama halaman ini masih tampil di layar. */
    private void mulaiAutoRefresh() {
        autoRefreshTimeline = new Timeline(new KeyFrame(INTERVAL_REFRESH, e -> muatSemuaData()));
        autoRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        autoRefreshTimeline.play();

        if (rootRingkasan != null) {
            rootRingkasan.parentProperty().addListener((obs, oldParent, newParent) -> {
                if (newParent == null) {
                    autoRefreshTimeline.stop();
                }
            });
        }
    }

    private void muatSemuaData() {
        muatDataPesananTerbaru();
        muatKartuStatistik();
    }

    private void muatDataPesananTerbaru() {
        int totalPorsiKeseluruhan = 0;

        try {
            // PENTING: pakai Pesanan.getAllPesanan() supaya path yang dibaca SAMA PERSIS
            // dengan path yang dipakai saat pelanggan menyimpan pesanan baru (model/Pesanan.java).
            List<Pesanan> semuaPesanan = Pesanan.getAllPesanan();

            // Balik urutan supaya pesanan yang paling akhir ditambahkan (terbaru)
            // ada di paling atas, baru ambil maksimal 7 teratas untuk ditampilkan.
            List<Pesanan> pesananTerbaruDulu = new ArrayList<>(semuaPesanan);
            Collections.reverse(pesananTerbaruDulu);

            dataPesanan.setAll(pesananTerbaruDulu.subList(0, Math.min(pesananTerbaruDulu.size(), 7)));

            if (tablePesananTerbaru != null) {
                tablePesananTerbaru.setItems(dataPesanan);
                tablePesananTerbaru.refresh();
            }

            if (txtJumlahPesananTerbaru != null) {
                txtJumlahPesananTerbaru.setText(dataPesanan.size() + " pesanan");
            }

            // Hitung akumulasi porsi dari SEMUA pesanan (bukan cuma yang ditampilkan di tabel)
            // untuk indikator bar kapasitas produksi
            for (Pesanan p : semuaPesanan) {
                totalPorsiKeseluruhan += p.getJumlahPorsi();
            }

            // 3. Logika atur Progress Bar Kapasitas Maksimal Produksi
            double persentaseKapasitas = (double) totalPorsiKeseluruhan / KAPASITAS_MAKS_PORSI;
            int persenBulat = (int) Math.round(persentaseKapasitas * 100);

            if (progressKapasitas != null) {
                progressKapasitas.setProgress(Math.min(persentaseKapasitas, 1.0));
            }
            if (txtKapasitasPersen != null) {
                txtKapasitasPersen.setText("\u26A0\uFE0F Kapasitas Produksi: " + persenBulat + "% \u2014 Mendekati Batas (80%)");
            }
            if (txtKapasitasPorsi != null) {
                txtKapasitasPorsi.setText(totalPorsiKeseluruhan + " / " + KAPASITAS_MAKS_PORSI + " porsi");
            }

            // Tampilan alert cuma kalau kapasitas sudah cukup tinggi (>= 50 porsi)
            if (boxAlertKapasitas != null) {
                boolean tampilkanAlert = totalPorsiKeseluruhan >= 50;
                boxAlertKapasitas.setVisible(tampilkanAlert);
                boxAlertKapasitas.setManaged(tampilkanAlert);
            }
        } catch (Exception e) {
            System.out.println("Gagal membaca data ringkasan admin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Isi 4 kartu statistik atas (Total Pesanan, Pesanan Hari Ini, Pendapatan Bulan Ini, Menu Aktif). */
    private void muatKartuStatistik() {
        try {
            List<Pesanan> semuaPesanan = Pesanan.getAllPesanan();
            LocalDate hariIni = LocalDate.now();
            LocalDate bulanLalu = hariIni.minusMonths(1);

            int pesananHariIni = 0;
            double pendapatanBulanIni = 0.0;
            double pendapatanBulanLalu = 0.0;

            for (Pesanan p : semuaPesanan) {
                LocalDate tglKirim = parseTanggal(p.getTanggalPengiriman());
                if (tglKirim == null) continue;

                if (tglKirim.isEqual(hariIni)) {
                    pesananHariIni++;
                }

                boolean lunas = "Lunas".equalsIgnoreCase(p.getStatusPembayaran());

                if (lunas && tglKirim.getMonthValue() == hariIni.getMonthValue()
                        && tglKirim.getYear() == hariIni.getYear()) {
                    pendapatanBulanIni += p.getTotalHarga();
                }

                if (lunas && tglKirim.getMonthValue() == bulanLalu.getMonthValue()
                        && tglKirim.getYear() == bulanLalu.getYear()) {
                    pendapatanBulanLalu += p.getTotalHarga();
                }
            }

            int menuAktif = 0;
            List<Menu> semuaMenu = Menu.getAllMenu();
            for (Menu m : semuaMenu) {
                if ("aktif".equalsIgnoreCase(m.getStatus())) {
                    menuAktif++;
                }
            }

            if (txtTotalPesanan != null) txtTotalPesanan.setText(String.valueOf(semuaPesanan.size()));
            if (txtPesananHariIni != null) txtPesananHariIni.setText(String.valueOf(pesananHariIni));
            if (txtPesananHariIniTanggal != null) {
                txtPesananHariIniTanggal.setText(
                    hariIni.getDayOfMonth() + " " + BULAN_SINGKAT[hariIni.getMonthValue() - 1] + " " + hariIni.getYear());
            }
            if (txtPendapatanBulanIni != null) txtPendapatanBulanIni.setText("Rp " + formatRingkas(pendapatanBulanIni));
            if (txtMenuAktif != null) txtMenuAktif.setText(String.valueOf(menuAktif));
            if (txtMenuAktifSub != null) txtMenuAktifSub.setText("dari " + semuaMenu.size() + " menu");

            if (txtTrendPendapatan != null) {
                if (pendapatanBulanLalu > 0) {
                    double persenPerubahan = ((pendapatanBulanIni - pendapatanBulanLalu) / pendapatanBulanLalu) * 100.0;
                    String panah = persenPerubahan >= 0 ? "\u2191" : "\u2193";
                    txtTrendPendapatan.setText(panah + " " + trimNolBelakang(Math.abs(persenPerubahan)) + "% vs bulan lalu");
                } else if (pendapatanBulanIni > 0) {
                    txtTrendPendapatan.setText("\u2191 Baru mulai bulan ini");
                } else {
                    txtTrendPendapatan.setText("Belum ada pendapatan");
                }
            }
        } catch (Exception e) {
            System.out.println("Gagal menghitung kartu statistik admin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ======================================================
    //  Badge STATUS & PEMBAYARAN (kolom tabel Pesanan Terbaru)
    // ======================================================

    private TableCell<Pesanan, String> badgeCell(Function<String, String> gayaFn) {
        return new TableCell<Pesanan, String>() {
            private final Label badge = new Label();
            {
                badge.getStyleClass().add("badge-pill");
                setAlignment(Pos.CENTER_LEFT);
            }
            @Override
            protected void updateItem(String nilai, boolean empty) {
                super.updateItem(nilai, empty);
                if (empty || nilai == null || nilai.isEmpty()) {
                    setGraphic(null);
                } else {
                    badge.setText(gayaLabel(nilai));
                    badge.setStyle(gayaFn.apply(nilai));
                    setGraphic(badge);
                }
            }
        };
    }

    /** Tambahkan ikon kecil di depan teks status/pembayaran, senada dengan desain. */
    private String gayaLabel(String status) {
        String s = status.trim().toLowerCase();
        if (s.contains("dikirim")) return "\u2713 " + status;
        if (s.contains("dimasak")) return "\u23F3 " + status;
        if (s.contains("dikonfirmasi")) return "\u25D0 " + status;
        return status;
    }

    /** Warna badge untuk kolom STATUS pesanan. */
    private String gayaStatus(String status) {
        String s = status.trim().toLowerCase();
        if (s.contains("dikirim")) return "-fx-background-color:#DCF5DF; -fx-text-fill:#1E8E4D;";
        if (s.contains("dimasak")) return "-fx-background-color:#FDF0CE; -fx-text-fill:#B8860B;";
        if (s.contains("dikonfirmasi")) return "-fx-background-color:#DCEBFB; -fx-text-fill:#2E71B8;";
        if (s.contains("selesai")) return "-fx-background-color:#DCF5DF; -fx-text-fill:#1E8E4D;";
        return "-fx-background-color:#ECECEC; -fx-text-fill:#6B6B6B;";
    }

    /** Warna badge untuk kolom PEMBAYARAN, mengikuti palet pada desain (hijau/kuning/merah). */
    private String gayaPembayaran(String status) {
        String s = status.trim().toLowerCase();
        // PENTING: cek "belum" LEBIH DULU sebelum "lunas", karena data asli
        // memakai teks "Belum Lunas"/"Belum Bayar" yang juga mengandung kata "lunas".
        if (s.contains("belum")) return "-fx-background-color:#FBDCDF; -fx-text-fill:#C0392B;";
        if (s.contains("dp")) return "-fx-background-color:#FDF0CE; -fx-text-fill:#B8860B;";
        if (s.contains("lunas")) return "-fx-background-color:#DCF5DF; -fx-text-fill:#1E8E4D;";
        return "-fx-background-color:#ECECEC; -fx-text-fill:#6B6B6B;";
    }

    // ======================================================
    //  Utilitas
    // ======================================================

    /** Format angka jadi bentuk ringkas ala kartu statistik, mis. 6200000 -> "6,2 Jt", 250000 -> "250 Rb". */
    private String formatRingkas(double angka) {
        if (angka >= 1_000_000) {
            double jt = angka / 1_000_000.0;
            return trimNolBelakang(jt) + " Jt";
        } else if (angka >= 1_000) {
            double rb = angka / 1_000.0;
            return trimNolBelakang(rb) + " Rb";
        }
        return RUPIAH.format((long) angka);
    }

    private String trimNolBelakang(double nilai) {
        // Bulatkan 1 desimal, buang ",0" kalau bulat
        String teks = String.format(ID_LOCALE, "%.1f", nilai);
        if (teks.endsWith(",0")) {
            teks = teks.substring(0, teks.length() - 2);
        }
        return teks;
    }

    /**
     * Mencoba mem-parse berbagai format tanggal yang ada di pesanan.xml:
     * "25 Jun 2026", "2 Agustus 2026", "2026-07-11", "8 Juli 2026", dll.
     */
    private LocalDate parseTanggal(String tanggal) {
        if (tanggal == null || tanggal.trim().isEmpty()) return null;
        tanggal = tanggal.trim();

        try { return LocalDate.parse(tanggal, DateTimeFormatter.ISO_LOCAL_DATE); }
        catch (DateTimeParseException ignored) {}

        String[] bagian = tanggal.split("\\s+");
        if (bagian.length == 3) {
            try {
                int hari  = Integer.parseInt(bagian[0]);
                int bulan = namaBulanKeAngka(bagian[1]);
                int tahun = Integer.parseInt(bagian[2]);
                if (bulan > 0) return LocalDate.of(tahun, bulan, hari);
            } catch (Exception ignored) {}
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
