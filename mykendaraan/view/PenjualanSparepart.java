package com.mykendaraan.view;

import com.mykendaraan.model.*;
import com.mykendaraan.service.DataService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PenjualanSparepart extends JPanel {
    private JTable sparepartTable;
    private JTable pelangganTable;
    private JTable penjualanTable;
    private DefaultTableModel sparepartTableModel;
    private DefaultTableModel pelangganTableModel;
    private DefaultTableModel penjualanTableModel;
    private JTextField searchSparepartField;
    private JTextField searchPelangganField;
    private JTextField searchPenjualanField;
    private JTextField jumlahField;
    private JTextField hargaField;
    private JTextField subtotalField;
    private JComboBox<String> metodePembayaranCombo;
    private JTextArea catatanArea;

    private DataService dataService;

    // Selected items
    private Sparepart selectedSparepart;
    private Pelanggan selectedPelanggan;

    public PenjualanSparepart() {
        dataService = DataService.getInstance();
        initComponents();
        loadDataSparepart();
        loadDataPelanggan();
        loadDataPenjualan();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Main panel dengan tab
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Transaksi Penjualan
        tabbedPane.addTab("Transaksi Penjualan", createTransaksiPanel());

        // Tab 2: Data Penjualan
        tabbedPane.addTab("Data Penjualan", createDataPenjualanPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createTransaksiPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Panel atas untuk pemilihan sparepart dan pelanggan
        JPanel selectionPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // Panel sparepart
        selectionPanel.add(createSparepartPanel());

        // Panel pelanggan
        selectionPanel.add(createPelangganPanel());

        // Panel tengah untuk form transaksi
        JPanel formPanel = createFormPanel();

        // Panel bawah untuk tombol aksi
        JPanel buttonPanel = createButtonPanel();

        panel.add(selectionPanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSparepartPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Pilih Sparepart"));

        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchSparepartField = new JTextField();
        JButton searchSparepartBtn = new JButton("Cari");

        searchPanel.add(new JLabel("Cari Sparepart:"), BorderLayout.WEST);
        searchPanel.add(searchSparepartField, BorderLayout.CENTER);
        searchPanel.add(searchSparepartBtn, BorderLayout.EAST);

        // Table
        String[] columnNames = {"ID", "Nama", "Kategori", "Merk", "Harga", "Stok"};
        sparepartTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        sparepartTable = new JTable(sparepartTableModel);
        JScrollPane scrollPane = new JScrollPane(sparepartTable);

        // Selection listener
        sparepartTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = sparepartTable.getSelectedRow();
                if (selectedRow != -1) {
                    selectSparepart(selectedRow);
                }
            }
        });

        // Search button action
        searchSparepartBtn.addActionListener(e -> searchSparepart());
        searchSparepartField.addActionListener(e -> searchSparepart());

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPelangganPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Pilih Pelanggan"));

        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPelangganField = new JTextField();
        JButton searchPelangganBtn = new JButton("Cari");

        searchPanel.add(new JLabel("Cari Pelanggan:"), BorderLayout.WEST);
        searchPanel.add(searchPelangganField, BorderLayout.CENTER);
        searchPanel.add(searchPelangganBtn, BorderLayout.EAST);

        // Table
        String[] columnNames = {"ID", "Nama", "Telepon", "Email", "Alamat"};
        pelangganTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pelangganTable = new JTable(pelangganTableModel);
        JScrollPane scrollPane = new JScrollPane(pelangganTable);

        // Selection listener
        pelangganTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = pelangganTable.getSelectedRow();
                if (selectedRow != -1) {
                    selectPelanggan(selectedRow);
                }
            }
        });

        // Search button action
        searchPelangganBtn.addActionListener(e -> searchPelanggan());
        searchPelangganField.addActionListener(e -> searchPelanggan());

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Detail Transaksi"));

        // Form fields
        hargaField = new JTextField();
        hargaField.setEditable(false);

        jumlahField = new JTextField();
        jumlahField.setText("1");

        subtotalField = new JTextField();
        subtotalField.setEditable(false);

        String[] metodePembayaran = {"Cash", "Transfer Bank", "Kredit"};
        metodePembayaranCombo = new JComboBox<>(metodePembayaran);

        catatanArea = new JTextArea(3, 20);
        JScrollPane catatanScroll = new JScrollPane(catatanArea);

        // Action listener untuk jumlah field
        jumlahField.addActionListener(e -> calculateSubtotal());
        jumlahField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                calculateSubtotal();
            }
        });

        panel.add(new JLabel("Harga Sparepart:"));
        panel.add(hargaField);
        panel.add(new JLabel("Jumlah:"));
        panel.add(jumlahField);
        panel.add(new JLabel("Subtotal:"));
        panel.add(subtotalField);
        panel.add(new JLabel("Metode Pembayaran:"));
        panel.add(metodePembayaranCombo);
        panel.add(new JLabel("Catatan:"));
        panel.add(catatanScroll);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JButton prosesBtn = new JButton("Proses Penjualan");
        JButton resetBtn = new JButton("Reset Form");
        JButton printBtn = new JButton("Cetak Invoice");

        prosesBtn.addActionListener(e -> prosesPenjualan());
        resetBtn.addActionListener(e -> resetForm());
        printBtn.addActionListener(e -> cetakInvoice());

        panel.add(prosesBtn);
        panel.add(resetBtn);
        panel.add(printBtn);

        return panel;
    }

    private JPanel createDataPenjualanPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPenjualanField = new JTextField();
        JButton searchPenjualanBtn = new JButton("Cari");
        JButton refreshBtn = new JButton("Refresh");

        JPanel searchButtonPanel = new JPanel(new FlowLayout());
        searchButtonPanel.add(searchPenjualanBtn);
        searchButtonPanel.add(refreshBtn);

        searchPanel.add(new JLabel("Cari Penjualan:"), BorderLayout.WEST);
        searchPanel.add(searchPenjualanField, BorderLayout.CENTER);
        searchPanel.add(searchButtonPanel, BorderLayout.EAST);

        // Table
        String[] columnNames = {"ID", "Tanggal", "Pelanggan", "Sparepart", "Jumlah", "Total", "Metode", "Status"};
        penjualanTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        penjualanTable = new JTable(penjualanTableModel);
        JScrollPane scrollPane = new JScrollPane(penjualanTable);

        // Button panel untuk data penjualan
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton hapusBtn = new JButton("Hapus Penjualan");
        JButton detailBtn = new JButton("Lihat Detail");

        hapusBtn.addActionListener(e -> hapusPenjualan());
        detailBtn.addActionListener(e -> lihatDetailPenjualan());

        buttonPanel.add(hapusBtn);
        buttonPanel.add(detailBtn);

        // Search actions
        searchPenjualanBtn.addActionListener(e -> searchPenjualan());
        refreshBtn.addActionListener(e -> loadDataPenjualan());
        searchPenjualanField.addActionListener(e -> searchPenjualan());

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // === DATA OPERATIONS ===

    private void loadDataSparepart() {
        List<Sparepart> sparepartList = dataService.getAllSparepart();
        sparepartTableModel.setRowCount(0);

        for (Sparepart s : sparepartList) {
            if (s.getStok() > 0) {
                Object[] row = {
                        s.getId(),
                        s.getNama(),
                        s.getKategori(),
                        s.getMerk(),
                        formatCurrency(s.getHarga()),
                        s.getStok()
                };
                sparepartTableModel.addRow(row);
            }
        }
    }

    private void loadDataPelanggan() {
        List<Pelanggan> pelangganList = dataService.getAllPelanggan();
        pelangganTableModel.setRowCount(0);

        for (Pelanggan p : pelangganList) {
            Object[] row = {
                    p.getId(),
                    p.getNama(),
                    p.getTelepon(),
                    p.getEmail(),
                    p.getAlamat()
            };
            pelangganTableModel.addRow(row);
        }
    }

    private void loadDataPenjualan() {
        List<Penjualan> penjualanList = dataService.getAllPenjualan();
        penjualanTableModel.setRowCount(0);

        for (Penjualan p : penjualanList) {
            // Cari penjualan yang mengandung sparepart
            for (PenjualanItem item : p.getItems()) {
                if ("sparepart".equals(item.getTipe())) {
                    Sparepart s = dataService.getSparepartById(item.getItemId());
                    Pelanggan pl = dataService.getPelangganById(p.getPelangganId());

                    if (s != null && pl != null) {
                        Object[] row = {
                                p.getId(),
                                p.getTanggal(),
                                pl.getNama(),
                                s.getNama(),
                                item.getJumlah(),
                                formatCurrency(item.getSubtotal()),
                                p.getMetodePembayaran(),
                                p.getStatus()
                        };
                        penjualanTableModel.addRow(row);
                    }
                    break; // Hanya tampilkan satu item per baris
                }
            }
        }
    }

    // === SELECTION HANDLERS ===

    private void selectSparepart(int row) {
        String sparepartId = (String) sparepartTableModel.getValueAt(row, 0);
        selectedSparepart = dataService.getSparepartById(sparepartId);

        if (selectedSparepart != null) {
            hargaField.setText(formatCurrency(selectedSparepart.getHarga()));
            calculateSubtotal();
        }
    }

    private void selectPelanggan(int row) {
        String pelangganId = (String) pelangganTableModel.getValueAt(row, 0);
        selectedPelanggan = dataService.getPelangganById(pelangganId);
    }

    private void calculateSubtotal() {
        if (selectedSparepart != null && !jumlahField.getText().isEmpty()) {
            try {
                int jumlah = Integer.parseInt(jumlahField.getText());
                if (jumlah > 0) {
                    double subtotal = selectedSparepart.getHarga() * jumlah;
                    subtotalField.setText(formatCurrency(subtotal));
                } else {
                    subtotalField.setText("Rp 0");
                }
            } catch (NumberFormatException e) {
                subtotalField.setText("Rp 0");
            }
        } else {
            subtotalField.setText("Rp 0");
        }
    }

    // === ACTION HANDLERS ===

    private void searchSparepart() {
        String searchTerm = searchSparepartField.getText().trim();
        List<Sparepart> results;

        if (searchTerm.isEmpty()) {
            results = dataService.getAllSparepart();
        } else {
            results = dataService.searchSparepart(searchTerm);
        }

        sparepartTableModel.setRowCount(0);
        for (Sparepart s : results) {
            if (s.getStok() > 0) {
                Object[] row = {
                        s.getId(),
                        s.getNama(),
                        s.getKategori(),
                        s.getMerk(),
                        formatCurrency(s.getHarga()),
                        s.getStok()
                };
                sparepartTableModel.addRow(row);
            }
        }
    }

    private void searchPelanggan() {
        String searchTerm = searchPelangganField.getText().trim();
        List<Pelanggan> results;

        if (searchTerm.isEmpty()) {
            results = dataService.getAllPelanggan();
        } else {
            results = dataService.searchPelanggan(searchTerm);
        }

        pelangganTableModel.setRowCount(0);
        for (Pelanggan p : results) {
            Object[] row = {
                    p.getId(),
                    p.getNama(),
                    p.getTelepon(),
                    p.getEmail(),
                    p.getAlamat()
            };
            pelangganTableModel.addRow(row);
        }
    }

    private void searchPenjualan() {
        String searchTerm = searchPenjualanField.getText().trim().toLowerCase();
        List<Penjualan> allPenjualan = dataService.getAllPenjualan();

        penjualanTableModel.setRowCount(0);
        for (Penjualan p : allPenjualan) {
            for (PenjualanItem item : p.getItems()) {
                if ("sparepart".equals(item.getTipe())) {
                    Sparepart s = dataService.getSparepartById(item.getItemId());
                    Pelanggan pl = dataService.getPelangganById(p.getPelangganId());

                    if (s != null && pl != null) {
                        boolean match = p.getId().toLowerCase().contains(searchTerm) ||
                                pl.getNama().toLowerCase().contains(searchTerm) ||
                                s.getNama().toLowerCase().contains(searchTerm) ||
                                p.getTanggal().toLowerCase().contains(searchTerm);

                        if (match || searchTerm.isEmpty()) {
                            Object[] row = {
                                    p.getId(),
                                    p.getTanggal(),
                                    pl.getNama(),
                                    s.getNama(),
                                    item.getJumlah(),
                                    formatCurrency(item.getSubtotal()),
                                    p.getMetodePembayaran(),
                                    p.getStatus()
                            };
                            penjualanTableModel.addRow(row);
                        }
                    }
                    break;
                }
            }
        }
    }

    private void prosesPenjualan() {
        // Validasi
        if (selectedSparepart == null) {
            JOptionPane.showMessageDialog(this, "Pilih sparepart terlebih dahulu!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedPelanggan == null) {
            JOptionPane.showMessageDialog(this, "Pilih pelanggan terlebih dahulu!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (jumlahField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan jumlah sparepart!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int jumlah = Integer.parseInt(jumlahField.getText());
            double harga = selectedSparepart.getHarga();

            if (jumlah <= 0) {
                JOptionPane.showMessageDialog(this, "Jumlah harus lebih dari 0!",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (jumlah > selectedSparepart.getStok()) {
                JOptionPane.showMessageDialog(this,
                        "Stok tidak mencukupi! Stok tersedia: " + selectedSparepart.getStok(),
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Konfirmasi
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Proses penjualan sparepart?\n\n" +
                            "Pelanggan: " + selectedPelanggan.getNama() + "\n" +
                            "Sparepart: " + selectedSparepart.getNama() + "\n" +
                            "Jumlah: " + jumlah + "\n" +
                            "Harga: " + formatCurrency(harga) + "\n" +
                            "Subtotal: " + formatCurrency(harga * jumlah) + "\n" +
                            "Metode: " + metodePembayaranCombo.getSelectedItem(),
                    "Konfirmasi Penjualan",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                simpanPenjualan(jumlah);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Format jumlah tidak valid!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void simpanPenjualan(int jumlah) {
        try {
            // Buat penjualan item untuk sparepart
            PenjualanItem sparepartItem = new PenjualanItem();
            sparepartItem.setTipe("sparepart");
            sparepartItem.setItemId(selectedSparepart.getId());
            sparepartItem.setJumlah(jumlah);
            sparepartItem.setHargaSatuan(selectedSparepart.getHarga());
            sparepartItem.setSubtotal(selectedSparepart.getHarga() * jumlah);

            // Buat objek penjualan
            Penjualan penjualan = new Penjualan();
            penjualan.setPelangganId(selectedPelanggan.getId());
            penjualan.setTotalDp(0); // Untuk sparepart biasanya full payment
            penjualan.setMetodePembayaran((String) metodePembayaranCombo.getSelectedItem());
            penjualan.setStatus("Selesai");
            penjualan.setCatatan(catatanArea.getText());
            penjualan.setTanggal(new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()));

            // Tambah item ke penjualan
            penjualan.addItem(sparepartItem);

            // Simpan ke DataService
            dataService.addPenjualan(penjualan);

            JOptionPane.showMessageDialog(this,
                    "Penjualan sparepart berhasil diproses!\nID Transaksi: " + penjualan.getId(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            resetForm();
            loadDataSparepart(); // Refresh stok
            loadDataPenjualan(); // Refresh data penjualan

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error processing penjualan: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void resetForm() {
        selectedSparepart = null;
        selectedPelanggan = null;
        hargaField.setText("");
        jumlahField.setText("1");
        subtotalField.setText("");
        metodePembayaranCombo.setSelectedIndex(0);
        catatanArea.setText("");
        sparepartTable.clearSelection();
        pelangganTable.clearSelection();
    }

    private void cetakInvoice() {
        int selectedRow = penjualanTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih penjualan terlebih dahulu!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String penjualanId = (String) penjualanTableModel.getValueAt(selectedRow, 0);
        List<Penjualan> allPenjualan = dataService.getAllPenjualan();
        Penjualan selectedPenjualan = null;

        for (Penjualan p : allPenjualan) {
            if (p.getId().equals(penjualanId)) {
                selectedPenjualan = p;
                break;
            }
        }

        if (selectedPenjualan != null) {
            showInvoice(selectedPenjualan);
        }
    }

    private void showInvoice(Penjualan penjualan) {
        // Implementasi showInvoice untuk sparepart
        // Similar to Kendaraan version but for sparepart
        JOptionPane.showMessageDialog(this,
                "Fitur cetak invoice untuk sparepart akan diimplementasikan",
                "Cetak Invoice", JOptionPane.INFORMATION_MESSAGE);
    }

    private void hapusPenjualan() {
        // Similar to Kendaraan version
        JOptionPane.showMessageDialog(this,
                "Fitur hapus penjualan membutuhkan implementasi lebih lanjut",
                "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void lihatDetailPenjualan() {
        int selectedRow = penjualanTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih penjualan terlebih dahulu!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Implementasi lihat detail
        JOptionPane.showMessageDialog(this,
                "Detail penjualan sparepart akan ditampilkan di sini",
                "Detail Penjualan", JOptionPane.INFORMATION_MESSAGE);
    }

    // === UTILITY METHODS ===

    private String formatCurrency(double amount) {
        return String.format("Rp %,10.2f", amount).trim();
    }

    public JPanel getContentPane() {
        return this;
    }
}