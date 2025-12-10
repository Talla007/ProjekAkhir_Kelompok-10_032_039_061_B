package com.mykendaraan.model;

public class Sparepart {
    private String id;
    private String nama;
    private String kategori;
    private String merk;
    private String tipeKendaraan;
    private double harga;
    private int stok;
    private int stokMinimal;

    public Sparepart(String id, String nama, String kategori, String merk,
                     String tipeKendaraan, double harga, int stok, int stokMinimal) {
        this.id = id;
        this.nama = nama;
        this.kategori = kategori;
        this.merk = merk;
        this.tipeKendaraan = tipeKendaraan;
        this.harga = harga;
        this.stok = stok;
        this.stokMinimal = stokMinimal;
    }

    // Getters
    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getKategori() { return kategori; }
    public String getMerk() { return merk; }
    public String getTipeKendaraan() { return tipeKendaraan; }
    public double getHarga() { return harga; }
    public int getStok() { return stok; }
    public int getStokMinimal() { return stokMinimal; }

    // Setters
    public void setHarga(double harga) { this.harga = harga; }
    public void setStok(int stok) { this.stok = stok; }
    public void setStokMinimal(int stokMinimal) { this.stokMinimal = stokMinimal; }

    public String getHargaFormatted() {
        return String.format("Rp %,d", (long) harga).replace(",", ".");
    }

    public Object[] toTableRow() {
        return new Object[]{id, nama, kategori, merk, tipeKendaraan, getHargaFormatted(), stok, stokMinimal};
    }
}