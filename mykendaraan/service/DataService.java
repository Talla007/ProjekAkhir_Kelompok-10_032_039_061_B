package com.mykendaraan.service;

import com.mykendaraan.database.DatabaseConnection;
import com.mykendaraan.model.Kendaraan;
import com.mykendaraan.model.Pelanggan;
import com.mykendaraan.model.Sparepart;
import com.mykendaraan.model.Penjualan;
import com.mykendaraan.model.PenjualanItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataService {
    private static DataService instance;
    private List<Penjualan> penjualanList = new ArrayList<>(); // Simpanan sementara Penjualan

    public static DataService getInstance() {
        if (instance == null) instance = new DataService();
        return instance;
    }

    // ==========================================
    // BAGIAN 1: KENDARAAN (FIXED TIPE DATA)
    // ==========================================
    public List<Kendaraan> getAllKendaraan() {
        List<Kendaraan> list = new ArrayList<>();
        String query = "SELECT * FROM kendaraan";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Kendaraan(
                        rs.getString("id"),
                        rs.getString("merk"),
                        rs.getString("model"),
                        rs.getInt("tahun"),         // FIX: Pakai getInt
                        rs.getString("warna"),
                        rs.getString("jenis"),
                        rs.getDouble("harga"),      // FIX: Pakai getDouble
                        rs.getInt("stok"),          // FIX: Pakai getInt
                        rs.getString("deskripsi")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Kendaraan> searchKendaraan(String keyword) {
        List<Kendaraan> list = new ArrayList<>();
        String query = "SELECT * FROM kendaraan WHERE merk LIKE ? OR model LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Kendaraan(
                            rs.getString("id"), rs.getString("merk"), rs.getString("model"),
                            rs.getInt("tahun"), rs.getString("warna"), rs.getString("jenis"),
                            rs.getDouble("harga"), rs.getInt("stok"), rs.getString("deskripsi")
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addKendaraan(Kendaraan k) {
        String query = "INSERT INTO kendaraan (merk, model, tahun, warna, jenis, harga, stok, deskripsi) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, k.getMerk());
            ps.setString(2, k.getModel());
            ps.setInt(3, k.getTahun());
            ps.setString(4, k.getWarna());
            ps.setString(5, k.getJenis());
            ps.setDouble(6, k.getHarga());
            ps.setInt(7, k.getStok());
            ps.setString(8, k.getDeskripsi());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateKendaraan(Kendaraan k) {
        String query = "UPDATE kendaraan SET merk=?, model=?, tahun=?, warna=?, jenis=?, harga=?, stok=?, deskripsi=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, k.getMerk());
            ps.setString(2, k.getModel());
            ps.setInt(3, k.getTahun());
            ps.setString(4, k.getWarna());
            ps.setString(5, k.getJenis());
            ps.setDouble(6, k.getHarga());
            ps.setInt(7, k.getStok());
            ps.setString(8, k.getDeskripsi());
            ps.setString(9, k.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deleteKendaraan(String id) {
        String query = "DELETE FROM kendaraan WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public Kendaraan getKendaraanById(String id) {
        String query = "SELECT * FROM kendaraan WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Kendaraan(
                            rs.getString("id"), rs.getString("merk"), rs.getString("model"),
                            rs.getInt("tahun"), rs.getString("warna"), rs.getString("jenis"),
                            rs.getDouble("harga"), rs.getInt("stok"), rs.getString("deskripsi")
                    );
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ==========================================
    // BAGIAN 2: PELANGGAN (DITAMBAHKAN KEMBALI)
    // ==========================================
    public List<Pelanggan> getAllPelanggan() {
        List<Pelanggan> list = new ArrayList<>();
        String query = "SELECT * FROM pelanggan";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Pelanggan(
                        rs.getString("id"), rs.getString("nama"), rs.getString("telepon"),
                        rs.getString("email"), rs.getString("alamat")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Pelanggan> searchPelanggan(String keyword) {
        List<Pelanggan> list = new ArrayList<>();
        String query = "SELECT * FROM pelanggan WHERE nama LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Pelanggan(
                            rs.getString("id"), rs.getString("nama"), rs.getString("telepon"),
                            rs.getString("email"), rs.getString("alamat")
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addPelanggan(Pelanggan p) {
        String query = "INSERT INTO pelanggan (nama, telepon, email, alamat) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, p.getNama());
            ps.setString(2, p.getTelepon());
            ps.setString(3, p.getEmail());
            ps.setString(4, p.getAlamat());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updatePelanggan(String id, Pelanggan p) {
        String query = "UPDATE pelanggan SET nama=?, telepon=?, email=?, alamat=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, p.getNama());
            ps.setString(2, p.getTelepon());
            ps.setString(3, p.getEmail());
            ps.setString(4, p.getAlamat());
            ps.setString(5, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deletePelanggan(String id) {
        String query = "DELETE FROM pelanggan WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public Pelanggan getPelangganById(String id) {
        String query = "SELECT * FROM pelanggan WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Pelanggan(
                            rs.getString("id"), rs.getString("nama"), rs.getString("telepon"),
                            rs.getString("email"), rs.getString("alamat")
                    );
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ==========================================
    // BAGIAN 3: SPAREPART (DITAMBAHKAN KEMBALI)
    // ==========================================
    public List<Sparepart> getAllSparepart() {
        List<Sparepart> list = new ArrayList<>();
        String query = "SELECT * FROM sparepart";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Sparepart(
                        rs.getString("id"), rs.getString("nama"), rs.getString("kategori"),
                        rs.getString("merk"), rs.getString("tipe_kendaraan"),
                        rs.getDouble("harga"), rs.getInt("stok"), rs.getInt("stok_minimal")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addSparepart(Sparepart s) {
        String query = "INSERT INTO sparepart (nama, kategori, merk, tipe_kendaraan, harga, stok, stok_minimal) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, s.getNama());
            ps.setString(2, s.getKategori());
            ps.setString(3, s.getMerk());
            ps.setString(4, s.getTipeKendaraan());
            ps.setDouble(5, s.getHarga());
            ps.setInt(6, s.getStok());
            ps.setInt(7, s.getStokMinimal());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // Perbaikan method updateSparepart yang error di gambar kamu
    public boolean updateSparepart(Sparepart s) {
        String query = "UPDATE sparepart SET nama=?, kategori=?, merk=?, tipe_kendaraan=?, harga=?, stok=?, stok_minimal=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, s.getNama());
            ps.setString(2, s.getKategori());
            ps.setString(3, s.getMerk());
            ps.setString(4, s.getTipeKendaraan());
            ps.setDouble(5, s.getHarga());
            ps.setInt(6, s.getStok());
            ps.setInt(7, s.getStokMinimal());
            ps.setString(8, s.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // Support jika di dialog ada yang memanggil dengan 2 parameter
    public boolean updateSparepart(String id, Sparepart s) {
        s.getId();
        return updateSparepart(s);
    }

    public List<Sparepart> searchSparepart(String keyword) {
        List<Sparepart> list = new ArrayList<>();
        String query = "SELECT * FROM sparepart WHERE nama LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Sparepart(
                            rs.getString("id"), rs.getString("nama"), rs.getString("kategori"),
                            rs.getString("merk"), rs.getString("tipe_kendaraan"),
                            rs.getDouble("harga"), rs.getInt("stok"), rs.getInt("stok_minimal")
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean deleteSparepart(String id) {
        String query = "DELETE FROM sparepart WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public Sparepart getSparepartById(String id) {
        String query = "SELECT * FROM sparepart WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Sparepart(
                            rs.getString("id"), rs.getString("nama"), rs.getString("kategori"),
                            rs.getString("merk"), rs.getString("tipe_kendaraan"),
                            rs.getDouble("harga"), rs.getInt("stok"), rs.getInt("stok_minimal")
                    );
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ==========================================
    // BAGIAN 4: PENJUALAN
    // ==========================================
    public List<Penjualan> getAllPenjualan() {
        return penjualanList;
    }

    public boolean addPenjualan(Penjualan p) {
        penjualanList.add(p);
        for (PenjualanItem item : p.getItems()) {
            if ("kendaraan".equals(item.getTipe())) {
                Kendaraan k = getKendaraanById(item.getItemId());
                if (k != null) {
                    k.setStok(k.getStok() - 1);
                    updateKendaraan(k);
                }
            }
        }
        return true;
    }

    public String getNextSparepartId() {
        return "";
    }
}