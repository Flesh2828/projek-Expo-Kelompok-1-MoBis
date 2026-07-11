package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import java.util.Optional;
import javafx.scene.control.DialogPane;
import util.SceneNavigator;

public class AdminDashboardController {
    @FXML private AnchorPane paneKontenAdmin; // Wadah tengah halaman admin
    @FXML private Button btnRingkasan;
    @FXML private Button btnKelolaPesanan;
    @FXML private Button btnKelolaMenu;

    @FXML
    public void initialize() {
        // default admin masuk
        bukaHalamanAdmin("/view/AdminRingkasan.fxml");
        setTombolAktif(btnRingkasan);
    }

    // ubah konten tengah admin
    public void bukaHalamanAdmin(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent halaman = loader.load();
            paneKontenAdmin.getChildren().setAll(halaman);
        } catch (Exception e) {
            System.out.println("Gagal memuat halaman admin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // tandai tombol sidebar yang sedang aktif (sama seperti Owner & Pelanggan)
    private void setTombolAktif(Button tombolAktif) {
        Button[] semuaTombol = {btnRingkasan, btnKelolaPesanan, btnKelolaMenu};

        for (Button btn : semuaTombol) {
            if (btn != null) {
                btn.getStyleClass().remove("aktif");
            }
        }

        if (tombolAktif != null) {
            tombolAktif.getStyleClass().add("aktif");
        }
    }

    @FXML void tampilkanRingkasan(ActionEvent event) {
        bukaHalamanAdmin("/view/AdminRingkasan.fxml");
        setTombolAktif(btnRingkasan);
    }

    @FXML void tampilkanKelolaPesanan(ActionEvent event) {
        bukaHalamanAdmin("/view/KelolaPesanan.fxml");
        setTombolAktif(btnKelolaPesanan);
    }

    @FXML void tampilkanKelolaMenu(ActionEvent event) {
        bukaHalamanAdmin("/view/KelolaMenu.fxml");
        setTombolAktif(btnKelolaMenu);
    }
    
    @FXML
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
                SceneNavigator.switchTo(stage, root, "SINARING - Login");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

