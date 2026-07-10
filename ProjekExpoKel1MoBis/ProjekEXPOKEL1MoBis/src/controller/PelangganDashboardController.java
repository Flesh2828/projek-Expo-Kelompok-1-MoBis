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
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import java.util.Optional;
import javafx.scene.control.DialogPane;

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

    // Tambahkan method ini di PelangganDashboardController
        public void setPaneKontenTengah(AnchorPane pane) {
            this.paneKontenTengah = pane;
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
                tampilkanDiPanel(halaman);
            } else if (fxmlPath.equals("/view/RiwayatPesanan.fxml")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent halaman = loader.load();
                RiwayatPesananController controller = loader.getController();
                controller.setPaneKontenTengah(paneKontenTengah);
                tampilkanDiPanel(halaman);
            } else if (fxmlPath.equals("/view/Pembayaran.fxml")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent halaman = loader.load();
                PembayaranController controller = loader.getController();
                controller.setPaneKontenTengah(paneKontenTengah);
                tampilkanDiPanel(halaman);
            } else if (fxmlPath.equals("/view/KonfirmasiPembayaran.fxml")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent halaman = loader.load();
                KonfirmasiPembayaranController controller = loader.getController();
                controller.setPaneKontenTengah(paneKontenTengah);
                tampilkanDiPanel(halaman);
            } else {
                Parent halaman = FXMLLoader.load(getClass().getResource(fxmlPath));
                tampilkanDiPanel(halaman);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Menampilkan halaman di paneKontenTengah dan meregangkannya agar pas dengan jendela
    private void tampilkanDiPanel(Parent halaman) {
        paneKontenTengah.getChildren().setAll(halaman);
        AnchorPane.setTopAnchor(halaman, 0.0);
        AnchorPane.setBottomAnchor(halaman, 0.0);
        AnchorPane.setLeftAnchor(halaman, 0.0);
        AnchorPane.setRightAnchor(halaman, 0.0);
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

            tampilkanDiPanel(halamanForm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==================== LOGOUT ====================

    public void handleLogout(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Konfirmasi Keluar");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialogPane.setStyle("-fx-background-color: #FFFAF0; -fx-border-color: #C35E2A; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(javafx.geometry.Pos.CENTER);

        Label labelTanya = new Label("🚪 Konfirmasi Keluar");
        labelTanya.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #C35E2A;");

        Label labelSub = new Label("Apakah Anda yakin ingin keluar dari akun?");
        labelSub.setStyle("-fx-font-size: 14px; -fx-text-fill: #4A2E1B;");

        content.getChildren().addAll(labelTanya, labelSub);
        dialogPane.setContent(content);

        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setText("Keluar");
        okButton.setStyle("-fx-background-color: #C35E2A; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 8 20; -fx-cursor: hand;");

        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
        cancelButton.setText("Batal");
        cancelButton.setStyle("-fx-background-color: #E0E0E0; -fx-text-fill: #333333; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 8 20; -fx-cursor: hand;");

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
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


        
}