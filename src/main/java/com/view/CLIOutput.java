package main.java.com.view;

import main.java.com.model.CPUProcess;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CLIOutput {

    private static final Scanner scanner = new Scanner(System.in);

    public static int getNumberOfProcesses() {
        System.out.print("Enter number of processes: ");
        return scanner.nextInt();
    }

    public static int getContextSwitchTime() {
        System.out.print("Enter context switch time: ");
        return scanner.nextInt();
    }

    public static List<CPUProcess> getProcessesInput(int n) {
        List<CPUProcess> processes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            System.out.println("Enter details for process " + (i+1));
            System.out.print("Name: ");
            String name = scanner.next();
            System.out.print("Priority: ");
            int priority = scanner.nextInt();
            System.out.print("Arrival Time: ");
            int arrivalTime = scanner.nextInt();
            System.out.print("Burst Time: ");
            int burstTime = scanner.nextInt();

            processes.add(new CPUProcess(name, arrivalTime, burstTime, priority));
        }
        return processes;
    }

    public static String getSchedulerType() {
        System.out.print("Enter scheduling type (priority/srtf): ");
        return scanner.next();
    }


    public static void printInvalidSchedulerType() {
        System.out.println("Invalid scheduling type. Please use 'priority' or 'srtf'.");
    }

    public static void printExecutionOrder(List<CPUProcess> scheduledProcesses) {
        System.out.println("\nExecution Order:");
        for (CPUProcess process : scheduledProcesses) {
            System.out.println("Process: " + process.getName());
        }
    }

    public static void printProcessDetails(List<CPUProcess> scheduledProcesses, int totalProcesses) {
        double totalWaitingTime = 0;
        double totalTurnaroundTime = 0;

        System.out.println("\nDetails:");
        for (CPUProcess process : scheduledProcesses) {
            System.out.println("Process " + process.getName() + ": Waiting Time = " +
                    process.getWaitingTime() + ", Turnaround Time = " +
                    process.getTurnAroundTime());
            totalWaitingTime += process.getWaitingTime();
            totalTurnaroundTime += process.getTurnAroundTime();
        }

        System.out.printf("\nAverage Waiting Time: %.2f\n", totalWaitingTime / totalProcesses);
        System.out.printf("Average Turnaround Time: %.2f\n", totalTurnaroundTime / totalProcesses);
    }
}
