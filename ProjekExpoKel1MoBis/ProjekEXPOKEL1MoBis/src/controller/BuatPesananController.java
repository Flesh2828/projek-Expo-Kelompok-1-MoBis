package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Pesanan;

public class BuatPesananController {

    @FXML private TextField txtPilihanMenu;
    @FXML private TextField txtJumlahPorsi;
    @FXML private TextField txtTanggalKirim;
    @FXML private TextArea txtAlamatKirim;
    @FXML private TextField txtCatatan;
    @FXML private Label lblStatusOrder;

    // Method khusus untuk menerima lemparan nama menu dari halaman depan
    public void setMenuPilihan(String namaMenu) {
        txtPilihanMenu.setText(namaMenu);
    }

    @FXML
    public void handleKirimPesanan() {
        String menu = txtPilihanMenu.getText();
        String porsiStr = txtJumlahPorsi.getText();
        String tanggal = txtTanggalKirim.getText();
        String alamat = txtAlamatKirim.getText();
        String catatan = txtCatatan.getText();

        if (menu.isEmpty() || porsiStr.isEmpty() || tanggal.isEmpty() || alamat.isEmpty()) {
            lblStatusOrder.setStyle("-fx-text-fill: red;");
            lblStatusOrder.setText("Semua kolom wajib diisi!");
            return;
        }

        try {
            int porsi = Integer.parseInt(porsiStr);
            String idPesanan = "ORD-0" + (int)(Math.random() * 100); // Generate ID acak sederhana
            
            // Hitung harga dasar acak simulasi
            double totalHarga = porsi * 25000; 

            // Buat objek pesanan baru (Default Status: Dikonfirmasi, Belum Lunas)
            Pesanan baru = new Pesanan(idPesanan, "Pelanggan Aktif", menu, porsi, tanggal, alamat, catatan, "Dikonfirmasi", "Belum Lunas", totalHarga);
            
            boolean sukses = Pesanan.simpanPesananBaru(baru);
            if (sukses) {
                lblStatusOrder.setStyle("-fx-text-fill: green;");
                lblStatusOrder.setText("Pesanan Berhasil Dibuat! ID: " + idPesanan);
            } else {
                lblStatusOrder.setStyle("-fx-text-fill: red;");
                lblStatusOrder.setText("Gagal menyimpan ke database XML!");
            }
        } catch (NumberFormatException e) {
            lblStatusOrder.setText("Jumlah porsi harus berupa angka!");
        }
    }
}