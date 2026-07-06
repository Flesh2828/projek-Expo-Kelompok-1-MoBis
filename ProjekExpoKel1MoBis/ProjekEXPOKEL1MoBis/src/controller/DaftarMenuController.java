package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class DaftarMenuController {

    private AnchorPane paneKontenTengah;

    public void setPaneKontenTengah(AnchorPane pane) {
        this.paneKontenTengah = pane;
    }

    @FXML private void handlePesanNasiBox(ActionEvent event) { bukaBuatPesanan("Nasi Box Spesial", 25000); }
    @FXML private void handlePesanNasiGudeg(ActionEvent event) { bukaBuatPesanan("Nasi Gudeg Komplit", 28000); }
    @FXML private void handlePesanNasiPadang(ActionEvent event) { bukaBuatPesanan("Nasi Padang", 30000); }
    @FXML private void handlePesanAyamBakar(ActionEvent event) { bukaBuatPesanan("Ayam Bakar Komplit", 32000); }
    @FXML private void handlePesanMieGoreng(ActionEvent event) { bukaBuatPesanan("Mie Goreng Spesial", 20000); }
    @FXML private void handlePesanPrasmanan(ActionEvent event) { bukaBuatPesanan("Catering Prasmanan", 45000); }

    private void bukaBuatPesanan(String namaMenu, double harga) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BuatPesanan.fxml"));
            Parent root = loader.load();

            BuatPesananController controller = loader.getController();
            controller.setMenuTerpilih(namaMenu, harga);

            if (paneKontenTengah != null) {
                paneKontenTengah.getChildren().setAll(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}