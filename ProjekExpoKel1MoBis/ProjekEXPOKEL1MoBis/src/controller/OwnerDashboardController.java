package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class OwnerDashboardController {

    @FXML private LineChart<String, Number> trenLabaChart;

    @FXML
    public void initialize() {
        populateChart();
    }

    private void populateChart() {
        if (trenLabaChart == null) return;
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Feb", 6.5));
        series.getData().add(new XYChart.Data<>("Mar", 7.5));
        series.getData().add(new XYChart.Data<>("Apr", 6.5));
        series.getData().add(new XYChart.Data<>("Mei", 10.5));
        series.getData().add(new XYChart.Data<>("Jun", 9.2));
        series.getData().add(new XYChart.Data<>("Jul", 11.0));
        
        trenLabaChart.getData().clear();
        trenLabaChart.getData().add(series);

        // Styling Line Color and Symbols to match Terracotta Theme
        series.getNode().setStyle("-fx-stroke: #B54D22; -fx-stroke-width: 3.5px;");
        for (XYChart.Data<String, Number> data : series.getData()) {
            if (data.getNode() != null) {
                data.getNode().setStyle("-fx-background-color: #B54D22, white; -fx-background-radius: 5px; -fx-padding: 5px;");
            }
        }
    }

    @FXML
    public void handleLaporanKeuangan(MouseEvent event) {
        switchScene(event, "/view/AdminRingkasan.fxml", "SINARING - Laporan Keuangan");
    }

    @FXML
    public void handleLaporanKeuanganAction(ActionEvent event) {
        switchSceneAction(event, "/view/AdminRingkasan.fxml", "SINARING - Laporan Keuangan");
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

    private void switchSceneAction(ActionEvent event, String fxmlPath, String title) {
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
}
