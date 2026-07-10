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
    private final String xmlPath = Menu.getFilePath();

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

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == simpanButtonType) {
            String randomId = "MNU-" + (int)(Math.random() * 900 + 100);
            simpanKeXML(randomId, txtNama.getText(), txtDeskripsi.getText(), Double.parseDouble(txtHarga.getText()), cmbStatus.getValue());
            loadMenuDariXML();
            tableMenu.setItems(listMasterMenu);
            tableMenu.refresh();
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

    @FXML
    void handleHapusMenu() {
        Menu selected = tableMenu.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Peringatan");
            alert.setHeaderText("Tidak ada menu yang dipilih");
            alert.setContentText("Silakan pilih menu dari tabel terlebih dahulu untuk dihapus.");
            alert.showAndWait();
            return;
        }

        // Tampilkan dialog konfirmasi penghapusan
        Alert alertConfirm = new Alert(Alert.AlertType.CONFIRMATION);
        alertConfirm.setTitle("Konfirmasi Hapus Menu");
        alertConfirm.setHeaderText("Hapus Menu: " + selected.getNamaMenu() + "?");
        alertConfirm.setContentText("Tindakan ini tidak dapat dibatalkan. Apakah Anda yakin ingin menghapus menu ini?");

        Optional<ButtonType> result = alertConfirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
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

                // Beritahu pengguna bahwa data berhasil dihapus
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Informasi");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Menu berhasil dihapus.");
                successAlert.showAndWait();
            } catch (Exception e) { 
                e.printStackTrace(); 
            }
        }
    }

    @FXML
    void handleEditMenu() {
        Menu selected = tableMenu.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Peringatan");
            alert.setHeaderText("Tidak ada menu yang dipilih");
            alert.setContentText("Silakan pilih menu dari tabel terlebih dahulu untuk diedit.");
            alert.showAndWait();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Menu");
        dialog.setHeaderText("Update Informasi Data Menu SINARING");

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);

        TextField txtNama = new TextField(selected.getNamaMenu());
        TextField txtDeskripsi = new TextField(selected.getDeskripsi());
        TextField txtHarga = new TextField(String.valueOf((int) selected.getHarga()));
        ComboBox<String> cmbStatus = new ComboBox<>(FXCollections.observableArrayList("aktif", "Tidak Aktif"));
        cmbStatus.setValue(selected.getStatus());

        grid.add(new Label("Nama Menu:"), 0, 0); grid.add(txtNama, 1, 0);
        grid.add(new Label("Deskripsi:"), 0, 1); grid.add(txtDeskripsi, 1, 1);
        grid.add(new Label("Harga (Rp):"), 0, 2); grid.add(txtHarga, 1, 2);
        grid.add(new Label("Status:"), 0, 3); grid.add(cmbStatus, 1, 3);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == updateButtonType) {
            try {
                String nama = txtNama.getText().trim();
                String deskripsi = txtDeskripsi.getText().trim();
                double harga = Double.parseDouble(txtHarga.getText().trim());
                String status = cmbStatus.getValue();

                if (nama.isEmpty() || deskripsi.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Input tidak valid");
                    alert.setContentText("Nama menu dan deskripsi tidak boleh kosong.");
                    alert.showAndWait();
                    return;
                }

                updateDiXML(selected.getId(), nama, deskripsi, harga, status);
                loadMenuDariXML();
                tableMenu.refresh();
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Harga tidak valid");
                alert.setContentText("Masukkan nominal harga berupa angka saja.");
                alert.showAndWait();
            }
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
}
