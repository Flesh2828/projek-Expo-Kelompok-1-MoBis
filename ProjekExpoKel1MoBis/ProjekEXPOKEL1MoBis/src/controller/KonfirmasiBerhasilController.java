package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;

public class KonfirmasiBerhasilController {

    @FXML
    private void handleKembali(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/PelangganDashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard Pelanggan - SINARING");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLihatRiwayat(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/RiwayatPesanan.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Riwayat Pesanan - SINARING");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}