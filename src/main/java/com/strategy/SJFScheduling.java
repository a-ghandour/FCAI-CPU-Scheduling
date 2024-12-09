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
        int currentTime = 0, completed = 0, cur = 0;
        while (completed < n) {while (cur < n && processes.get(cur).getArrivalTime() <= currentTime) {
                readyQueue.add(processes.get(cur));
                cur++;
            }
            for (CPUProcess process : readyQueue)
                process.setPriority(process.getPriority() + 1);

            // Select the process with the shortest burst time or highest priority
            readyQueue.sort((p1, p2) -> {
                if (p1.getBurstTime() == p2.getBurstTime())
                    return Integer.compare(p2.getPriority(), p1.getPriority());
                return Integer.compare(p1.getBurstTime(), p2.getBurstTime());
            });

            if (!readyQueue.isEmpty()) {
                CPUProcess currentProcess = readyQueue.removeFirst();
                currentTime += currentProcess.getBurstTime();
                currentProcess.setTurnAroundTime(currentTime - currentProcess.getArrivalTime());
                currentProcess.setWaitingTime(currentProcess.getTurnAroundTime() - currentProcess.getBurstTime());
                completed++;
            }
            else {
                // If no process is ready, increment time
                currentTime++;
            }
        }
        return processes;
    }

}
