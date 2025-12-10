package com.mykendaraan.view;

import com.mykendaraan.model.Kendaraan;
import com.mykendaraan.service.DataService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class KendaraanDialog extends JDialog {
    private JTextField txtModel;
    private JTextField txtTahun;
    private JTextField txtWarna;
    private JTextField txtHarga;
    private JTextField txtStok;
    private JTextArea txtDeskripsi;

    // UBAH: Merk sekarang menggunakan JComboBox, bukan JTextField
    private JComboBox<String> cmbJenis;
    private JComboBox<String> cmbMerk;

    private boolean isSuccess = false;
    private Kendaraan kendaraan; // Jika null = Add, jika ada = Edit
    private DataService dataService;

    // === DATA MERK REAL-WORLD ===
    // Daftar merk Mobil di Indonesia
    private final String[] MERK_MOBIL = {
            "Toyota", "Daihatsu", "Honda", "Mitsubishi", "Suzuki",
            "Hyundai", "Wuling", "Nissan", "Mazda", "BMW", "Mercedes-Benz"
    };

    // Daftar merk Motor di Indonesia
    private final String[] MERK_MOTOR = {
            "Honda", "Yamaha", "Suzuki", "Kawasaki", "Vespa",
            "TVS", "Ducati", "Harley-Davidson", "KTM"
    };

    public KendaraanDialog(JFrame parent, Kendaraan kendaraan) {
        super(parent, kendaraan == null ? "Tambah Kendaraan" : "Edit Kendaraan", true);
        this.kendaraan = kendaraan;
        this.dataService = DataService.getInstance();

        initComponents();

        // Jika mode EDIT, isi form dengan data lama
        if (kendaraan != null) {
            populateForm();
        } else {
            // Jika mode TAMBAH, panggil update sekali untuk isi default (Mobil)
            updateMerkList();
        }

        setSize(450, 600); // Ukuran disesuaikan agar muat
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Jarak antar komponen

        // --- 1. JENIS KENDARAAN ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Jenis:"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        cmbJenis = new JComboBox<>(new String[]{"Mobil", "Motor"});
        formPanel.add(cmbJenis, gbc);

        // --- 2. MERK KENDARAAN (Dynamic) ---
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Merk:"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        cmbMerk = new JComboBox<>(); // Kosong dulu, nanti diisi updateMerkList()
        formPanel.add(cmbMerk, gbc);

        // LOGIC UTAMA: Action Listener untuk mengubah Merk saat Jenis dipilih
        cmbJenis.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateMerkList();
            }
        });

        // --- 3. MODEL ---
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Model/Tipe:"), gbc);

        gbc.gridx = 1;
        txtModel = new JTextField();
        formPanel.add(txtModel, gbc);

        // --- 4. TAHUN ---
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Tahun:"), gbc);

        gbc.gridx = 1;
        txtTahun = new JTextField();
        formPanel.add(txtTahun, gbc);

        // --- 5. WARNA ---
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Warna:"), gbc);

        gbc.gridx = 1;
        txtWarna = new JTextField();
        formPanel.add(txtWarna, gbc);

        // --- 6. HARGA ---
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Harga (Rp):"), gbc);

        gbc.gridx = 1;
        txtHarga = new JTextField();
        formPanel.add(txtHarga, gbc);

        // --- 7. STOK ---
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Stok:"), gbc);

        gbc.gridx = 1;
        txtStok = new JTextField();
        formPanel.add(txtStok, gbc);

        // --- 8. DESKRIPSI ---
        gbc.gridx = 0; gbc.gridy = 7; gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Deskripsi:"), gbc);

        gbc.gridx = 1; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        txtDeskripsi = new JTextArea(3, 20);
        txtDeskripsi.setLineWrap(true);
        JScrollPane scrollDesc = new JScrollPane(txtDeskripsi);
        formPanel.add(scrollDesc, gbc);

        // --- BUTTONS ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Simpan");
        JButton btnCancel = new JButton("Batal");

        btnSave.setBackground(new Color(60, 179, 113));
        btnSave.setForeground(Color.BLACK); // Text hitam agar terbaca
        btnCancel.setBackground(new Color(192, 80, 77));
        btnCancel.setForeground(Color.BLACK);

        btnSave.addActionListener(e -> save());
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // METHOD KHUSUS: Mengganti isi combo box Merk berdasarkan Jenis
    private void updateMerkList() {
        String jenis = (String) cmbJenis.getSelectedItem();

        // Hapus semua item yang ada
        cmbMerk.removeAllItems();

        if ("Mobil".equalsIgnoreCase(jenis)) {
            for (String m : MERK_MOBIL) {
                cmbMerk.addItem(m);
            }
        } else if ("Motor".equalsIgnoreCase(jenis)) {
            for (String m : MERK_MOTOR) {
                cmbMerk.addItem(m);
            }
        }
    }

    private void populateForm() {
        // 1. Set Jenis dulu
        // Data di DB tersimpan lowercase ("mobil"), kita ubah jadi Title Case ("Mobil") untuk GUI
        String jenisDB = kendaraan.getJenis(); // misal "mobil"
        String jenisUI = jenisDB.substring(0, 1).toUpperCase() + jenisDB.substring(1); // Jadi "Mobil"

        cmbJenis.setSelectedItem(jenisUI);

        // 2. Update list Merk sesuai jenis yang baru diset
        updateMerkList();

        // 3. Set Merk terpilih
        cmbMerk.setSelectedItem(kendaraan.getMerk());

        // 4. Set sisanya
        txtModel.setText(kendaraan.getModel());
        txtTahun.setText(String.valueOf(kendaraan.getTahun()));
        txtWarna.setText(kendaraan.getWarna());
        txtHarga.setText(String.valueOf((long)kendaraan.getHarga()));
        txtStok.setText(String.valueOf(kendaraan.getStok()));
        txtDeskripsi.setText(kendaraan.getDeskripsi());
    }

    private void save() {
        // Validasi input
        if (txtModel.getText().isEmpty() || txtHarga.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mohon lengkapi data wajib (Model, Harga)!");
            return;
        }

        try {
            // Ambil data dari form
            String jenis = ((String) cmbJenis.getSelectedItem()).toLowerCase(); // simpan sbg "mobil"/"motor"
            String merk = (String) cmbMerk.getSelectedItem();
            String model = txtModel.getText();
            int tahun = Integer.parseInt(txtTahun.getText());
            String warna = txtWarna.getText();
            double harga = Double.parseDouble(txtHarga.getText());
            int stok = Integer.parseInt(txtStok.getText());
            String deskripsi = txtDeskripsi.getText();

            if (kendaraan == null)
                {Kendaraan newK = new Kendaraan(
                    "0",     // ID (String)
                    merk,    // Merk (String)
                    model,   // Model (String)
                    tahun,   // Tahun (int) <-- Sudah dikonversi
                    warna,   // Warna (String)
                    jenis,   // Jenis (String)
                    harga,   // Harga (double) <-- Sudah dikonversi
                    stok,    // Stok (int) <-- Sudah dikonversi
                    deskripsi// Deskripsi (String)
            );
                dataService.addKendaraan(newK);
            } else {
                // EDIT EXISTING
                kendaraan.setJenis(jenis);
                kendaraan.setMerk(merk);
                kendaraan.setModel(model);
                kendaraan.setTahun(tahun);
                kendaraan.setWarna(warna);
                kendaraan.setHarga(harga);
                kendaraan.setStok(stok);
                kendaraan.setDeskripsi(deskripsi);
                dataService.updateKendaraan(kendaraan);
            }

            isSuccess = true;
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Format angka salah (Tahun, Harga, atau Stok)!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}