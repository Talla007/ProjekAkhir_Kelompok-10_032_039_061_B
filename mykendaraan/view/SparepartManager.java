package com.mykendaraan.view;

import com.mykendaraan.model.Sparepart;
import com.mykendaraan.model.User; // Import User
import com.mykendaraan.service.DataService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class SparepartManager extends JInternalFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private DataService dataService;
    private User currentUser; // Tambahkan variabel untuk menyimpan user yang login

    // MODIFIKASI: Constructor menerima parameter User
    public SparepartManager(User user) {
        super("Management Data Sparepart", true, true, true, true);
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

        JLabel titleLabel = new JLabel("MANAGEMENT DATA SPAREPART", JLabel.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        titleLabel.setForeground(new Color(70, 130, 180));

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(Color.WHITE);

        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Tahoma", Font.PLAIN, 12));

        JButton btnSearch = createButton("Cari", new Color(70, 130, 180));
        JButton btnClear = createButton("Reset", new Color(150, 150, 150));

        searchPanel.add(new JLabel("Pencarian:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnClear);

        // Action Panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        actionPanel.setBackground(Color.WHITE);

        JButton btnAdd = createButton("Tambah Sparepart", new Color(60, 150, 100));
        JButton btnEdit = createButton("Edit", new Color(70, 130, 180));
        JButton btnDelete = createButton("Hapus", new Color(200, 60, 60));
        JButton btnRefresh = createButton("Refresh", new Color(150, 150, 150));

        // MODIFIKASI: Logika Hak Akses (Role)
        // Jika pelanggan, matikan tombol CRUD, tapi biarkan Refresh
        if ("pelanggan".equals(currentUser.getRole())) {
            btnAdd.setEnabled(false);
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);

            // Opsional: Beri tooltip agar user tahu kenapa tombol mati
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
        String[] columns = {"ID", "Nama", "Kategori", "Merk", "Tipe Kendaraan", "Harga", "Stok", "Stok Minimal"};
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
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setPreferredWidth(60);
        table.getColumnModel().getColumn(7).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(table);

        // Status Panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusPanel.setBackground(Color.WHITE);

        JLabel statusLabel = new JLabel("Total: 0 sparepart");
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
                    // Cek role sebelum mengizinkan edit via double click
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
        button.setForeground(Color.black); // Ubah text jadi hitam agar kontras dengan warna disabled

        // Ubah UI Manager untuk warna disabled button agar tetap terbaca meski abu-abu
        UIManager.put("Button.disabledText", Color.GRAY);

        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return button;
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try {
            java.util.List<Sparepart> spareparts = dataService.getAllSparepart();
            for (Sparepart sparepart : spareparts) {
                tableModel.addRow(sparepart.toTableRow());
            }
            updateStatus("Total: " + spareparts.size() + " sparepart");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchData() {
        String keyword = txtSearch.getText().trim();
        tableModel.setRowCount(0);
        try {
            List<Sparepart> results = dataService.getAllSparepart();
            for (Sparepart sparepart : results) {
                tableModel.addRow(sparepart.toTableRow());
            }
            updateStatus("Ditemukan: " + results.size() + " sparepart");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error searching: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearSearch() {
        txtSearch.setText("");
        loadData();
    }

    private void showAddDialog() {
        try {
            SparepartDialog dialog = new SparepartDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
            dialog.setVisible(true);
            if (dialog.isSuccess()) {
                loadData();
                JOptionPane.showMessageDialog(this, "Data sparepart berhasil ditambahkan");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih sparepart yang akan diedit");
            return;
        }
        try {
            String id = (String) tableModel.getValueAt(row, 0);
            Sparepart sparepart = dataService.getSparepartById(id);
            if (sparepart != null) {
                SparepartDialog dialog = new SparepartDialog((JFrame) SwingUtilities.getWindowAncestor(this), sparepart);
                dialog.setVisible(true);
                if (dialog.isSuccess()) {
                    loadData();
                    JOptionPane.showMessageDialog(this, "Data sparepart berhasil diupdate");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih sparepart yang akan dihapus");
            return;
        }
        try {
            String id = (String) tableModel.getValueAt(row, 0);
            String nama = (String) tableModel.getValueAt(row, 1);
            int confirm = JOptionPane.showConfirmDialog(this, "Hapus sparepart: " + nama + "?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dataService.deleteSparepart(id);
                loadData();
                JOptionPane.showMessageDialog(this, "Data sparepart berhasil dihapus");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStatus(String message) {
        try {
            ((JLabel) ((JPanel) getContentPane().getComponent(2)).getComponent(0)).setText(message);
        } catch (Exception e) {}
    }
}