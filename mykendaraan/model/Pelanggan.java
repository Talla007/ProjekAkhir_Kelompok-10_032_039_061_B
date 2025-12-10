package com.mykendaraan.model;

public class Pelanggan {
    private String id;
    private String nama;
    private String alamat;
    private String telepon;
    private String email;

    public Pelanggan(String id, String nama, String alamat, String telepon, String email) {
        this.id = id;
        this.nama = nama;
        this.alamat = alamat;
        this.telepon = telepon;
        this.email = email;
    }

    public String getId() { return id; }
    public String getNama() { return nama; }
    public String getAlamat() { return alamat; }
    public String getTelepon() { return telepon; }
    public String getEmail() { return email; }

    public Object[] toTableRow() {
        return new Object[]{id, nama, telepon, email, alamat};
    }
}