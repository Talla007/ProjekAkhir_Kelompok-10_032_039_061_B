package com.mykendaraan.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID; // Untuk generate ID unik

public class Penjualan {
    private String id;
    private String tanggal;
    private String pelangganId;
    private double totalDp;
    private String metodePembayaran;
    private String status;
    private String catatan;
    private List<PenjualanItem> items;

    public Penjualan() {
        // Generate ID otomatis saat object dibuat
        this.id = "TRX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.items = new ArrayList<>();
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }

    public String getPelangganId() { return pelangganId; }
    public void setPelangganId(String pelangganId) { this.pelangganId = pelangganId; }

    public double getTotalDp() { return totalDp; }
    public void setTotalDp(double totalDp) { this.totalDp = totalDp; }

    public String getMetodePembayaran() { return metodePembayaran; }
    public void setMetodePembayaran(String metodePembayaran) { this.metodePembayaran = metodePembayaran; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }

    public List<PenjualanItem> getItems() { return items; }
    public void setItems(List<PenjualanItem> items) { this.items = items; }
    public void addItem(PenjualanItem sparepartItem) {
    }
}