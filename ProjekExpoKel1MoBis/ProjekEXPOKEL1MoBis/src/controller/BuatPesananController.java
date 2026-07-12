package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Menu;
import model.Pesanan;
import java.time.LocalDate;
import java.util.List;

public class BuatPesananController {
    @FXML
    private TextField txtPilihanMenu;
    @FXML
    private TextField txtJumlahPorsi;
    
    // PERBAIKAN: Sesuaikan tipe objek dengan FXML agar tidak bernilai null!
    @FXML
    private DatePicker dpTanggalKirim; 
    @FXML
    private TextArea txtAlamatKirim;
    @FXML
    private TextField txtCatatan;
    @FXML
    private Label lblStatusOrder;

    // PERBAIKAN BUG HARGA: dulu total selalu porsi * 25000 (hardcode),
    // sekarang harga per porsi disimpan sesuai menu yang benar-benar dipilih.
    private double hargaPerPorsi = 0;

    private String usernameSession = "Pelanggan Aktif";
    public void setUsernameSession(String username) { this.usernameSession = username; }

    public void setMenuPilihan(String namaMenu) {
        txtPilihanMenu.setText(namaMenu);
        hargaPerPorsi = cariHargaMenu(namaMenu, hargaPerPorsi);
    }

    @FXML
    public void handleKirimPesanan() {
        String menu = txtPilihanMenu.getText();
        String porsiStr = txtJumlahPorsi.getText();
        
        // PERBAIKAN: Ambil nilai string dari objek DatePicker secara aman
        String tanggal = "";
        if (dpTanggalKirim != null && dpTanggalKirim.getValue() != null) {
            tanggal = dpTanggalKirim.getValue().toString();
        }
        
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
            // PERBAIKAN: pakai harga menu yang sebenarnya, bukan angka tetap 25000
            double totalHarga = porsi * hargaPerPorsi;

            // Membuat objek model pesanan baru sesuai konstruktor kelompok kalian
            Pesanan baru = new Pesanan(idPesanan, usernameSession, menu, porsi, tanggal, alamat, catatan, "Dikonfirmasi", "Belum Bayar", totalHarga);
            
            // Eksekusi penyimpanan data terpusat ke database XML
            boolean sukses = Pesanan.simpanPesananBaru(baru);
            
            if (sukses) {
                lblStatusOrder.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                lblStatusOrder.setText("Pesanan Berhasil Dibuat! ID: " + idPesanan);
                
                // Reset form isian field secara otomatis demi kenyamanan pelanggan
                new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            javafx.application.Platform.runLater(() -> {
                                txtPilihanMenu.clear();
                                txtJumlahPorsi.setText("1");
                                if (dpTanggalKirim != null) dpTanggalKirim.setValue(null);
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
            lblStatusOrder.setStyle("-fx-text-fill: red;");
            lblStatusOrder.setText("Jumlah porsi harus berupa angka!");
        }
    }

    // ===== DIPANGGIL DARI DAFTAR MENU (HARGA INT) =====
    public void setMenuTerpilih(String menu, int harga) {
        txtPilihanMenu.setText(menu);
        hargaPerPorsi = harga;
        System.out.println("Berhasil menerima lembaran data dari Daftar Menu!");
    }

    // ===== DIPANGGIL DARI RIWAYAT PESANAN (PESANAN LAMA) =====
    public void setDataPesanLama(Pesanan pesananLama) {
        if (pesananLama != null) {
            txtPilihanMenu.setText(pesananLama.getNamaMenu());
            txtJumlahPorsi.setText(String.valueOf(pesananLama.getJumlahPorsi()));
            if (dpTanggalKirim != null) {
                try {
                    dpTanggalKirim.setValue(LocalDate.parse(pesananLama.getTanggalPengiriman()));
                } catch(Exception e) {
                    System.out.println("Gagal mem-parsing tanggal lama.");
                }
            }
            txtAlamatKirim.setText(pesananLama.getAlamatPengiriman());
            txtCatatan.setText(pesananLama.getCatatanKhusus());

            // PERBAIKAN: ambil harga per porsi TERKINI dari daftar menu (bisa saja
            // harga menu sudah berubah sejak pesanan lama dibuat). Kalau menunya
            // sudah tidak ada di daftar menu, baru fallback ke harga pesanan lama.
            double fallbackHargaLama = pesananLama.getJumlahPorsi() > 0
                    ? pesananLama.getTotalHarga() / pesananLama.getJumlahPorsi()
                    : 0;
            hargaPerPorsi = cariHargaMenu(pesananLama.getNamaMenu(), fallbackHargaLama);

            lblStatusOrder.setStyle("-fx-text-fill: #2E7D32;");
            lblStatusOrder.setText("  Data pesanan lama telah diisi. Silakan edit jika perlu.");
        }
    }

    // ===== DIPANGGIL DARI DAFTAR MENU (HARGA DOUBLE) =====
    public void setMenuTerpilih(String namaMenu, double harga) {
        txtPilihanMenu.setText(namaMenu);
        hargaPerPorsi = harga;
        lblStatusOrder.setStyle("-fx-text-fill: #2E7D32;");
        lblStatusOrder.setText("  Menu dipilih: " + namaMenu + " (Rp " + String.format("%,.0f", harga) + "/porsi)");
    }

    // ===== HELPER: cari harga menu terkini berdasarkan nama, dengan nilai fallback =====
    private double cariHargaMenu(String namaMenu, double fallback) {
        if (namaMenu == null || namaMenu.isEmpty()) return fallback;
        List<Menu> semuaMenu = Menu.getAllMenu();
        for (Menu m : semuaMenu) {
            if (m.getNamaMenu().equalsIgnoreCase(namaMenu.trim())) {
                return m.getHarga();
            }
        }
        return fallback;
    }
}