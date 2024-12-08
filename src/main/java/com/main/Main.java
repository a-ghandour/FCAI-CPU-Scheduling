package main.java.com.main;

import main.java.com.factory.SchedulerFactory;
import main.java.com.model.CPUProcess;
import main.java.com.strategy.SchedulingStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter number of processes: ");
        int n = scanner.nextInt();

        List<CPUProcess> processes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            System.out.println("Enter details for process " + (i + 1));
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

        System.out.print("Enter scheduling type (priority/srtf): ");
        String schedulerType = scanner.next();

        SchedulingStrategy scheduler;
        try {
            scheduler = SchedulerFactory.getScheduler(schedulerType);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid scheduling type. Please use 'priority' or 'srtf'.");
            return;
        }

        List<CPUProcess> scheduledProcesses = scheduler.schedule(processes);
        Set<CPUProcess> scheduledSet = new HashSet<>(scheduledProcesses);
        System.out.println("\nExecution Order:");
        for (CPUProcess process : scheduledProcesses) {
            System.out.println("Process: " + process.getName());
        }

        double totalWaitingTime = 0;
        double totalTurnaroundTime = 0;

        System.out.println("\nDetails:");
        for (CPUProcess process : scheduledSet) {
            System.out.println("Process " + process.getName() + ": Waiting Time = " +
                    process.getWaitingTime() + ", Turnaround Time = " +
                    process.getTurnAroundTime());
            totalWaitingTime += process.getWaitingTime();
            totalTurnaroundTime += process.getTurnAroundTime();
        }

        System.out.printf("\nAverage Waiting Time: %.2f\n", totalWaitingTime / n);
        System.out.printf("Average Turnaround Time: %.2f\n", totalTurnaroundTime / n);
    }
}
