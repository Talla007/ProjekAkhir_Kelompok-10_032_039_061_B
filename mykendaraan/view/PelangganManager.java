package com.mykendaraan.view;

import com.mykendaraan.model.Pelanggan;
import com.mykendaraan.model.User; // Import User
import com.mykendaraan.service.DataService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class PelangganManager extends JInternalFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private DataService dataService;
    private User currentUser; // Variabel untuk menyimpan user yang login

    public PelangganManager(User user) {
        super("Management Data Member", true, true, true, true);
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
        headerPanel.setBackground(Color.white);

        JLabel titleLabel = new JLabel("MANAGEMENT DATA MEMBER", JLabel.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        titleLabel.setForeground(new Color(70, 130, 180));

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(Color.white);

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
        actionPanel.setBackground(Color.white);

        JButton btnAdd = createButton("Tambah Member", new Color(60, 150, 100));
        JButton btnEdit = createButton("Edit", new Color(70, 130, 180));
        JButton btnDelete = createButton("Hapus", new Color(200, 60, 60));
        JButton btnRefresh = createButton("Refresh", new Color(150, 150, 150));

        // Logika Hak Akses (Role)
        // Jika pelanggan, matikan tombol CRUD
        if ("pelanggan".equals(currentUser.getRole())) {
            btnAdd.setEnabled(false);
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);

            // Tooltip & Style
            String tooltip = "Akses ditolak untuk Member";
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
        String[] columns = {"ID", "Nama", "Telepon", "Email", "Alamat"};
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
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        table.getColumnModel().getColumn(4).setPreferredWidth(250);

        JScrollPane scrollPane = new JScrollPane(table);

        // Status Panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusPanel.setBackground(Color.white);

        JLabel statusLabel = new JLabel("Total: 0 Member");
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

        // Double click to view details
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showDetails();
                }
            }
        });
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Tahoma", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.black);

        // Agar teks tombol yang didisable tetap terbaca (abu-abu)
        UIManager.put("Button.disabledText", Color.GRAY);

        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return button;
    }

    private void loadData() {
        tableModel.setRowCount(0);
        java.util.List<Pelanggan> pelanggans = dataService.getAllPelanggan();
        for (Pelanggan p : pelanggans) {
            tableModel.addRow(p.toTableRow());
        }
        updateStatus("Total: " + pelanggans.size() + " member");
    }

    private void searchData() {
        String keyword = txtSearch.getText().trim();
        tableModel.setRowCount(0);
        java.util.List<Pelanggan> results = dataService.searchPelanggan(keyword);

        for (Pelanggan p : results) {
            tableModel.addRow(p.toTableRow());
        }
        updateStatus("Ditemukan: " + results.size() + " member");
    }

    private void clearSearch() {
        txtSearch.setText("");
        loadData();
    }

    private void showAddDialog() {
        PelangganDialog dialog = new PelangganDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadData();
            JOptionPane.showMessageDialog(this, "Data member berhasil ditambahkan");
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih member yang akan diedit");
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);
        Pelanggan pelanggan = dataService.getPelangganById(id);

        if (pelanggan != null) {
            PelangganDialog dialog = new PelangganDialog((JFrame) SwingUtilities.getWindowAncestor(this), pelanggan);
            dialog.setVisible(true);
            if (dialog.isSuccess()) {
                loadData();
                JOptionPane.showMessageDialog(this, "Data member berhasil diupdate");
            }
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih member yang akan dihapus");
            return;
        }

        String id = (String) tableModel.getValueAt(row, 0);
        String nama = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Hapus member: " + nama + "?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dataService.deletePelanggan(id);
            loadData();
            JOptionPane.showMessageDialog(this, "Data member berhasil dihapus");
        }
    }

    private void showDetails() {
        int row = table.getSelectedRow();
        if (row == -1) return;

        String id = (String) tableModel.getValueAt(row, 0);
        String nama = (String) tableModel.getValueAt(row, 1);
        String telepon = (String) tableModel.getValueAt(row, 2);
        String email = (String) tableModel.getValueAt(row, 3);
        String alamat = (String) tableModel.getValueAt(row, 4);

        JOptionPane.showMessageDialog(this,
                "<html><div style='text-align: left;'>" +
                        "<h3>Detail Member</h3>" +
                        "<b>ID:</b> " + id + "<br>" +
                        "<b>Nama:</b> " + nama + "<br>" +
                        "<b>Telepon:</b> " + telepon + "<br>" +
                        "<b>Email:</b> " + email + "<br>" +
                        "<b>Alamat:</b> " + alamat +
                        "</div></html>",
                "Detail Member",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateStatus(String message) {
        try {
            ((JLabel) ((JPanel) getContentPane().getComponent(2)).getComponent(0)).setText(message);
        } catch (Exception e) {}
    }
}