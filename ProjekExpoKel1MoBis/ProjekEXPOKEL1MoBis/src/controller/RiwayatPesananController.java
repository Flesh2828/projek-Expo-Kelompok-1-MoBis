package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import model.Pesanan;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class RiwayatPesananController {
    
    @FXML private VBox riwayatContainer;
    private AnchorPane paneKontenTengah;
    private String usernameSession;

    public void setPaneKontenTengah(AnchorPane pane) {
        this.paneKontenTengah = pane;
    }

    public void setUsernameSession(String username) {
        this.usernameSession = username;
        loadDataRiwayatRealTime(); // Otomatis refresh saat diklik
    }

    @FXML
    public void initialize() {
        // Dikosongkan, menunggu setUsernameSession dipanggil
    }

    private void loadDataRiwayatRealTime() {
        if (riwayatContainer == null) return;
        riwayatContainer.getChildren().clear(); // Bersihkan layar

        // Ambil data asli dari XML berdasarkan pelanggan yang sedang login
        List<Pesanan> riwayat = Pesanan.getRiwayatByUser(usernameSession);
        Collections.reverse(riwayat); // Balik list supaya yang terbaru ada di paling atas

        if (riwayat.isEmpty()) {
            Text txtKosong = new Text("Belum ada riwayat pesanan. Yuk buat pesanan pertamamu!");
            txtKosong.setStyle("-fx-font-size: 18; -fx-fill: #999999; -fx-font-style: italic;");
            riwayatContainer.getChildren().add(txtKosong);
            return;
        }

        // Loop untuk menggambar kartu secara otomatis
        for (Pesanan p : riwayat) {
            VBox card = buatDesainKartu(p);
            riwayatContainer.getChildren().add(card);
        }
    }

    private VBox buatDesainKartu(Pesanan p) {
        VBox card = new VBox(5.0);
        card.setStyle("-fx-background-color: #FFFAF0; -fx-border-color: #E0E0E0; -fx-border-radius: 8; -fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 2);");

        HBox header = new HBox(10.0);
        Text txtId = new Text(p.getIdPesanan());
        txtId.setFont(Font.font("System", FontWeight.BOLD, 28.0));

        // Pewarnaan Label Status Dinamis
        Label lblStatus = new Label("  " + p.getStatusPesanan() + "  ");
        if (p.getStatusPesanan().equalsIgnoreCase("Dikonfirmasi")) {
            lblStatus.setStyle("-fx-font-size: 12; -fx-text-fill: white; -fx-background-color: #F39C12; -fx-padding: 2 8 2 8; -fx-background-radius: 4;");
        } else if (p.getStatusPesanan().equalsIgnoreCase("Dikirim")) {
            lblStatus.setStyle("-fx-font-size: 12; -fx-text-fill: white; -fx-background-color: #2E7D32; -fx-padding: 2 8 2 8; -fx-background-radius: 4;");
        } else {
            lblStatus.setStyle("-fx-font-size: 12; -fx-text-fill: white; -fx-background-color: #3498DB; -fx-padding: 2 8 2 8; -fx-background-radius: 4;");
        }

        Label lblBayar = new Label("  " + p.getStatusPembayaran() + "  ");
        if (p.getStatusPembayaran().equalsIgnoreCase("Lunas")) {
            lblBayar.setStyle("-fx-font-size: 12; -fx-text-fill: white; -fx-background-color: #1B5E20; -fx-padding: 2 8 2 8; -fx-background-radius: 4;");
        } else {
            lblBayar.setStyle("-fx-font-size: 12; -fx-text-fill: white; -fx-background-color: #E67E22; -fx-padding: 2 8 2 8; -fx-background-radius: 4;");
        }
        header.getChildren().addAll(txtId, lblStatus, lblBayar);

        Text txtMenu = new Text(p.getNamaMenu());
        txtMenu.setFont(Font.font("System", FontWeight.BOLD, 28.0));

        Text txtDetail = new Text(p.getJumlahPorsi() + " porsi   " + p.getTanggalPengiriman());
        txtDetail.setStyle("-fx-font-size: 20; -fx-fill: #666666;");

        HBox footer = new HBox(15.0);
        Text txtHarga = new Text(String.format("Rp %,.0f", p.getTotalHarga()).replace(',', '.'));
        txtHarga.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-fill: #C35E2A;");

        // Spacer untuk mendorong tombol ke kanan
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnPesanUlang = new Button("Pesan Ulang");
        btnPesanUlang.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-cursor: hand;");
        btnPesanUlang.setPrefSize(150, 40);
        btnPesanUlang.setOnAction(e -> bukaBuatPesanan(p));

        footer.getChildren().addAll(txtHarga, spacer, btnPesanUlang);
        card.getChildren().addAll(header, txtMenu, txtDetail, footer);
        
        return card;
    }

    private void bukaBuatPesanan(Pesanan pesananLama) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BuatPesanan.fxml"));
            Parent root = loader.load();
            BuatPesananController controller = loader.getController();
            
            // Oper data username & isi form
            controller.setUsernameSession(usernameSession);
            controller.setDataPesanLama(pesananLama);

            if (paneKontenTengah != null) {
                paneKontenTengah.getChildren().setAll(root);
                AnchorPane.setTopAnchor(root, 0.0);
                AnchorPane.setBottomAnchor(root, 0.0);
                AnchorPane.setLeftAnchor(root, 0.0);
                AnchorPane.setRightAnchor(root, 0.0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}