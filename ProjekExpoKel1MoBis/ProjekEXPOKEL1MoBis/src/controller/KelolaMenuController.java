package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import javafx.beans.property.*;
import model.Menu;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Optional;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;

public class KelolaMenuController {
    @FXML private TableView<Menu> tableMenu;
    @FXML private TableColumn<Menu, String> colId, colNama, colDeskripsi, colStatus;
    @FXML private TableColumn<Menu, Double> colHarga;
    @FXML private TableColumn<Menu, Void> colAksi;
    @FXML private Label lblJumlahMenu;

    private ObservableList<Menu> listMasterMenu = FXCollections.observableArrayList();
    private final String xmlPath = Menu.getFilePath();
    private static final DecimalFormat FORMAT_HARGA = new DecimalFormat("#,###");

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        colNama.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNamaMenu()));
        colDeskripsi.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDeskripsi()));
        colHarga.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getHarga()));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));

        colHarga.setCellFactory(col -> new TableCell<Menu, Double>() {
            @Override
            protected void updateItem(Double harga, boolean empty) {
                super.updateItem(harga, empty);
                setText(empty || harga == null ? null : "Rp " + FORMAT_HARGA.format(harga));
            }
        });

        colStatus.setCellFactory(col -> new TableCell<Menu, String>() {
            private final Label badge = new Label();
            { badge.getStyleClass().add("badge-pill"); setAlignment(Pos.CENTER_LEFT); }
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null || status.isEmpty()) {
                    setGraphic(null);
                } else {
                    badge.setText(status);
                    boolean aktif = status.trim().equalsIgnoreCase("aktif");
                    badge.setStyle(aktif
                        ? "-fx-background-color:#DCF5DF; -fx-text-fill:#1E8E4D;"
                        : "-fx-background-color:#FBDCDF; -fx-text-fill:#C0392B;");
                    setGraphic(badge);
                }
            }
        });

        colAksi.setCellFactory(col -> new TableCell<Menu, Void>() {
            private final Button btnEdit = new Button("\u270E");
            private final Button btnHapus = new Button("\uD83D\uDDD1");
            private final HBox box = new HBox(6.0, btnEdit, btnHapus);
            {
                btnEdit.getStyleClass().add("btn-aksi-icon");
                btnHapus.getStyleClass().addAll("btn-aksi-icon", "btn-aksi-hapus");
                box.setAlignment(Pos.CENTER_LEFT);
                btnEdit.setOnAction(e -> bukaDialogEditMenu(getTableView().getItems().get(getIndex())));
                btnHapus.setOnAction(e -> hapusMenu(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });

        loadMenuDariXML();
    }

    private void loadMenuDariXML() {
        listMasterMenu.clear();
        try {
            File file = new File(xmlPath);
            if (!file.exists()) return;

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            NodeList nList = doc.getElementsByTagName("menu");

            for (int i = 0; i < nList.getLength(); i++) {
                Element el = (Element) nList.item(i);
                Menu m = new Menu(
                    el.getAttribute("id"),
                    el.getElementsByTagName("nama").item(0).getTextContent(),
                    el.getElementsByTagName("deskripsi").item(0).getTextContent(),
                    Double.parseDouble(el.getElementsByTagName("harga").item(0).getTextContent()),
                    el.getElementsByTagName("status").item(0).getTextContent()
                );
                listMasterMenu.add(m);
            }
            tableMenu.setItems(listMasterMenu);
            if (lblJumlahMenu != null) lblJumlahMenu.setText(listMasterMenu.size() + " menu");
        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Membangun Dialog bergaya kartu putih custom (judul + tombol X, field-field
     * dengan latar krem rounded, tombol Batal/Simpan pil) seperti pada desain.
     * Dipakai bersama oleh alur Tambah Menu maupun Edit Menu.
     */
    private Dialog<ButtonType> buatDialogFormMenu(String judul, String labelAksi,
                                                   TextField txtNama, TextArea txtDeskripsi,
                                                   TextField txtHarga, ComboBox<String> cmbStatus) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setTitle(judul);

        // CATATAN PERBAIKAN: sebelumnya tombol Batal/Simpan diambil lewat
        // pane.lookupButton(...) dari ButtonBar bawaan DialogPane, lalu di-"pindah"
        // (reparent) ke footerRow custom. ButtonBar bawaan itu di-nol-kan tingginya
        // lewat CSS (.dialog-pane-menu > .button-bar), dan proses reparent-nya
        // tidak selalu selesai sebelum dialog tampil sehingga tombolnya jadi
        // tidak kelihatan di modal (lihat gambar 2). Sekarang tombol Batal/Simpan
        // dibuat sebagai Button biasa yang berdiri sendiri di footerRow, jadi selalu
        // pasti tampil, tidak bergantung pada ButtonBar bawaan JavaFX.
        ButtonType aksiButtonType = new ButtonType(labelAksi, ButtonBar.ButtonData.OK_DONE);
        DialogPane pane = dialog.getDialogPane();
        // Tetap didaftarkan (kosong secara visual) supaya tombol Enter/Esc & hasil
        // dialog.showAndWait() tetap konsisten dengan ButtonType di atas.
        pane.getButtonTypes().addAll(aksiButtonType, ButtonType.CANCEL);
        pane.getStyleClass().add("dialog-pane-menu");
        pane.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());

        Label lblJudul = new Label(judul);
        lblJudul.getStyleClass().add("modal-title");
        Button btnClose = new Button("\u00D7");
        btnClose.getStyleClass().add("modal-close-btn");
        btnClose.setOnAction(e -> { dialog.setResult(ButtonType.CANCEL); dialog.close(); });
        Region spacerHeader = new Region();
        HBox.setHgrow(spacerHeader, Priority.ALWAYS);
        HBox headerRow = new HBox(lblJudul, spacerHeader, btnClose);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label lblNama = new Label("Nama Menu"); lblNama.getStyleClass().add("modal-label");
        txtNama.getStyleClass().add("modal-field");
        txtNama.setPromptText("Contoh: Nasi Box Spesial");

        Label lblDeskripsi = new Label("Deskripsi"); lblDeskripsi.getStyleClass().add("modal-label");
        txtDeskripsi.getStyleClass().add("modal-field");
        txtDeskripsi.setPromptText("Deskripsi singkat menu");
        txtDeskripsi.setWrapText(true);
        txtDeskripsi.setPrefRowCount(3);

        Label lblHarga = new Label("Harga (Rp)"); lblHarga.getStyleClass().add("modal-label");
        txtHarga.getStyleClass().add("modal-field");
        txtHarga.setPromptText("25000");

        Label lblStatus = new Label("Status"); lblStatus.getStyleClass().add("modal-label");
        cmbStatus.getStyleClass().add("modal-field");
        cmbStatus.setMaxWidth(Double.MAX_VALUE);

        Button btnBatal = new Button("Batal");
        btnBatal.getStyleClass().add("btn-batal-modal");
        btnBatal.setOnAction(e -> { dialog.setResult(ButtonType.CANCEL); dialog.close(); });

        Button btnAksi = new Button(labelAksi);
        btnAksi.getStyleClass().add("btn-simpan-modal");
        btnAksi.setDefaultButton(true);
        btnAksi.setOnAction(e -> { dialog.setResult(aksiButtonType); dialog.close(); });

        HBox footerRow = new HBox(10.0, btnBatal, btnAksi);
        footerRow.setAlignment(Pos.CENTER_RIGHT);

        VBox content = new VBox(14.0,
            headerRow,
            new VBox(6.0, lblNama, txtNama),
            new VBox(6.0, lblDeskripsi, txtDeskripsi),
            new VBox(6.0, lblHarga, txtHarga),
            new VBox(6.0, lblStatus, cmbStatus),
            footerRow
        );
        content.getStyleClass().add("modal-card");
        content.setPrefWidth(400.0);
        pane.setContent(content);

        return dialog;
    }

    /**
     * Dialog konfirmasi bergaya kartu putih custom yang sama dengan modal
     * Tambah/Edit Menu (bukan Alert bawaan JavaFX yang tampilannya "default").
     * Dipakai untuk konfirmasi Hapus Menu (dan bisa dipakai ulang untuk
     * konfirmasi lain yang butuh tombol Batal + tombol aksi).
     *
     * @param bahaya true supaya tombol aksi tampil merah (mis. untuk Hapus),
     *               false untuk tampil oranye seperti tombol Simpan biasa.
     */
    private Dialog<ButtonType> buatDialogKonfirmasi(String judul, String pesan, String labelAksi, boolean bahaya) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setTitle(judul);

        ButtonType aksiButtonType = new ButtonType(labelAksi, ButtonBar.ButtonData.OK_DONE);
        DialogPane pane = dialog.getDialogPane();
        pane.getButtonTypes().addAll(aksiButtonType, ButtonType.CANCEL);
        pane.getStyleClass().add("dialog-pane-menu");
        pane.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());

        Label lblJudul = new Label(judul);
        lblJudul.getStyleClass().add("modal-title");
        lblJudul.setWrapText(true);
        Button btnClose = new Button("\u00D7");
        btnClose.getStyleClass().add("modal-close-btn");
        btnClose.setOnAction(e -> { dialog.setResult(ButtonType.CANCEL); dialog.close(); });
        Region spacerHeader = new Region();
        HBox.setHgrow(spacerHeader, Priority.ALWAYS);
        HBox headerRow = new HBox(lblJudul, spacerHeader, btnClose);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        Label lblPesan = new Label(pesan);
        lblPesan.getStyleClass().add("modal-message");
        lblPesan.setWrapText(true);

        Button btnBatal = new Button("Batal");
        btnBatal.getStyleClass().add("btn-batal-modal");
        btnBatal.setOnAction(e -> { dialog.setResult(ButtonType.CANCEL); dialog.close(); });

        Button btnAksi = new Button(labelAksi);
        btnAksi.getStyleClass().add(bahaya ? "btn-hapus-modal" : "btn-simpan-modal");
        btnAksi.setDefaultButton(!bahaya);
        btnAksi.setOnAction(e -> { dialog.setResult(aksiButtonType); dialog.close(); });

        HBox footerRow = new HBox(10.0, btnBatal, btnAksi);
        footerRow.setAlignment(Pos.CENTER_RIGHT);

        VBox content = new VBox(16.0, headerRow, lblPesan, footerRow);
        content.getStyleClass().add("modal-card");
        content.setPrefWidth(400.0);
        pane.setContent(content);

        return dialog;
    }

    @FXML
    void handleTambahMenu() {
        TextField txtNama = new TextField();
        TextArea txtDeskripsi = new TextArea();
        TextField txtHarga = new TextField();
        ComboBox<String> cmbStatus = new ComboBox<>(FXCollections.observableArrayList("aktif", "Tidak Aktif"));
        cmbStatus.setValue("aktif");

        Dialog<ButtonType> dialog = buatDialogFormMenu("Tambah Menu Baru", "Simpan", txtNama, txtDeskripsi, txtHarga, cmbStatus);
        ButtonType simpanButtonType = dialog.getDialogPane().getButtonTypes().get(0);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == simpanButtonType) {
            String nama = txtNama.getText().trim();
            String deskripsi = txtDeskripsi.getText().trim();
            if (nama.isEmpty() || deskripsi.isEmpty()) {
                tampilkanAlert(Alert.AlertType.ERROR, "Input tidak valid", "Nama menu dan deskripsi tidak boleh kosong.");
                return;
            }
            double harga;
            try {
                harga = Double.parseDouble(txtHarga.getText().trim());
            } catch (NumberFormatException e) {
                tampilkanAlert(Alert.AlertType.ERROR, "Harga tidak valid", "Masukkan nominal harga berupa angka saja.");
                return;
            }
            String randomId = "MNU-" + (int) (Math.random() * 900 + 100);
            simpanKeXML(randomId, nama, deskripsi, harga, cmbStatus.getValue());
            loadMenuDariXML();
        }
    }

    private void simpanKeXML(String id, String nama, String deskripsi, double harga, String status) {
        try {
            File file = new File(xmlPath);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            Element root = doc.getDocumentElement();

            Element newMenu = doc.createElement("menu");
            newMenu.setAttribute("id", id);

            Element nNode = doc.createElement("nama"); nNode.setTextContent(nama); newMenu.appendChild(nNode);
            Element dNode = doc.createElement("deskripsi"); dNode.setTextContent(deskripsi); newMenu.appendChild(dNode);
            Element hNode = doc.createElement("harga"); hNode.setTextContent(String.valueOf(harga)); newMenu.appendChild(hNode);
            Element sNode = doc.createElement("status"); sNode.setTextContent(status); newMenu.appendChild(sNode);

            root.appendChild(newMenu);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(file));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void hapusMenu(Menu selected) {
        if (selected == null) {
            tampilkanAlert(Alert.AlertType.WARNING, "Tidak ada menu yang dipilih", "Silakan pilih menu dari tabel terlebih dahulu untuk dihapus.");
            return;
        }

        Dialog<ButtonType> dialog = buatDialogKonfirmasi(
            "Hapus Menu: " + selected.getNamaMenu() + "?",
            "Tindakan ini tidak dapat dibatalkan. Apakah Anda yakin ingin menghapus menu ini?",
            "Hapus",
            true
        );
        ButtonType hapusButtonType = dialog.getDialogPane().getButtonTypes().get(0);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == hapusButtonType) {
            try {
                File file = new File(xmlPath);
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
                NodeList nList = doc.getElementsByTagName("menu");
                for (int i = 0; i < nList.getLength(); i++) {
                    Element el = (Element) nList.item(i);
                    if (el.getAttribute("id").equals(selected.getId())) {
                        el.getParentNode().removeChild(el);
                        break;
                    }
                }
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.transform(new DOMSource(doc), new StreamResult(file));
                loadMenuDariXML();
                tampilkanAlert(Alert.AlertType.INFORMATION, null, "Menu berhasil dihapus.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void bukaDialogEditMenu(Menu selected) {
        if (selected == null) {
            tampilkanAlert(Alert.AlertType.WARNING, "Tidak ada menu yang dipilih", "Silakan pilih menu dari tabel terlebih dahulu untuk diedit.");
            return;
        }

        TextField txtNama = new TextField(selected.getNamaMenu());
        TextArea txtDeskripsi = new TextArea(selected.getDeskripsi());
        TextField txtHarga = new TextField(String.valueOf((int) selected.getHarga()));
        ComboBox<String> cmbStatus = new ComboBox<>(FXCollections.observableArrayList("aktif", "Tidak Aktif"));
        cmbStatus.setValue(selected.getStatus());

        Dialog<ButtonType> dialog = buatDialogFormMenu("Edit Menu", "Update", txtNama, txtDeskripsi, txtHarga, cmbStatus);
        ButtonType updateButtonType = dialog.getDialogPane().getButtonTypes().get(0);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == updateButtonType) {
            String nama = txtNama.getText().trim();
            String deskripsi = txtDeskripsi.getText().trim();
            if (nama.isEmpty() || deskripsi.isEmpty()) {
                tampilkanAlert(Alert.AlertType.ERROR, "Input tidak valid", "Nama menu dan deskripsi tidak boleh kosong.");
                return;
            }
            double harga;
            try {
                harga = Double.parseDouble(txtHarga.getText().trim());
            } catch (NumberFormatException e) {
                tampilkanAlert(Alert.AlertType.ERROR, "Harga tidak valid", "Masukkan nominal harga berupa angka saja.");
                return;
            }
            updateDiXML(selected.getId(), nama, deskripsi, harga, cmbStatus.getValue());
            loadMenuDariXML();
        }
    }

    private void updateDiXML(String id, String nama, String deskripsi, double harga, String status) {
        try {
            File file = new File(xmlPath);
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            NodeList nList = doc.getElementsByTagName("menu");
            for (int i = 0; i < nList.getLength(); i++) {
                Element el = (Element) nList.item(i);
                if (el.getAttribute("id").equals(id)) {
                    el.getElementsByTagName("nama").item(0).setTextContent(nama);
                    el.getElementsByTagName("deskripsi").item(0).setTextContent(deskripsi);
                    el.getElementsByTagName("harga").item(0).setTextContent(String.valueOf(harga));
                    el.getElementsByTagName("status").item(0).setTextContent(status);
                    break;
                }
            }
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tampilkanAlert(Alert.AlertType tipe, String judul, String pesan) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initStyle(StageStyle.UNDECORATED);

        ButtonType okButtonType = new ButtonType("Oke", ButtonBar.ButtonData.OK_DONE);
        DialogPane pane = dialog.getDialogPane();
        pane.getButtonTypes().add(okButtonType);
        pane.getStyleClass().add("dialog-pane-menu");
        pane.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());

        String ikon;
        String kelasIkon;
        String judulDefault;
        switch (tipe) {
            case ERROR:
                ikon = "\u2715"; kelasIkon = "alert-icon-error"; judulDefault = "Terjadi Kesalahan"; break;
            case WARNING:
                ikon = "\u26A0"; kelasIkon = "alert-icon-warning"; judulDefault = "Perhatian"; break;
            default:
                ikon = "\u2713"; kelasIkon = "alert-icon-info"; judulDefault = "Informasi"; break;
        }

        Label lblIkon = new Label(ikon);
        lblIkon.getStyleClass().addAll("modal-alert-icon", kelasIkon);

        Label lblJudul = new Label(judul == null ? judulDefault : judul);
        lblJudul.getStyleClass().add("modal-title");
        lblJudul.setWrapText(true);

        Label lblPesan = new Label(pesan);
        lblPesan.getStyleClass().add("modal-message");
        lblPesan.setWrapText(true);

        VBox textBox = new VBox(4.0, lblJudul, lblPesan);
        HBox bodyRow = new HBox(14.0, lblIkon, textBox);
        bodyRow.setAlignment(Pos.TOP_LEFT);

        Button btnOk = new Button("Oke");
        btnOk.getStyleClass().add("btn-simpan-modal");
        btnOk.setDefaultButton(true);
        btnOk.setOnAction(e -> { dialog.setResult(okButtonType); dialog.close(); });

        HBox footerRow = new HBox(btnOk);
        footerRow.setAlignment(Pos.CENTER_RIGHT);

        VBox content = new VBox(18.0, bodyRow, footerRow);
        content.getStyleClass().add("modal-card");
        content.setPrefWidth(370.0);
        pane.setContent(content);

        dialog.showAndWait();
    }
}
