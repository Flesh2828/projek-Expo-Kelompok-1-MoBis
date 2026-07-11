package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.User;
import util.SceneNavigator;

public class RegistrasiController {

    @FXML private TextField txtRegUsername;
    @FXML private PasswordField txtRegPassword;
    @FXML private Label lblPesanStatus;

    @FXML
    public void handleRegistrasi() {
        String userBaru = txtRegUsername.getText();
        String passBaru = txtRegPassword.getText();

        boolean sukses = User.laksanakanRegistrasi(userBaru, passBaru);

        if (sukses) {
            lblPesanStatus.setStyle("-fx-text-fill: green;");
            lblPesanStatus.setText("Registrasi Berhasil! Silakan kembali ke Login.");
        } else {
            lblPesanStatus.setStyle("-fx-text-fill: red;");
            lblPesanStatus.setText("Gagal! Username sudah ada atau kolom kosong.");
        }
    }

    @FXML
    public void keHalamanLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            SceneNavigator.switchTo(stage, root, "SINARING - Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}