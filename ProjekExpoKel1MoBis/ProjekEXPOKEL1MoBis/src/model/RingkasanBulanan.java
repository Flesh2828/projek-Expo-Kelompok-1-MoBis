package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model untuk satu baris di tabel Ringkasan Bulanan pada halaman Laporan Keuangan Owner.
 */
public class RingkasanBulanan {

    private final StringProperty bulan;
    private final StringProperty jumlahPesanan;
    private final StringProperty pendapatan;
    private final StringProperty pengeluaran;
    private final StringProperty labaBersih;
    private final StringProperty margin;

    public RingkasanBulanan(String bulan, String jumlahPesanan, String pendapatan,
                             String pengeluaran, String labaBersih,
                             String margin) {
        this.bulan         = new SimpleStringProperty(bulan);
        this.jumlahPesanan = new SimpleStringProperty(jumlahPesanan);
        this.pendapatan    = new SimpleStringProperty(pendapatan);
        this.pengeluaran   = new SimpleStringProperty(pengeluaran);
        this.labaBersih    = new SimpleStringProperty(labaBersih);
        this.margin        = new SimpleStringProperty(margin);
    }

    // --- Property getters (dibutuhkan PropertyValueFactory) ---
    public StringProperty bulanProperty()         { return bulan; }
    public StringProperty jumlahPesananProperty() { return jumlahPesanan; }
    public StringProperty pendapatanProperty()    { return pendapatan; }
    public StringProperty pengeluaranProperty()   { return pengeluaran; }
    public StringProperty labaBersihProperty()    { return labaBersih; }
    public StringProperty marginProperty()        { return margin; }

    // --- Plain getters ---
    public String getBulan()         { return bulan.get(); }
    public String getJumlahPesanan() { return jumlahPesanan.get(); }
    public String getPendapatan()    { return pendapatan.get(); }
    public String getPengeluaran()   { return pengeluaran.get(); }
    public String getLabaBersih()    { return labaBersih.get(); }
    public String getMargin()        { return margin.get(); }
}
