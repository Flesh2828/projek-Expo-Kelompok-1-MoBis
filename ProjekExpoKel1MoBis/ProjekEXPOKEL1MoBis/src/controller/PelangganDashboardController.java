package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class PelangganDashboardController {
    @FXML private AnchorPane paneKontenTengah;
    @FXML private Text txtNamaUser;
    private String userAktifSession = "Pelanggan Aktif"; // Placeholder sementara, nanti diganti dengan session user yang login
    
    public void initialize() {
        txtNamaUser.setText(userAktifSession);
        bukaHalamanKonten("/view/DaftarMenu.fxml");
    }
    private void bukaHalamanKonten(String fxmlPath) {
        try {
            Parent halaman = FXMLLoader.load(getClass().getResource(fxmlPath));
            paneKontenTengah.getChildren().setAll(halaman);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML void tampilkanDaftarMenu(ActionEvent event) { bukaHalamanKonten("/view/DaftarMenu.fxml"); }
    @FXML void tampilkanBuatPesanan(ActionEvent event) { bukaHalamanKonten("/view/BuatPesanan.fxml"); }
    @FXML void tampilkanRiwayatPesanan(ActionEvent event) { bukaHalamanKonten("/view/RiwayatPesanan.fxml"); }
    @FXML void tampilkanLangganan(ActionEvent event) { bukaHalamanKonten("/view/Langganan.fxml"); }
    @FXML void tampilkanPembayaran(ActionEvent event) { bukaHalamanKonten("/view/Pembayaran.fxml"); }

    public void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("SINARING - Login");
            stage.setScene(new Scene(root, 400, 300));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}