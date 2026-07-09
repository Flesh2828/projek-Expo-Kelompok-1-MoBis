package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Pesanan;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RiwayatPesananController {

    private List<Pesanan> daftarRiwayat = new ArrayList<>();

    @FXML
    private Button btnPesanUlang1, btnPesanUlang2, btnPesanUlang3;

    private AnchorPane paneKontenTengah;

    @FXML
    public void initialize() {
        daftarRiwayat.add(new Pesanan("ORD-011", "Pelanggan Aktif", "Nasi Box Spesial", 10, "25 Jun 2026", "Jl. Contoh No. 1", "", "Dikirim", "Lunas", 250000));
        daftarRiwayat.add(new Pesanan("ORD-009", "Pelanggan Aktif", "Nasi Gudeq Komplit", 5, "18 Jun 2026", "Jl. Contoh No. 2", "", "Dikirim", "Lunas", 140000));
        daftarRiwayat.add(new Pesanan("ORD-012", "Pelanggan Aktif", "Catering Prasmanan", 50, "10 Jul 2026", "Jl. Contoh No. 3", "", "Dikonfirmasi", "DP", 2250000));
    }

    public void setPaneKontenTengah(AnchorPane pane) {
        this.paneKontenTengah = pane;
    }

    @FXML
    private void handlePesanUlang(javafx.event.ActionEvent event) {
        Button source = (Button) event.getSource();
        int index = -1;

        if (source == btnPesanUlang1) index = 0;
        else if (source == btnPesanUlang2) index = 1;
        else if (source == btnPesanUlang3) index = 2;

        if (index >= 0 && index < daftarRiwayat.size()) {
            Pesanan pesananLama = daftarRiwayat.get(index);
            bukaBuatPesanan(pesananLama);
        }
    }

    private void bukaBuatPesanan(Pesanan pesananLama) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BuatPesanan.fxml"));
            Parent root = loader.load();

            BuatPesananController controller = loader.getController();
            controller.setDataPesanLama(pesananLama);

            if (paneKontenTengah != null) {
                paneKontenTengah.getChildren().setAll(root);
                AnchorPane.setTopAnchor(root, 0.0);
                AnchorPane.setBottomAnchor(root, 0.0);
                AnchorPane.setLeftAnchor(root, 0.0);
                AnchorPane.setRightAnchor(root, 0.0);
            } else {
                System.err.println("paneKontenTengah is null!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}