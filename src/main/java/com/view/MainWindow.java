package main.java.com.view;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private JTextField contextSwitchingTimeField;
    private JComboBox<String> schedulingTypeComboBox;
    private JTextField numberOfProcessesField;


    public MainWindow() {
        initializeComponents();
        createLayout();
        setupWindowProperties();
    }

    private void initializeComponents() {
        // Scheduling Type Dropdown
        schedulingTypeComboBox = new JComboBox<>(new String[]{
                "SJF", "SRTF", "Priority", "FCAI"
        });
        schedulingTypeComboBox.addActionListener(e -> {
            String selectedType = (String) schedulingTypeComboBox.getSelectedItem();
            boolean isSJForSRTF = selectedType.equals("SJF") || selectedType.equals("SRTF")|| selectedType.equals("Priority");
            contextSwitchingTimeField.setEnabled(isSJForSRTF);
            if (!isSJForSRTF) {
                contextSwitchingTimeField.setText("0");
            }
        });

        schedulingTypeComboBox.setToolTipText("Select Scheduling Algorithm");

        // Context Switching Time Field
        contextSwitchingTimeField = new JTextField(10);
        contextSwitchingTimeField.setToolTipText("Context Switching Time in milliseconds");

        // Number of Processes Field
        numberOfProcessesField = new JTextField(10);
        numberOfProcessesField.setToolTipText("Total number of processes");




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

        // Scheduling Type Label and Dropdown
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        add(new JLabel("Scheduling Type:"), gbc);
        gbc.gridx = 1;
        add(schedulingTypeComboBox, gbc);

        // Number of Processes Label and Field
        gbc.gridy = 3;
        gbc.gridx = 0;
        add(new JLabel("Number of Processes:"), gbc);
        gbc.gridx = 1;
        add(numberOfProcessesField, gbc);



        // Context Switching Time Label and Field
        gbc.gridy = 4;
        gbc.gridx = 0;
        add(new JLabel("Context Switching Time (ms):"), gbc);
        gbc.gridx = 1;
        add(contextSwitchingTimeField, gbc);

        // Proceed Button
        JButton proceedButton = new JButton("Proceed to Scheduler");
        proceedButton.addActionListener(e -> openSchedulerWindow());
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        add(proceedButton, gbc);
    }

    private void setupWindowProperties() {
        setTitle("CPU Scheduler Simulator");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void openSchedulerWindow() {
        try {
            // Validate inputs
            String schedulingType = (String) schedulingTypeComboBox.getSelectedItem();
            int numberOfProcesses = Integer.parseInt(numberOfProcessesField.getText());
            int contextSwitchingTime = Integer.parseInt(contextSwitchingTimeField.getText());



            // Input validation
            if (numberOfProcesses <= 0 || contextSwitchingTime < 0) {
                throw new IllegalArgumentException("Invalid number of processes or context switching time");
            }

            // Open scheduler window with parameters
            CPUSchedulerMainWindow schedulerWindow = new CPUSchedulerMainWindow(
                    schedulingType,
                    numberOfProcesses,
                    contextSwitchingTime
            );
            schedulerWindow.setVisible(true);
            this.dispose(); // Close main window
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numeric values",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
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