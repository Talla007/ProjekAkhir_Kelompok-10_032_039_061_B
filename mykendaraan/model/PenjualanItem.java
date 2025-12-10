package com.mykendaraan.model;

public class PenjualanItem {
    private String tipe; // "kendaraan" atau "sparepart"
    private String itemId;
    private int jumlah;
    private double hargaSatuan;
    private double subtotal;

    public PenjualanItem() {}

    // Getters & Setters
    public String getTipe() { return tipe; }
    public void setTipe(String tipe) { this.tipe = tipe; }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public int getJumlah() { return jumlah; }
    public void setJumlah(int jumlah) { this.jumlah = jumlah; }

    public double getHargaSatuan() { return hargaSatuan; }
    public void setHargaSatuan(double hargaSatuan) { this.hargaSatuan = hargaSatuan; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}