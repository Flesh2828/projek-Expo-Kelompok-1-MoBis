package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.event.ActionEvent;
import java.io.IOException;

public class KonfirmasiBerhasilController {

    private AnchorPane paneKontenTengah;
    private String usernameSession;

    public void setPaneKontenTengah(AnchorPane pane) {
        this.paneKontenTengah = pane;
    }

    public void setUsernameSession(String username) {
        this.usernameSession = username;
    }

    @FXML
    private void handleKembali(ActionEvent event) {
        try {
            // === PERBAIKAN DI SINI ===
            // Load halaman PelangganDashboard
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PelangganDashboard.fxml"));
            Parent dashboardView = loader.load();
            
            // Dapatkan controller dashboard
            PelangganDashboardController dashboardController = loader.getController();
            dashboardController.setPaneKontenTengah(this.paneKontenTengah);
            // Set username terakhir supaya dashboard tidak balik ke default "Pelanggan"
            // (harus dipanggil SEBELUM ditampilkan, biar tidak menimpa tampilan dashboard)
            dashboardController.setUsernameSession(this.usernameSession);
            
            // Ganti konten di paneKontenTengah
            if (paneKontenTengah != null) {
                paneKontenTengah.getChildren().clear();
                paneKontenTengah.getChildren().add(dashboardView);
                
                AnchorPane.setTopAnchor(dashboardView, 0.0);
                AnchorPane.setBottomAnchor(dashboardView, 0.0);
                AnchorPane.setLeftAnchor(dashboardView, 0.0);
                AnchorPane.setRightAnchor(dashboardView, 0.0);
            }
            // ========================
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLihatRiwayat(ActionEvent event) {
        try {
            // === PERBAIKAN DI SINI ===
            // Load halaman RiwayatPesanan
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/RiwayatPesanan.fxml"));
            Parent riwayatView = loader.load();

            // Dapatkan controller riwayat & set username, pane kontennya
            RiwayatPesananController riwayatController = loader.getController();
            riwayatController.setPaneKontenTengah(this.paneKontenTengah);
            riwayatController.setUsernameSession(this.usernameSession); // WAJIB, biar data riwayat ikut termuat

            // Ganti konten di paneKontenTengah
            if (paneKontenTengah != null) {
                paneKontenTengah.getChildren().clear();
                paneKontenTengah.getChildren().add(riwayatView);
                
                AnchorPane.setTopAnchor(riwayatView, 0.0);
                AnchorPane.setBottomAnchor(riwayatView, 0.0);
                AnchorPane.setLeftAnchor(riwayatView, 0.0);
                AnchorPane.setRightAnchor(riwayatView, 0.0);
            }
            // ========================
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}