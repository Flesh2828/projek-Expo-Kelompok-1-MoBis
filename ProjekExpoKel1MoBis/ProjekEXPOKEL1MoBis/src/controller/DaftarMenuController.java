package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class DaftarMenuController {

    @FXML
    private void handlePesanNasiBox(ActionEvent event) {
        bukaBuatPesanan(event, "Nasi Box Spesial", 25000);
    }

    @FXML
    private void handlePesanNasiGudeg(ActionEvent event) {
        bukaBuatPesanan(event, "Nasi Gudeg Komplit", 28000);
    }

    @FXML
    private void handlePesanNasiPadang(ActionEvent event) {
        bukaBuatPesanan(event, "Nasi Padang", 30000);
    }

    @FXML
    private void handlePesanAyamBakar(ActionEvent event) {
        bukaBuatPesanan(event, "Ayam Bakar Komplit", 32000);
    }

    @FXML
    private void handlePesanMieGoreng(ActionEvent event) {
        bukaBuatPesanan(event, "Mie Goreng Spesial", 20000);
    }

    @FXML
    private void handlePesanPrasmanan(ActionEvent event) {
        bukaBuatPesanan(event, "Catering Prasmanan", 45000);
    }

    private void bukaBuatPesanan(ActionEvent event, String menu, int harga) {
        try {
            // Load halaman Buat Pesanan
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BuatPesanan.fxml"));
            Parent halaman = loader.load();

            // Ambil controller dari halaman BuatPesanan
            BuatPesananController controller = loader.getController();

            // Kirim data menu dan harga ke halaman BuatPesanan
            controller.setMenuTerpilih(menu, harga);

            // Temukan AnchorPane (paneKontenTengah) dari PelangganDashboard
            AnchorPane paneKontenTengah = (AnchorPane) ((Node) event.getSource()).getScene().lookup("#paneKontenTengah");

            if (paneKontenTengah != null) {
                paneKontenTengah.getChildren().setAll(halaman);
            } else {
                System.err.println("paneKontenTengah tidak ditemukan!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}