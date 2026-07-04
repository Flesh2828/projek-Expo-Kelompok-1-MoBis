


package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;

public class PembayaranController {

    @FXML private Text txtNoPesanan;
    @FXML private Text txtDpDibayar;
    @FXML private Text txtSisaTagihan;

    // ===== INI YANG DITAMBAHKAN =====
    private AnchorPane paneKontenTengah;

    public void setPaneKontenTengah(AnchorPane pane) {
        this.paneKontenTengah = pane;
    }
    // ================================

    @FXML
    public void initialize() {
        txtNoPesanan.setText("ORD-012 - Catering Prasmanan (50 porsi)");
        txtDpDibayar.setText("DP sudah dibayar: Rp 225.000");
        txtSisaTagihan.setText("Rp 2.025.000");
    }

    @FXML
    private void handleSalinRekening(ActionEvent event) {
        Button btn = (Button) event.getSource();
        String rekening = "";

        VBox parent = (VBox) btn.getParent();
        Text rekeningText = (Text) parent.getChildren().get(1);
        rekening = rekeningText.getText();

        StringSelection selection = new StringSelection(rekening);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);

        btn.setText("✔ Disalin");
        btn.setStyle("-fx-background-color: #A5D6A7; -fx-text-fill: #2E7D32; -fx-font-size: 11;");
    }

    @FXML
    private void handleKonfirmasiBayar(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/KonfirmasiPembayaran.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Konfirmasi Pembayaran - SINARING");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}