package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class AdminRingkasanController {

    @FXML private BarChart<String, Number> barChartPerbandingan;
    @FXML private LineChart<String, Number> lineChartLaba;
    @FXML private TableView<RingkasanModel> tableRingkasan;
    @FXML private TableColumn<RingkasanModel, String> colBulan;
    @FXML private TableColumn<RingkasanModel, String> colPendapatan;
    @FXML private TableColumn<RingkasanModel, String> colPengeluaran;
    @FXML private TableColumn<RingkasanModel, String> colLaba;
    @FXML private TableColumn<RingkasanModel, String> colMargin;

    @FXML
    public void initialize() {
        populateCharts();
        populateTable();
    }

    private void populateCharts() {
        // 1. Bar Chart: Pendapatan vs Pengeluaran
        XYChart.Series<String, Number> seriesPendapatan = new XYChart.Series<>();
        seriesPendapatan.setName("Pendapatan");
        seriesPendapatan.getData().add(new XYChart.Data<>("Feb", 18.5));
        seriesPendapatan.getData().add(new XYChart.Data<>("Mar", 22.0));
        seriesPendapatan.getData().add(new XYChart.Data<>("Apr", 19.5));
        seriesPendapatan.getData().add(new XYChart.Data<>("Mei", 25.0));
        seriesPendapatan.getData().add(new XYChart.Data<>("Jun", 24.0));
        seriesPendapatan.getData().add(new XYChart.Data<>("Jul", 26.5));

        XYChart.Series<String, Number> seriesPengeluaran = new XYChart.Series<>();
        seriesPengeluaran.setName("Pengeluaran");
        seriesPengeluaran.getData().add(new XYChart.Data<>("Feb", 12.0));
        seriesPengeluaran.getData().add(new XYChart.Data<>("Mar", 14.5));
        seriesPengeluaran.getData().add(new XYChart.Data<>("Apr", 13.0));
        seriesPengeluaran.getData().add(new XYChart.Data<>("Mei", 14.5));
        seriesPengeluaran.getData().add(new XYChart.Data<>("Jun", 14.8));
        seriesPengeluaran.getData().add(new XYChart.Data<>("Jul", 15.5));

        barChartPerbandingan.getData().addAll(seriesPendapatan, seriesPengeluaran);

        // 2. Line Chart: Tren Laba Bersih
        XYChart.Series<String, Number> seriesLaba = new XYChart.Series<>();
        seriesLaba.setName("Laba Bersih");
        seriesLaba.getData().add(new XYChart.Data<>("Feb", 6.5));
        seriesLaba.getData().add(new XYChart.Data<>("Mar", 7.5));
        seriesLaba.getData().add(new XYChart.Data<>("Apr", 6.5));
        seriesLaba.getData().add(new XYChart.Data<>("Mei", 10.5));
        seriesLaba.getData().add(new XYChart.Data<>("Jun", 9.2));
        seriesLaba.getData().add(new XYChart.Data<>("Jul", 11.0));

        lineChartLaba.getData().add(seriesLaba);
    }

    private void populateTable() {
        colBulan.setCellValueFactory(new PropertyValueFactory<>("bulan"));
        colPendapatan.setCellValueFactory(new PropertyValueFactory<>("pendapatan"));
        colPengeluaran.setCellValueFactory(new PropertyValueFactory<>("pengeluaran"));
        colLaba.setCellValueFactory(new PropertyValueFactory<>("laba"));
        colMargin.setCellValueFactory(new PropertyValueFactory<>("margin"));

        ObservableList<RingkasanModel> data = FXCollections.observableArrayList(
            new RingkasanModel("Feb 2026", "Rp 18.500.000", "Rp 12.000.000", "Rp 6.500.000", "35%"),
            new RingkasanModel("Mar 2026", "Rp 22.000.000", "Rp 14.500.000", "Rp 7.500.000", "34%"),
            new RingkasanModel("Apr 2026", "Rp 19.500.000", "Rp 13.000.000", "Rp 6.500.000", "33%"),
            new RingkasanModel("Mei 2026", "Rp 25.000.000", "Rp 14.500.000", "Rp 10.500.000", "42%"),
            new RingkasanModel("Jun 2026", "Rp 24.000.000", "Rp 14.800.000", "Rp 9.200.000", "38%"),
            new RingkasanModel("Jul 2026", "Rp 26.500.000", "Rp 15.500.000", "Rp 11.000.000", "41%")
        );
        tableRingkasan.setItems(data);
    }

    @FXML
    public void handleDashboard(MouseEvent event) {
        switchScene(event, "/view/OwnerDashboard.fxml", "SINARING - Dashboard Owner");
    }

    @FXML
    public void handlePiutang(MouseEvent event) {
        switchScene(event, "/view/Langganan.fxml", "SINARING - Piutang Pelanggan");
    }

    @FXML
    public void handleLogout(MouseEvent event) {
        switchScene(event, "/view/Login.fxml", "SINARING - Login");
    }

    private void switchScene(MouseEvent event, String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root, 1280, 720));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Inner Model Class for Table Binding
    public static class RingkasanModel {
        private final String bulan;
        private final String pendapatan;
        private final String pengeluaran;
        private final String laba;
        private final String margin;

        public RingkasanModel(String bulan, String pendapatan, String pengeluaran, String laba, String margin) {
            this.bulan = bulan;
            this.pendapatan = pendapatan;
            this.pengeluaran = pengeluaran;
            this.laba = laba;
            this.margin = margin;
        }

        public String getBulan() { return bulan; }
        public String getPendapatan() { return pendapatan; }
        public String getPengeluaran() { return pengeluaran; }
        public String getLaba() { return laba; }
        public String getMargin() { return margin; }
    }
}