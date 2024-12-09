package main.java.com.view;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private JTextField contextSwitchingTimeField;

    public MainWindow() {
        initializeComponents();
        createLayout();
        setupWindowProperties();
    }

    private void initializeComponents() {
        // Title label with larger, bold font
        JLabel titleLabel = new JLabel("CPU Scheduler Simulator", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Author label with italicized font
        JLabel authorLabel = new JLabel("Done by Mohamed Taha", SwingConstants.CENTER);
        authorLabel.setFont(new Font("Arial", Font.ITALIC, 16));

        // Context switching time label
        JLabel contextSwitchLabel = new JLabel("Enter Context Switching Time (ms):");
        contextSwitchingTimeField = new JTextField(10);
        contextSwitchingTimeField.setToolTipText("Context Switching Time in milliseconds");
    }

    private void createLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(new JLabel("CPU Scheduler Simulator", SwingConstants.CENTER), gbc);

        // Author
        gbc.gridy = 1;
        add(new JLabel("Done by Mohamed Taha", SwingConstants.CENTER), gbc);

        // Context Switching Time Label
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(new JLabel("Context Switching Time (s):"), gbc);

        // Context Switching Time Field
        gbc.gridx = 1;
        add(contextSwitchingTimeField, gbc);

        // Proceed Button
        JButton proceedButton = new JButton("Proceed to Scheduler");
        proceedButton.addActionListener(e -> openSchedulerWindow());
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        add(proceedButton, gbc);
    }

    private void setupWindowProperties() {
        setTitle("CPU Scheduler Simulator");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void openSchedulerWindow() {
        try {
            int contextSwitchingTime = Integer.parseInt(contextSwitchingTimeField.getText());
            if (contextSwitchingTime < 0) {
                JOptionPane.showMessageDialog(this,
                        "Context Switching Time must be non-negative",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Open scheduler window with context switching time
            CPUSchedulerMainWindow schedulerWindow = new CPUSchedulerMainWindow(contextSwitchingTime);
            schedulerWindow.setVisible(true);
            this.dispose(); // Close main window
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid number for Context Switching Time",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new MainWindow().setVisible(true);
        });
    }
}