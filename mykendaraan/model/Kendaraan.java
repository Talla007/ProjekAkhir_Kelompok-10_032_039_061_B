package com.mykendaraan.model;

public class Kendaraan {
    private String id;
    private String merk;
    private String model;
    private int tahun;          // Tipe int
    private String warna;
    private String jenis;       // mobil/motor
    private double harga;       // Tipe double
    private int stok;           // Tipe int
    private String deskripsi;

    public Kendaraan() {}

    public Kendaraan(String id, String merk, String model, int tahun, String warna, String jenis, double harga, int stok, String deskripsi) {
        this.id = id;
        this.merk = merk;
        this.model = model;
        this.tahun = tahun;
        this.warna = warna;
        this.jenis = jenis;
        this.harga = harga;
        this.stok = stok;
        this.deskripsi = deskripsi;
    }

    // Getters
    public String getId() { return id; }
    public String getMerk() { return merk; }
    public String getModel() { return model; }
    public int getTahun() { return tahun; }
    public String getWarna() { return warna; }
    public String getJenis() { return jenis; }
    public double getHarga() { return harga; }
    public int getStok() { return stok; }
    public String getDeskripsi() { return deskripsi; }

    // SETTERS (INI WAJIB ADA UNTUK MENGHILANGKAN ERROR DI DIALOG)
    public void setId(String id) { this.id = id; }
    public void setMerk(String merk) { this.merk = merk; }
    public void setModel(String model) { this.model = model; }
    public void setTahun(int tahun) { this.tahun = tahun; }
    public void setWarna(String warna) { this.warna = warna; }
    public void setJenis(String jenis) { this.jenis = jenis; }
    public void setHarga(double harga) { this.harga = harga; }
    public void setStok(int stok) { this.stok = stok; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public Object[] toTableRow() {
        return new Object[]{id, merk, model, tahun, warna, jenis, String.format("Rp %,.0f", harga), stok, deskripsi};
    }
}