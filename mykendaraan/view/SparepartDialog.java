package com.mykendaraan.view;

import com.mykendaraan.model.Sparepart;
import com.mykendaraan.service.DataService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SparepartDialog extends JDialog {
    private JTextField txtId, txtNama, txtKategori, txtMerk, txtTipe, txtHarga, txtStok, txtStokMin;
    private JButton btnSave, btnCancel;
    private boolean success = false;
    private Sparepart existingSparepart;
    private DataService dataService;

    public SparepartDialog(JFrame parent, Sparepart sparepart) {
        super(parent, sparepart == null ? "Tambah Sparepart" : "Edit Sparepart", true);
        this.existingSparepart = sparepart;
        this.dataService = DataService.getInstance();
        initComponents();
        if (sparepart != null) {
            populateFields(sparepart);
        }
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Form fields
        formPanel.add(new JLabel("ID Sparepart:"));
        txtId = new JTextField();
        if (existingSparepart == null) {
            txtId.setText(dataService.getNextSparepartId());
            txtId.setEditable(false);
            txtId.setBackground(new Color(240, 240, 240));
        } else {
            txtId.setText(existingSparepart.getId());
            txtId.setEditable(false);
        }
        formPanel.add(txtId);

        formPanel.add(new JLabel("Nama Sparepart:"));
        txtNama = new JTextField();
        formPanel.add(txtNama);

        formPanel.add(new JLabel("Kategori:"));
        txtKategori = new JTextField();
        formPanel.add(txtKategori);

        formPanel.add(new JLabel("Merk:"));
        txtMerk = new JTextField();
        formPanel.add(txtMerk);

        formPanel.add(new JLabel("Tipe Kendaraan:"));
        txtTipe = new JTextField();
        formPanel.add(txtTipe);

        formPanel.add(new JLabel("Harga:"));
        txtHarga = new JTextField();
        formPanel.add(txtHarga);

        formPanel.add(new JLabel("Stok:"));
        txtStok = new JTextField();
        formPanel.add(txtStok);

        formPanel.add(new JLabel("Stok Minimal:"));
        txtStokMin = new JTextField();
        formPanel.add(txtStokMin);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        btnSave = new JButton("Simpan");
        btnCancel = new JButton("Batal");

        btnSave.setBackground(new Color(60, 150, 100));
        btnCancel.setBackground(new Color(200, 60, 60));
        btnSave.setForeground(Color.BLACK);
        btnCancel.setForeground(Color.BLACK);
        btnSave.setFocusPainted(false);
        btnCancel.setFocusPainted(false);

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSparepart();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        getRootPane().setDefaultButton(btnSave);

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void populateFields(Sparepart sparepart) {
        txtId.setText(sparepart.getId());
        txtNama.setText(sparepart.getNama());
        txtKategori.setText(sparepart.getKategori());
        txtMerk.setText(sparepart.getMerk());
        txtTipe.setText(sparepart.getTipeKendaraan());
        txtHarga.setText(String.valueOf(sparepart.getHarga()));
        txtStok.setText(String.valueOf(sparepart.getStok()));
        txtStokMin.setText(String.valueOf(sparepart.getStokMinimal()));
    }

    private void saveSparepart() {
        try {
            // Validation
            if (txtNama.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama sparepart harus diisi");
                return;
            }

            double harga;
            int stok, stokMin;

            try {
                harga = Double.parseDouble(txtHarga.getText().trim());
                stok = Integer.parseInt(txtStok.getText().trim());
                stokMin = Integer.parseInt(txtStokMin.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Harga, stok, dan stok minimal harus angka");
                return;
            }

            Sparepart sparepart = new Sparepart(
                    txtId.getText().trim(),
                    txtNama.getText().trim(),
                    txtKategori.getText().trim(),
                    txtMerk.getText().trim(),
                    txtTipe.getText().trim(),
                    harga,
                    stok,
                    stokMin
            );

            if (existingSparepart == null) {
                dataService.addSparepart(sparepart);
            } else {
                dataService.updateSparepart(existingSparepart.getId(), sparepart);
            }

            success = true;
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    public boolean isSuccess() {
        return success;
    }
}