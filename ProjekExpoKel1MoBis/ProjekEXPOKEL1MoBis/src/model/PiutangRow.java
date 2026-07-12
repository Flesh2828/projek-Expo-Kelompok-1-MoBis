package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model untuk satu baris di tabel Piutang Owner.
 * Mewakili satu pesanan yang statusPembayaran == "Belum Lunas".
 */
public class PiutangRow {

    private final StringProperty pelanggan;
    private final StringProperty idPesanan;
    private final StringProperty totalTagihan;
    private final StringProperty dpDibayar;
    private final StringProperty sisaTagihan;
    private final StringProperty jatuhTempo;
    private final StringProperty status;
    private final String kategori; // "MERAH" | "KUNING" | "HIJAU" — dipakai untuk styling tabel saja

    public PiutangRow(String pelanggan, String idPesanan,
                      String totalTagihan, String dpDibayar,
                      String sisaTagihan, String jatuhTempo,
                      String status, String kategori) {
        this.pelanggan     = new SimpleStringProperty(pelanggan);
        this.idPesanan     = new SimpleStringProperty(idPesanan);
        this.totalTagihan  = new SimpleStringProperty(totalTagihan);
        this.dpDibayar     = new SimpleStringProperty(dpDibayar);
        this.sisaTagihan   = new SimpleStringProperty(sisaTagihan);
        this.jatuhTempo    = new SimpleStringProperty(jatuhTempo);
        this.status        = new SimpleStringProperty(status);
        this.kategori      = kategori;
    }

    // --- Property getters (dibutuhkan PropertyValueFactory) ---
    public StringProperty pelangganProperty()    { return pelanggan; }
    public StringProperty idPesananProperty()    { return idPesanan; }
    public StringProperty totalTagihanProperty() { return totalTagihan; }
    public StringProperty dpDibayarProperty()    { return dpDibayar; }
    public StringProperty sisaTagihanProperty()  { return sisaTagihan; }
    public StringProperty jatuhTempoProperty()   { return jatuhTempo; }
    public StringProperty statusProperty()       { return status; }

    // --- Plain getters ---
    public String getPelanggan()    { return pelanggan.get(); }
    public String getIdPesanan()    { return idPesanan.get(); }
    public String getTotalTagihan() { return totalTagihan.get(); }
    public String getDpDibayar()    { return dpDibayar.get(); }
    public String getSisaTagihan()  { return sisaTagihan.get(); }
    public String getJatuhTempo()   { return jatuhTempo.get(); }
    public String getStatus()       { return status.get(); }
    public String getKategori()     { return kategori; }
}
