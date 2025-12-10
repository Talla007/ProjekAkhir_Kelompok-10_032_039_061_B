package com.mykendaraan.view;

import com.mykendaraan.model.User;
import com.mykendaraan.service.UserService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MainMenu extends JFrame {
    private User currentUser;
    private LoginForm loginForm;

    public MainMenu(User user, LoginForm loginForm) {
        this.currentUser = user;
        this.loginForm = loginForm;
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        setTitle("MyKendaraan - Menu Utama");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Gunakan JPanel biasa, bukan JDesktopPane
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        setContentPane(mainPanel);

        createMenuBar();
        createMainMenuPanel();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);

        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setForeground(Color.BLACK);
        fileMenu.setFont(new Font("Tahoma", Font.PLAIN, 12));

        JMenuItem logoutMenuItem = new JMenuItem("Logout");
        JMenuItem exitMenuItem = new JMenuItem("Keluar Aplikasi");

        logoutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmExit();
            }
        });

        fileMenu.add(logoutMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        // User Management Menu (hanya untuk admin)
        if ("admin".equals(currentUser.getRole())) {
            JMenu userMenu = new JMenu("Manajemen User");
            userMenu.setForeground(Color.BLACK);
            userMenu.setFont(new Font("Tahoma", Font.PLAIN, 12));

            JMenuItem manageUsersItem = new JMenuItem("Kelola User");
            JMenuItem addUserItem = new JMenuItem("Tambah User Baru");

            manageUsersItem.addActionListener(e -> showUserManagement());
            addUserItem.addActionListener(e -> showAddUserForm());

            userMenu.add(manageUsersItem);
            userMenu.add(addUserItem);

            menuBar.add(userMenu);

            // Tools Menu untuk admin (opsional)
            JMenu toolsMenu = new JMenu("Tools");
            toolsMenu.setForeground(Color.BLACK);
            toolsMenu.setFont(new Font("Tahoma", Font.PLAIN, 12));

            JMenuItem checkDataItem = new JMenuItem("Cek Database");
            checkDataItem.addActionListener(e -> showDatabaseData());

            toolsMenu.add(checkDataItem);
            menuBar.add(toolsMenu);
        }

        // User info on the right
        menuBar.add(Box.createHorizontalGlue());
        JLabel userLabel = new JLabel("User: " + currentUser.getFullName() + " (" + currentUser.getRole() + ")");
        userLabel.setForeground(Color.BLACK);
        userLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        userLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        menuBar.add(userLabel);

        setJMenuBar(menuBar);
    }

    private void createMainMenuPanel() {
        // Main container panel
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.WHITE);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JLabel welcomeLabel = new JLabel("<html><div style='text-align: center;'>"
                + "<h1 style='color: black; font-size: 36px; margin-bottom: 10px;'>Selamat Datang di MyKendaraan</h1>"
                + "<h2 style='color: black; font-size: 24px; margin-bottom: 10px;'>Dealer Kendaraan & Sparepart</h2>"
                + "<p style='color: black; font-size: 16px;'>Login sebagai: <strong>" + currentUser.getFullName() + "</strong> | Role: <strong>" + currentUser.getRole() + "</strong></p>"
                + "</div></html>", JLabel.CENTER);

        welcomeLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        headerPanel.add(welcomeLabel, BorderLayout.CENTER);

        // === PERUBAHAN 1: Atur layout Grid agar rapi sesuai jumlah menu ===
        int rows, cols;

        if ("pelanggan".equals(currentUser.getRole())) {
            // Jika Pelanggan: Hanya lihat Data Kendaraan, Sparepart, Pelanggan (3 menu)
            rows = 1;
            cols = 3;
        } else {
            // Jika Admin/Karyawan: Lihat semua (6 menu termasuk Laporan)
            rows = 2;
            cols = 3;
        }

        JPanel menuPanel = new JPanel(new GridLayout(rows, cols, 20, 20));
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Buat kotak menu
        JPanel box1 = createMenuBox("Data Kendaraan", "Lihat katalog kendaraan", new Color(70, 130, 180));
        JPanel box2 = createMenuBox("Data Sparepart", "Lihat katalog sparepart", new Color(60, 150, 100));
        JPanel box3 = createMenuBox("Data Pelanggan", "Data akun member", new Color(180, 120, 70));

        // Setup Action Listeners standar
        addMenuBoxAction(box1, e -> openKendaraanManager());
        addMenuBoxAction(box2, e -> openSparepartManager());
        addMenuBoxAction(box3, e -> openPelangganManager());

        // Tambahkan menu dasar (Semua role bisa melihat ini, tapi akses edit dibatasi di dalam manager)
        menuPanel.add(box1);
        menuPanel.add(box2);
        menuPanel.add(box3);

        // === PERUBAHAN 2: Menu Penjualan & Laporan hanya untuk BUKAN pelanggan ===
        if (!"pelanggan".equals(currentUser.getRole())) {

            // Buat menu khusus Admin/Karyawan
            JPanel box4 = createMenuBox("Penjualan Kendaraan", "Proses penjualan kendaraan", new Color(150, 80, 160));
            JPanel box5 = createMenuBox("Penjualan Sparepart", "Proses penjualan sparepart", new Color(200, 100, 50));
            JPanel box6 = createMenuBox("Laporan", "Lihat laporan sistem", new Color(100, 100, 150));

            // Setup Action Listeners
            addMenuBoxAction(box4, e -> openPenjualanKendaraan());
            addMenuBoxAction(box5, e -> openPenjualanSparepart());
            addMenuBoxAction(box6, e -> openLaporan());

            // Tambahkan ke panel
            menuPanel.add(box4);
            menuPanel.add(box5);
            menuPanel.add(box6);
        }

        // Footer panel
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JLabel footerLabel = new JLabel("MyKendaraan v1.0 - © 2025 Jual Beli Kendaraan & Sparepart");
        footerLabel.setForeground(Color.GRAY);
        footerLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        footerPanel.add(footerLabel);

        // Assemble main panel
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        mainContainer.add(menuPanel, BorderLayout.CENTER);
        mainContainer.add(footerPanel, BorderLayout.SOUTH);

        add(mainContainer, BorderLayout.CENTER);
    }

    private JPanel createEmptyBox() {
        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(new Color(240, 240, 240));
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        box.setPreferredSize(new Dimension(250, 200));

        JLabel titleLabel = new JLabel("Fitur Tambahan", JLabel.CENTER);
        titleLabel.setForeground(Color.GRAY);
        titleLabel.setFont(new Font("Tahoma", Font.ITALIC, 14));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel descLabel = new JLabel("<html><div style='text-align: center; color: gray;'>Fitur tersedia untuk role tertentu</div></html>", JLabel.CENTER);
        descLabel.setForeground(Color.GRAY);
        descLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));

        JLabel iconLabel = new JLabel("🔒", JLabel.CENTER);
        iconLabel.setFont(new Font("Tahoma", Font.PLAIN, 36));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        box.add(titleLabel, BorderLayout.NORTH);
        box.add(iconLabel, BorderLayout.CENTER);
        box.add(descLabel, BorderLayout.SOUTH);

        return box;
    }

    private JPanel createMenuBox(String title, String description, Color color) {
        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        box.setCursor(new Cursor(Cursor.HAND_CURSOR));
        box.setPreferredSize(new Dimension(250, 200)); // Set fixed size

        // Title label
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setForeground(color);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Description label
        JLabel descLabel = new JLabel("<html><div style='text-align: center; color: black;'>" + description + "</div></html>", JLabel.CENTER);
        descLabel.setForeground(Color.BLACK);
        descLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));

        // Icon
        JLabel iconLabel = new JLabel(getIconForMenu(title), JLabel.CENTER);
        iconLabel.setFont(new Font("Tahoma", Font.PLAIN, 36));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        box.add(titleLabel, BorderLayout.NORTH);
        box.add(iconLabel, BorderLayout.CENTER);
        box.add(descLabel, BorderLayout.SOUTH);

        return box;
    }

    private String getIconForMenu(String title) {
        switch (title) {
            case "Data Kendaraan": return "🚗";
            case "Data Sparepart": return "🔧";
            case "Data Member": return "👥";
            case "Penjualan Kendaraan": return "💰";
            case "Penjualan Sparepart": return "🛒";
            case "Laporan": return "📊";
            default: return "📁";
        }
    }

    private void addMenuBoxAction(JPanel box, ActionListener action) {
        // Add mouse listener untuk hover effect dan click
        box.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(new ActionEvent(box, ActionEvent.ACTION_PERFORMED, "click"));
            }

            public void mouseEntered(MouseEvent e) {
                box.setBackground(new Color(245, 245, 245));
                box.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(((JLabel)((BorderLayout)box.getLayout()).getLayoutComponent(BorderLayout.NORTH)).getForeground(), 3),
                        BorderFactory.createEmptyBorder(19, 19, 19, 19)
                ));
            }

            public void mouseExited(MouseEvent e) {
                box.setBackground(Color.WHITE);
                Color originalColor = ((JLabel)((BorderLayout)box.getLayout()).getLayoutComponent(BorderLayout.NORTH)).getForeground();
                box.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(originalColor, 2),
                        BorderFactory.createEmptyBorder(20, 20, 20, 20)
                ));
            }
        });
    }

    private void openKendaraanManager() {
        try {
            KendaraanManager manager = new KendaraanManager(currentUser);

            JFrame managerFrame = new JFrame("Data Kendaraan");
            managerFrame.setContentPane(manager.getContentPane());
            managerFrame.setSize(1020, 620);
            managerFrame.setLocationRelativeTo(this);
            managerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            managerFrame.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error membuka Data Kendaraan: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openSparepartManager() {
        try {
            SparepartManager manager = new SparepartManager(currentUser);

            JFrame managerFrame = new JFrame("Data Sparepart");
            managerFrame.setContentPane(manager.getContentPane());
            managerFrame.setSize(1020, 620);
            managerFrame.setLocationRelativeTo(this);
            managerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            managerFrame.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error membuka Data Sparepart: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openPelangganManager() {
        try {
            PelangganManager manager = new PelangganManager(currentUser);

            JFrame managerFrame = new JFrame("Data Member");
            managerFrame.setContentPane(manager.getContentPane());
            managerFrame.setSize(1020, 620);
            managerFrame.setLocationRelativeTo(this);
            managerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            managerFrame.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error membuka Data Member: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openPenjualanKendaraan() {
        try {
            PenjualanKendaraan penjualan = new PenjualanKendaraan();
            JFrame penjualanFrame = new JFrame("Penjualan Kendaraan");
            penjualanFrame.setContentPane(penjualan.getContentPane());
            penjualanFrame.setSize(1120, 720);
            penjualanFrame.setLocationRelativeTo(this);
            penjualanFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            penjualanFrame.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error membuka Penjualan Kendaraan: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openPenjualanSparepart() {
        try {
            PenjualanSparepart penjualan = new PenjualanSparepart();
            JFrame penjualanFrame = new JFrame("Penjualan Sparepart");
            penjualanFrame.setContentPane(penjualan.getContentPane());
            penjualanFrame.setSize(1120, 720);
            penjualanFrame.setLocationRelativeTo(this);
            penjualanFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            penjualanFrame.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error membuka Penjualan Sparepart: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openLaporan() {
        // Validasi tambahan untuk memastikan pelanggan tidak bisa akses
        if ("pelanggan".equals(currentUser.getRole())) {
            JOptionPane.showMessageDialog(this,
                    "Akses ditolak! Fitur laporan hanya tersedia untuk admin dan karyawan.",
                    "Akses Ditolak",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            JFrame laporanFrame = new JFrame("Laporan Sistem");
            laporanFrame.setSize(1200, 700);
            laporanFrame.setLocationRelativeTo(this);
            laporanFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JTabbedPane tabbedPane = new JTabbedPane();

            // Tab Laporan Dasar
            LaporanManager laporanManager = new LaporanManager();
            tabbedPane.addTab("Laporan Penjualan", laporanManager.getContentPane());

            // Tab Analisis
            LaporanAnalisis laporanAnalisis = new LaporanAnalisis();
            tabbedPane.addTab("Analisis & Statistik", laporanAnalisis.getContentPane());

            laporanFrame.add(tabbedPane);
            laporanFrame.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error membuka Laporan: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showUserManagement() {
        if (!"admin".equals(currentUser.getRole())) {
            JOptionPane.showMessageDialog(this,
                    "Akses ditolak! Fitur manajemen user hanya tersedia untuk administrator.",
                    "Akses Ditolak",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            UserService userService = new UserService();
            List<User> users = userService.getAllUsers();

            JFrame userFrame = new JFrame("Manajemen User");
            userFrame.setSize(700, 500);
            userFrame.setLocationRelativeTo(this);
            userFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            // Table model
            String[] columnNames = {"ID", "Username", "Nama Lengkap", "Role", "Dibuat"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (User user : users) {
                Object[] row = {
                        user.getId(),
                        user.getUsername(),
                        user.getFullName(),
                        user.getRole(),
                        user.getCreatedAt()
                };
                model.addRow(row);
            }

            JTable userTable = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(userTable);

            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton editButton = new JButton("Edit User");
            JButton deleteButton = new JButton("Hapus User");
            JButton refreshButton = new JButton("Refresh");
            JButton closeButton = new JButton("Tutup");

            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedRow = userTable.getSelectedRow();
                    if (selectedRow != -1) {
                        int userId = (int) model.getValueAt(selectedRow, 0);
                        showEditUserForm(userId);
                    } else {
                        JOptionPane.showMessageDialog(userFrame,
                                "Pilih user terlebih dahulu!", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                }
            });

            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedRow = userTable.getSelectedRow();
                    if (selectedRow != -1) {
                        int userId = (int) model.getValueAt(selectedRow, 0);
                        String username = (String) model.getValueAt(selectedRow, 1);

                        // Tidak boleh hapus user sendiri
                        if (userId == currentUser.getId()) {
                            JOptionPane.showMessageDialog(userFrame,
                                    "Tidak dapat menghapus user yang sedang login!",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        int confirm = JOptionPane.showConfirmDialog(userFrame,
                                "Hapus user '" + username + "'? Tindakan ini tidak dapat dibatalkan!",
                                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                        if (confirm == JOptionPane.YES_OPTION) {
                            UserService service = new UserService();
                            if (service.deleteUser(userId)) {
                                JOptionPane.showMessageDialog(userFrame,
                                        "User berhasil dihapus!", "Success", JOptionPane.INFORMATION_MESSAGE);
                                userFrame.dispose();
                                showUserManagement(); // Refresh
                            } else {
                                JOptionPane.showMessageDialog(userFrame,
                                        "Gagal menghapus user!", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(userFrame,
                                "Pilih user terlebih dahulu!", "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                }
            });

            refreshButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    userFrame.dispose();
                    showUserManagement();
                }
            });

            closeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    userFrame.dispose();
                }
            });

            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(refreshButton);
            buttonPanel.add(closeButton);

            userFrame.add(scrollPane, BorderLayout.CENTER);
            userFrame.add(buttonPanel, BorderLayout.SOUTH);
            userFrame.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showAddUserForm() {
        JDialog addUserDialog = new JDialog(this, "Tambah User Baru", true);
        addUserDialog.setSize(400, 350);
        addUserDialog.setLocationRelativeTo(this);
        addUserDialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        final JTextField usernameField = new JTextField();
        final JPasswordField passwordField = new JPasswordField();
        final JPasswordField confirmPasswordField = new JPasswordField();
        final JTextField fullNameField = new JTextField();
        final JComboBox<String> roleCombo = new JComboBox<>(new String[]{"admin", "karyawan", "member"});

        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Konfirmasi Password:"));
        formPanel.add(confirmPasswordField);
        formPanel.add(new JLabel("Nama Lengkap:"));
        formPanel.add(fullNameField);
        formPanel.add(new JLabel("Role:"));
        formPanel.add(roleCombo);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Simpan");
        JButton cancelButton = new JButton("Batal");

        saveButton.setBackground(new Color(60, 179, 113));
        saveButton.setForeground(Color.black);
        cancelButton.setBackground(new Color(192, 80, 77));
        cancelButton.setForeground(Color.black);

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                String fullName = fullNameField.getText().trim();
                String role = (String) roleCombo.getSelectedItem();

                // Validasi
                if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
                    JOptionPane.showMessageDialog(addUserDialog,
                            "Semua field harus diisi!", "Validasi Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(addUserDialog,
                            "Password dan konfirmasi password tidak sama!",
                            "Validasi Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (username.length() < 3) {
                    JOptionPane.showMessageDialog(addUserDialog,
                            "Username minimal 3 karakter!", "Validasi Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                UserService userService = new UserService();
                if (userService.isUsernameExists(username)) {
                    JOptionPane.showMessageDialog(addUserDialog,
                            "Username sudah digunakan!", "Validasi Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Simpan user baru
                User newUser = new User(username, password, role, fullName);
                if (userService.addUser(newUser)) {
                    JOptionPane.showMessageDialog(addUserDialog,
                            "User berhasil ditambahkan!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    addUserDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(addUserDialog,
                            "Gagal menambahkan user!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addUserDialog.dispose();
            }
        });

        addUserDialog.add(formPanel, BorderLayout.CENTER);
        addUserDialog.add(buttonPanel, BorderLayout.SOUTH);
        addUserDialog.setVisible(true);
    }

    private void showEditUserForm(final int userId) {
        UserService userService = new UserService();
        List<User> users = userService.getAllUsers();
        final User userToEdit;

        User tempUser = null;
        for (User user : users) {
            if (user.getId() == userId) {
                tempUser = user;
                break;
            }
        }
        userToEdit = tempUser;

        if (userToEdit == null) {
            JOptionPane.showMessageDialog(this, "User tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final JDialog editUserDialog = new JDialog(this, "Edit User", true);
        editUserDialog.setSize(400, 350);
        editUserDialog.setLocationRelativeTo(this);
        editUserDialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        final JTextField usernameField = new JTextField(userToEdit.getUsername());
        final JPasswordField passwordField = new JPasswordField();
        final JPasswordField confirmPasswordField = new JPasswordField();
        final JTextField fullNameField = new JTextField(userToEdit.getFullName());
        final JComboBox<String> roleCombo = new JComboBox<>(new String[]{"admin", "karyawan", "member"});
        roleCombo.setSelectedItem(userToEdit.getRole());

        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password (kosongkan jika tidak diubah):"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Konfirmasi Password:"));
        formPanel.add(confirmPasswordField);
        formPanel.add(new JLabel("Nama Lengkap:"));
        formPanel.add(fullNameField);
        formPanel.add(new JLabel("Role:"));
        formPanel.add(roleCombo);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Simpan Perubahan");
        JButton cancelButton = new JButton("Batal");

        saveButton.setBackground(new Color(60, 179, 113));
        saveButton.setForeground(Color.black);
        cancelButton.setBackground(new Color(192, 80, 77));
        cancelButton.setForeground(Color.black);

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                String fullName = fullNameField.getText().trim();
                String role = (String) roleCombo.getSelectedItem();

                // Validasi
                if (username.isEmpty() || fullName.isEmpty()) {
                    JOptionPane.showMessageDialog(editUserDialog,
                            "Username dan nama lengkap harus diisi!", "Validasi Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (!password.isEmpty() && !password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(editUserDialog,
                            "Password dan konfirmasi password tidak sama!",
                            "Validasi Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                UserService service = new UserService();

                // Cek username jika diubah
                if (!username.equals(userToEdit.getUsername()) && service.isUsernameExists(username)) {
                    JOptionPane.showMessageDialog(editUserDialog,
                            "Username sudah digunakan!", "Validasi Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Update user
                userToEdit.setUsername(username);
                userToEdit.setFullName(fullName);
                userToEdit.setRole(role);

                // Update password hanya jika diisi
                if (!password.isEmpty()) {
                    userToEdit.setPassword(password);
                }

                if (service.updateUser(userToEdit)) {
                    JOptionPane.showMessageDialog(editUserDialog,
                            "User berhasil diupdate!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    editUserDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(editUserDialog,
                            "Gagal mengupdate user!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editUserDialog.dispose();
            }
        });

        editUserDialog.add(formPanel, BorderLayout.CENTER);
        editUserDialog.add(buttonPanel, BorderLayout.SOUTH);
        editUserDialog.setVisible(true);
    }

    // === DATABASE CHECK METHOD ===

    private void showDatabaseData() {
        if (!"admin".equals(currentUser.getRole())) {
            JOptionPane.showMessageDialog(this,
                    "Akses ditolak! Fitur ini hanya tersedia untuk administrator.",
                    "Akses Ditolak",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            JFrame dataFrame = new JFrame("Data Database");
            dataFrame.setSize(800, 600);
            dataFrame.setLocationRelativeTo(this);

            JTabbedPane tabbedPane = new JTabbedPane();

            // Tab Users
            tabbedPane.addTab("Users", createUserDataPanel());

            dataFrame.add(tabbedPane);
            dataFrame.setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createUserDataPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"ID", "Username", "Full Name", "Role", "Created At"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        try (java.sql.Connection conn = com.mykendaraan.database.DatabaseConnection.getConnection();
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getString("role"),
                        rs.getTimestamp("created_at")
                };
                model.addRow(row);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Apakah Anda yakin ingin logout?",
                "Konfirmasi Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this,
                    "Anda telah logout dari sistem",
                    "Logout Berhasil",
                    JOptionPane.INFORMATION_MESSAGE);

            loginForm.showLoginForm();
            dispose();
        }
    }

    private void confirmExit() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Apakah Anda yakin ingin keluar dari aplikasi?",
                "Konfirmasi Keluar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}