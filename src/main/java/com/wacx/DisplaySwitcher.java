package com.wacx;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.PopupMenu;
import java.awt.MenuItem;
import java.awt.AWTException;

public class DisplaySwitcher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(DisplaySwitcher::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Display Switcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(350, 250); // Increased the size for better spacing

        // Set the layout for the frame and align buttons horizontally
        frame.setLayout(new BorderLayout(20, 20)); // Adding padding between components

        // Set the dark background color and light font color for minimalistic dark mode
        frame.getContentPane().setBackground(new Color(45, 45, 48)); // Dark gray background

        // Create a label with a light gray font color
        JLabel label = new JLabel("Switch Display");
        label.setForeground(new Color(220, 220, 220)); // Light gray text
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setHorizontalAlignment(SwingConstants.CENTER); // Center the label

        // Apply the icon (ensure it's placed in resources)
        ImageIcon icon = new ImageIcon(DisplaySwitcher.class.getResource("/icon.jpg"));
        frame.setIconImage(icon.getImage());

        // Create a vertical box layout for the label and an empty space
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BorderLayout()); // Use BorderLayout for better control
        labelPanel.setBackground(new Color(45, 45, 48)); // Same dark background to match
        labelPanel.add(Box.createVerticalStrut(30), BorderLayout.NORTH); // Space before the label
        labelPanel.add(label, BorderLayout.CENTER); // Center the label

        // Panel to hold the buttons and arrange them horizontally
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Horizontal layout with space between buttons
        buttonPanel.setBackground(new Color(45, 45, 48)); // Same dark background for consistency

        // Laptop Display button with styling
        JButton laptopButton = new JButton("Laptop Display");
        laptopButton.setBackground(new Color(60, 60, 60)); // Darker button background
        laptopButton.setForeground(new Color(220, 220, 220)); // Light button text
        laptopButton.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100))); // Subtle border
        laptopButton.setFocusPainted(false);
        laptopButton.setFont(new Font("Arial", Font.PLAIN, 14));
        laptopButton.setPreferredSize(new Dimension(150, 40)); // Adjust the button size
        laptopButton.setOpaque(true);
        laptopButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        laptopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runBatchFile("laptop display.bat");
            }
        });

        // PC Display button with styling
        JButton pcButton = new JButton("PC Display");
        pcButton.setBackground(new Color(60, 60, 60)); // Darker button background
        pcButton.setForeground(new Color(220, 220, 220)); // Light button text
        pcButton.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100))); // Subtle border
        pcButton.setFocusPainted(false);
        pcButton.setFont(new Font("Arial", Font.PLAIN, 14));
        pcButton.setPreferredSize(new Dimension(150, 40)); // Adjust the button size
        pcButton.setOpaque(true);
        pcButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pcButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runBatchFile("pc display.bat");
            }
        });

        // Add the label panel and button panel
        frame.add(labelPanel, BorderLayout.NORTH); // Add label at the top with space
        frame.add(buttonPanel, BorderLayout.CENTER); // Add buttons in the center

        // Add buttons to the button panel
        buttonPanel.add(laptopButton);
        buttonPanel.add(pcButton);

        // Create a tray icon and set up the context menu
        if (SystemTray.isSupported()) {
            SystemTray systemTray = SystemTray.getSystemTray();
            TrayIcon trayIcon = new TrayIcon(createImageIcon("/icon.jpg").getImage(), "Display Switcher");

            // Set up the popup menu for the tray icon
            PopupMenu popupMenu = new PopupMenu();
            MenuItem laptopDisplayItem = new MenuItem("Laptop Display");
            MenuItem pcDisplayItem = new MenuItem("PC Display");
            MenuItem exitItem = new MenuItem("Exit");

            // Add listeners for each menu item
            laptopDisplayItem.addActionListener(e -> runBatchFile("laptop display.bat"));
            pcDisplayItem.addActionListener(e -> runBatchFile("pc display.bat"));
            exitItem.addActionListener(e -> System.exit(0));

            // Add the items to the popup menu
            popupMenu.add(laptopDisplayItem);
            popupMenu.add(pcDisplayItem);
            popupMenu.addSeparator();
            popupMenu.add(exitItem);

            // Set the popup menu for the tray icon
            trayIcon.setPopupMenu(popupMenu);

            // Add the tray icon to the system tray
            try {
                systemTray.add(trayIcon);
            } catch (AWTException ex) {
                ex.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(frame, "System Tray is not supported on this platform.");
        }

        // Center the window on the screen
        frame.setLocationRelativeTo(null);
        frame.setVisible(true); // Show window AFTER all components are added
    }

    // Utility method to create an ImageIcon from the resource path
    private static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = DisplaySwitcher.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    // Method to run the batch file
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
}
