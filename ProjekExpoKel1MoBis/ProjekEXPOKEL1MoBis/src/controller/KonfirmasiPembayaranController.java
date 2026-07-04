
package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;

public class KonfirmasiPembayaranController {

    @FXML private TextField txtJumlahTransfer;
    @FXML private DatePicker dpTanggalTransfer;
    @FXML private ComboBox<String> cmbMetodePembayaran;
    @FXML private TextField txtNamaPengirim;
    @FXML private TextArea txtCatatan;

    // ===== INI YANG DITAMBAHKAN =====
    private AnchorPane paneKontenTengah;

    public void setPaneKontenTengah(AnchorPane pane) {
        this.paneKontenTengah = pane;
    }
    // ================================

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
            Parent root = FXMLLoader.load(getClass().getResource("/view/KonfirmasiBerhasil.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Konfirmasi Berhasil - SINARING");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBatal(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/Pembayaran.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Pembayaran - SINARING");
            stage.show();
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