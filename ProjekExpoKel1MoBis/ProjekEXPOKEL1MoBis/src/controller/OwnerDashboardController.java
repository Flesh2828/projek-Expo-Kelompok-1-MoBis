package controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class OwnerDashboardController {

    @FXML private Button btnDashboard;
    @FXML private Button btnLaporan;
    @FXML private Button btnPiutang;
    @FXML private LineChart<String, Number> chartDashboard;

    @FXML
    public void initialize() {
        System.out.println("=== OwnerDashboardController Berhasil Diinisialisasi ===");
        
        if (btnDashboard == null) System.out.println("Peringatan: btnDashboard tidak terhubung ke FXML!");
        if (btnLaporan == null) System.out.println("Peringatan: btnLaporan tidak terhubung ke FXML!");
        if (btnPiutang == null) System.out.println("Peringatan: btnPiutang tidak terhubung ke FXML!");

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
    }

    @FXML
    private void showLaporanContent() {
        setMenuActive(btnLaporan);
        switchScene("/view/OwnerLaporan.fxml");
    }

    @FXML
    private void showPiutangContent() {
        setMenuActive(btnPiutang);
        switchScene("/view/OwnerPiutang.fxml");
    }

    private void switchScene(String fxmlPath) {
        System.out.println("Mencoba berpindah halaman ke: " + fxmlPath);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newRoot = loader.load();
            
            if (newRoot == null) {
                System.out.println("ERROR: File FXML ditemukan tapi isinya kosong/null: " + fxmlPath);
                return;
            }

            if (btnDashboard.getScene() == null) {
                System.out.println("ERROR: Scene dari btnDashboard bernilai null!");
                return;
            }

            Stage stage = (Stage) btnDashboard.getScene().getWindow();
            if (stage == null) {
                System.out.println("ERROR: Windows Stage tidak ditemukan!");
                return;
            }

            stage.getScene().setRoot(newRoot);
            System.out.println("BERHASIL: Scene Root sukses diganti ke " + fxmlPath);
            
        } catch (IOException e) {
            System.out.println("CRITICAL ERROR saat memuat fxml: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("UNKNOWN ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setMenuActive(Button activeButton) {
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: #F0D0C0; -fx-font-size: 14; -fx-font-weight: medium; -fx-alignment: LEFT; -fx-background-radius: 12; -fx-padding: 12;";
        btnDashboard.setStyle(defaultStyle);
        btnLaporan.setStyle(defaultStyle);
        btnPiutang.setStyle(defaultStyle);

        String activeStyle = "-fx-background-color: rgba(255, 255, 255, 0.15); -fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold; -fx-alignment: LEFT; -fx-background-radius: 12; -fx-padding: 12;";
        activeButton.setStyle(activeStyle);
    }
}