package main.java.com.strategy;

import java.util.List;

import main.java.com.model.CPUProcess;

public interface SchedulingStrategy {

    List<CPUProcess> schedule(List<CPUProcess> processes, int contextSwitchingTime);
}
