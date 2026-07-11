package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.event.ActionEvent;
import java.io.IOException;

public class KonfirmasiPembayaranController {

    @FXML private TextField txtJumlahTransfer;
    @FXML private DatePicker dpTanggalTransfer;
    @FXML private ComboBox<String> cmbMetodePembayaran;
    @FXML private TextField txtNamaPengirim;
    @FXML private TextArea txtCatatan;

    private AnchorPane paneKontenTengah;
    private String usernameSession;

    public void setPaneKontenTengah(AnchorPane pane) {
        this.paneKontenTengah = pane;
    }

    public void setUsernameSession(String username) {
        this.usernameSession = username;
    }

    @FXML
    public void initialize() {
        cmbMetodePembayaran.getItems().addAll(
            "Transfer Bank BCA",
            "Transfer Bank BRI",
            "Transfer Bank Mandiri",
            "QRIS (GoPay/OVO/Dana)"
        );
        cmbMetodePembayaran.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleKirimKonfirmasi(ActionEvent event) {
        if (txtJumlahTransfer.getText().isEmpty() || 
            dpTanggalTransfer.getValue() == null || 
            cmbMetodePembayaran.getValue() == null || 
            txtNamaPengirim.getText().isEmpty()) {

            showAlert("Semua field wajib diisi!", Alert.AlertType.WARNING);
            return;
        }

        try {
            // === PERBAIKAN DI SINI ===
            // Load halaman KonfirmasiBerhasil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/KonfirmasiBerhasil.fxml"));
            Parent berhasilView = loader.load();
            
            // Set paneKontenTengah & username ke KonfirmasiBerhasilController
            KonfirmasiBerhasilController berhasilController = loader.getController();
            berhasilController.setPaneKontenTengah(this.paneKontenTengah);
            berhasilController.setUsernameSession(this.usernameSession);
            
            // Ganti konten di paneKontenTengah (BUKAN stage.setScene!)
            if (paneKontenTengah != null) {
                paneKontenTengah.getChildren().clear();
                paneKontenTengah.getChildren().add(berhasilView);
                
                // Set anchor agar mengikuti ukuran panel
                AnchorPane.setTopAnchor(berhasilView, 0.0);
                AnchorPane.setBottomAnchor(berhasilView, 0.0);
                AnchorPane.setLeftAnchor(berhasilView, 0.0);
                AnchorPane.setRightAnchor(berhasilView, 0.0);
            }
            // ========================
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBatal(ActionEvent event) {
        try {
            // === PERBAIKAN DI SINI ===
            // Load halaman Pembayaran
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Pembayaran.fxml"));
            Parent pembayaranView = loader.load();
            
            // Set paneKontenTengah & username ke PembayaranController
            PembayaranController pembayaranController = loader.getController();
            pembayaranController.setPaneKontenTengah(this.paneKontenTengah);
            pembayaranController.setUsernameSession(this.usernameSession);
            
            // Ganti konten di paneKontenTengah (BUKAN stage.setScene!)
            if (paneKontenTengah != null) {
                paneKontenTengah.getChildren().clear();
                paneKontenTengah.getChildren().add(pembayaranView);
                
                AnchorPane.setTopAnchor(pembayaranView, 0.0);
                AnchorPane.setBottomAnchor(pembayaranView, 0.0);
                AnchorPane.setLeftAnchor(pembayaranView, 0.0);
                AnchorPane.setRightAnchor(pembayaranView, 0.0);
            }
            // ========================
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Informasi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}