package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class OwnerDashboardController {

    @FXML private Button btnDashboard;
    @FXML private Button btnLaporan;
    @FXML private Button btnPiutang;
    @FXML private VBox contentArea;

    @FXML
    public void initialize() {
        System.out.println("=== OwnerDashboardController Berhasil Diinisialisasi ===");
        
        // ===== INI YANG DITAMBAH! =====
        // Set dashboard sebagai default saat pertama kali dibuka
        showDashboardContent();
    }

    @FXML
    public void showDashboardContent() {
        setMenuActive(btnDashboard);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/OwnerDashboardContent.fxml"));
            Parent dashboardContent = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(dashboardContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showLaporanContent() {
        setMenuActive(btnLaporan);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/OwnerLaporan.fxml"));
            Parent laporanContent = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(laporanContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showPiutangContent() {
        setMenuActive(btnPiutang);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/OwnerPiutang.fxml"));
            Parent piutangContent = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(piutangContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setMenuActive(Button activeButton) {
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: #333333; -fx-font-size: 25; -fx-background-radius: 8;";
        String activeStyle = "-fx-background-color: #C35E2A; -fx-text-fill: white; -fx-font-size: 25; -fx-font-weight: bold; -fx-background-radius: 8;";

        btnDashboard.setStyle(defaultStyle);
        btnLaporan.setStyle(defaultStyle);
        btnPiutang.setStyle(defaultStyle);

        if (activeButton != null) {
            activeButton.setStyle(activeStyle);
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
            Stage stage = (Stage) btnDashboard.getScene().getWindow();
            stage.setTitle("SINARING - Login");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}