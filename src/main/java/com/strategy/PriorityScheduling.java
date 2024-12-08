package main.java.com.strategy;

import main.java.com.model.CPUProcess;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PriorityScheduling implements SchedulingStrategy {

    @Override
    public List<CPUProcess> schedule(List<CPUProcess> processes, int contextSwitchTime) {
       processes.sort(Comparator.comparingInt(CPUProcess::getArrivalTime)
               .thenComparingInt(CPUProcess::getPriority));
        List<CPUProcess> scheduledProcesses = new ArrayList<>();
        int currentTime = 0;
        while (!processes.isEmpty()){
            if (currentTime < processes.getFirst().getArrivalTime()) {
                currentTime = processes.getFirst().getArrivalTime();
            }
            processes.getFirst().setWaitingTime(currentTime - processes.getFirst().getArrivalTime());
            currentTime += processes.getFirst().getBurstTime();
            processes.getFirst().setTurnAroundTime(currentTime);
            scheduledProcesses.add(processes.remove(0));
            currentTime += contextSwitchTime;
            for (CPUProcess process : processes) {
                if (process.getArrivalTime() < currentTime)
                    process.setFakeArrivalTime(currentTime);
                else break;
            }
            processes.sort(Comparator.comparingInt(CPUProcess::getFakeArrivalTime)
                    .thenComparingInt(CPUProcess::getPriority));
        }
        return scheduledProcesses;
    }
}
