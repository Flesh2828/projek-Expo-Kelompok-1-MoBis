package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import java.util.Optional;
import javafx.scene.control.DialogPane;
import java.io.IOException;
import util.SceneNavigator;

public class OwnerDashboardController {

    @FXML private Button btnDashboard;
    @FXML private Button btnLaporan;
    @FXML private Button btnPiutang;
    @FXML private VBox contentArea;

    @FXML
    public void initialize() {
        System.out.println("=== OwnerDashboardController Berhasil Diinisialisasi ===");
        
        // ===== INI YANG DITAMBAH! =====
        // Set dashboard sebagai default saat pertama kali dibuka
        showDashboardContent();
    }

    @FXML
    public void showDashboardContent() {
        setMenuActive(btnDashboard);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/OwnerDashboardContent.fxml"));
            Parent dashboardContent = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(dashboardContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showLaporanContent() {
        setMenuActive(btnLaporan);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/OwnerLaporan.fxml"));
            Parent laporanContent = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(laporanContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showPiutangContent() {
        setMenuActive(btnPiutang);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/OwnerPiutang.fxml"));
            Parent piutangContent = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(piutangContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setMenuActive(Button activeButton) {
        Button[] semuaTombol = {btnDashboard, btnLaporan, btnPiutang};

        for (Button btn : semuaTombol) {
            if (btn != null) {
                btn.getStyleClass().remove("aktif");
            }
        }

        if (activeButton != null) {
            activeButton.getStyleClass().add("aktif");
        }
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
                Stage stage = (Stage) btnDashboard.getScene().getWindow();
                SceneNavigator.switchTo(stage, root, "SINARING - Login");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}