package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.Menu;
import java.io.IOException;
import java.util.List;

public class DaftarMenuController {

    @FXML
    private VBox menuContainer;

    // ===== INI DITAMBAHKAN =====
    private AnchorPane paneKontenTengah;  // ← VARIABEL
    // ===========================

    private List<Menu> daftarMenu;

    @FXML
    public void initialize() {
        daftarMenu = Menu.getAllMenu();
        if (daftarMenu == null || daftarMenu.isEmpty()) {
            daftarMenu = List.of(
                new Menu("M001", "Nasi Box Spesial", "Nasi putih, ayam goreng, tempe, sayur, sambal", 25000, "aktif"),
                new Menu("M002", "Nasi Gudeg Komplit", "Nasi gudeg, ayam, krecek, telur bacem", 28000, "aktif"),
                new Menu("M003", "Nasi Padang", "Nasi dengan lauk pilihan rendang dan gulai", 30000, "aktif"),
                new Menu("M004", "Ayam Bakar Komplit", "Ayam bakar, nasi, lalapan, sambal terasi", 32000, "aktif"),
                new Menu("M005", "Mie Goreng Spesial", "Mie goreng dengan telur, udang, sayuran segar", 20000, "aktif"),
                new Menu("M006", "Catering Prasmanan", "Paket lengkap untuk acara, min. 50 porsi", 45000, "aktif")
            );
        }
    }

    // ===== INI DITAMBAHKAN (METHOD SETTER) =====
    public void setPaneKontenTengah(AnchorPane pane) {
        this.paneKontenTengah = pane;
    }
    // ===========================================

    @FXML
    private void handlePesanNasiBox(ActionEvent event) {
        bukaBuatPesanan("Nasi Box Spesial", 25000);
    }

    @FXML
    private void handlePesanNasiGudeg(ActionEvent event) {
        bukaBuatPesanan("Nasi Gudeg Komplit", 28000);
    }

    @FXML
    private void handlePesanNasiPadang(ActionEvent event) {
        bukaBuatPesanan("Nasi Padang", 30000);
    }

    @FXML
    private void handlePesanAyamBakar(ActionEvent event) {
        bukaBuatPesanan("Ayam Bakar Komplit", 32000);
    }

    @FXML
    private void handlePesanMieGoreng(ActionEvent event) {
        bukaBuatPesanan("Mie Goreng Spesial", 20000);
    }

    @FXML
    private void handlePesanPrasmanan(ActionEvent event) {
        bukaBuatPesanan("Catering Prasmanan", 45000);
    }

    private void bukaBuatPesanan(String namaMenu, double harga) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BuatPesanan.fxml"));
            Parent root = loader.load();

            BuatPesananController controller = loader.getController();
            controller.setMenuTerpilih(namaMenu, harga);

            if (paneKontenTengah != null) {
                paneKontenTengah.getChildren().setAll(root);
            } else {
                System.err.println("paneKontenTengah is null!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBuatPesanan() {
        // Method untuk tombol sidebar "Buat Pesanan"
    }
}