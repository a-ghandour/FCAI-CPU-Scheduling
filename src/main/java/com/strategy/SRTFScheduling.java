package main.java.com.strategy;

import main.java.com.model.CPUProcess;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class SRTFScheduling implements SchedulingStrategy {

    @Override
    public List<CPUProcess> schedule(List<CPUProcess> processes, int contextSwitchingTime) {
        processes.sort(Comparator.comparingInt(CPUProcess::getArrivalTime));
        List<CPUProcess> scheduledProcesses = new ArrayList<>();
        List<CPUProcess> readyQueue = new ArrayList<>();
        int currentTime = 0;
        int totalProcesses = processes.size();
        int completedProcesses = 0;
        while (completedProcesses < totalProcesses) {
            // Add processes to readyQueue if they have arrived
            for (CPUProcess p : processes) {
                if (p.getArrivalTime() <= currentTime && !readyQueue.contains(p) && p.getRemainingTime() > 0) {
                    readyQueue.add(p);
                }
            }
            // Sort readyQueue based on remaining time
            readyQueue.sort(Comparator.comparingInt(CPUProcess::getRemainingTime));
            if (!readyQueue.isEmpty()) {
                CPUProcess currentProcess = readyQueue.get(0);
                // Execute the process for 1 time unit
                if(scheduledProcesses.isEmpty() || !Objects.equals(currentProcess,scheduledProcesses.getLast())){
                    scheduledProcesses.add(currentProcess);
                }
                currentProcess.setRemainingTime(currentProcess.getRemainingTime() - 1);
                currentTime++;
                if (currentProcess.getRemainingTime() == 0) {
                    currentProcess.setTurnAroundTime(currentTime - currentProcess.getArrivalTime());
                    currentProcess.setWaitingTime(currentProcess.getTurnAroundTime() - currentProcess.getBurstTime());
                    currentTime+=contextSwitchingTime;
                    completedProcesses++;
                    readyQueue.remove(currentProcess);
                }
            } else {
                // If no process is ready, increment time
                currentTime++;
            }
        }

        return scheduledProcesses;
    }
}