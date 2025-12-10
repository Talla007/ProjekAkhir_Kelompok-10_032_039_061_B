package com.mykendaraan.view;

import com.mykendaraan.model.*;
import com.mykendaraan.service.DataService;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class LaporanManager extends JPanel {
    private JTable laporanTable;
    private DefaultTableModel laporanTableModel;
    private JComboBox<String> jenisLaporanCombo;
    private JComboBox<String> periodeCombo;
    private JDateChooser tanggalAwalChooser;
    private JDateChooser tanggalAkhirChooser;
    private JTextField searchField;
    private JLabel totalLabel;
    private JLabel transaksiLabel;
    private JLabel rataRataLabel;
    private JLabel summaryLabel;

    private DataService dataService;

    public LaporanManager() {
        dataService = DataService.getInstance();
        initComponents();
        loadDataLaporan();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel filter
        add(createFilterPanel(), BorderLayout.NORTH);

        // Panel tabel
        add(createTablePanel(), BorderLayout.CENTER);

        // Panel summary
        add(createSummaryPanel(), BorderLayout.SOUTH);
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 4, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Filter Laporan"));

        // Jenis Laporan
        panel.add(new JLabel("Jenis Laporan:"));
        String[] jenisLaporan = {
                "Semua Penjualan",
                "Penjualan Kendaraan",
                "Penjualan Sparepart"
        };
        jenisLaporanCombo = new JComboBox<>(jenisLaporan);
        panel.add(jenisLaporanCombo);

        // Periode
        panel.add(new JLabel("Periode:"));
        String[] periode = {"Hari Ini", "Minggu Ini", "Bulan Ini", "Tahun Ini", "Kustom", "Semua Data"};
        periodeCombo = new JComboBox<>(periode);
        panel.add(periodeCombo);

        // Tanggal Awal dengan JDateChooser
        panel.add(new JLabel("Tanggal Awal:"));
        tanggalAwalChooser = new JDateChooser();
        tanggalAwalChooser.setDateFormatString("dd-MM-yyyy");
        tanggalAwalChooser.setPreferredSize(new Dimension(150, 25));
        panel.add(tanggalAwalChooser);

        // Tanggal Akhir dengan JDateChooser
        panel.add(new JLabel("Tanggal Akhir:"));
        tanggalAkhirChooser = new JDateChooser();
        tanggalAkhirChooser.setDateFormatString("dd-MM-yyyy");
        tanggalAkhirChooser.setPreferredSize(new Dimension(150, 25));
        panel.add(tanggalAkhirChooser);

        // Search
        panel.add(new JLabel("Pencarian:"));
        searchField = new JTextField();
        panel.add(searchField);

        // Empty cells for alignment
        panel.add(new JLabel(""));
        panel.add(new JLabel(""));

        // Tombol
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton filterBtn = new JButton("Terapkan Filter");
        JButton resetBtn = new JButton("Reset Filter");
        JButton exportBtn = new JButton("Export ke Excel");
        JButton printBtn = new JButton("Cetak Laporan");

        filterBtn.addActionListener(e -> filterLaporan());
        resetBtn.addActionListener(e -> resetFilter());
        exportBtn.addActionListener(e -> exportToExcel());
        printBtn.addActionListener(e -> cetakLaporan());

        buttonPanel.add(filterBtn);
        buttonPanel.add(resetBtn);
        buttonPanel.add(exportBtn);
        buttonPanel.add(printBtn);

        // Action listener untuk periode combo
        periodeCombo.addActionListener(e -> handlePeriodeChange());

        // Set tanggal default ke hari ini
        setDefaultDates();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private void handlePeriodeChange() {
        String periode = (String) periodeCombo.getSelectedItem();
        Calendar cal = Calendar.getInstance();

        switch (periode) {
            case "Hari Ini":
                tanggalAwalChooser.setDate(cal.getTime());
                tanggalAkhirChooser.setDate(cal.getTime());
                break;
            case "Minggu Ini":
                // Start of week (Monday)
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                tanggalAwalChooser.setDate(cal.getTime());
                cal.add(Calendar.DATE, 6); // End of week (Sunday)
                tanggalAkhirChooser.setDate(cal.getTime());
                break;
            case "Bulan Ini":
                // Start of month
                cal.set(Calendar.DAY_OF_MONTH, 1);
                tanggalAwalChooser.setDate(cal.getTime());
                // End of month
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                tanggalAkhirChooser.setDate(cal.getTime());
                break;
            case "Tahun Ini":
                // Start of year
                cal.set(Calendar.MONTH, Calendar.JANUARY);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                tanggalAwalChooser.setDate(cal.getTime());
                // End of year
                cal.set(Calendar.MONTH, Calendar.DECEMBER);
                cal.set(Calendar.DAY_OF_MONTH, 31);
                tanggalAkhirChooser.setDate(cal.getTime());
                break;
            case "Semua Data":
                // Set range yang sangat luas
                cal.set(2000, Calendar.JANUARY, 1);
                tanggalAwalChooser.setDate(cal.getTime());
                cal.set(2030, Calendar.DECEMBER, 31);
                tanggalAkhirChooser.setDate(cal.getTime());
                break;
            case "Kustom":
                // Biarkan user memilih manual
                break;
        }
    }

    private void setDefaultDates() {
        // Set default ke bulan ini
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        tanggalAwalChooser.setDate(cal.getTime());

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        tanggalAkhirChooser.setDate(cal.getTime());

        periodeCombo.setSelectedItem("Bulan Ini");
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Data Laporan"));

        String[] columnNames = {
                "ID", "Tanggal", "Jenis", "Item", "Pelanggan",
                "Jumlah", "Harga", "Subtotal", "Metode", "Status"
        };

        laporanTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5) return Integer.class; // Jumlah
                if (columnIndex == 6 || columnIndex == 7) return Double.class; // Harga dan Subtotal
                return String.class;
            }
        };

        laporanTable = new JTable(laporanTableModel);
        laporanTable.setAutoCreateRowSorter(true);
        laporanTable.setFillsViewportHeight(true);

        // Set column widths
        laporanTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // ID
        laporanTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Tanggal
        laporanTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Jenis
        laporanTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Item
        laporanTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Pelanggan
        laporanTable.getColumnModel().getColumn(5).setPreferredWidth(60);  // Jumlah
        laporanTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Harga
        laporanTable.getColumnModel().getColumn(7).setPreferredWidth(120); // Subtotal
        laporanTable.getColumnModel().getColumn(8).setPreferredWidth(80);  // Metode
        laporanTable.getColumnModel().getColumn(9).setPreferredWidth(80);  // Status

        JScrollPane scrollPane = new JScrollPane(laporanTable);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Ringkasan Laporan"));
        panel.setPreferredSize(new Dimension(panel.getWidth(), 80));

        totalLabel = new JLabel("Total: Rp 0", JLabel.CENTER);
        totalLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        totalLabel.setForeground(Color.BLUE);
        totalLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        transaksiLabel = new JLabel("Transaksi: 0", JLabel.CENTER);
        transaksiLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        transaksiLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        rataRataLabel = new JLabel("Rata-rata: Rp 0", JLabel.CENTER);
        rataRataLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        rataRataLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        summaryLabel = new JLabel("Item: 0", JLabel.CENTER);
        summaryLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(totalLabel);
        panel.add(transaksiLabel);
        panel.add(rataRataLabel);
        panel.add(summaryLabel);

        return panel;
    }

    private void loadDataLaporan() {
        List<Penjualan> semuaPenjualan = dataService.getAllPenjualan();
        tampilkanDataLaporan(semuaPenjualan);
    }

    private void tampilkanDataLaporan(List<Penjualan> penjualanList) {
        laporanTableModel.setRowCount(0);
        double totalPenjualan = 0;
        int jumlahTransaksi = 0;
        int totalItem = 0;

        SimpleDateFormat displayFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        SimpleDateFormat dbFormat = new SimpleDateFormat("dd-MM-yyyy");

        for (Penjualan penjualan : penjualanList) {
            for (PenjualanItem item : penjualan.getItems()) {
                String jenis = item.getTipe();
                String itemName = getItemName(item);
                String pelangganName = getPelangganName(penjualan.getPelangganId());

                // Format tanggal untuk display
                String formattedDate = penjualan.getTanggal();
                try {
                    Date date = dbFormat.parse(penjualan.getTanggal().substring(0, 10));
                    formattedDate = displayFormat.format(date);
                } catch (Exception e) {
                    // Jika parsing gagal, gunakan format asli
                }

                Object[] row = {
                        penjualan.getId(),
                        formattedDate,
                        jenis.toUpperCase(),
                        itemName,
                        pelangganName,
                        item.getJumlah(),
                        item.getHargaSatuan(),
                        item.getSubtotal(),
                        penjualan.getMetodePembayaran(),
                        penjualan.getStatus()
                };
                laporanTableModel.addRow(row);

                totalPenjualan += item.getSubtotal();
                totalItem += item.getJumlah();
            }
            jumlahTransaksi++;
        }

        updateSummary(totalPenjualan, jumlahTransaksi, totalItem);
    }

    private void filterLaporan() {
        String jenis = (String) jenisLaporanCombo.getSelectedItem();
        Date tanggalAwal = tanggalAwalChooser.getDate();
        Date tanggalAkhir = tanggalAkhirChooser.getDate();
        String searchTerm = searchField.getText().trim().toLowerCase();

        // Validasi tanggal
        if (tanggalAwal == null || tanggalAkhir == null) {
            JOptionPane.showMessageDialog(this,
                    "Pilih tanggal awal dan tanggal akhir!",
                    "Validasi Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (tanggalAwal.after(tanggalAkhir)) {
            JOptionPane.showMessageDialog(this,
                    "Tanggal awal tidak boleh setelah tanggal akhir!",
                    "Validasi Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Penjualan> semuaPenjualan = dataService.getAllPenjualan();
        List<Penjualan> filtered = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        for (Penjualan p : semuaPenjualan) {
            try {
                // Parse tanggal dari penjualan
                String tanggalStr = p.getTanggal().substring(0, 10); // Ambil bagian tanggal saja
                Date tanggalPenjualan = dateFormat.parse(tanggalStr);

                // Filter tanggal
                boolean matchTanggal = !tanggalPenjualan.before(tanggalAwal) &&
                        !tanggalPenjualan.after(tanggalAkhir);

                if (!matchTanggal) {
                    continue;
                }

                // Filter jenis
                boolean matchJenis = true;
                if (!"Semua Penjualan".equals(jenis)) {
                    matchJenis = false;
                    for (PenjualanItem item : p.getItems()) {
                        if ("Penjualan Kendaraan".equals(jenis) && "kendaraan".equals(item.getTipe())) {
                            matchJenis = true;
                            break;
                        } else if ("Penjualan Sparepart".equals(jenis) && "sparepart".equals(item.getTipe())) {
                            matchJenis = true;
                            break;
                        }
                    }
                }

                // Filter pencarian
                boolean matchSearch = true;
                if (!searchTerm.isEmpty()) {
                    matchSearch = false;
                    if (p.getId().toLowerCase().contains(searchTerm)) {
                        matchSearch = true;
                    } else {
                        Pelanggan pl = dataService.getPelangganById(p.getPelangganId());
                        if (pl != null && pl.getNama().toLowerCase().contains(searchTerm)) {
                            matchSearch = true;
                        } else {
                            for (PenjualanItem item : p.getItems()) {
                                String itemName = getItemName(item);
                                if (itemName.toLowerCase().contains(searchTerm)) {
                                    matchSearch = true;
                                    break;
                                }
                            }
                        }
                    }
                }

                if (matchTanggal && matchJenis && matchSearch) {
                    filtered.add(p);
                }

            } catch (Exception e) {
                System.err.println("Error parsing date: " + e.getMessage());
            }
        }

        tampilkanDataLaporan(filtered);

        // Tampilkan info filter
        String infoFilter = String.format("Data ditampilkan: %s - %s (%d transaksi)",
                dateFormat.format(tanggalAwal),
                dateFormat.format(tanggalAkhir),
                filtered.size());

        JOptionPane.showMessageDialog(this, infoFilter, "Filter Diterapkan",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateSummary(double total, int transaksi, int item) {
        totalLabel.setText("Total: " + formatCurrency(total));
        transaksiLabel.setText("Transaksi: " + transaksi);
        rataRataLabel.setText("Rata-rata: " + formatCurrency(transaksi > 0 ? total / transaksi : 0));
        summaryLabel.setText("Item: " + item);
    }

    private String getItemName(PenjualanItem item) {
        if ("kendaraan".equals(item.getTipe())) {
            Kendaraan k = dataService.getKendaraanById(item.getItemId());
            return k != null ? k.getMerk() + " " + k.getModel() : "Unknown Kendaraan";
        } else if ("sparepart".equals(item.getTipe())) {
            Sparepart s = dataService.getSparepartById(item.getItemId());
            return s != null ? s.getNama() : "Unknown Sparepart";
        }
        return "Unknown";
    }

    private String getPelangganName(String pelangganId) {
        Pelanggan p = dataService.getPelangganById(pelangganId);
        return p != null ? p.getNama() : "Unknown Pelanggan";
    }

    private void resetFilter() {
        jenisLaporanCombo.setSelectedIndex(0);
        setDefaultDates();
        searchField.setText("");
        loadDataLaporan();
    }

    private void exportToExcel() {
        try {
            if (laporanTableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                        "Tidak ada data untuk diexport!",
                        "Export Data", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Simulasi export (dalam implementasi real, gunakan Apache POI)
            StringBuilder csvData = new StringBuilder();

            // Header
            for (int i = 0; i < laporanTableModel.getColumnCount(); i++) {
                csvData.append(laporanTableModel.getColumnName(i));
                if (i < laporanTableModel.getColumnCount() - 1) {
                    csvData.append(",");
                }
            }
            csvData.append("\n");

            // Data
            for (int row = 0; row < laporanTableModel.getRowCount(); row++) {
                for (int col = 0; col < laporanTableModel.getColumnCount(); col++) {
                    Object value = laporanTableModel.getValueAt(row, col);
                    csvData.append(value != null ? value.toString() : "");
                    if (col < laporanTableModel.getColumnCount() - 1) {
                        csvData.append(",");
                    }
                }
                csvData.append("\n");
            }

            // Tampilkan preview
            JTextArea textArea = new JTextArea(csvData.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));

            JOptionPane.showMessageDialog(this, scrollPane,
                    "Preview Data untuk Export", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error saat export: " + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cetakLaporan() {
        try {
            if (laporanTableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this,
                        "Tidak ada data untuk dicetak!",
                        "Cetak Laporan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Simulasi cetak laporan
            StringBuilder printData = new StringBuilder();
            printData.append("LAPORAN PENJUALAN MY KENDARAAN\n");
            printData.append("===============================\n\n");

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            printData.append("Periode: ")
                    .append(dateFormat.format(tanggalAwalChooser.getDate()))
                    .append(" s/d ")
                    .append(dateFormat.format(tanggalAkhirChooser.getDate()))
                    .append("\n");
            printData.append("Jenis Laporan: ").append(jenisLaporanCombo.getSelectedItem()).append("\n\n");

            // Data
            for (int row = 0; row < laporanTableModel.getRowCount(); row++) {
                printData.append(String.format("%-8s", laporanTableModel.getValueAt(row, 0))); // ID
                printData.append(String.format("%-15s", laporanTableModel.getValueAt(row, 1))); // Tanggal
                printData.append(String.format("%-12s", laporanTableModel.getValueAt(row, 2))); // Jenis
                printData.append(String.format("%-20s", laporanTableModel.getValueAt(row, 3))); // Item
                printData.append(String.format("%-15s", laporanTableModel.getValueAt(row, 4))); // Pelanggan
                printData.append(String.format("%-8s", laporanTableModel.getValueAt(row, 5))); // Jumlah
                printData.append(String.format("%-12s", laporanTableModel.getValueAt(row, 7))); // Subtotal
                printData.append("\n");
            }

            // Tampilkan preview cetak
            JTextArea textArea = new JTextArea(printData.toString());
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(700, 500));

            int option = JOptionPane.showConfirmDialog(this, scrollPane,
                    "Preview Cetak Laporan", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(this,
                        "Laporan berhasil dicetak!",
                        "Cetak Berhasil", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error saat mencetak: " + e.getMessage(),
                    "Cetak Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatCurrency(double amount) {
        return String.format("Rp %,10.2f", amount).trim();
    }

    public JPanel getContentPane() {
        return this;
    }
}