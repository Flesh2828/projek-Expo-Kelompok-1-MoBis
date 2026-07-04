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

    @FXML
    private AnchorPane paneKontenTengah;  // ← HURUF K BESAR!
    @FXML
    private Text txtNamaUser;

    private String userAktifSession = "Pelanggan Aktif";

    // Getter untuk diakses dari controller lain
    public AnchorPane getPaneKontenTengah() {
        return paneKontenTengah;
    }

    @FXML
    public void initialize() {
        txtNamaUser.setText(userAktifSession);
        bukaHalamanKonten("/view/DaftarMenu.fxml");
    }

    // ==================== METHOD UNTUK BUKA HALAMAN ====================

    private void bukaHalamanKonten(String fxmlPath) {
        try {
            if (fxmlPath.equals("/view/DaftarMenu.fxml")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent halaman = loader.load();
                DaftarMenuController controller = loader.getController();
                controller.setPaneKontenTengah(paneKontenTengah);
                paneKontenTengah.getChildren().setAll(halaman);
            } else if (fxmlPath.equals("/view/RiwayatPesanan.fxml")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent halaman = loader.load();
                RiwayatPesananController controller = loader.getController();
                controller.setPaneKontenTengah(paneKontenTengah);
                paneKontenTengah.getChildren().setAll(halaman);
            } else if (fxmlPath.equals("/view/Pembayaran.fxml")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent halaman = loader.load();
                PembayaranController controller = loader.getController();
                controller.setPaneKontenTengah(paneKontenTengah);
                paneKontenTengah.getChildren().setAll(halaman);
            } else if (fxmlPath.equals("/view/KonfirmasiPembayaran.fxml")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent halaman = loader.load();
                KonfirmasiPembayaranController controller = loader.getController();
                controller.setPaneKontenTengah(paneKontenTengah);
                paneKontenTengah.getChildren().setAll(halaman);
            } else {
                Parent halaman = FXMLLoader.load(getClass().getResource(fxmlPath));
                paneKontenTengah.getChildren().setAll(halaman);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==================== NAVIGASI SIDEBAR ====================

    @FXML
    void tampilkanDaftarMenu(ActionEvent event) {
        bukaHalamanKonten("/view/DaftarMenu.fxml");
    }

    @FXML
    void tampilkanBuatPesanan(ActionEvent event) {
        bukaHalamanKonten("/view/BuatPesanan.fxml");
    }

    @FXML
    void tampilkanRiwayatPesanan(ActionEvent event) {
        bukaHalamanKonten("/view/RiwayatPesanan.fxml");
    }

    @FXML
    void tampilkanLangganan(ActionEvent event) {
        bukaHalamanKonten("/view/Langganan.fxml");
    }

    @FXML
    void tampilkanPembayaran(ActionEvent event) {
        bukaHalamanKonten("/view/Pembayaran.fxml");
    }

    // ==================== UNTUK FORM PEMESANAN DARI MENU ====================

    @FXML
    void handlepesanNasiBox(ActionEvent event) {
        bukaFormPemesananDenganMenu("Nasi Box Spesial");
    }

    @FXML
    void handlepesanNasiGudeg(ActionEvent event) {
        bukaFormPemesananDenganMenu("Nasi Gudeg Komplit");
    }

    @FXML
    void handlePesanMieGoreng(ActionEvent event) {
        bukaFormPemesananDenganMenu("Mie Goreng Spesial");
    }

    @FXML
    void handlePesanPrasmanan(ActionEvent event) {
        bukaFormPemesananDenganMenu("Catering Prasmanan");
    }

    @FXML
    void handlePesanNasiPadang(ActionEvent event) {
        bukaFormPemesananDenganMenu("Nasi Padang");
    }

    @FXML
    void handlePesanAyamBakar(ActionEvent event) {
        bukaFormPemesananDenganMenu("Ayam Bakar Komplit");
    }

    private void bukaFormPemesananDenganMenu(String namaMenu) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BuatPesanan.fxml"));
            Parent halamanForm = loader.load();

            BuatPesananController formController = loader.getController();
            formController.setMenuPilihan(namaMenu);

            paneKontenTengah.getChildren().setAll(halamanForm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==================== LOGOUT ====================

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