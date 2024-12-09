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

    public static List<CPUProcess> getProcessesInput(String schedulerType, int n) {
        List<CPUProcess> processes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            System.out.println("Enter details for process " + (i + 1));
            System.out.print("Name: ");
            String name = scanner.next();
            System.out.print("Arrival Time: ");
            int arrivalTime = scanner.nextInt();
            System.out.print("Burst Time: ");
            int burstTime = scanner.nextInt();

            if (schedulerType.equalsIgnoreCase("priority") || schedulerType.equalsIgnoreCase("fcai")) {
                System.out.print("Priority: ");
                int priority = scanner.nextInt();

                if (schedulerType.equalsIgnoreCase("fcai")) {
                    System.out.print("Quantum: ");
                    int quantum = scanner.nextInt();
                    processes.add(new CPUProcess(name, arrivalTime, burstTime, priority, quantum));
                } else {
                    processes.add(new CPUProcess(name, arrivalTime, burstTime, priority));
                }
            } else {
                processes.add(new CPUProcess(name, arrivalTime, burstTime));
            }
        }
        return processes;
    }

    public static String getSchedulerType() {
        System.out.print("Enter scheduling type (priority/srtf/FCAI/sjf): ");
        return scanner.next();
    }


    public static void printInvalidSchedulerType() {
        System.out.println("Invalid scheduling type. Please use 'priority' or 'srtf' or 'fcai' or 'sjf'.");
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

        System.out.printf("\nAverage Waiting Time: %.2f\n", Math.ceil(totalWaitingTime / totalProcesses));
        System.out.printf("Average Turnaround Time: %.2f\n", Math.ceil(totalTurnaroundTime / totalProcesses));
    }

    public static void printMessages(List<String> messages) {
        System.out.println("\nScheduler Messages:");
        for (String message : messages) {
            System.out.println(message);
        }
    }
}
