package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Pesanan;
import model.PiutangRow;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

public class OwnerPiutangController {

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

        muatDataPiutang();
        System.out.println("Halaman Piutang Pelanggan diinisialisasi.");
    }

    private void muatDataPiutang() {
        List<Pesanan> semuaPesanan = Pesanan.getAllPesanan();

        ObservableList<PiutangRow> dataPiutang = FXCollections.observableArrayList();

        double totalPiutang = 0.0;
        int melewatiJT = 0;
        int akanJT     = 0;

        LocalDate hari = LocalDate.now();

        for (Pesanan p : semuaPesanan) {
            if (!"Belum Lunas".equalsIgnoreCase(p.getStatusPembayaran())) continue;

            double tagihan = p.getTotalHarga();
            totalPiutang += tagihan;

            // Tentukan status berdasarkan tanggal jatuh tempo
            String tglStr   = p.getTanggalPengiriman();
            LocalDate tglJT = parseTanggal(tglStr);
            String statusLabel;

            if (tglJT != null) {
                long selisih = hari.toEpochDay() - tglJT.toEpochDay(); // positif = sudah lewat
                if (selisih > 7) {
                    statusLabel = "Lewat >7 hari";
                    melewatiJT++;
                } else if (selisih >= 0) {
                    statusLabel = "Jatuh tempo";
                    akanJT++;
                } else if (selisih >= -7) {
                    statusLabel = "Akan JT (≤7 hr)";
                    akanJT++;
                } else {
                    statusLabel = "Belum jatuh tempo";
                }
            } else {
                statusLabel = "Belum jatuh tempo";
            }

            dataPiutang.add(new PiutangRow(
                    p.getUsernamePelanggan(),
                    p.getIdPesanan(),
                    "Rp " + formatRp(tagihan),
                    "Rp 0",                     // DP — sistem saat ini tidak menyimpan DP terpisah
                    "Rp " + formatRp(tagihan),  // Sisa = total (belum ada pembayaran parsial)
                    tglStr,
                    statusLabel
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