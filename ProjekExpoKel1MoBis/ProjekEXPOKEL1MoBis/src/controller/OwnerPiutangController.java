package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public class OwnerPiutangController {

    @FXML private TableView<?> tablePiutang;

    @FXML
    public void initialize() {
        // Logika pengisian data tabel piutang (PT Maju Jaya, Dinas Pendidikan, dll.) 
        System.out.println("Halaman Piutang Pelanggan diinisialisasi.");
    }
}