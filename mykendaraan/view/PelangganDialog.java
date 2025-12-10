package com.mykendaraan.view;

import com.mykendaraan.model.Pelanggan;
import com.mykendaraan.service.DataService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PelangganDialog extends JDialog {
    private JTextField txtId, txtNama, txtTelepon, txtEmail, txtAlamat;
    private JButton btnSave, btnCancel;
    private boolean success = false;
    private Pelanggan existingPelanggan;
    private DataService dataService;

    public PelangganDialog(JFrame parent, Pelanggan pelanggan) {
        super(parent, pelanggan == null ? "Tambah Member" : "Edit Member", true);
        this.existingPelanggan = pelanggan;
        this.dataService = DataService.getInstance();
        initComponents();
        if (pelanggan != null) {
            populateFields(pelanggan);
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
        formPanel.add(new JLabel("ID Pelanggan:"));
        txtId = new JTextField();
        if (existingPelanggan == null) {
            txtId.setText(dataService.getAllPelanggan().toString());
            txtId.setEditable(false);
            txtId.setBackground(new Color(240, 240, 240));
        } else {
            txtId.setText(existingPelanggan.getId());
            txtId.setEditable(false);
        }
        formPanel.add(txtId);

        formPanel.add(new JLabel("Nama:"));
        txtNama = new JTextField();
        formPanel.add(txtNama);

        formPanel.add(new JLabel("Telepon:"));
        txtTelepon = new JTextField();
        formPanel.add(txtTelepon);

        formPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        formPanel.add(txtEmail);

        formPanel.add(new JLabel("Alamat:"));
        txtAlamat = new JTextField();
        formPanel.add(txtAlamat);

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
                savePelanggan();
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

    private void populateFields(Pelanggan pelanggan) {
        txtId.setText(pelanggan.getId());
        txtNama.setText(pelanggan.getNama());
        txtTelepon.setText(pelanggan.getTelepon());
        txtEmail.setText(pelanggan.getEmail());
        txtAlamat.setText(pelanggan.getAlamat());
    }

    private void savePelanggan() {
        try {
            // Validation
            if (txtNama.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama Member harus diisi");
                return;
            }

            if (txtTelepon.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Telepon Member harus diisi");
                return;
            }

            Pelanggan pelanggan = new Pelanggan(
                    txtId.getText().trim(),
                    txtNama.getText().trim(),
                    txtAlamat.getText().trim(),
                    txtTelepon.getText().trim(),
                    txtEmail.getText().trim()
            );

            if (existingPelanggan == null) {
                dataService.addPelanggan(pelanggan);
            } else {
                dataService.updatePelanggan(existingPelanggan.getId(), pelanggan);
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