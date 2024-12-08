package main.java.com.view;

import main.java.com.factory.SchedulerFactory;
import main.java.com.model.CPUProcess;
import main.java.com.strategy.SchedulingStrategy;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.LinkedHashSet;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CPUSchedulerGUI extends JFrame {
    private List<CPUProcess> processes = new ArrayList<>();
    private Color currentColor = Color.BLUE;

    // Input Components
    private JTextField contextSwitchingTimeField;
    private JTextField nameField;
    private JTextField priorityField;
    private JTextField arrivalTimeField;
    private JTextField burstTimeField;
    private JComboBox<String> schedulerTypeComboBox;
    private JTable processTable;
    private DefaultTableModel tableModel;
    private JTextArea outputArea;
    private JButton colorButton;
    private GanttChartPanel ganttChart;
    private boolean schedulerHasRun = false;

    public CPUSchedulerGUI() {
        initializeComponents();
        createLayout();
        setTitle("CPU Scheduler Simulator");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initializeComponents() {

        // Input Fields
        nameField = new JTextField(10);
        nameField.setToolTipText("Process Name");

        priorityField = new JTextField(10);
        priorityField.setToolTipText("Priority");

        arrivalTimeField = new JTextField(10);
        arrivalTimeField.setToolTipText("Arrival Time");

        burstTimeField = new JTextField(10);
        burstTimeField.setToolTipText("Burst Time");

        contextSwitchingTimeField = new JTextField(10);
        contextSwitchingTimeField.setToolTipText("context switching time:");

        // Color Button
        colorButton = new JButton("Choose Color");
        colorButton.setBackground(currentColor);
        colorButton.addActionListener(e -> chooseColor());

        // Scheduler Type Dropdown
        schedulerTypeComboBox = new JComboBox<>(new String[]{"Priority", "SRTF", "FCFS"});

        // Process Table
        String[] columnNames = {"Name", "Priority", "Arrival Time", "Burst Time","context switching time", "Color"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        processTable = new JTable(tableModel);
        processTable.getColumnModel().getColumn(5).setCellRenderer(new ColorCellRenderer());

        // Gantt Chart
        ganttChart = new GanttChartPanel();

        // Output Area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    }

    private void createLayout() {
        setLayout(new BorderLayout(10, 10));

        // Input Panel (Left)
        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.WEST);

        // Center Panel (Table and Gantt Chart)
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(new JScrollPane(processTable), BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(ganttChart), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Output Panel (Right)
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.add(new JLabel("Execution Results:"), BorderLayout.NORTH);
        outputPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        outputPanel.setPreferredSize(new Dimension(300, 0));
        add(outputPanel, BorderLayout.EAST);
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add components with proper spacing
        addToPanel(inputPanel, new JLabel("Process Name:"), nameField, gbc, 0);
        addToPanel(inputPanel, new JLabel("Priority:"), priorityField, gbc, 1);
        addToPanel(inputPanel, new JLabel("Arrival Time:"), arrivalTimeField, gbc, 2);
        addToPanel(inputPanel, new JLabel("Burst Time:"), burstTimeField, gbc, 3);
        addToPanel(inputPanel, new JLabel("context switchingTime:"), contextSwitchingTimeField, gbc, 4);


        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        inputPanel.add(colorButton, gbc);

        JButton addButton = new JButton("Add Process");
        addButton.addActionListener(e -> addProcess());
        gbc.gridy = 6;
        inputPanel.add(addButton, gbc);

        gbc.gridy = 7;
        inputPanel.add(new JLabel("Scheduler Type:"), gbc);

        gbc.gridy = 8;
        inputPanel.add(schedulerTypeComboBox, gbc);

        JButton runButton = new JButton("Run Scheduler");
        runButton.addActionListener(e -> runScheduler());
        gbc.gridy = 9;
        inputPanel.add(runButton, gbc);

        JButton clearButton = new JButton("Clear All");
        clearButton.addActionListener(e -> clearAll());
        gbc.gridy = 10;
        inputPanel.add(clearButton, gbc);

        return inputPanel;
    }

    private void addToPanel(JPanel panel, JLabel label, JTextField field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(label, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void chooseColor() {
        Color newColor = JColorChooser.showDialog(this, "Choose Process Color", currentColor);
        if (newColor != null) {
            currentColor = newColor;
            colorButton.setBackground(currentColor);
        }
    }

    private void addProcess() {
        try {
            // Validate inputs
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                showError("Process Name cannot be empty");
                return;
            }
            int contextSwtichingTime = Integer.parseInt(contextSwitchingTimeField.getText());
            int priority = Integer.parseInt(priorityField.getText());
            int arrivalTime = Integer.parseInt(arrivalTimeField.getText());
            int burstTime = Integer.parseInt(burstTimeField.getText());

            if (arrivalTime < 0 || burstTime <= 0 || priority < 0 || contextSwtichingTime < 0) {
                showError("Please enter non-negative values");
                return;
            }

            // Create process
            CPUProcess process = new CPUProcess(name, arrivalTime, burstTime, priority);
            process.setColor(currentColor);
            processes.add(process);

            // Add to table
            Object[] rowData = {
                    name,
                    priority,
                    arrivalTime,
                    burstTime,
                    contextSwtichingTime,
                    currentColor
            };
            tableModel.addRow(rowData);

            // Clear input fields
            clearInputFields();

            // Choose a new random color for the next process
            currentColor = new Color(
                    (int)(Math.random() * 256),
                    (int)(Math.random() * 256),
                    (int)(Math.random() * 256)
            );
            colorButton.setBackground(currentColor);

        } catch (NumberFormatException e) {
            showError("Please enter valid numeric values");
        }
    }

    private void runScheduler() {
        if (processes.isEmpty()) {
            showError("Please add processes before running the scheduler");
            return;
        }

        String schedulerType = schedulerTypeComboBox.getSelectedItem().toString();
        if (schedulerType.equals("SRTF") && schedulerHasRun) {
            showError("SRTF scheduler can only be run once");
            return;
        }

        try {
            if (schedulerType.equals("SRTF")) {
                schedulerHasRun = true;
            }
            int contextSwtichingTime = Integer.parseInt(contextSwitchingTimeField.getText());
            SchedulingStrategy scheduler = SchedulerFactory.getScheduler(schedulerType.toLowerCase());
            List<CPUProcess> scheduledProcesses = scheduler.schedule(new ArrayList<>(processes),contextSwtichingTime);

            outputArea.setText("");
            outputArea.append("Execution Order:\n");
            for (CPUProcess process : scheduledProcesses) {
                outputArea.append(process.getName() + " ");
            }
            outputArea.append("\n\nProcess Details:\n");

            double totalWaitingTime = 0;
            double totalTurnaroundTime = 0;

            for (CPUProcess process : processes) {
                outputArea.append(String.format("Process %s:\n", process.getName()));
                outputArea.append(String.format("  Waiting Time: %d\n", process.getWaitingTime()));
                outputArea.append(String.format("  Turnaround Time: %d\n\n", process.getTurnAroundTime()));

                totalWaitingTime += process.getWaitingTime();
                totalTurnaroundTime += process.getTurnAroundTime();
            }

            outputArea.append(String.format("\nAverage Waiting Time: %.2f\n",
                    totalWaitingTime / processes.size()));
            outputArea.append(String.format("Average Turnaround Time: %.2f\n",
                    totalTurnaroundTime / processes.size()));

            ganttChart.setProcesses(scheduledProcesses);

        } catch (Exception e) {
            showError("Error running scheduler: " + e.getMessage());
        }
    }

    private void clearAll() {
        processes.clear();
        tableModel.setRowCount(0);
        outputArea.setText("");
        clearInputFields();
        schedulerTypeComboBox.setSelectedIndex(0);
        schedulerHasRun = false;
        ganttChart.setProcesses(new ArrayList<>());
    }

    private void clearInputFields() {
        nameField.setText("");
        priorityField.setText("");
        arrivalTimeField.setText("");
        burstTimeField.setText("");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Custom renderer for color column in table
    private static class ColorCellRenderer extends JLabel implements javax.swing.table.TableCellRenderer {
        public ColorCellRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object color,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (color instanceof Color) {
                setBackground((Color)color);
            }
            return this;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new CPUSchedulerGUI().setVisible(true);
        });
    }
}

class GanttChartPanel extends JPanel {
    private List<CPUProcess> processSequence;
    private int timeUnit = 40;  // pixels per time unit
    private int rowHeight = 40;
    private int xOffset = 100;  // space for labels
    private int yOffset = 60;   // increased space for top time markers
    private int maxTime = 0;

    private List<TimeSlot> timeSlots;
    private Map<String, Integer> processRows;

    private static class TimeSlot {
        final CPUProcess process;
        final int startTime;
        final int burstTime;

        TimeSlot(CPUProcess process, int startTime, int burstTime) {
            this.process = process;
            this.startTime = startTime;
            this.burstTime = burstTime;
        }
    }

    public GanttChartPanel() {
        this.processSequence = new ArrayList<>();
        this.timeSlots = new ArrayList<>();
        this.processRows = new HashMap<>();
        setBackground(Color.WHITE);
    }

    public void setProcesses(List<CPUProcess> processes) {
        this.processSequence = new ArrayList<>(processes);
        this.timeSlots = new ArrayList<>();
        this.processRows = new HashMap<>();

        if (!processes.isEmpty()) {
            int currentTime = 0;
            CPUProcess lastProcess = null;
            int lastStartTime = 0;
            int remainingBurst = 0;

            // Calculate time slots
            for (int i = 0; i < processes.size(); i++) {
                CPUProcess currentProcess = processes.get(i);

                if (lastProcess != null && !lastProcess.equals(currentProcess)) {
                    // Add time slot for the previous process
                    timeSlots.add(new TimeSlot(lastProcess, lastStartTime, remainingBurst));
                    currentTime += remainingBurst;
                    lastStartTime = currentTime;
                    remainingBurst = currentProcess.getBurstTime();
                } else if (lastProcess == null) {
                    lastStartTime = currentTime;
                    remainingBurst = currentProcess.getBurstTime();
                }

                lastProcess = currentProcess;
            }

            // Add the final time slot
            if (lastProcess != null) {
                timeSlots.add(new TimeSlot(lastProcess, lastStartTime, remainingBurst));
                currentTime += remainingBurst;
            }

            maxTime = currentTime;

            // Assign rows to processes
            Set<String> processNames = new LinkedHashSet<>();
            for (CPUProcess process : processSequence) {
                processNames.add(process.getName());
            }

            int rowIndex = 0;
            for (String name : processNames) {
                processRows.put(name, rowIndex++);
            }
        }

        int width = xOffset + (maxTime + 1) * timeUnit + 50;
        int height = yOffset + processRows.size() * rowHeight + 50;
        setPreferredSize(new Dimension(width, height));
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawTimeUnits(g2d);
        drawTimeGrid(g2d);

        // Draw process names
        for (Map.Entry<String, Integer> entry : processRows.entrySet()) {
            g2d.setColor(Color.BLACK);
            g2d.drawString(entry.getKey(), 10, yOffset + entry.getValue() * rowHeight + rowHeight/2 + 5);
        }

        // Draw time slots
        for (TimeSlot slot : timeSlots) {
            int row = processRows.get(slot.process.getName());
            int x = xOffset + (slot.startTime * timeUnit);
            int y = yOffset + row * rowHeight;
            int width = slot.burstTime * timeUnit;

            // Draw the bar
            g2d.setColor(slot.process.getColor());
            g2d.fillRect(x, y, width, rowHeight - 10);

            // Draw border
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y, width, rowHeight - 10);

            // Draw burst time in the middle of the bar if there's enough space
            if (width > 30) {
                String burstText = String.valueOf(slot.burstTime);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(burstText);
                int textX = x + (width - textWidth) / 2;
                int textY = y + (rowHeight - 10) / 2 + fm.getAscent() / 2;
                g2d.setColor(Color.BLACK);
                g2d.drawString(burstText, textX, textY);
            }
        }
    }

    private void drawTimeUnits(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        FontMetrics metrics = g2d.getFontMetrics();

        // Draw time markers at appropriate intervals
        int interval = maxTime > 50 ? 5 : 1;
        for (int t = 0; t <= maxTime; t += interval) {
            int x = xOffset + (t * timeUnit);
            String timeStr = String.valueOf(t);
            int stringWidth = metrics.stringWidth(timeStr);

            g2d.drawString(timeStr, x - stringWidth/2, 20);
            g2d.drawLine(x, 25, x, 30);
        }
    }

    private void drawTimeGrid(Graphics2D g2d) {
        g2d.setColor(Color.LIGHT_GRAY);

        // Vertical lines
        for (int t = 0; t <= maxTime; t++) {
            int x = xOffset + (t * timeUnit);
            g2d.drawLine(x, yOffset, x, getHeight() - 50);
        }

        // Horizontal lines
        for (int i = 0; i <= processRows.size(); i++) {
            int y = yOffset + (i * rowHeight);
            g2d.drawLine(xOffset, y, xOffset + (maxTime + 1) * timeUnit, y);
        }
    }
}