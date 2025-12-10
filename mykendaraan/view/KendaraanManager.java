package com.mykendaraan.view;

import com.mykendaraan.model.Kendaraan;
import com.mykendaraan.model.User; // Import User
import com.mykendaraan.service.DataService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class KendaraanManager extends JInternalFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cmbFilter;
    private DataService dataService;
    private User currentUser; // Tambahkan variabel User

    // MODIFIKASI: Constructor menerima parameter User
    public KendaraanManager(User user) {
        super("Management Data Kendaraan", true, true, true, true);
        this.currentUser = user; // Simpan user
        this.dataService = DataService.getInstance();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setSize(1100, 600);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("MANAGEMENT DATA KENDARAAN", JLabel.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        titleLabel.setForeground(new Color(70, 130, 180));

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(Color.WHITE);

        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Tahoma", Font.PLAIN, 12));

        cmbFilter = new JComboBox<>(new String[]{"Semua", "Mobil", "Motor"});
        cmbFilter.setFont(new Font("Tahoma", Font.PLAIN, 12));

        JButton btnSearch = createButton("Cari", new Color(70, 130, 180));
        JButton btnClear = createButton("Reset", new Color(150, 150, 150));

        searchPanel.add(new JLabel("Pencarian:"));
        searchPanel.add(txtSearch);
        searchPanel.add(new JLabel("Filter:"));
        searchPanel.add(cmbFilter);
        searchPanel.add(btnSearch);
        searchPanel.add(btnClear);

        // Action Panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        actionPanel.setBackground(Color.WHITE);

        JButton btnAdd = createButton("Tambah Kendaraan", new Color(60, 150, 100));
        JButton btnEdit = createButton("Edit", new Color(70, 130, 180));
        JButton btnDelete = createButton("Hapus", new Color(200, 60, 60));
        JButton btnRefresh = createButton("Refresh", new Color(150, 150, 150));

        // MODIFIKASI: Logika Hak Akses (Role)
        // Jika pelanggan, matikan tombol CRUD
        if ("pelanggan".equals(currentUser.getRole())) {
            btnAdd.setEnabled(false);
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);

            // Tooltip info
            String tooltip = "Akses ditolak untuk Pelanggan";
            btnAdd.setToolTipText(tooltip);
            btnEdit.setToolTipText(tooltip);
            btnDelete.setToolTipText(tooltip);
        }

        actionPanel.add(btnAdd);
        actionPanel.add(btnEdit);
        actionPanel.add(btnDelete);
        actionPanel.add(btnRefresh);

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(searchPanel, BorderLayout.CENTER);
        headerPanel.add(actionPanel, BorderLayout.SOUTH);

        // Table
        String[] columns = {"ID", "Merk", "Model", "Tahun", "Warna", "Jenis", "Harga", "Stok", "Deskripsi"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.setFont(new Font("Tahoma", Font.PLAIN, 11));

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(60);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);
        table.getColumnModel().getColumn(6).setPreferredWidth(120);
        table.getColumnModel().getColumn(7).setPreferredWidth(50);
        table.getColumnModel().getColumn(8).setPreferredWidth(200);

        JScrollPane scrollPane = new JScrollPane(table);

        // Status Panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusPanel.setBackground(Color.WHITE);

        JLabel statusLabel = new JLabel("Total: 0 kendaraan");
        statusPanel.add(statusLabel);

        // Add to frame
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);

        // Event Listeners
        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> editSelected());
        btnDelete.addActionListener(e -> deleteSelected());
        btnRefresh.addActionListener(e -> loadData());
        btnSearch.addActionListener(e -> searchData());
        btnClear.addActionListener(e -> clearSearch());

        // MODIFIKASI: Double click to edit hanya jika bukan pelanggan
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (!"pelanggan".equals(currentUser.getRole())) {
                        editSelected();
                    }
                }
            }
        });
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Tahoma", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.black);
        UIManager.put("Button.disabledText", Color.GRAY);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return button;
    }

    private void loadData() {
        tableModel.setRowCount(0);
        java.util.List<Kendaraan> kendaraans = dataService.getAllKendaraan();
        for (Kendaraan k : kendaraans) {
            tableModel.addRow(k.toTableRow());
        }
        updateStatus("Total: " + kendaraans.size() + " kendaraan");
    }

    private void searchData() {
        String keyword = txtSearch.getText().trim();
        String filter = (String) cmbFilter.getSelectedItem();

        tableModel.setRowCount(0);
        java.util.List<Kendaraan> results = dataService.searchKendaraan(keyword);

        int count = 0;
        for (Kendaraan k : results) {
            boolean matchesFilter = "Semua".equals(filter) ||
                    ("Mobil".equals(filter) && "mobil".equals(k.getJenis())) ||
                    ("Motor".equals(filter) && "motor".equals(k.getJenis()));

            if (matchesFilter) {
                tableModel.addRow(k.toTableRow());
                count++;
            }
        }
        updateStatus("Ditemukan: " + count + " kendaraan");
    }

    private void clearSearch() {
        txtSearch.setText("");
        cmbFilter.setSelectedIndex(0);
        loadData();
    }

    private void showAddDialog() {
        KendaraanDialog dialog = new KendaraanDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadData();
            JOptionPane.showMessageDialog(this, "Data kendaraan berhasil ditambahkan");
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih kendaraan yang akan diedit");
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);
        Kendaraan kendaraan = dataService.getKendaraanById(id);

        if (kendaraan != null) {
            KendaraanDialog dialog = new KendaraanDialog((JFrame) SwingUtilities.getWindowAncestor(this), kendaraan);
            dialog.setVisible(true);
            if (dialog.isSuccess()) {
                loadData();
                JOptionPane.showMessageDialog(this, "Data kendaraan berhasil diupdate");
            }
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih kendaraan yang akan dihapus");
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);
        String nama = tableModel.getValueAt(row, 1) + " " + tableModel.getValueAt(row, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus kendaraan: " + nama + "?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dataService.deleteKendaraan(id);
            loadData();
            JOptionPane.showMessageDialog(this, "Data kendaraan berhasil dihapus");
        }
    }

    private void updateStatus(String message) {
        ((JLabel) ((JPanel) getContentPane().getComponent(2)).getComponent(0)).setText(message);
    }
}