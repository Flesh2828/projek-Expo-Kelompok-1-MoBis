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

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;

public class PembayaranController {

    @FXML 
    private Text txtNoPesanan;
    
    @FXML 
    private Text txtDpDibayar;
    
    @FXML 
    private Text txtSisaTagihan;
    
    @FXML 
    private Button btnKonfirmasiBayar;

    // ===== DEKLARASIKAN VARIABEL INI =====
    private AnchorPane paneKontenTengah;
    private String usernameSession;
    // ====================================

    // ===== SETTER UNTUK paneKontenTengah =====
    public void setPaneKontenTengah(AnchorPane pane) {
        this.paneKontenTengah = pane;
    }
    // ========================================

    // ===== SETTER UNTUK usernameSession =====
    public void setUsernameSession(String username) {
        this.usernameSession = username;
    }
    // ========================================

    @FXML
    public void initialize() {
        txtNoPesanan.setText("ORD-012 - Catering Prasmanan (50 porsi)");
        txtDpDibayar.setText("DP sudah dibayar: Rp 225.000");
        txtSisaTagihan.setText("Rp 2.025.000");
    }

    @FXML
    private void handleSalinRekening(ActionEvent event) {
        try {
            Button btn = (Button) event.getSource();
            VBox parent = (VBox) btn.getParent();
            
            if (parent.getChildren().size() > 1) {
                Node node = parent.getChildren().get(1);
                if (node instanceof Text) {
                    Text rekeningText = (Text) node;
                    String rekening = rekeningText.getText();
                    
                    if (rekening != null && !rekening.isEmpty()) {
                        StringSelection selection = new StringSelection(rekening);
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
                        
                        btn.setText("✔ Disalin");
                        btn.setStyle("-fx-background-color: #A5D6A7; -fx-text-fill: #2E7D32; -fx-font-size: 11;");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleKonfirmasiBayar(ActionEvent event) {
        try {
            // Load halaman KonfirmasiPembayaran
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/KonfirmasiPembayaran.fxml"));
            Parent konfirmasiView = loader.load();
            
            // Set paneKontenTengah & username ke KonfirmasiPembayaranController
            KonfirmasiPembayaranController konfirmasiController = loader.getController();
            konfirmasiController.setPaneKontenTengah(this.paneKontenTengah);
            konfirmasiController.setUsernameSession(this.usernameSession);
            
            // Ganti konten di paneKontenTengah
            if (paneKontenTengah != null) {
                paneKontenTengah.getChildren().clear();
                paneKontenTengah.getChildren().add(konfirmasiView);
                
                AnchorPane.setTopAnchor(konfirmasiView, 0.0);
                AnchorPane.setBottomAnchor(konfirmasiView, 0.0);
                AnchorPane.setLeftAnchor(konfirmasiView, 0.0);
                AnchorPane.setRightAnchor(konfirmasiView, 0.0);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}