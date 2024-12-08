package main.java.com.main;

import main.java.com.factory.SchedulerFactory;
import main.java.com.model.CPUProcess;
import main.java.com.strategy.SchedulingStrategy;
import main.java.com.view.CLIOutput;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        int n = CLIOutput.getNumberOfProcesses();
        int contextSwitchTime = CLIOutput.getContextSwitchTime();

        List<CPUProcess> processes = CLIOutput.getProcessesInput(n);

        String schedulerType = CLIOutput.getSchedulerType();

        SchedulingStrategy scheduler;
        try {
            scheduler = SchedulerFactory.getScheduler(schedulerType);
        } catch (IllegalArgumentException e) {
            CLIOutput.printInvalidSchedulerType();
            return;
        }

        List<CPUProcess> scheduledProcesses = scheduler.schedule(processes, contextSwitchTime);
        CLIOutput.printExecutionOrder(scheduledProcesses);

        CLIOutput.printProcessDetails(scheduledProcesses, n);
    }
}
