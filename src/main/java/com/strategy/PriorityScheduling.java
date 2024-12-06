package main.java.com.strategy;

import main.java.com.model.CPUProcess;
import java.util.Comparator;
import java.util.List;

public class PriorityScheduling implements SchedulingStrategy {

    @Override
    public List<CPUProcess> schedule(List<CPUProcess> processes) {
       processes.sort(Comparator.comparingInt(CPUProcess::getArrivalTime)
               .thenComparingInt(CPUProcess::getPriority));

        int currentTime = 0;
        for (CPUProcess process : processes) {
            if (currentTime < process.getArrivalTime()) {
                currentTime = process.getArrivalTime();
            }
            process.setWaitingTime(currentTime - process.getArrivalTime());
            currentTime += process.getBurstTime();
            process.setTurnAroundTime(process.getWaitingTime() + process.getBurstTime());
        }
        return processes;
    }
}
