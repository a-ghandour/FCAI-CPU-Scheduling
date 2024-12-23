package main.java.com.view;

import main.java.com.view.MainWindow;
import main.java.com.strategy.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import main.java.com.factory.SchedulerFactory;
import main.java.com.model.CPUProcess;
import main.java.com.strategy.SchedulingStrategy;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.LinkedHashSet;


public class CPUSchedulerMainWindow extends JFrame {
    private int contextSwitchingTime;
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
    private int maxProcesses;
    private String schedulingType;
    private JTextField roundRobinQuantumField;
    private int roundRobinQuantum;

    // Constructor with context switching time
    public CPUSchedulerMainWindow(
            String schedulingType,
            int numberOfProcesses,
            int contextSwitchingTime
    ) {
        this.contextSwitchingTime = contextSwitchingTime;
        this.maxProcesses = numberOfProcesses;
        this.schedulingType = schedulingType;


        initializeComponents(schedulingType, contextSwitchingTime);
        createLayout();

        setTitle("CPU Scheduler Simulator - " + schedulingType);
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // Default constructor
    public CPUSchedulerMainWindow() {
    }

    // Implement missing method for input panel
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name Label and Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Process Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        // Priority Label and Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Priority:"), gbc);
        gbc.gridx = 1;
        panel.add(priorityField, gbc);

        // Arrival Time Label and Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Arrival Time:"), gbc);
        gbc.gridx = 1;
        panel.add(arrivalTimeField, gbc);

        // Burst Time Label and Field
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Burst Time:"), gbc);
        gbc.gridx = 1;
        panel.add(burstTimeField, gbc);
        // Round Robin Quantum Field
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Round Robin Quantum:"), gbc);
        gbc.gridx = 1;
        panel.add(roundRobinQuantumField, gbc);

        // Context Switching Time
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Context Switching Time:"), gbc);
        gbc.gridx = 1;
        panel.add(contextSwitchingTimeField, gbc);

        // Scheduler Type
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Scheduler Type:"), gbc);
        gbc.gridx = 1;
        panel.add(schedulerTypeComboBox, gbc);

        // Color Button
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        panel.add(colorButton, gbc);

        // Add Process Button
        JButton addProcessButton = new JButton("Add Process");
        addProcessButton.addActionListener(e -> addProcess());
        gbc.gridy = 8;
        panel.add(addProcessButton, gbc);

        // Run Scheduler Button
        JButton runSchedulerButton = new JButton("Run Scheduler");
        runSchedulerButton.addActionListener(e -> runScheduler());
        gbc.gridy = 9;
        panel.add(runSchedulerButton, gbc);

        // Clear All Button
        JButton clearAllButton = new JButton("Clear All");
        clearAllButton.addActionListener(e -> clearAll());
        gbc.gridy = 10;
        panel.add(clearAllButton, gbc);

        return panel;
    }

    // Implement missing method for color selection
    private void chooseColor() {
        Color selectedColor = JColorChooser.showDialog(this, "Choose Process Color", currentColor);
        if (selectedColor != null) {
            currentColor = selectedColor;
            colorButton.setBackground(currentColor);
        }
    }


    private void initializeComponents(String schedulingType, int contextSwitchingTime) {

        // Input Fields
        nameField = new JTextField(10);
        nameField.setToolTipText("Process Name");

        if (schedulingType.equals("SJF") || schedulingType.equals("SRTF")) {
            priorityField = new JTextField("0", 10);
            priorityField.setEditable(false);
            priorityField.setToolTipText("Priority is fixed at 0 for " + schedulingType);
        } else {
            priorityField = new JTextField(10);
            priorityField.setToolTipText("Priority");
        }

        arrivalTimeField = new JTextField(10);
        arrivalTimeField.setToolTipText("Arrival Time");

        burstTimeField = new JTextField(10);
        burstTimeField.setToolTipText("Burst Time");

        contextSwitchingTimeField = new JTextField(10);
        contextSwitchingTimeField.setToolTipText("Context Switching Time");
        contextSwitchingTimeField = new JTextField(10);
        contextSwitchingTimeField.setText(String.valueOf(contextSwitchingTime));
        contextSwitchingTimeField.setEditable(false);

        // Initialize Round Robin Quantum field
        roundRobinQuantumField = new JTextField(10);
        roundRobinQuantumField.setToolTipText("Round Robin Time Quantum");
        roundRobinQuantumField.setText(String.valueOf(roundRobinQuantum));
        roundRobinQuantumField.setEnabled(schedulingType.equals("FCAI"));

        // Color Button
        colorButton = new JButton("Choose Color");
        colorButton.setBackground(currentColor);
        colorButton.addActionListener(e -> chooseColor());

        // Scheduler Type Dropdown
        schedulerTypeComboBox = new JComboBox<>(new String[]{"SJF", "SRTF", "Priority", "FCAI"});
        schedulerTypeComboBox = new JComboBox<>(new String[]{schedulingType});
        schedulerTypeComboBox.setEnabled(false);
        // Process Table
        String[] columnNames = {"Name", "Priority", "Arrival Time", "Burst Time", "Color"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        processTable = new JTable(tableModel);
        processTable.getColumnModel().getColumn(4).setCellRenderer(new ColorCellRenderer());

        // Gantt Chart with reduced time unit and height
        ganttChart = new GanttChartPanel(20, 25, 60, 30, contextSwitchingTime);

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

        // Back Button to return to main window
        JButton backButton = new JButton("Back to Main Window");
        backButton.addActionListener(e -> returnToMainWindow());
        add(backButton, BorderLayout.SOUTH);
    }

    private void returnToMainWindow() {
        new CPUSchedulerMainWindow().setVisible(true);
        this.dispose();
    }

    private void addProcess() {
        try {
            if (processes.size() >= maxProcesses) {
                showError("Maximum number of processes (" + maxProcesses + ") has been reached");
                return;
            }
            // Validate inputs
            String name = nameField.getText().trim();

            if (name.isEmpty()) {
                showError("Process Name cannot be empty");
                return;
            }

            int contextSwtichingTime = Integer.parseInt(contextSwitchingTimeField.getText());
            int priority;
            if (schedulingType.equals("SJF") || schedulingType.equals("SRTF")) {
                priority = 0;
            } else {
                priority = Integer.parseInt(priorityField.getText());
            }
            int quantum = 0; // Default quantum
            if (schedulingType.equalsIgnoreCase("FCAI")) {
                quantum = Integer.parseInt(roundRobinQuantumField.getText());
            }
            int arrivalTime = Integer.parseInt(arrivalTimeField.getText());
            int burstTime = Integer.parseInt(burstTimeField.getText());

            if (arrivalTime < 0 || burstTime <= 0 || priority < 0 || contextSwtichingTime < 0) {
                showError("Please enter non-negative values");
                return;
            }

            // Create process
            CPUProcess process = new CPUProcess(name, arrivalTime, burstTime, priority, quantum);
            process.setColor(currentColor);
            processes.add(process);

            // Add to table
            Object[] rowData = {
                    name,
                    priority,
                    arrivalTime,
                    burstTime,
                    currentColor
            };
            if (arrivalTime < 0) {
                showError("Arrival Time cannot be negative");
                return;
            }
            if (burstTime <= 0) {
                showError("Burst Time must be positive");
                return;
            }
            if (priority < 0) {
                showError("Priority cannot be negative");
                return;
            }
            if (contextSwtichingTime < 0) {
                showError("Context Switching Time cannot be negative");
                return;
            }

            tableModel.addRow(rowData);

            // Clear input fields
            clearInputFields();

            // Choose a new random color for the next process
            currentColor = new Color(
                    (int) (Math.random() * 256),
                    (int) (Math.random() * 256),
                    (int) (Math.random() * 256)
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

        try {
            SchedulingStrategy scheduler = SchedulerFactory.getScheduler(schedulingType.toLowerCase());
            List<CPUProcess> scheduledProcesses = scheduler.schedule(new ArrayList<>(processes), contextSwitchingTime);

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

            outputArea.append(String.format("\nAverage Waiting Time: %.2f\n", Math.ceil(totalWaitingTime / processes.size())));
            outputArea.append(String.format("Average Turnaround Time: %.2f\n", Math.ceil(totalTurnaroundTime / processes.size())));

            // FCAI Messages
            if (scheduler instanceof FCAIScheduling) {
                FCAIScheduling fcaiScheduler = (FCAIScheduling) scheduler;
                List<String> messages = fcaiScheduler.getOutputMessages();// Fetch timeline
                outputArea.append("\nFCAI Messages:\n");
                for (String message : messages) {
                    outputArea.append(message + "\n");
                }
            }

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
                setBackground((Color) color);
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
            new CPUSchedulerMainWindow().setVisible(true);
        });
    }
}

class GanttChartPanel extends JPanel {
    private List<CPUProcess> processSequence;
    private int timeUnit;
    private int rowHeight;
    private int xOffset;
    private int yOffset;
    private int maxTime = 0;
    private int contextSwitchingTime;

    private List<TimeSlot> timeSlots;
    private Map<String, Integer> processRows;

    private static class TimeSlot {
        final CPUProcess process;
        final int startTime;
        final int duration;
        final boolean isContextSwitch;
        final Color color;

        TimeSlot(CPUProcess process, int startTime, int duration, boolean isContextSwitch) {
            this.process = process;
            this.startTime = startTime;
            this.duration = duration;
            this.isContextSwitch = isContextSwitch;
            // Use a lighter gray for context switching
            this.color = isContextSwitch ? new Color(200, 200, 200, 150) : process.getColor();
        }
    }
    public GanttChartPanel(int timeUnit, int rowHeight, int xOffset, int yOffset) {
        this(timeUnit, rowHeight, xOffset, yOffset, 0); // Default context switching time to 0
    }
    public GanttChartPanel(int timeUnit, int rowHeight, int xOffset, int yOffset, int contextSwitchingTime) {
        this.timeUnit = timeUnit;
        this.rowHeight = rowHeight;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.contextSwitchingTime = contextSwitchingTime;
        this.processSequence = new ArrayList<>();
        this.timeSlots = new ArrayList<>();
        this.processRows = new HashMap<>();
        setBackground(Color.WHITE);
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
            List<TimeSlot> actualTimeSlots = new ArrayList<>();
            CPUProcess lastProcess = null;

            for (int i = 0; i < processes.size(); i++) {
                CPUProcess currentProcess = processes.get(i);

                // Add context switching time between different processes
                if (lastProcess != null && !lastProcess.getName().equals(currentProcess.getName())) {
                    TimeSlot contextSwitchSlot = new TimeSlot(
                            null,
                            currentTime,
                            contextSwitchingTime,
                            true
                    );
                    actualTimeSlots.add(contextSwitchSlot);
                    currentTime += contextSwitchingTime;
                }

                // Calculate actual execution duration for this segment
                int executionDuration;

                // Look ahead to find when this process appears next or ends
                if (i < processes.size() - 1) {
                    int nextOccurrence = -1;
                    for(int j = i + 1; j < processes.size(); j++) {
                        if(processes.get(j).getName().equals(currentProcess.getName())) {
                            nextOccurrence = j;
                            break;
                        }
                    }

                    if(nextOccurrence != -1) {
                        // Process will be preempted - calculate time until next process
                        executionDuration = 0;
                        for(int j = i + 1; j < nextOccurrence; j++) {
                            if(!processes.get(j).getName().equals(currentProcess.getName())) {
                                executionDuration++;
                            }
                        }
                    } else {
                        // This is the final occurrence of this process
                        executionDuration = currentProcess.getBurstTime();
                        for(TimeSlot slot : actualTimeSlots) {
                            if(slot.process != null && slot.process.getName().equals(currentProcess.getName())) {
                                executionDuration -= slot.duration;
                            }
                        }
                    }
                } else {
                    // Last process in the sequence
                    executionDuration = currentProcess.getBurstTime();
                    for(TimeSlot slot : actualTimeSlots) {
                        if(slot.process != null && slot.process.getName().equals(currentProcess.getName())) {
                            executionDuration -= slot.duration;
                        }
                    }
                }

                TimeSlot processSlot = new TimeSlot(
                        currentProcess,
                        currentTime,
                        executionDuration,
                        false
                );
                actualTimeSlots.add(processSlot);
                currentTime += executionDuration;

                lastProcess = currentProcess;
            }

            this.timeSlots = actualTimeSlots;
            maxTime = currentTime;

            // Process row assignment
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
            g2d.drawString(entry.getKey(), 10, yOffset + entry.getValue() * rowHeight + rowHeight / 2 + 5);
        }

        // Draw time slots
        for (TimeSlot slot : timeSlots) {
            // Skip null process slots (happens in context switching)
            if (slot.process == null && slot.isContextSwitch) {
                int row = 0; // Context switch spans all rows
                int x = xOffset + (slot.startTime * timeUnit);
                int y = yOffset;
                int width = slot.duration * timeUnit;

                // Draw the context switch bar across all rows
                g2d.setColor(slot.color);
                g2d.fillRect(x, y, width, processRows.size() * rowHeight);

                // Draw border
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawRect(x, y, width, processRows.size() * rowHeight);

                continue;
            }

            // Regular process slots
            if (slot.process != null) {
                int row = processRows.get(slot.process.getName());
                int x = xOffset + (slot.startTime * timeUnit);
                int y = yOffset + row * rowHeight;
                int width = slot.duration * timeUnit;

                // Draw the bar
                g2d.setColor(slot.color);
                g2d.fillRect(x, y, width, rowHeight - 10);

                // Draw border
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, y, width, rowHeight - 10);

                // Draw burst time in the middle of the bar if there's enough space
                if (width > 30) {
                    String burstText = String.valueOf(slot.duration);
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(burstText);
                    int textX = x + (width - textWidth) / 2;
                    int textY = y + (rowHeight - 10) / 2 + fm.getAscent() / 2;
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(burstText, textX, textY);
                }
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

            g2d.drawString(timeStr, x - stringWidth / 2, 20);
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
