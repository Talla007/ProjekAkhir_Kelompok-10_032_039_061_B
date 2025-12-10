package com.mykendaraan.view;

import com.mykendaraan.model.*;
import com.mykendaraan.service.DataService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PenjualanKendaraan extends JPanel {
    private JTable kendaraanTable;
    private JTable pelangganTable;
    private JTable penjualanTable;
    private DefaultTableModel kendaraanTableModel;
    private DefaultTableModel pelangganTableModel;
    private DefaultTableModel penjualanTableModel;
    private JTextField searchKendaraanField;
    private JTextField searchPelangganField;
    private JTextField searchPenjualanField;
    private JTextField hargaField;
    private JTextField dpField;
    private JComboBox<String> metodePembayaranCombo;
    private JTextArea catatanArea;

    private DataService dataService;

    // Selected items
    private Kendaraan selectedKendaraan;
    private Pelanggan selectedPelanggan;

    public PenjualanKendaraan() {
        dataService = DataService.getInstance();
        initComponents();
        loadDataKendaraan();
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

        // Panel atas untuk pemilihan kendaraan dan pelanggan
        JPanel selectionPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // Panel kendaraan
        selectionPanel.add(createKendaraanPanel());

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

    private JPanel createKendaraanPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Pilih Kendaraan"));

        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchKendaraanField = new JTextField();
        JButton searchKendaraanBtn = new JButton("Cari");

        searchPanel.add(new JLabel("Cari Kendaraan:"), BorderLayout.WEST);
        searchPanel.add(searchKendaraanField, BorderLayout.CENTER);
        searchPanel.add(searchKendaraanBtn, BorderLayout.EAST);

        // Table
        String[] columnNames = {"ID", "Jenis", "Merk", "Model", "Tahun", "Harga", "Stok"};
        kendaraanTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        kendaraanTable = new JTable(kendaraanTableModel);
        JScrollPane scrollPane = new JScrollPane(kendaraanTable);

        // Selection listener
        kendaraanTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = kendaraanTable.getSelectedRow();
                if (selectedRow != -1) {
                    selectKendaraan(selectedRow);
                }
            }
        });

        // Search button action
        searchKendaraanBtn.addActionListener(e -> searchKendaraan());

        // Enter key untuk search
        searchKendaraanField.addActionListener(e -> searchKendaraan());

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

        // Enter key untuk search
        searchPelangganField.addActionListener(e -> searchPelanggan());

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Detail Transaksi"));

        // Form fields
        hargaField = new JTextField();
        hargaField.setEditable(false);

        dpField = new JTextField();

        String[] metodePembayaran = {"Cash", "Kredit", "Transfer Bank"};
        metodePembayaranCombo = new JComboBox<>(metodePembayaran);

        catatanArea = new JTextArea(3, 20);
        JScrollPane catatanScroll = new JScrollPane(catatanArea);

        panel.add(new JLabel("Harga Kendaraan:"));
        panel.add(hargaField);
        panel.add(new JLabel("DP (Down Payment):"));
        panel.add(dpField);
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
        String[] columnNames = {"ID", "Tanggal", "Pelanggan", "Kendaraan", "Harga", "DP", "Metode", "Status"};
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

        // Enter key untuk search
        searchPenjualanField.addActionListener(e -> searchPenjualan());

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // === DATA OPERATIONS ===

    private void loadDataKendaraan() {
        List<Kendaraan> kendaraanList = dataService.getAllKendaraan();
        kendaraanTableModel.setRowCount(0);

        for (Kendaraan k : kendaraanList) {
            if (k.getStok() > 0) { // Hanya tampilkan yang stoknya tersedia
                Object[] row = {
                        k.getId(),
                        k.getJenis(),
                        k.getMerk(),
                        k.getModel(),
                        k.getTahun(),
                        formatCurrency(k.getHarga()),
                        k.getStok()
                };
                kendaraanTableModel.addRow(row);
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
            // Ambil info pelanggan dan kendaraan dari items
            String pelangganInfo = "Unknown";
            String kendaraanInfo = "Unknown";
            double totalHarga = 0;
            double dp = 0;
            String metode = "Cash";

            if (!p.getItems().isEmpty()) {
                PenjualanItem firstItem = p.getItems().get(0);
                if ("kendaraan".equals(firstItem.getTipe())) {
                    Kendaraan k = dataService.getKendaraanById(firstItem.getItemId());
                    if (k != null) {
                        kendaraanInfo = k.getMerk() + " " + k.getModel();
                        totalHarga = k.getHarga();
                    }
                }

                // Ambil info pelanggan
                Pelanggan pl = dataService.getPelangganById(p.getPelangganId());
                if (pl != null) {
                    pelangganInfo = pl.getNama();
                }

                // Info tambahan dari penjualan
                dp = p.getTotalDp();
                metode = p.getMetodePembayaran();
            }

            Object[] row = {
                    p.getId(),
                    p.getTanggal(),
                    pelangganInfo,
                    kendaraanInfo,
                    formatCurrency(totalHarga),
                    formatCurrency(dp),
                    metode,
                    p.getStatus()
            };
            penjualanTableModel.addRow(row);
        }
    }

    // === SELECTION HANDLERS ===

    private void selectKendaraan(int row) {
        String kendaraanId = (String) kendaraanTableModel.getValueAt(row, 0);
        selectedKendaraan = dataService.getKendaraanById(kendaraanId);

        if (selectedKendaraan != null) {
            hargaField.setText(formatCurrency(selectedKendaraan.getHarga()));
        }
    }

    private void selectPelanggan(int row) {
        String pelangganId = (String) pelangganTableModel.getValueAt(row, 0);
        selectedPelanggan = dataService.getPelangganById(pelangganId);
    }

    // === ACTION HANDLERS ===

    private void searchKendaraan() {
        String searchTerm = searchKendaraanField.getText().trim();
        List<Kendaraan> results;

        if (searchTerm.isEmpty()) {
            results = dataService.getAllKendaraan();
        } else {
            results = dataService.searchKendaraan(searchTerm);
        }

        kendaraanTableModel.setRowCount(0);
        for (Kendaraan k : results) {
            if (k.getStok() > 0) {
                Object[] row = {
                        k.getId(),
                        k.getJenis(),
                        k.getMerk(),
                        k.getModel(),
                        k.getTahun(),
                        formatCurrency(k.getHarga()),
                        k.getStok()
                };
                kendaraanTableModel.addRow(row);
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
        // Untuk sederhana, kita filter di client side
        String searchTerm = searchPenjualanField.getText().trim().toLowerCase();
        List<Penjualan> allPenjualan = dataService.getAllPenjualan();

        penjualanTableModel.setRowCount(0);
        for (Penjualan p : allPenjualan) {
            // Cek apakah search term cocok dengan ID atau info lainnya
            boolean match = p.getId().toLowerCase().contains(searchTerm) ||
                    p.getTanggal().toLowerCase().contains(searchTerm);

            if (!match && !p.getItems().isEmpty()) {
                PenjualanItem firstItem = p.getItems().get(0);
                if ("kendaraan".equals(firstItem.getTipe())) {
                    Kendaraan k = dataService.getKendaraanById(firstItem.getItemId());
                    if (k != null) {
                        match = k.getMerk().toLowerCase().contains(searchTerm) ||
                                k.getModel().toLowerCase().contains(searchTerm);
                    }
                }

                Pelanggan pl = dataService.getPelangganById(p.getPelangganId());
                if (pl != null) {
                    match = match || pl.getNama().toLowerCase().contains(searchTerm);
                }
            }

            if (match || searchTerm.isEmpty()) {
                String pelangganInfo = "Unknown";
                String kendaraanInfo = "Unknown";
                double totalHarga = 0;
                double dp = p.getTotalDp();
                String metode = p.getMetodePembayaran();

                if (!p.getItems().isEmpty()) {
                    PenjualanItem firstItem = p.getItems().get(0);
                    if ("kendaraan".equals(firstItem.getTipe())) {
                        Kendaraan k = dataService.getKendaraanById(firstItem.getItemId());
                        if (k != null) {
                            kendaraanInfo = k.getMerk() + " " + k.getModel();
                            totalHarga = k.getHarga();
                        }
                    }

                    Pelanggan pl = dataService.getPelangganById(p.getPelangganId());
                    if (pl != null) {
                        pelangganInfo = pl.getNama();
                    }
                }

                Object[] row = {
                        p.getId(),
                        p.getTanggal(),
                        pelangganInfo,
                        kendaraanInfo,
                        formatCurrency(totalHarga),
                        formatCurrency(dp),
                        metode,
                        p.getStatus()
                };
                penjualanTableModel.addRow(row);
            }
        }
    }

    private void prosesPenjualan() {
        // Validasi
        if (selectedKendaraan == null) {
            JOptionPane.showMessageDialog(this, "Pilih kendaraan terlebih dahulu!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedPelanggan == null) {
            JOptionPane.showMessageDialog(this, "Pilih pelanggan terlebih dahulu!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (dpField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan jumlah DP!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double dp = Double.parseDouble(dpField.getText());
            double harga = selectedKendaraan.getHarga();

            if (dp < 0) {
                JOptionPane.showMessageDialog(this, "DP tidak boleh negatif!",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (dp > harga) {
                JOptionPane.showMessageDialog(this, "DP tidak boleh lebih besar dari harga kendaraan!",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (selectedKendaraan.getStok() <= 0) {
                JOptionPane.showMessageDialog(this, "Stok kendaraan tidak tersedia!",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Konfirmasi
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Proses penjualan kendaraan?\n\n" +
                            "Pelanggan: " + selectedPelanggan.getNama() + "\n" +
                            "Kendaraan: " + selectedKendaraan.getMerk() + " " + selectedKendaraan.getModel() + "\n" +
                            "Harga: " + formatCurrency(harga) + "\n" +
                            "DP: " + formatCurrency(dp) + "\n" +
                            "Metode: " + metodePembayaranCombo.getSelectedItem(),
                    "Konfirmasi Penjualan",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                simpanPenjualan(dp);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Format DP tidak valid!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void simpanPenjualan(double dp) {
        try {
            // Buat penjualan item untuk kendaraan
            PenjualanItem kendaraanItem = new PenjualanItem();
            kendaraanItem.setTipe("kendaraan");
            kendaraanItem.setItemId(selectedKendaraan.getId());
            kendaraanItem.setJumlah(1);
            kendaraanItem.setHargaSatuan(selectedKendaraan.getHarga());
            kendaraanItem.setSubtotal(selectedKendaraan.getHarga());

            // Buat objek penjualan menggunakan constructor tanpa parameter
            Penjualan penjualan = new Penjualan();
            penjualan.setPelangganId(selectedPelanggan.getId());
            penjualan.setTotalDp(dp);
            penjualan.setMetodePembayaran((String) metodePembayaranCombo.getSelectedItem());
            penjualan.setStatus("Selesai");
            penjualan.setCatatan(catatanArea.getText());
            penjualan.setTanggal(new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()));

            // Tambah item ke penjualan
            penjualan.getItems().add(kendaraanItem);

            // Simpan ke DataService
            dataService.addPenjualan(penjualan);

            JOptionPane.showMessageDialog(this,
                    "Penjualan berhasil diproses!\nID Transaksi: " + penjualan.getId(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            resetForm();
            loadDataKendaraan(); // Refresh stok
            loadDataPenjualan(); // Refresh data penjualan

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error processing penjualan: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void resetForm() {
        selectedKendaraan = null;
        selectedPelanggan = null;
        hargaField.setText("");
        dpField.setText("");
        metodePembayaranCombo.setSelectedIndex(0);
        catatanArea.setText("");
        kendaraanTable.clearSelection();
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
        // Ambil data pelanggan
        Pelanggan pelanggan = dataService.getPelangganById(penjualan.getPelangganId());
        String pelangganInfo = pelanggan != null ?
                pelanggan.getNama() + "\n" + pelanggan.getAlamat() + "\nTelp: " + pelanggan.getTelepon() :
                "Unknown";

        // Ambil data kendaraan dari items
        String kendaraanInfo = "";
        double totalHarga = 0;

        for (PenjualanItem item : penjualan.getItems()) {
            if ("kendaraan".equals(item.getTipe())) {
                Kendaraan k = dataService.getKendaraanById(item.getItemId());
                if (k != null) {
                    kendaraanInfo = k.getMerk() + " " + k.getModel() + " (" + k.getTahun() + ")";
                    totalHarga = k.getHarga();
                    break;
                }
            }
        }

        String invoiceText = String.format(
                "INVOICE PENJUALAN KENDARAAN\n\n" +
                        "ID Transaksi: %s\n" +
                        "Tanggal: %s\n\n" +
                        "PELANGGAN:\n%s\n\n" +
                        "KENDARAAN:\n%s\n\n" +
                        "DETAIL PEMBAYARAN:\n" +
                        "Harga Kendaraan: %s\n" +
                        "DP: %s\n" +
                        "Sisa: %s\n" +
                        "Metode Pembayaran: %s\n" +
                        "Status: %s\n\n" +
                        "Catatan: %s\n\n" +
                        "Terima kasih atas pembelian Anda!",
                penjualan.getId(),
                penjualan.getTanggal(),
                pelangganInfo,
                kendaraanInfo,
                formatCurrency(totalHarga),
                formatCurrency(penjualan.getTotalDp()),
                formatCurrency(totalHarga - penjualan.getTotalDp()),
                penjualan.getMetodePembayaran(),
                penjualan.getStatus(),
                penjualan.getCatatan()
        );

        JTextArea textArea = new JTextArea(invoiceText);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Invoice Penjualan", JOptionPane.INFORMATION_MESSAGE);
    }

    private void hapusPenjualan() {
        int selectedRow = penjualanTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih penjualan terlebih dahulu!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String penjualanId = (String) penjualanTableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus penjualan " + penjualanId + "?\n\n" +
                        "Tindakan ini akan mengembalikan stok kendaraan dan tidak dapat dibatalkan!",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Untuk DataService sederhana, kita tidak implementasi delete penjualan
            // Karena complex untuk mengembalikan stok
            JOptionPane.showMessageDialog(this,
                    "Fitur hapus penjualan membutuhkan implementasi lebih lanjut.\n" +
                            "Pada implementasi real, ini akan mengembalikan stok kendaraan.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void lihatDetailPenjualan() {
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
            showDetailPenjualan(selectedPenjualan);
        }
    }

    private void showDetailPenjualan(Penjualan penjualan) {
        // Ambil data pelanggan
        Pelanggan pelanggan = dataService.getPelangganById(penjualan.getPelangganId());
        String pelangganInfo = pelanggan != null ?
                pelanggan.getNama() + " (" + pelanggan.getTelepon() + ")" : "Unknown";

        // Kumpulkan info items
        StringBuilder itemsInfo = new StringBuilder();
        double totalHarga = 0;

        for (PenjualanItem item : penjualan.getItems()) {
            if ("kendaraan".equals(item.getTipe())) {
                Kendaraan k = dataService.getKendaraanById(item.getItemId());
                if (k != null) {
                    itemsInfo.append("- ").append(k.getMerk()).append(" ").append(k.getModel())
                            .append(" (").append(k.getTahun()).append(")\n");
                    totalHarga += k.getHarga();
                }
            }
        }

        String detail = String.format(
                "DETAIL PENJUALAN KENDARAAN\n\n" +
                        "ID Transaksi: %s\n" +
                        "Tanggal: %s\n" +
                        "Pelanggan: %s\n\n" +
                        "Items:\n%s\n" +
                        "Total Harga: %s\n" +
                        "DP: %s\n" +
                        "Sisa: %s\n" +
                        "Metode Pembayaran: %s\n" +
                        "Status: %s\n" +
                        "Catatan: %s",
                penjualan.getId(),
                penjualan.getTanggal(),
                pelangganInfo,
                itemsInfo.toString(),
                formatCurrency(totalHarga),
                formatCurrency(penjualan.getTotalDp()),
                formatCurrency(totalHarga - penjualan.getTotalDp()),
                penjualan.getMetodePembayaran(),
                penjualan.getStatus(),
                penjualan.getCatatan()
        );

        JOptionPane.showMessageDialog(this, detail, "Detail Penjualan", JOptionPane.INFORMATION_MESSAGE);
    }

    // === UTILITY METHODS ===

    private String formatCurrency(double amount) {
        return String.format("Rp %,10.2f", amount).trim();
    }

    public JPanel getContentPane() {
        return this;
    }
}