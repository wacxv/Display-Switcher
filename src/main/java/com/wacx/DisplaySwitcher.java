package com.wacx;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;
import java.util.prefs.Preferences;

public class DisplaySwitcher {
    private static JButton laptopButton;
    private static JButton pcButton;
    private static JFrame settingsFrame;
    private static Preferences prefs = Preferences.userRoot().node("DisplaySwitcher");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DisplaySwitcher::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Display Switcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 165);
        frame.setLayout(new BorderLayout(10, 10));
        frame.getContentPane().setBackground(new Color(30, 30, 30));

        // Create minimalistic toolbar
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setBackground(new Color(50, 50, 50));
        toolBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton settingsButton = new JButton("⚙");
        settingsButton.setFocusable(false);
        settingsButton.setBackground(new Color(50, 50, 50));
        settingsButton.setForeground(Color.WHITE);
        settingsButton.setBorderPainted(false);
        settingsButton.setFont(new Font("Arial", Font.PLAIN, 16));
        settingsButton.addActionListener(e -> openSettings());

        toolBar.add(settingsButton);
        frame.add(toolBar, BorderLayout.NORTH);

        JLabel label = new JLabel("Switch Display");
        label.setForeground(new Color(220, 220, 220));
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setHorizontalAlignment(SwingConstants.CENTER);

        ImageIcon icon = new ImageIcon(DisplaySwitcher.class.getResource("/icon.jpg"));
        frame.setIconImage(icon.getImage());

        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.setBackground(new Color(30, 30, 30));
        labelPanel.add(Box.createVerticalStrut(20), BorderLayout.NORTH);
        labelPanel.add(label, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(30, 30, 30));

        laptopButton = new JButton("Laptop");
        styleButton(laptopButton);
        laptopButton.addActionListener(e -> {
            runBatchFile("laptop display.bat");
            highlightButton(laptopButton, pcButton);
        });

        pcButton = new JButton("PC");
        styleButton(pcButton);
        pcButton.addActionListener(e -> {
            runBatchFile("pc display.bat");
            highlightButton(pcButton, laptopButton);
        });

        buttonPanel.add(laptopButton);
        buttonPanel.add(pcButton);

        frame.add(labelPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        createSystemTray();

        // Auto-start if enabled
        if (prefs.getBoolean("runAtStartup", false)) {
            frame.setVisible(false);
        }
    }

    private static void styleButton(JButton button) {
        button.setBackground(new Color(60, 60, 60));
        button.setForeground(new Color(220, 220, 220));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(100, 35));
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private static void highlightButton(JButton highlight, JButton other) {
        highlight.setBackground(new Color(80, 80, 80));
        highlight.setForeground(new Color(255, 255, 255));
        other.setBackground(new Color(60, 60, 60));
        other.setForeground(new Color(220, 220, 220));
    }

    private static void runBatchFile(String fileName) {
        try {
            String batchFilePath = System.getProperty("user.dir") + "\\" + fileName;
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", batchFilePath);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            process.waitFor();
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error executing " + fileName, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void createSystemTray() {
        if (!SystemTray.isSupported()) {
            System.out.println("System tray is not supported");
            return;
        }

        SystemTray tray = SystemTray.getSystemTray();
        TrayIcon trayIcon = new TrayIcon(new ImageIcon(DisplaySwitcher.class.getResource("/icon.jpg")).getImage(), "Display Switcher");
        trayIcon.setImageAutoSize(true);

        PopupMenu popupMenu = new PopupMenu();

        MenuItem laptopItem = new MenuItem("Laptop Display");
        laptopItem.addActionListener(e -> runBatchFile("laptop display.bat"));
        popupMenu.add(laptopItem);

        MenuItem pcItem = new MenuItem("PC Display");
        pcItem.addActionListener(e -> runBatchFile("pc display.bat"));
        popupMenu.add(pcItem);

        MenuItem settingsItem = new MenuItem("Settings");
        settingsItem.addActionListener(e -> openSettings());
        popupMenu.add(settingsItem);

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        popupMenu.add(exitItem);

        trayIcon.setPopupMenu(popupMenu);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private static void openSettings() {
        if (settingsFrame != null) {
            settingsFrame.toFront();
            return;
        }

        settingsFrame = new JFrame("Settings");
        settingsFrame.setSize(250, 120);
        settingsFrame.setLayout(new FlowLayout());
        settingsFrame.getContentPane().setBackground(new Color(30, 30, 30));

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(DisplaySwitcher.class.getResource("/icon.jpg")));
        settingsFrame.setIconImage(icon.getImage());

        JCheckBox startupCheckBox = new JCheckBox("Run at Startup");
        startupCheckBox.setForeground(new Color(220, 220, 220));
        startupCheckBox.setBackground(new Color(30, 30, 30));
        startupCheckBox.setSelected(prefs.getBoolean("runAtStartup", false));
        startupCheckBox.setFocusPainted(false);

        JButton saveButton = new JButton("Save");
        styleButton(saveButton);
        saveButton.addActionListener(e -> {
            boolean runAtStartup = startupCheckBox.isSelected();
            prefs.putBoolean("runAtStartup", runAtStartup);

            if (runAtStartup) {
                addAppToStartup();  // Add to startup registry
            } else {
                removeAppFromStartup();  // Remove from startup registry
            }

            JOptionPane.showMessageDialog(settingsFrame, "Settings saved!");
        });

        settingsFrame.add(startupCheckBox);
        settingsFrame.add(saveButton);
        settingsFrame.setLocationRelativeTo(null);
        settingsFrame.setVisible(true);

        settingsFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                settingsFrame = null;
            }
        });
    }

    private static void addAppToStartup() {
        String appPath = System.getProperty("user.dir") + "\\DisplaySwitcher.exe";  // Replace with your actual executable path
        String key = "Software\\Microsoft\\Windows\\CurrentVersion\\Run";
        try {
            Preferences userPrefs = Preferences.userRoot().node(key);
            userPrefs.put("DisplaySwitcher", appPath);  // This will add the executable path to the registry
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error adding to startup.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void removeAppFromStartup() {
        String key = "Software\\Microsoft\\Windows\\CurrentVersion\\Run";
        try {
            Preferences userPrefs = Preferences.userRoot().node(key);
            userPrefs.remove("DisplaySwitcher");  // This will remove the registry entry for your app
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error removing from startup.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
