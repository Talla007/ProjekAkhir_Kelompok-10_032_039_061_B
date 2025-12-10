package com.mykendaraan.view;

import com.mykendaraan.model.User;
import com.mykendaraan.service.UserService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private UserService userService;

    public LoginForm() {
        userService = new UserService();
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        setTitle("MyKendaraan - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        Font labelFont = new Font("Tahoma", Font.PLAIN, 16); // Font label
        Font inputFont = new Font("Tahoma", Font.PLAIN, 16); // Font isian

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        mainPanel.setBackground(Color.WHITE);

        // Header
        JLabel headerLabel = new JLabel("MY KENDARAAN", JLabel.CENTER);
        headerLabel.setFont(new Font("Tahoma", Font.BOLD, 32));
        headerLabel.setForeground(new Color(70, 130, 180));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Login form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Login Sistem"));

        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(labelFont); // Set Font Besar
        formPanel.add(lblUser);

        usernameField = new JTextField();
        usernameField.setFont(inputFont); // Set Font Besar
        formPanel.add(usernameField);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(labelFont); // Set Font Besar
        formPanel.add(lblPass);

        passwordField = new JPasswordField();
        passwordField.setFont(inputFont); // Set Font Besar
        formPanel.add(passwordField);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        loginButton = new JButton("Login");
        registerButton = new JButton("Daftar Baru");

        loginButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(120, 40));
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);

        registerButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        registerButton.setPreferredSize(new Dimension(140, 40));
        registerButton.setBackground(new Color(60, 179, 113));
        registerButton.setForeground(Color.black);
        registerButton.setFocusPainted(false);

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        // Add action listeners
        loginButton.addActionListener(e -> performLogin());
        registerButton.addActionListener(e -> showRegistrationForm());

        // Enter key untuk login
        passwordField.addActionListener(e -> performLogin());

        // Footer
        JLabel footerLabel = new JLabel("© 2025 MyKendaraan - Jual Beli Kendaraan & Sparepart", JLabel.CENTER);
        footerLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        footerLabel.setForeground(Color.GRAY);
        footerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        // Assemble main panel
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
        add(footerLabel, BorderLayout.SOUTH);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username dan password harus diisi!",
                    "Validasi Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            User user = userService.authenticate(username, password);

            if (user != null) {
                JOptionPane.showMessageDialog(this,
                        "Login berhasil! Selamat datang " + user.getFullName(),
                        "Login Success", JOptionPane.INFORMATION_MESSAGE);

                // Buka main menu
                openMainMenu(user);

            } else {
                JOptionPane.showMessageDialog(this,
                        "Username atau password salah!",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage() + "\nPastikan database berjalan!",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRegistrationForm() {
        JDialog registerDialog = new JDialog(this, "Registrasi User Baru", true);
        registerDialog.setSize(400, 320); // Tinggi dikurangi sedikit karena kolom berkurang
        registerDialog.setLocationRelativeTo(this);
        registerDialog.setLayout(new BorderLayout());

        // UBAH GRID LAYOUT: Dari (5, 2) menjadi (4, 2) karena Role dihapus
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField regUsernameField = new JTextField();
        JPasswordField regPasswordField = new JPasswordField();
        JPasswordField regConfirmPasswordField = new JPasswordField();
        JTextField regFullNameField = new JTextField();

        // HAPUS: JComboBox roleCombo tidak lagi dibuat

        formPanel.add(new JLabel("Username:"));
        formPanel.add(regUsernameField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(regPasswordField);
        formPanel.add(new JLabel("Konfirmasi Password:"));
        formPanel.add(regConfirmPasswordField);
        formPanel.add(new JLabel("Nama Lengkap:"));
        formPanel.add(regFullNameField);

        // HAPUS: Label dan Input Role tidak dimasukkan ke formPanel

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Daftar Sekarang");
        JButton cancelButton = new JButton("Batal");

        saveButton.setBackground(new Color(60, 179, 113));
        saveButton.setForeground(Color.BLACK);
        cancelButton.setBackground(new Color(192, 80, 77));
        cancelButton.setForeground(Color.BLACK);

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        saveButton.addActionListener(e -> {
            String username = regUsernameField.getText().trim();
            String password = new String(regPasswordField.getPassword());
            String confirmPassword = new String(regConfirmPasswordField.getPassword());
            String fullName = regFullNameField.getText().trim();

            // LOGIKA BARU: Role ditentukan otomatis oleh sistem
            String role = "member"; // Default otomatis "member" (atau ganti "pelanggan")

            // Validasi
            if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
                JOptionPane.showMessageDialog(registerDialog,
                        "Semua field harus diisi!", "Validasi Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(registerDialog,
                        "Password dan konfirmasi password tidak sama!",
                        "Validasi Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (username.length() < 3) {
                JOptionPane.showMessageDialog(registerDialog,
                        "Username minimal 3 karakter!", "Validasi Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (userService.isUsernameExists(username)) {
                JOptionPane.showMessageDialog(registerDialog,
                        "Username sudah digunakan!", "Validasi Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Simpan user baru dengan role default
            User newUser = new User(username, password, role, fullName);

            if (userService.addUser(newUser)) {
                JOptionPane.showMessageDialog(registerDialog,
                        "Registrasi Berhasil! Silakan login sebagai Member.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                registerDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(registerDialog,
                        "Gagal mendaftarkan user!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> registerDialog.dispose());

        registerDialog.add(formPanel, BorderLayout.CENTER);
        registerDialog.add(buttonPanel, BorderLayout.SOUTH);
        registerDialog.setVisible(true);
    }

    private void openMainMenu(User user) {
        // Close login form
        dispose();

        // Open main menu
        MainMenu mainMenu = new MainMenu(user, this);
        mainMenu.setVisible(true);
    }

    public void showLoginForm() {
        usernameField.setText("");
        passwordField.setText("");
        setVisible(true);
    }

    // Main method
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new LoginForm();
        });
    }
}