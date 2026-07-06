package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Pesanan;

public class BuatPesananController {

    @FXML
    private TextField txtPilihanMenu;
    @FXML
    private TextField txtJumlahPorsi;
    @FXML
    private TextField txtTanggalKirim;
    @FXML
    private TextArea txtAlamatKirim;
    @FXML
    private TextField txtCatatan;
    @FXML
    private Label lblStatusOrder;

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
            String idPesanan = "ORD-0" + (int) (Math.random() * 100);
            double totalHarga = porsi * 25000;

            Pesanan baru = new Pesanan(idPesanan, "Pelanggan Aktif", menu, porsi, tanggal, alamat, catatan, "Dikonfirmasi", "Belum Lunas", totalHarga);
            boolean sukses = Pesanan.simpanPesananBaru(baru);

            if (sukses) {
                lblStatusOrder.setStyle("-fx-text-fill: green;");
                lblStatusOrder.setText("✅ Pesanan Berhasil Dibuat! ID: " + idPesanan);

                new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            javafx.application.Platform.runLater(() -> {
                                txtPilihanMenu.clear();
                                txtJumlahPorsi.setText("1");
                                txtTanggalKirim.clear();
                                txtAlamatKirim.clear();
                                txtCatatan.clear();
                                lblStatusOrder.setText("");
                            });
                        }
                    },
                    3000
                );

            } else {
                lblStatusOrder.setStyle("-fx-text-fill: red;");
                lblStatusOrder.setText("Gagal menyimpan ke database XML!");
            }
        } catch (NumberFormatException e) {
            lblStatusOrder.setText("Jumlah porsi harus berupa angka!");
        }
    }

    // ===== DIPANGGIL DARI DAFTAR MENU (HARGA INT) =====
    public void setMenuTerpilih(String menu, int harga) {
        txtPilihanMenu.setText(menu);
        System.out.println("Berhasil menerima lembaran data dari Daftar Menu!");
        System.out.println("Menu: " + menu + " | Harga Dasar: Rp " + harga);
    }

    // ===== DIPANGGIL DARI RIWAYAT PESANAN (PESANAN LAMA) =====
    public void setDataPesanLama(Pesanan pesananLama) {
        if (pesananLama != null) {
            txtPilihanMenu.setText(pesananLama.getNamaMenu());
            txtJumlahPorsi.setText(String.valueOf(pesananLama.getJumlahPorsi()));
            txtTanggalKirim.setText(pesananLama.getTanggalPengiriman());
            txtAlamatKirim.setText(pesananLama.getAlamatPengiriman());
            txtCatatan.setText(pesananLama.getCatatanKhusus());

            lblStatusOrder.setStyle("-fx-text-fill: #2E7D32;");
            lblStatusOrder.setText("✏️ Data pesanan lama telah diisi. Silakan edit jika perlu.");
        }
    }

    // ===== DIPANGGIL DARI DAFTAR MENU (HARGA DOUBLE) =====
    public void setMenuTerpilih(String namaMenu, double harga) {
        txtPilihanMenu.setText(namaMenu);
        lblStatusOrder.setStyle("-fx-text-fill: #2E7D32;");
        lblStatusOrder.setText("📝 Menu dipilih: " + namaMenu + " (Rp " + String.format("%,.0f", harga) + "/porsi)");
    }
}