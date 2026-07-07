package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.MenuKatering; // sesuaikan nama model menu kalian
import java.io.File;

public class KelolaMenuController {

    @FXML private TableView<MenuKatering> tblKelolaMenu;
    @FXML private TableColumn<MenuKatering, String> colIdMenu, colNamaMenu, colDeskripsi, colHarga, colStatusMenu;

    private final ObservableList<MenuKatering> daftarMenu = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Sesuaikan parameter di dalam kurung dengan getter yang ada di model Menu kalian
        colIdMenu.setCellValueFactory(new PropertyValueFactory<>("idMenu"));
        colNamaMenu.setCellValueFactory(new PropertyValueFactory<>("namaMenu"));
        colDeskripsi.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
        colHarga.setCellValueFactory(new PropertyValueFactory<>("harga"));
        colStatusMenu.setCellValueFactory(new PropertyValueFactory<>("status"));

        muatMenuDariXML();
        tblKelolaMenu.setItems(daftarMenu);
    }

    private void muatMenuDariXML() {
        // Logika simpan/baca menu katering kalian dari menu.xml mirip dengan pesanan XML
        System.out.println("Memuat data dari menu.xml...");
    }

    @FXML
    void handleTambahMenu() {
        // Aksi ketika tombol "+ Tambah Menu" diklik untuk memunculkan pop-up modal dialog
    }
}