package main.java.com.strategy;

import main.java.com.model.CPUProcess;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class SRTFScheduling implements SchedulingStrategy {
    private static final int AGING_THRESHOLD = 10;
    private static final int AGING_INCREMENT = 1;

    @Override
    public List<CPUProcess> schedule(List<CPUProcess> processes, int contextSwitchingTime) {
        processes.sort(Comparator.comparingInt(CPUProcess::getArrivalTime));
        List<CPUProcess> scheduledProcesses = new ArrayList<>();
        List<CPUProcess> readyQueue = new ArrayList<>();
        int currentTime = 0;
        int totalProcesses = processes.size();
        int completedProcesses = 0;
        CPUProcess previousProcess = null;

        while (completedProcesses < totalProcesses) {
            // Add newly arrived processes to ready queue
            for (CPUProcess p : processes) {
                if (p.getArrivalTime() <= currentTime && !readyQueue.contains(p) && p.getRemainingTime() > 0) {
                    readyQueue.add(p);
                }
            }

            applyAging(readyQueue, currentTime);
            readyQueue.sort(Comparator.comparingInt((CPUProcess p) -> p.getRemainingTime() - p.getAgingPriority()));

            if (!readyQueue.isEmpty()) {
                CPUProcess currentProcess = readyQueue.get(0);

                if (previousProcess != null && !Objects.equals(currentProcess, previousProcess)) {
                    currentTime += contextSwitchingTime;
                    scheduledProcesses.add(currentProcess);
                } else if (previousProcess == null) {
                    scheduledProcesses.add(currentProcess);
                }

                currentProcess.setRemainingTime(currentProcess.getRemainingTime() - 1);
                currentTime++;

                if (currentProcess.getRemainingTime() == 0) {
                    currentProcess.setTurnAroundTime(currentTime - currentProcess.getArrivalTime());
                    currentProcess.setWaitingTime(currentProcess.getTurnAroundTime() - currentProcess.getBurstTime());
                    completedProcesses++;
                    readyQueue.remove(currentProcess);
                }

                previousProcess = currentProcess;
            } else {
                currentTime++;
                previousProcess = null;
            }
        }

        return scheduledProcesses;
    }

    private void applyAging(List<CPUProcess> readyQueue, int currentTime) {
        for (CPUProcess process : readyQueue) {
            int waitingTime = currentTime - process.getArrivalTime();
            if (waitingTime > AGING_THRESHOLD) {
                process.setAgingPriority(process.getAgingPriority() + AGING_INCREMENT);
            }
        }
    }
}