package main.java.com.main;

import javax.swing.*;
import main.java.com.view.MainWindow;
import main.java.com.factory.SchedulerFactory;
import main.java.com.model.CPUProcess;
import main.java.com.strategy.SchedulingStrategy;
import main.java.com.strategy.FCAIScheduling;
import main.java.com.view.CLIOutput;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new MainWindow().setVisible(true);
        });

        int n = CLIOutput.getNumberOfProcesses();
        int contextSwitchTime = CLIOutput.getContextSwitchTime();

        String schedulerType = CLIOutput.getSchedulerType();

        List<CPUProcess> processes = CLIOutput.getProcessesInput(schedulerType, n);

        SchedulingStrategy scheduler;
        try {
            scheduler = SchedulerFactory.getScheduler(schedulerType);
        } catch (IllegalArgumentException e) {
            CLIOutput.printInvalidSchedulerType();
            return;
        }

        List<CPUProcess> scheduledProcesses = scheduler.schedule(processes, contextSwitchTime);
        if (scheduler instanceof FCAIScheduling) {
            List<String> messages = ((FCAIScheduling) scheduler).getOutputMessages();
            CLIOutput.printMessages(messages);
        }
        CLIOutput.printExecutionOrder(scheduledProcesses);

        CLIOutput.printProcessDetails(scheduledProcesses, n);
    }
}