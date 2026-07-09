package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.beans.property.*;
import model.Menu;
import java.io.File;
import java.util.Optional;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import javafx.beans.property.*;

public class KelolaMenuController {
    @FXML private TableView<Menu> tableMenu;
    @FXML private TableColumn<Menu, String> colId, colNama, colDeskripsi, colStatus;
    @FXML private TableColumn<Menu, Double> colHarga;

    private ObservableList<Menu> listMasterMenu = FXCollections.observableArrayList();
    private final String xmlPath = "data/menu.xml";

    @FXML
    public void initialize() {
     colId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        colNama.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNamaMenu()));
        colDeskripsi.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDeskripsi()));
        colHarga.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getHarga()));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));

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
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    void handleTambahMenu() {
        // Pembuatan Pop-up Dialog Isian Data Mandiri Bebas Hambatan FXML Bersarang
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Tambah Menu Baru");
        dialog.setHeaderText("Masukkan Informasi Data Menu SINARING");

        ButtonType simpanButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(simpanButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);

        TextField txtNama = new TextField(); txtNama.setPromptText("Nama Menu");
        TextField txtDeskripsi = new TextField(); txtDeskripsi.setPromptText("Deskripsi Komponen Menu");
        TextField txtHarga = new TextField(); txtHarga.setPromptText("25000");
        ComboBox<String> cmbStatus = new ComboBox<>(FXCollections.observableArrayList("aktif", "Tidak Aktif"));
        cmbStatus.setValue("aktif");

        grid.add(new Label("Nama Menu:"), 0, 0); grid.add(txtNama, 1, 0);
        grid.add(new Label("Deskripsi:"), 0, 1); grid.add(txtDeskripsi, 1, 1);
        grid.add(new Label("Harga (Rp):"), 0, 2); grid.add(txtHarga, 1, 2);
        grid.add(new Label("Status:"), 0, 3); grid.add(cmbStatus, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Tombol Simpan tidak langsung menutup dialog kalau validasi gagal,
        // supaya user bisa memperbaiki isian tanpa kehilangan data yang sudah diketik.
        Button btnSimpan = (Button) dialog.getDialogPane().lookupButton(simpanButtonType);
        btnSimpan.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String pesanError = validasiInputMenu(txtNama.getText(), txtDeskripsi.getText(), txtHarga.getText());
            if (pesanError != null) {
                tampilkanAlert(pesanError, Alert.AlertType.WARNING);
                event.consume(); // batalkan penutupan dialog
            }
        });

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == simpanButtonType) {
            double harga = Double.parseDouble(bersihkanAngka(txtHarga.getText()));
            String id = buatIdBaru();

            boolean sukses = simpanKeXML(id, txtNama.getText().trim(), txtDeskripsi.getText().trim(), harga, cmbStatus.getValue());
            if (sukses) {
                loadMenuDariXML();
                tampilkanAlert("Menu \"" + txtNama.getText().trim() + "\" berhasil ditambahkan.", Alert.AlertType.INFORMATION);
            } else {
                tampilkanAlert("Gagal menyimpan menu baru. Periksa apakah file data/menu.xml dapat diakses.", Alert.AlertType.ERROR);
            }
        }
    }

    /** Validasi isian form. Mengembalikan pesan error, atau null jika valid. */
    private String validasiInputMenu(String nama, String deskripsi, String hargaText) {
        if (nama == null || nama.trim().isEmpty()) return "Nama menu wajib diisi.";
        if (deskripsi == null || deskripsi.trim().isEmpty()) return "Deskripsi menu wajib diisi.";
        if (hargaText == null || hargaText.trim().isEmpty()) return "Harga wajib diisi.";
        try {
            double harga = Double.parseDouble(bersihkanAngka(hargaText));
            if (harga <= 0) return "Harga harus lebih besar dari 0.";
        } catch (NumberFormatException e) {
            return "Harga harus berupa angka, contoh: 25000.";
        }
        return null;
    }

    /** Menghapus karakter non-angka seperti "Rp", spasi, atau titik ribuan agar parseDouble tidak gagal. */
    private String bersihkanAngka(String input) {
        return input.trim().replaceAll("[^0-9.]", "");
    }

    /** Membuat ID baru yang dijamin unik terhadap data menu yang sudah ada. */
    private String buatIdBaru() {
        String id;
        do {
            id = "MNU-" + (int) (Math.random() * 900 + 100);
        } while (idSudahAda(id));
        return id;
    }

    private boolean idSudahAda(String id) {
        for (Menu m : listMasterMenu) {
            if (m.getId().equals(id)) return true;
        }
        return false;
    }

    private boolean simpanKeXML(String id, String nama, String deskripsi, double harga, String status) {
        try {
            File file = new File(xmlPath);
            Document doc;

            if (file.exists()) {
                doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            } else {
                // Kalau file XML belum ada, buat dokumen baru dengan root <menus>
                File parentDir = file.getParentFile();
                if (parentDir != null && !parentDir.exists()) parentDir.mkdirs();
                doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                doc.appendChild(doc.createElement("menus"));
            }

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
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @FXML
    void handleHapusMenu() {
        Menu selected = tableMenu.getSelectionModel().getSelectedItem();
        if (selected == null) {
            tampilkanAlert("Pilih menu yang ingin dihapus terlebih dahulu.", Alert.AlertType.WARNING);
            return;
        }

        Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION);
        konfirmasi.setTitle("Konfirmasi Hapus");
        konfirmasi.setHeaderText(null);
        konfirmasi.setContentText("Yakin ingin menghapus menu \"" + selected.getNamaMenu() + "\"?");
        Optional<ButtonType> hasil = konfirmasi.showAndWait();
        if (hasil.isEmpty() || hasil.get() != ButtonType.OK) return;

        try {
            File file = new File(xmlPath);
            if (!file.exists()) {
                tampilkanAlert("File data/menu.xml tidak ditemukan.", Alert.AlertType.ERROR);
                return;
            }

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
            NodeList nList = doc.getElementsByTagName("menu");
            boolean ditemukan = false;
            for (int i = 0; i < nList.getLength(); i++) {
                Element el = (Element) nList.item(i);
                if (el.getAttribute("id").equals(selected.getId())) {
                    el.getParentNode().removeChild(el);
                    ditemukan = true;
                    break;
                }
            }

            if (!ditemukan) {
                tampilkanAlert("Menu tidak ditemukan di data (mungkin sudah dihapus sebelumnya).", Alert.AlertType.WARNING);
                loadMenuDariXML();
                return;
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(file));
            loadMenuDariXML();
            tampilkanAlert("Menu \"" + selected.getNamaMenu() + "\" berhasil dihapus.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
            tampilkanAlert("Gagal menghapus menu: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void tampilkanAlert(String pesan, Alert.AlertType tipe) {
        Alert alert = new Alert(tipe);
        alert.setTitle("Kelola Menu");
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }
}
