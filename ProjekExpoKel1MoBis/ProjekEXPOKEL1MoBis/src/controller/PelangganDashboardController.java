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
import javafx.scene.control.Button;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;

public class PelangganDashboardController {
    @FXML private Button btnDaftarMenu;
    @FXML private Button btnBuatPesanan;
    @FXML private Button btnRiwayat;
    @FXML private Button btnLangganan;
    @FXML private Button btnPembayaran;

   
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
    @FXML
    private void setTombolAktif(Button tombolAktif) {
    // Kumpulkan semua variabel tombol sidebar yang terdaftar via fx:id
    Button[] semuaTombol = {btnDaftarMenu, btnBuatPesanan, btnRiwayat, btnLangganan, btnPembayaran};
    
    // Reset class aktif dari semua tombol
    for (Button btn : semuaTombol) {
        if (btn != null) {
            btn.getStyleClass().remove("aktif");
        }
    }
    
    // Tambahkan class aktif ke tombol yang baru saja diklik
    if (tombolAktif != null) {
        tombolAktif.getStyleClass().add("aktif");
    }
}

// Lakukan hal yang sama untuk tombol Riwayat, Langganan, dan Pembayaran...
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
        setTombolAktif(btnDaftarMenu);
    }

    @FXML
    void tampilkanBuatPesanan(ActionEvent event) {
        bukaHalamanKonten("/view/BuatPesanan.fxml");
        setTombolAktif(btnBuatPesanan);
    }

    @FXML
    void tampilkanRiwayatPesanan(ActionEvent event) {
        bukaHalamanKonten("/view/RiwayatPesanan.fxml"); // sesuaikan dengan nama file fxml riwayat kalian
        setTombolAktif(btnRiwayat);
    }

    @FXML
    void tampilkanLangganan(ActionEvent event) {
        bukaHalamanKonten("/view/Langganan.fxml"); // sesuaikan dengan nama file fxml langganan kalian
        setTombolAktif(btnLangganan);
    }

    @FXML
    void tampilkanPembayaran(ActionEvent event) {
        bukaHalamanKonten("/view/Pembayaran.fxml"); // sesuaikan dengan nama file fxml pembayaran kalian
        setTombolAktif(btnPembayaran);
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