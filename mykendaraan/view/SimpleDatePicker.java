package com.mykendaraan.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SimpleDatePicker extends JPanel {
    private JComboBox<Integer> dayCombo;
    private JComboBox<String> monthCombo;
    private JComboBox<Integer> yearCombo;
    private JButton todayButton;

    public SimpleDatePicker() {
        initComponents();
        setToday();
    }

    private void initComponents() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));

        // Day combo (1-31)
        dayCombo = new JComboBox<>();
        for (int i = 1; i <= 31; i++) {
            dayCombo.addItem(i);
        }

        // Month combo
        monthCombo = new JComboBox<>(new String[]{
                "Januari", "Februari", "Maret", "April", "Mei", "Juni",
                "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        });

        // Year combo (2020-2030)
        yearCombo = new JComboBox<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear - 3; i <= currentYear + 3; i++) {
            yearCombo.addItem(i);
        }
        yearCombo.setSelectedItem(currentYear);

        // Today button
        todayButton = new JButton("Hari Ini");
        todayButton.addActionListener(e -> setToday());

        add(dayCombo);
        add(monthCombo);
        add(yearCombo);
        add(todayButton);
    }

    public void setToday() {
        Calendar cal = Calendar.getInstance();
        dayCombo.setSelectedItem(cal.get(Calendar.DAY_OF_MONTH));
        monthCombo.setSelectedIndex(cal.get(Calendar.MONTH));
        yearCombo.setSelectedItem(cal.get(Calendar.YEAR));
    }

    public Date getDate() {
        try {
            int day = (Integer) dayCombo.getSelectedItem();
            int month = monthCombo.getSelectedIndex();
            int year = (Integer) yearCombo.getSelectedItem();

            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        } catch (Exception e) {
            return new Date();
        }
    }

    public void setDate(Date date) {
        if (date == null) return;

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        dayCombo.setSelectedItem(cal.get(Calendar.DAY_OF_MONTH));
        monthCombo.setSelectedIndex(cal.get(Calendar.MONTH));
        yearCombo.setSelectedItem(cal.get(Calendar.YEAR));
    }

    public String getDateString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(getDate());
    }
}