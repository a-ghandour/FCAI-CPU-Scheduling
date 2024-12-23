package main.java.com.strategy;

import main.java.com.model.CPUProcess;

import java.util.List;
import java.util.Comparator;
import java.util.ArrayList;

public class SJFScheduling implements SchedulingStrategy {
    @Override
    public List<CPUProcess> schedule(List<CPUProcess> processes, int contextSwitchingTime) {
        processes.sort(Comparator.comparingInt(p -> p.getArrivalTime()));
        int n = processes.size();
        List<CPUProcess> readyQueue = new ArrayList<>();
        List<CPUProcess> scheduledProcesses = new ArrayList<>();
        int currentTime = 0, completed = 0, cur = 0;
        while (completed < n) {
            while (cur < n && processes.get(cur).getArrivalTime() <= currentTime) {
                readyQueue.add(processes.get(cur));
                cur++;
            }
            for (CPUProcess process : readyQueue) {
                process.setWaitingTime(currentTime - process.getArrivalTime());
            }

            // Select the process with the shortest burst time or highest priority
            readyQueue.sort((p1, p2) -> {
                int prr1 = p1.getWaitingTime() * p2.getBurstTime(), prr2 = p2.getWaitingTime() * p1.getBurstTime();
                if (prr1 != prr2)
                    return Integer.compare(prr2, prr1);
                return Integer.compare(p1.getBurstTime(), p2.getBurstTime());
            });

            if (!readyQueue.isEmpty()) {
                CPUProcess currentProcess = readyQueue.removeFirst();
                currentTime += currentProcess.getBurstTime();
                currentProcess.setTurnAroundTime(currentTime - currentProcess.getArrivalTime());
                currentProcess.setWaitingTime(currentProcess.getTurnAroundTime() - currentProcess.getBurstTime());
                completed++;
                scheduledProcesses.add(currentProcess);
                currentTime += contextSwitchingTime;
            } else {
                // If no process is ready, increment time
                currentTime++;
            }
        }
        return scheduledProcesses;
    }

}
