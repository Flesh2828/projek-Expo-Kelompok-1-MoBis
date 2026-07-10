package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import model.Menu;
import java.io.IOException;
import java.util.List;

public class DaftarMenuController {

    private AnchorPane paneKontenTengah;

    @FXML
    private GridPane menuGrid;

    public void setPaneKontenTengah(AnchorPane pane) {
        this.paneKontenTengah = pane;
    }

    @FXML
    public void initialize() {
        loadMenuDariXML();
    }

    private void loadMenuDariXML() {
        if (menuGrid == null) return;
        menuGrid.getChildren().clear();

        List<Menu> allMenu = Menu.getAllMenu();
        int col = 0;
        int row = 0;

        for (Menu m : allMenu) {
            // Hanya tampilkan menu yang statusnya "aktif"
            if (!m.getStatus().equalsIgnoreCase("aktif")) {
                continue;
            }

            VBox card = createMenuCard(m);
            menuGrid.add(card, col, row);

            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createMenuCard(Menu m) {
        VBox card = new VBox();
        card.setSpacing(5.0);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setMaxHeight(Double.MAX_VALUE);
        card.setPrefHeight(485.0);
        card.setPrefWidth(472.0);
        card.setStyle("-fx-background-color: #FFFAF0; -fx-border-color: #E0E0E0; -fx-border-radius: 10; -fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 3);");

        // Gambar Menu
        ImageView imgView = new ImageView();
        imgView.setFitHeight(300.0);
        imgView.setFitWidth(300.0);
        imgView.setPickOnBounds(true);
        imgView.setPreserveRatio(true);
        imgView.setImage(getMenuImage(m.getNamaMenu()));
        VBox.setMargin(imgView, new Insets(0, 0, 0, 70.0));

        // Nama Menu
        Label lblNama = new Label(m.getNamaMenu());
        lblNama.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-fill: black;");

        // Deskripsi Menu
        Label lblDesc = new Label(m.getDeskripsi());
        lblDesc.setStyle("-fx-font-size: 20; -fx-fill: #666666;");
        lblDesc.setWrapText(true);

        // Harga Menu (format rupiah)
        String formattedHarga = String.format("Rp %,.0f/porsi", m.getHarga()).replace(',', '.');
        Label lblHarga = new Label(formattedHarga);
        lblHarga.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-fill: #F54D2D;");

        // Tombol Pesan
        Button btnPesan = new Button("Pesan");
        btnPesan.setPrefHeight(40.0);
        btnPesan.setPrefWidth(443.0);
        btnPesan.setStyle("-fx-background-color: #B8541F; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-font-size: 30; -fx-padding: 6 0;");
        btnPesan.setOnAction(event -> bukaBuatPesanan(m.getNamaMenu(), m.getHarga()));

        card.getChildren().addAll(imgView, lblNama, lblDesc, lblHarga, btnPesan);
        return card;
    }

    private Image getMenuImage(String namaMenu) {
        String name = namaMenu.toLowerCase();
        String imageName = "nasiboxspecial.jpg"; // default fallback

        if (name.contains("gudeg")) {
            imageName = "nasigudeg.jpg";
        } else if (name.contains("padang") || name.contains("naspad")) {
            imageName = "naspad.jpg";
        } else if (name.contains("bakar") || name.contains("ayam")) {
            imageName = "ayambakar.jpg";
        } else if (name.contains("mie") || name.contains("samyang") || name.contains("goreng")) {
            imageName = "migoreng.jpg";
        } else if (name.contains("catering") || name.contains("prasmanan")) {
            imageName = "catering.jpg";
        } else if (name.contains("box") || name.contains("geprek")) {
            imageName = "nasiboxspecial.jpg";
        }

        try {
            return new Image(getClass().getResourceAsStream("/view/" + imageName));
        } catch (Exception e) {
            return new Image(getClass().getResourceAsStream("/view/nasiboxspecial.jpg"));
        }
    }

    private void bukaBuatPesanan(String namaMenu, double harga) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BuatPesanan.fxml"));
            Parent root = loader.load();

            BuatPesananController controller = loader.getController();
            controller.setMenuTerpilih(namaMenu, harga);

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