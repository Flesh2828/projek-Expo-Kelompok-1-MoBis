package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;

public class AdminDashboardController {
    @FXML private AnchorPane paneKontenAdmin; // Wadah tengah halaman admin

    @FXML
    public void initialize() {
        // default admin masuk
        bukaHalamanAdmin("/view/AdminRingkasan.fxml"); 
    }

    // ubah konten tengah admin
    public void bukaHalamanAdmin(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent halaman = loader.load();
            paneKontenAdmin.getChildren().setAll(halaman);
        } catch (Exception e) {
            System.out.println("Gagal memuat halaman admin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML void tampilkanRingkasan(ActionEvent event) { bukaHalamanAdmin("/view/AdminRingkasan.fxml"); }
    @FXML void tampilkanKelolaPesanan(ActionEvent event) { bukaHalamanAdmin("/view/KelolaPesanan.fxml"); }
    @FXML void tampilkanKelolaMenu(ActionEvent event) { bukaHalamanAdmin("/view/KelolaMenu.fxml"); }
    
    @FXML
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

