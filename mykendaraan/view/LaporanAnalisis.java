package com.mykendaraan.view;

import com.mykendaraan.model.*;
import com.mykendaraan.service.DataService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class LaporanAnalisis extends JPanel {
    private DataService dataService;
    private SimpleDatePicker tanggalAwalPicker;
    private SimpleDatePicker tanggalAkhirPicker;

    public LaporanAnalisis() {
        dataService = DataService.getInstance();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel filter tanggal
        JPanel filterPanel = createFilterPanel();

        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab Analisis Penjualan
        tabbedPane.addTab("Dashboard Metrik", createAnalisisPanel());

        // Tab Statistik Produk
        tabbedPane.addTab("Statistik Produk", createStatistikPanel());

        // Tab Grafik
        tabbedPane.addTab("Grafik Penjualan", createGrafikPanel());

        add(filterPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Filter Periode Analisis"));

        // Tanggal Awal
        panel.add(new JLabel("Tanggal Awal:"));
        tanggalAwalPicker = new SimpleDatePicker();
        panel.add(tanggalAwalPicker);

        // Tanggal Akhir
        panel.add(new JLabel("Tanggal Akhir:"));
        tanggalAkhirPicker = new SimpleDatePicker();
        panel.add(tanggalAkhirPicker);

        // Tombol Terapkan
        JButton terapkanBtn = new JButton("Terapkan Filter");
        terapkanBtn.addActionListener(e -> refreshAnalisis());
        panel.add(terapkanBtn);

        // Set tanggal default (bulan ini)
        setDefaultDates();

        return panel;
    }

    private void setDefaultDates() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        tanggalAwalPicker.setDate(cal.getTime());

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        tanggalAkhirPicker.setDate(cal.getTime());
    }

    private void refreshAnalisis() {
        // Refresh semua tab
        JTabbedPane tabbedPane = (JTabbedPane) getComponent(1);
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            Component comp = tabbedPane.getComponentAt(i);
            if (comp instanceof JPanel) {
                comp.revalidate();
                comp.repaint();
            }
        }
    }

    private JPanel createAnalisisPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Hitung berbagai metrik dengan filter tanggal
        Date startDate = tanggalAwalPicker.getDate();
        Date endDate = tanggalAkhirPicker.getDate();
        Map<String, Object> metrics = calculateMetrics(startDate, endDate);

        panel.add(createMetricCard("Total Pendapatan",
                formatCurrency((Double)metrics.get("totalRevenue")), new Color(70, 130, 180)));
        panel.add(createMetricCard("Total Transaksi",
                metrics.get("totalTransactions") + " transaksi", new Color(60, 179, 113)));
        panel.add(createMetricCard("Rata-rata per Transaksi",
                formatCurrency((Double)metrics.get("avgTransaction")), new Color(255, 165, 0)));
        panel.add(createMetricCard("Produk Terlaris",
                (String)metrics.get("bestSeller"), new Color(220, 20, 60)));
        panel.add(createMetricCard("Penjualan Kendaraan",
                metrics.get("vehicleSales") + " unit", new Color(0, 206, 209)));
        panel.add(createMetricCard("Penjualan Sparepart",
                metrics.get("sparepartSales") + " item", new Color(186, 85, 211)));
        panel.add(createMetricCard("Metode Pembayaran Terpopuler",
                (String)metrics.get("popularPayment"), new Color(255, 182, 193)));
        panel.add(createMetricCard("Pelanggan Aktif",
                metrics.get("activeCustomers") + " pelanggan", new Color(255, 215, 0)));

        return panel;
    }

    private JPanel createMetricCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 3),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(Color.WHITE);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(245, 245, 245));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(Color.WHITE);
            }
        });

        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        titleLabel.setForeground(color);

        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        valueLabel.setForeground(Color.BLACK);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createStatistikPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Tampilkan statistik dalam text area
        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        Date startDate = tanggalAwalPicker.getDate();
        Date endDate = tanggalAkhirPicker.getDate();

        StringBuilder stats = new StringBuilder();
        stats.append("STATISTIK PENJUALAN DETAIL\n");
        stats.append("==========================\n\n");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        if (startDate != null && endDate != null) {
            stats.append("Periode: ").append(dateFormat.format(startDate))
                    .append(" s/d ").append(dateFormat.format(endDate)).append("\n\n");
        }

        // Statistik kendaraan
        stats.append("PENJUALAN KENDARAAN:\n");
        stats.append("--------------------\n");
        int totalKendaraan = 0;
        for (Kendaraan k : dataService.getAllKendaraan()) {
            int terjual = hitungTerjual(k.getId(), "kendaraan", startDate, endDate);
            if (terjual > 0) {
                stats.append(String.format("- %s %s: %d unit terjual\n",
                        k.getMerk(), k.getModel(), terjual));
                totalKendaraan += terjual;
            }
        }
        stats.append(String.format("TOTAL: %d unit\n\n", totalKendaraan));

        // Statistik sparepart
        stats.append("PENJUALAN SPAREPART:\n");
        stats.append("--------------------\n");
        int totalSparepart = 0;
        for (Sparepart s : dataService.getAllSparepart()) {
            int terjual = hitungTerjual(s.getId(), "sparepart", startDate, endDate);
            if (terjual > 0) {
                stats.append(String.format("- %s: %d item terjual\n", s.getNama(), terjual));
                totalSparepart += terjual;
            }
        }
        stats.append(String.format("TOTAL: %d item\n\n", totalSparepart));

        stats.append("RINGKASAN:\n");
        stats.append("----------\n");
        Map<String, Object> metrics = calculateMetrics(startDate, endDate);
        stats.append(String.format("Total Pendapatan: %s\n", formatCurrency((Double)metrics.get("totalRevenue"))));
        stats.append(String.format("Total Transaksi: %d\n", (Integer)metrics.get("totalTransactions")));
        stats.append(String.format("Pelanggan Unik: %d\n", (Integer)metrics.get("activeCustomers")));
        stats.append(String.format("Rata-rata per Transaksi: %s\n", formatCurrency((Double)metrics.get("avgTransaction"))));

        statsArea.setText(stats.toString());

        JScrollPane scrollPane = new JScrollPane(statsArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createGrafikPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create tabbed pane for different chart types
        JTabbedPane chartTabs = new JTabbedPane();

        // Tab 1: Pie Chart - Rasio Penjualan
        chartTabs.addTab("Rasio Penjualan", createPieChartPanel());

        // Tab 2: Bar Chart - Penjualan per Produk
        chartTabs.addTab("Top Produk", createBarChartPanel());

        // Tab 3: Line Chart - Trend Bulanan
        chartTabs.addTab("Trend Bulanan", createLineChartPanel());

        // Tab 4: Metode Pembayaran
        chartTabs.addTab("Metode Pembayaran", createPaymentChartPanel());

        panel.add(chartTabs, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPieChartPanel() {
        Date startDate = tanggalAwalPicker.getDate();
        Date endDate = tanggalAkhirPicker.getDate();

        DefaultPieDataset dataset = new DefaultPieDataset();

        // Hitung total penjualan kendaraan dan sparepart
        double totalKendaraan = 0;
        double totalSparepart = 0;

        List<Penjualan> penjualanList = dataService.getAllPenjualan();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        for (Penjualan p : penjualanList) {
            try {
                // Filter berdasarkan tanggal
                if (startDate != null && endDate != null) {
                    String tanggalStr = p.getTanggal().substring(0, 10);
                    Date tanggalPenjualan = dateFormat.parse(tanggalStr);

                    if (tanggalPenjualan.before(startDate) || tanggalPenjualan.after(endDate)) {
                        continue;
                    }
                }

                for (PenjualanItem item : p.getItems()) {
                    if ("kendaraan".equals(item.getTipe())) {
                        totalKendaraan += item.getSubtotal();
                    } else if ("sparepart".equals(item.getTipe())) {
                        totalSparepart += item.getSubtotal();
                    }
                }
            } catch (Exception e) {
                System.err.println("Error processing transaction: " + e.getMessage());
            }
        }

        dataset.setValue("Kendaraan (" + formatCurrencyShort(totalKendaraan) + ")", totalKendaraan);
        dataset.setValue("Sparepart (" + formatCurrencyShort(totalSparepart) + ")", totalSparepart);

        JFreeChart chart = ChartFactory.createPieChart(
                "RASIO PENJUALAN KENDARAAN vs SPAREPART",
                dataset,
                true,
                true,
                false
        );

        // Customize pie chart
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Kendaraan (" + formatCurrencyShort(totalKendaraan) + ")", new Color(79, 129, 189));
        plot.setSectionPaint("Sparepart (" + formatCurrencyShort(totalSparepart) + ")", new Color(192, 80, 77));
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
        plot.setNoDataMessage("Data tidak tersedia");
        plot.setCircular(true);
        plot.setLabelGap(0.02);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBarChartPanel() {
        Date startDate = tanggalAwalPicker.getDate();
        Date endDate = tanggalAkhirPicker.getDate();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Kumpulkan data penjualan per produk
        Map<String, Double> productSales = new HashMap<>();
        Map<String, String> productTypes = new HashMap<>();

        List<Penjualan> penjualanList = dataService.getAllPenjualan();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        for (Penjualan p : penjualanList) {
            try {
                // Filter berdasarkan tanggal
                if (startDate != null && endDate != null) {
                    String tanggalStr = p.getTanggal().substring(0, 10);
                    Date tanggalPenjualan = dateFormat.parse(tanggalStr);

                    if (tanggalPenjualan.before(startDate) || tanggalPenjualan.after(endDate)) {
                        continue;
                    }
                }

                for (PenjualanItem item : p.getItems()) {
                    String productName = getItemName(item);
                    double subtotal = item.getSubtotal();

                    productSales.put(productName, productSales.getOrDefault(productName, 0.0) + subtotal);
                    productTypes.put(productName, item.getTipe());
                }
            } catch (Exception e) {
                System.err.println("Error processing transaction: " + e.getMessage());
            }
        }

        // Sort by sales value (top 10)
        List<Map.Entry<String, Double>> sortedProducts = new ArrayList<>(productSales.entrySet());
        sortedProducts.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        // Add top 10 products to dataset
        int count = 0;
        for (Map.Entry<String, Double> entry : sortedProducts) {
            if (count >= 10) break;

            String productName = entry.getKey();
            if (productName.length() > 20) {
                productName = productName.substring(0, 17) + "...";
            }

            String productType = "kendaraan".equals(productTypes.get(entry.getKey())) ? "Kendaraan" : "Sparepart";
            dataset.addValue(entry.getValue(), productType, productName);
            count++;
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "TOP 10 PRODUK TERLARIS (BERDASARKAN PENDAPATAN)",
                "Produk",
                "Pendapatan (Rp)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Customize bar chart
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(79, 129, 189)); // Kendaraan
        renderer.setSeriesPaint(1, new Color(192, 80, 77));  // Sparepart

        // Format y-axis as currency
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setNumberFormatOverride(java.text.NumberFormat.getCurrencyInstance());

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLineChartPanel() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Group sales by month
        Map<String, Double> monthlySales = new TreeMap<>();
        Map<String, Integer> monthlyTransactions = new TreeMap<>();

        List<Penjualan> penjualanList = dataService.getAllPenjualan();

        for (Penjualan p : penjualanList) {
            try {
                String tanggalStr = p.getTanggal().substring(0, 10);
                String monthKey = tanggalStr.substring(3, 10); // MM-yyyy

                double totalSale = 0;
                for (PenjualanItem item : p.getItems()) {
                    totalSale += item.getSubtotal();
                }

                monthlySales.put(monthKey, monthlySales.getOrDefault(monthKey, 0.0) + totalSale);
                monthlyTransactions.put(monthKey, monthlyTransactions.getOrDefault(monthKey, 0) + 1);

            } catch (Exception e) {
                System.err.println("Error processing transaction: " + e.getMessage());
            }
        }

        // Add to dataset
        for (Map.Entry<String, Double> entry : monthlySales.entrySet()) {
            dataset.addValue(entry.getValue(), "Pendapatan", entry.getKey());
        }

        for (Map.Entry<String, Integer> entry : monthlyTransactions.entrySet()) {
            dataset.addValue(entry.getValue(), "Jumlah Transaksi", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "TREND PENJUALAN BULANAN",
                "Bulan-Tahun",
                "Nilai",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Customize line chart
        CategoryPlot plot = chart.getCategoryPlot();
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, new Color(79, 129, 189)); // Pendapatan
        renderer.setSeriesPaint(1, new Color(192, 80, 77));  // Transaksi
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShapesVisible(1, true);
        plot.setRenderer(renderer);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPaymentChartPanel() {
        Date startDate = tanggalAwalPicker.getDate();
        Date endDate = tanggalAkhirPicker.getDate();

        DefaultPieDataset dataset = new DefaultPieDataset();

        // Hitung metode pembayaran
        Map<String, Integer> paymentMethods = new HashMap<>();

        List<Penjualan> penjualanList = dataService.getAllPenjualan();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        for (Penjualan p : penjualanList) {
            try {
                // Filter berdasarkan tanggal
                if (startDate != null && endDate != null) {
                    String tanggalStr = p.getTanggal().substring(0, 10);
                    Date tanggalPenjualan = dateFormat.parse(tanggalStr);

                    if (tanggalPenjualan.before(startDate) || tanggalPenjualan.after(endDate)) {
                        continue;
                    }
                }

                String method = p.getMetodePembayaran();
                paymentMethods.put(method, paymentMethods.getOrDefault(method, 0) + 1);

            } catch (Exception e) {
                System.err.println("Error processing transaction: " + e.getMessage());
            }
        }

        // Add to dataset
        for (Map.Entry<String, Integer> entry : paymentMethods.entrySet()) {
            dataset.setValue(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue());
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "DISTRIBUSI METODE PEMBAYARAN",
                dataset,
                true,
                true,
                false
        );

        // Customize pie chart colors
        PiePlot plot = (PiePlot) chart.getPlot();
        Color[] colors = {new Color(79, 129, 189), new Color(192, 80, 77),
                new Color(155, 187, 89), new Color(128, 100, 162)};

        int colorIndex = 0;
        for (Object key : dataset.getKeys()) {
            plot.setSectionPaint((Comparable) key, colors[colorIndex % colors.length]);
            colorIndex++;
        }

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
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

    // ... (method-method calculateMetrics, hitungTerjual, formatCurrency tetap sama seperti sebelumnya)

    private Map<String, Object> calculateMetrics(Date startDate, Date endDate) {
        Map<String, Object> metrics = new HashMap<>();
        List<Penjualan> semuaPenjualan = dataService.getAllPenjualan();

        double totalRevenue = 0;
        int totalTransactions = 0;
        int vehicleSales = 0;
        int sparepartSales = 0;
        Map<String, Integer> productCount = new HashMap<>();
        Map<String, Integer> paymentCount = new HashMap<>();
        Set<String> uniqueCustomers = new HashSet<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        for (Penjualan p : semuaPenjualan) {
            try {
                // Filter berdasarkan tanggal jika provided
                if (startDate != null && endDate != null) {
                    String tanggalStr = p.getTanggal().substring(0, 10);
                    Date tanggalPenjualan = dateFormat.parse(tanggalStr);

                    if (tanggalPenjualan.before(startDate) || tanggalPenjualan.after(endDate)) {
                        continue;
                    }
                }

                totalTransactions++;
                uniqueCustomers.add(p.getPelangganId());

                // Hitung payment method
                paymentCount.put(p.getMetodePembayaran(),
                        paymentCount.getOrDefault(p.getMetodePembayaran(), 0) + 1);

                for (PenjualanItem item : p.getItems()) {
                    totalRevenue += item.getSubtotal();

                    if ("kendaraan".equals(item.getTipe())) {
                        vehicleSales += item.getJumlah();
                        Kendaraan k = dataService.getKendaraanById(item.getItemId());
                        if (k != null) {
                            String productName = k.getMerk() + " " + k.getModel();
                            productCount.put(productName,
                                    productCount.getOrDefault(productName, 0) + item.getJumlah());
                        }
                    } else if ("sparepart".equals(item.getTipe())) {
                        sparepartSales += item.getJumlah();
                        Sparepart s = dataService.getSparepartById(item.getItemId());
                        if (s != null) {
                            productCount.put(s.getNama(),
                                    productCount.getOrDefault(s.getNama(), 0) + item.getJumlah());
                        }
                    }
                }

            } catch (Exception e) {
                System.err.println("Error processing transaction: " + e.getMessage());
            }
        }

        // Cari produk terlaris
        String bestSeller = "Tidak ada data";
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : productCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                bestSeller = entry.getKey();
            }
        }

        // Cari metode pembayaran terpopuler
        String popularPayment = "Cash";
        int maxPayment = 0;
        for (Map.Entry<String, Integer> entry : paymentCount.entrySet()) {
            if (entry.getValue() > maxPayment) {
                maxPayment = entry.getValue();
                popularPayment = entry.getKey();
            }
        }

        metrics.put("totalRevenue", totalRevenue);
        metrics.put("totalTransactions", totalTransactions);
        metrics.put("avgTransaction", totalTransactions > 0 ? totalRevenue / totalTransactions : 0);
        metrics.put("bestSeller", bestSeller);
        metrics.put("vehicleSales", vehicleSales);
        metrics.put("sparepartSales", sparepartSales);
        metrics.put("popularPayment", popularPayment);
        metrics.put("activeCustomers", uniqueCustomers.size());

        return metrics;
    }

    private int hitungTerjual(String itemId, String tipe, Date startDate, Date endDate) {
        int total = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        for (Penjualan p : dataService.getAllPenjualan()) {
            try {
                // Filter berdasarkan tanggal jika provided
                if (startDate != null && endDate != null) {
                    String tanggalStr = p.getTanggal().substring(0, 10);
                    Date tanggalPenjualan = dateFormat.parse(tanggalStr);

                    if (tanggalPenjualan.before(startDate) || tanggalPenjualan.after(endDate)) {
                        continue;
                    }
                }

                for (PenjualanItem item : p.getItems()) {
                    if (tipe.equals(item.getTipe()) && itemId.equals(item.getItemId())) {
                        total += item.getJumlah();
                    }
                }
            } catch (Exception e) {
                System.err.println("Error processing transaction: " + e.getMessage());
            }
        }
        return total;
    }

    private String formatCurrency(double amount) {
        return String.format("Rp %,10.2f", amount).trim();
    }

    private String formatCurrencyShort(double amount) {
        if (amount >= 1000000) {
            return String.format("Rp %.1fM", amount / 1000000);
        } else if (amount >= 1000) {
            return String.format("Rp %.1fK", amount / 1000);
        }
        return String.format("Rp %.0f", amount);
    }

    public JPanel getContentPane() {
        return this;
    }
}