package controller;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import model.Pesanan;

import java.text.NumberFormat;
import java.util.*;

public class OwnerDashboardContentController {

    // --- Label Statistik Kartu ---
    @FXML private Label lblPendapatan;
    @FXML private Label lblSubPendapatan;
    @FXML private Label lblPengeluaran;
    @FXML private Label lblSubPengeluaran;
    @FXML private Label lblLaba;
    @FXML private Label lblSubLaba;
    @FXML private Label lblPiutang;
    @FXML private Label lblSubPiutang;

    // --- Chart ---
    @FXML private LineChart<String, Number> lineChartDashboard;

    /** Rasio biaya bahan baku + operasional terhadap pendapatan (40%). */
    private static final double RASIO_PENGELUARAN = 0.40;

    private static final Locale ID = new Locale("id", "ID");
    private static final NumberFormat RUPIAH = NumberFormat.getInstance(ID);

    @FXML
    public void initialize() {
        hitungDanTampilkanData();
    }

    private void hitungDanTampilkanData() {
        List<Pesanan> semuaPesanan = Pesanan.getAllPesanan();

        // ===== Kelompokkan per Bulan (hanya yang Lunas) =====
        // Key: "Bulan Tahun" (mis. "Jun 2026"), Value: total pendapatan
        Map<String, Double> pendapatanPerBulan = new LinkedHashMap<>();
        double totalPendapatan = 0.0;
        double totalPiutang    = 0.0;
        int    jumlahPiutang   = 0;

        for (Pesanan p : semuaPesanan) {
            String bulan = normalisasiBulan(p.getTanggalPengiriman());

            if ("Lunas".equalsIgnoreCase(p.getStatusPembayaran())) {
                totalPendapatan += p.getTotalHarga();
                pendapatanPerBulan.merge(bulan, p.getTotalHarga(), Double::sum);
            } else {
                totalPiutang  += p.getTotalHarga();
                jumlahPiutang++;
            }
        }

        double totalPengeluaran = totalPendapatan * RASIO_PENGELUARAN;
        double totalLaba        = totalPendapatan - totalPengeluaran;

        // ===== Isi Label Kartu =====
        if (lblPendapatan  != null) lblPendapatan .setText("Rp " + formatRp(totalPendapatan));
        if (lblPengeluaran != null) lblPengeluaran.setText("Rp " + formatRp(totalPengeluaran));
        if (lblLaba        != null) lblLaba       .setText("Rp " + formatRp(totalLaba));
        if (lblPiutang     != null) lblPiutang    .setText("Rp " + formatRp(totalPiutang));

        // Sub-label dinamis
        int bulanDidata = pendapatanPerBulan.size();
        String periodeTeks = bulanDidata + " bulan terakhir";

        if (lblSubPendapatan  != null) lblSubPendapatan .setText(periodeTeks);
        if (lblSubPengeluaran != null) lblSubPengeluaran.setText("Est. bahan baku & operasional");
        if (lblSubLaba        != null) {
            int margin = totalPendapatan > 0
                    ? (int) Math.round((totalLaba / totalPendapatan) * 100) : 0;
            lblSubLaba.setText("Margin rata-rata " + margin + "%");
        }
        if (lblSubPiutang != null) {
            lblSubPiutang.setText(jumlahPiutang + " tagihan belum lunas");
        }

        // ===== Isi LineChart Tren Laba =====
        isiLineChart(pendapatanPerBulan);
    }

    private void isiLineChart(Map<String, Double> pendapatanPerBulan) {
        if (lineChartDashboard == null) return;

        lineChartDashboard.getData().clear();

        // Urutkan bulan secara kronologis
        List<String> bulanUrut = new ArrayList<>(pendapatanPerBulan.keySet());
        bulanUrut.sort(Comparator.comparingInt(this::urutanBulan));

        XYChart.Series<String, Number> seriLaba       = new XYChart.Series<>();
        XYChart.Series<String, Number> seriPendapatan = new XYChart.Series<>();
        seriLaba.setName("Laba Bersih");
        seriPendapatan.setName("Pendapatan");

        for (String bulan : bulanUrut) {
            double pendapatan  = pendapatanPerBulan.get(bulan);
            double pengeluaran = pendapatan * RASIO_PENGELUARAN;
            double laba        = pendapatan - pengeluaran;

            // Konversi ke ribuan agar chart lebih terbaca
            seriPendapatan.getData().add(new XYChart.Data<>(bulan, pendapatan / 1000.0));
            seriLaba.getData().add(new XYChart.Data<>(bulan, laba / 1000.0));
        }

        // Jika belum ada data, tampilkan placeholder agar chart tidak kosong
        if (bulanUrut.isEmpty()) {
            seriPendapatan.getData().add(new XYChart.Data<>("(belum ada data)", 0));
            seriLaba.getData().add(new XYChart.Data<>("(belum ada data)", 0));
        }

        lineChartDashboard.getData().addAll(seriPendapatan, seriLaba);
    }

    // ======================================================
    //  Utilitas
    // ======================================================

    /** Format angka ke ribuan tanpa desimal, mis. 2500000 → "2.500.000" */
    private String formatRp(double angka) {
        return RUPIAH.format((long) angka);
    }

    /**
     * Ekstrak nama bulan + tahun dari berbagai format tanggal yang ada di XML:
     * "25 Jun 2026", "2 Agustus 2026", "16 Agustus 2026", "2026-07-11", "8 Juli 2026", dll.
     * Mengembalikan string "MMM YYYY" atau "Jul 2026".
     */
    private String normalisasiBulan(String tanggal) {
        if (tanggal == null || tanggal.trim().isEmpty()) return "Tidak Diketahui";

        tanggal = tanggal.trim();

        // Format ISO: "2026-07-11"
        if (tanggal.matches("\\d{4}-\\d{2}-\\d{2}")) {
            String[] bagian = tanggal.split("-");
            return singkatBulan(Integer.parseInt(bagian[1])) + " " + bagian[0];
        }

        // Format "DD Bulan YYYY" atau "DD BulanSingkat YYYY"
        String[] bagian = tanggal.split("\\s+");
        if (bagian.length >= 3) {
            String namaBulan = bagian[1];
            String tahun     = bagian[2];
            return singkatNamaBulan(namaBulan) + " " + tahun;
        }

        return tanggal; // Kembalikan apa adanya jika tidak bisa di-parse
    }

    private String singkatBulan(int bulan) {
        String[] nama = {"Jan","Feb","Mar","Apr","Mei","Jun","Jul","Agu","Sep","Okt","Nov","Des"};
        if (bulan >= 1 && bulan <= 12) return nama[bulan - 1];
        return "???";
    }

    private String singkatNamaBulan(String nama) {
        switch (nama.toLowerCase()) {
            case "januari":  case "jan": return "Jan";
            case "februari": case "feb": return "Feb";
            case "maret":    case "mar": return "Mar";
            case "april":    case "apr": return "Apr";
            case "mei":                  return "Mei";
            case "juni":     case "jun": return "Jun";
            case "juli":     case "jul": return "Jul";
            case "agustus":  case "agu": return "Agu";
            case "september":case "sep": return "Sep";
            case "oktober":  case "okt": return "Okt";
            case "november": case "nov": return "Nov";
            case "desember": case "des": return "Des";
            default: return nama;
        }
    }

    /** Angka urutan untuk sorting bulan secara kronologis. */
    private int urutanBulan(String bulanTahun) {
        // Format: "Jun 2026"
        String[] bagian = bulanTahun.split("\\s+");
        if (bagian.length < 2) return Integer.MAX_VALUE;
        int tahun = 0;
        try { tahun = Integer.parseInt(bagian[1]); } catch (NumberFormatException e) { return Integer.MAX_VALUE; }

        String[] namaBulan = {"Jan","Feb","Mar","Apr","Mei","Jun","Jul","Agu","Sep","Okt","Nov","Des"};
        int idxBulan = 0;
        for (int i = 0; i < namaBulan.length; i++) {
            if (namaBulan[i].equalsIgnoreCase(bagian[0])) { idxBulan = i + 1; break; }
        }
        return tahun * 100 + idxBulan;
    }
}
