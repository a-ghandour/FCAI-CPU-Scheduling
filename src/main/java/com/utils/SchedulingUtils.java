package main.java.com.utils;

import main.java.com.model.CPUProcess;
import java.util.Comparator;
import java.util.List;

public class SchedulingUtils {
    public static double calculateV1(List<CPUProcess> processes) {
        double lastArrivalTime = processes.stream().mapToDouble(CPUProcess::getArrivalTime).max().orElse(1);
        return Math.ceil(lastArrivalTime / 10);
    }

    public static double calculateV2(List<CPUProcess> processes) {
        double maxBurstTime = processes.stream().mapToDouble(CPUProcess::getBurstTime).max().orElse(1);
        return Math.ceil(maxBurstTime / 10);
    }

    public static double calculateFCAIFactor(CPUProcess process, double v1, double v2) {
        return Math.ceil((10 - process.getPriority()) + Math.ceil(process.getArrivalTime() / v1) + Math.ceil(process.getRemainingTime() / v2));
    }
}
