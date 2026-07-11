package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Pesanan;
import model.RingkasanBulanan;

import java.text.NumberFormat;
import java.util.*;

public class OwnerLaporanController {

    // --- Chart ---
    @FXML private BarChart<String, Number>  barChartLaporan;
    @FXML private LineChart<String, Number> lineChartLaporan;

    // --- Tabel Ringkasan ---
    @FXML private TableView<RingkasanBulanan>        tableRingkasan;
    @FXML private TableColumn<RingkasanBulanan, String> colBulan;
    @FXML private TableColumn<RingkasanBulanan, String> colPendapatan;
    @FXML private TableColumn<RingkasanBulanan, String> colPengeluaran;
    @FXML private TableColumn<RingkasanBulanan, String> colLabaBersih;
    @FXML private TableColumn<RingkasanBulanan, String> colMargin;

    // --- Label Statistik Total (opsional, jika ada di FXML) ---
    @FXML private Label lblTotalPendapatan;
    @FXML private Label lblTotalPengeluaran;
    @FXML private Label lblTotalLaba;

    /** Rasio estimasi pengeluaran (bahan baku + operasional) = 40% pendapatan */
    private static final double RASIO_PENGELUARAN = 0.40;

    private static final Locale ID     = new Locale("id", "ID");
    private static final NumberFormat RUPIAH = NumberFormat.getInstance(ID);

    @FXML
    public void initialize() {
        // Bind kolom tabel
        if (colBulan       != null) colBulan      .setCellValueFactory(new PropertyValueFactory<>("bulan"));
        if (colPendapatan  != null) colPendapatan .setCellValueFactory(new PropertyValueFactory<>("pendapatan"));
        if (colPengeluaran != null) colPengeluaran.setCellValueFactory(new PropertyValueFactory<>("pengeluaran"));
        if (colLabaBersih  != null) colLabaBersih .setCellValueFactory(new PropertyValueFactory<>("labaBersih"));
        if (colMargin      != null) colMargin     .setCellValueFactory(new PropertyValueFactory<>("margin"));

        muatDataDariXML();
    }

    private void muatDataDariXML() {
        List<Pesanan> semuaPesanan = Pesanan.getAllPesanan();

        // Kelompokkan pendapatan per bulan (hanya pesanan Lunas)
        Map<String, Double> pendapatanPerBulan = new LinkedHashMap<>();

        for (Pesanan p : semuaPesanan) {
            if (!"Lunas".equalsIgnoreCase(p.getStatusPembayaran())) continue;
            String bulan = normalisasiBulan(p.getTanggalPengiriman());
            pendapatanPerBulan.merge(bulan, p.getTotalHarga(), Double::sum);
        }

        // Urutkan kronologis
        List<String> bulanUrut = new ArrayList<>(pendapatanPerBulan.keySet());
        bulanUrut.sort(Comparator.comparingInt(this::urutanBulan));

        // ===== Bar Chart: Pendapatan vs Pengeluaran =====
        initBarChart(bulanUrut, pendapatanPerBulan);

        // ===== Line Chart: Tren Laba Bersih =====
        initLineChart(bulanUrut, pendapatanPerBulan);

        // ===== Tabel Ringkasan =====
        isiTabelRingkasan(bulanUrut, pendapatanPerBulan);
    }

    private void initBarChart(List<String> bulanUrut, Map<String, Double> pendapatanPerBulan) {
        if (barChartLaporan == null) return;
        barChartLaporan.getData().clear();

        XYChart.Series<String, Number> seriPendapatan  = new XYChart.Series<>();
        XYChart.Series<String, Number> seriPengeluaran = new XYChart.Series<>();
        seriPendapatan .setName("Pendapatan");
        seriPengeluaran.setName("Pengeluaran (Est. 40%)");

        for (String bulan : bulanUrut) {
            double pendapatan  = pendapatanPerBulan.get(bulan);
            double pengeluaran = pendapatan * RASIO_PENGELUARAN;

            // Nilai dalam ribuan agar chart lebih terbaca
            seriPendapatan .getData().add(new XYChart.Data<>(bulan, pendapatan  / 1000.0));
            seriPengeluaran.getData().add(new XYChart.Data<>(bulan, pengeluaran / 1000.0));
        }

        if (bulanUrut.isEmpty()) {
            seriPendapatan .getData().add(new XYChart.Data<>("(belum ada data)", 0));
            seriPengeluaran.getData().add(new XYChart.Data<>("(belum ada data)", 0));
        }

        barChartLaporan.getData().addAll(seriPendapatan, seriPengeluaran);
    }

    private void initLineChart(List<String> bulanUrut, Map<String, Double> pendapatanPerBulan) {
        if (lineChartLaporan == null) return;
        lineChartLaporan.getData().clear();

        XYChart.Series<String, Number> seriLaba       = new XYChart.Series<>();
        XYChart.Series<String, Number> seriPendapatan = new XYChart.Series<>();
        seriLaba      .setName("Laba Bersih");
        seriPendapatan.setName("Pendapatan");

        for (String bulan : bulanUrut) {
            double pendapatan  = pendapatanPerBulan.get(bulan);
            double pengeluaran = pendapatan * RASIO_PENGELUARAN;
            double laba        = pendapatan - pengeluaran;

            seriPendapatan.getData().add(new XYChart.Data<>(bulan, pendapatan / 1000.0));
            seriLaba      .getData().add(new XYChart.Data<>(bulan, laba       / 1000.0));
        }

        if (bulanUrut.isEmpty()) {
            seriPendapatan.getData().add(new XYChart.Data<>("(belum ada data)", 0));
            seriLaba      .getData().add(new XYChart.Data<>("(belum ada data)", 0));
        }

        lineChartLaporan.getData().addAll(seriPendapatan, seriLaba);
    }

    private void isiTabelRingkasan(List<String> bulanUrut, Map<String, Double> pendapatanPerBulan) {
        if (tableRingkasan == null) return;

        ObservableList<RingkasanBulanan> dataRingkasan = FXCollections.observableArrayList();

        double totalPendapatan  = 0;
        double totalPengeluaran = 0;
        double totalLaba        = 0;

        for (String bulan : bulanUrut) {
            double pendapatan  = pendapatanPerBulan.get(bulan);
            double pengeluaran = pendapatan * RASIO_PENGELUARAN;
            double laba        = pendapatan - pengeluaran;
            int    margin      = (int) Math.round((laba / pendapatan) * 100);

            totalPendapatan  += pendapatan;
            totalPengeluaran += pengeluaran;
            totalLaba        += laba;

            dataRingkasan.add(new RingkasanBulanan(
                    bulan,
                    "Rp " + formatRp(pendapatan),
                    "Rp " + formatRp(pengeluaran),
                    "Rp " + formatRp(laba),
                    margin + "%"
            ));
        }

        // Baris TOTAL di bawah
        if (!bulanUrut.isEmpty()) {
            int marginTotal = totalPendapatan > 0
                    ? (int) Math.round((totalLaba / totalPendapatan) * 100) : 0;
            dataRingkasan.add(new RingkasanBulanan(
                    "TOTAL",
                    "Rp " + formatRp(totalPendapatan),
                    "Rp " + formatRp(totalPengeluaran),
                    "Rp " + formatRp(totalLaba),
                    marginTotal + "%"
            ));
        }

        tableRingkasan.setItems(dataRingkasan);
        tableRingkasan.refresh();

        // Update label statistik total (jika ada di FXML)
        if (lblTotalPendapatan  != null) lblTotalPendapatan .setText("Rp " + formatRp(totalPendapatan));
        if (lblTotalPengeluaran != null) lblTotalPengeluaran.setText("Rp " + formatRp(totalPengeluaran));
        if (lblTotalLaba        != null) lblTotalLaba        .setText("Rp " + formatRp(totalLaba));
    }

    // ======================================================
    //  Utilitas (disalin dari OwnerDashboardContentController)
    // ======================================================

    private String formatRp(double angka) {
        return RUPIAH.format((long) angka);
    }

    private String normalisasiBulan(String tanggal) {
        if (tanggal == null || tanggal.trim().isEmpty()) return "Tidak Diketahui";
        tanggal = tanggal.trim();

        if (tanggal.matches("\\d{4}-\\d{2}-\\d{2}")) {
            String[] b = tanggal.split("-");
            return singkatBulan(Integer.parseInt(b[1])) + " " + b[0];
        }

        String[] bagian = tanggal.split("\\s+");
        if (bagian.length >= 3) {
            return singkatNamaBulan(bagian[1]) + " " + bagian[2];
        }
        return tanggal;
    }

    private String singkatBulan(int bulan) {
        String[] nama = {"Jan","Feb","Mar","Apr","Mei","Jun","Jul","Agu","Sep","Okt","Nov","Des"};
        return (bulan >= 1 && bulan <= 12) ? nama[bulan - 1] : "???";
    }

    private String singkatNamaBulan(String nama) {
        switch (nama.toLowerCase()) {
            case "januari":   case "jan": return "Jan";
            case "februari":  case "feb": return "Feb";
            case "maret":     case "mar": return "Mar";
            case "april":     case "apr": return "Apr";
            case "mei":                   return "Mei";
            case "juni":      case "jun": return "Jun";
            case "juli":      case "jul": return "Jul";
            case "agustus":   case "agu": return "Agu";
            case "september": case "sep": return "Sep";
            case "oktober":   case "okt": return "Okt";
            case "november":  case "nov": return "Nov";
            case "desember":  case "des": return "Des";
            default: return nama;
        }
    }

    private int urutanBulan(String bulanTahun) {
        String[] bagian = bulanTahun.split("\\s+");
        if (bagian.length < 2) return Integer.MAX_VALUE;
        int tahun = 0;
        try { tahun = Integer.parseInt(bagian[1]); } catch (NumberFormatException e) { return Integer.MAX_VALUE; }
        String[] namaBulan = {"Jan","Feb","Mar","Apr","Mei","Jun","Jul","Agu","Sep","Okt","Nov","Des"};
        int idx = 0;
        for (int i = 0; i < namaBulan.length; i++) {
            if (namaBulan[i].equalsIgnoreCase(bagian[0])) { idx = i + 1; break; }
        }
        return tahun * 100 + idx;
    }
}