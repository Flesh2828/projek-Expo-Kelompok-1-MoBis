package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.IOException;

public class OwnerDashboardController {

    @FXML private Button btnDashboard;
    @FXML private Button btnLaporan;
    @FXML private Button btnPiutang;
    @FXML private LineChart<String, Number> chartDashboard;
    @FXML private AnchorPane paneKontenTengah;

    @FXML
    public void initialize() {
        System.out.println("=== OwnerDashboardController Berhasil Diinisialisasi ===");

        btnDashboard.setOnAction(event -> {
            System.out.println("Tombol Dashboard diklik!");
            showDashboardContent();
        });

        btnLaporan.setOnAction(event -> {
            System.out.println("Tombol Laporan Keuangan diklik!");
            showLaporanContent();
        });

        btnPiutang.setOnAction(event -> {
            System.out.println("Tombol Piutang diklik!");
            showPiutangContent();
        });

        initDashboardChart();
        setMenuActive(btnDashboard);
    }

    private void initDashboardChart() {
        if (chartDashboard != null) {
            chartDashboard.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Laba Bersih");
            series.getData().add(new XYChart.Data<>("Feb", 6.5));
            series.getData().add(new XYChart.Data<>("Mar", 7.5));
            series.getData().add(new XYChart.Data<>("Apr", 6.5));
            series.getData().add(new XYChart.Data<>("Mei", 9.5));
            series.getData().add(new XYChart.Data<>("Jun", 8.5));
            series.getData().add(new XYChart.Data<>("Jul", 11.0));
            chartDashboard.getData().add(series);
        }
    }

    @FXML
    private void showDashboardContent() {
        setMenuActive(btnDashboard);
        tampilkanHalaman("/view/OwnerDashboard.fxml");
    }

    @FXML
    private void showLaporanContent() {
        setMenuActive(btnLaporan);
        tampilkanHalaman("/view/OwnerLaporan.fxml");
    }

    @FXML
    private void showPiutangContent() {
        setMenuActive(btnPiutang);
        tampilkanHalaman("/view/OwnerPiutang.fxml");
    }

    // ===== TAMPILKAN HALAMAN DI DALAM ANCHORPANE =====
    private void tampilkanHalaman(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent halaman = loader.load();

            if (paneKontenTengah != null) {
                paneKontenTengah.getChildren().setAll(halaman);
                System.out.println("BERHASIL: Halaman " + fxmlPath + " dimuat!");
            } else {
                System.out.println("ERROR: paneKontenTengah null!");
            }

        } catch (IOException e) {
            System.out.println("ERROR saat memuat " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setMenuActive(Button activeButton) {
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: #333333; -fx-font-size: 25; -fx-background-radius: 8;";
        String activeStyle = "-fx-background-color: #C35E2A; -fx-text-fill: white; -fx-font-size: 25; -fx-font-weight: bold; -fx-background-radius: 8;";

        btnDashboard.setStyle(defaultStyle);
        btnLaporan.setStyle(defaultStyle);
        btnPiutang.setStyle(defaultStyle);

        activeButton.setStyle(activeStyle);
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
            Stage stage = (Stage) btnDashboard.getScene().getWindow();
            stage.setTitle("SINARING - Login");
            stage.setScene(new Scene(root, 400, 300));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}