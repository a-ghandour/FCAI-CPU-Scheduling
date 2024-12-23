package main.java.com.strategy;

import main.java.com.model.*;
import main.java.com.utils.SchedulingUtils;
import java.util.*;

public class FCAIScheduling implements SchedulingStrategy {
    private List<String> output = new ArrayList<>();

    @Override
    public List<CPUProcess> schedule(List<CPUProcess> processes, int contextSwitchTime) {
        List<CPUProcess> scheduledProcesses = new ArrayList<>();
        Deque<CPUProcess> readyQueue = new LinkedList<>();

        // Calculate V1 and V2
        double v1 = SchedulingUtils.calculateV1(processes);
        double v2 = SchedulingUtils.calculateV2(processes);
        processes.forEach(process -> process.setFCAIFactor(SchedulingUtils.calculateFCAIFactor(process, v1, v2)));

        int currentTime = 0;

        while (!processes.isEmpty() || !readyQueue.isEmpty()) {
            // Add arriving processes to ready queue
            addArrivingProcesses(processes, readyQueue, currentTime);

            if (!readyQueue.isEmpty()) {
                CPUProcess currentProcess = readyQueue.pollFirst();

                // Calculate non-preemptive portion (40% of quantum)
                double quantum = currentProcess.getQuantum();
                double execTime = Math.ceil(quantum * 0.4);
                execTime = Math.min(execTime, currentProcess.getRemainingTime());

                // Execute non-preemptive portion
                output.add(String.format("Time %d: Process %s starts non-preemptive execution for %.0f units",
                        currentTime, currentProcess.getName(), execTime));

                currentTime += execTime;
                currentProcess.setRemainingTime(currentProcess.getRemainingTime() - (int)execTime);
                double remainingQuantum = quantum - execTime;
                currentProcess.setFCAIFactor(Math.ceil(SchedulingUtils.calculateFCAIFactor(currentProcess, v1, v2)));

                // Check completion after non-preemptive portion
                if (currentProcess.getRemainingTime() <= 0) {
                    completeProcess(currentProcess, currentTime, scheduledProcesses);
                    continue;
                }

                // Add any processes that arrived during non-preemptive execution
                addArrivingProcesses(processes, readyQueue, currentTime);

                CPUProcess preemptingProcess = findPreemptingProcess(readyQueue, currentProcess);
                if (preemptingProcess != null) {
                    // Handle preemption
                    Preemption(scheduledProcesses, readyQueue, currentTime, currentProcess, (int) remainingQuantum, preemptingProcess);
                    continue;
                }

                // Preemptive portion
                while (currentProcess.getRemainingTime() > 0 && remainingQuantum > 0) {
                    // Execute one time unit
                    currentTime++;
                    currentProcess.setRemainingTime(currentProcess.getRemainingTime() - 1);
                    remainingQuantum--;
                    currentProcess.setFCAIFactor(Math.ceil(SchedulingUtils.calculateFCAIFactor(currentProcess, v1, v2)));

                    // Add any new arriving processes
                    addArrivingProcesses(processes, readyQueue, currentTime);

                    // Check for completion
                    if (currentProcess.getRemainingTime() <= 0) {
                        completeProcess(currentProcess, currentTime, scheduledProcesses);
                        break;
                    }

                    // Check for preemption
                    preemptingProcess = findPreemptingProcess(readyQueue, currentProcess);
                    if (preemptingProcess != null) {
                        // Handle preemption
                        Preemption(scheduledProcesses, readyQueue, currentTime, currentProcess, (int) remainingQuantum, preemptingProcess);
                        break;
                    }
                    // Record execution progress
                    output.add(String.format("Time %d: Process %s executed for 1 unit, Remaining Burst: %d, Quantum: %d, FCAI Factor: %.2f",
                            currentTime, currentProcess.getName(), currentProcess.getRemainingTime(),
                            currentProcess.getQuantum(), currentProcess.getFCAIFactor()));
                }

                // Handle quantum expiration if process not completed or preempted
                if (currentProcess.getRemainingTime() > 0 && remainingQuantum <= 0) {
                    currentProcess.setQuantum(currentProcess.getQuantum() + 2);
                    output.add(String.format("Time %d: Process %s quantum expired, new quantum: %d",
                            currentTime, currentProcess.getName(), currentProcess.getQuantum()));
                    readyQueue.addLast(currentProcess);
                    scheduledProcesses.add(currentProcess);
                }
            } else {
                currentTime++;
            }
        }
        return scheduledProcesses;
    }

    private void addArrivingProcesses(List<CPUProcess> processes, Deque<CPUProcess> readyQueue, int currentTime) {
        Iterator<CPUProcess> iterator = processes.iterator();
        while (iterator.hasNext()) {
            CPUProcess process = iterator.next();
            if (process.getArrivalTime() <= currentTime) {
                readyQueue.addLast(process);
                iterator.remove();
                output.add(String.format("Time %d: Process %s arrived", currentTime, process.getName()));
            }
        }
    }

    private void Preemption(List<CPUProcess> scheduledProcesses, Deque<CPUProcess> readyQueue, int currentTime, CPUProcess currentProcess, int remainingQuantum, CPUProcess preemptingProcess) {
        currentProcess.setQuantum(currentProcess.getQuantum() + remainingQuantum);
        output.add(String.format("Time %d: Process %s preempted by Process %s - Process %s : Remaining Burst %d - Quantum %d - FCAI Factor %f",
                currentTime, currentProcess.getName(), preemptingProcess.getName(), currentProcess.getName(), currentProcess.getRemainingTime(),
                currentProcess.getQuantum(), currentProcess.getFCAIFactor()));
        readyQueue.addLast(currentProcess);
        readyQueue.remove(preemptingProcess);
        readyQueue.addFirst(preemptingProcess);

        scheduledProcesses.add(currentProcess);
    }

    private CPUProcess findPreemptingProcess(Deque<CPUProcess> readyQueue, CPUProcess currentProcess) {
        for (CPUProcess process : readyQueue) {
            if (process.getFCAIFactor() <= currentProcess.getFCAIFactor())
                return process;
        }
        return null;
    }

    private void completeProcess(CPUProcess process, int currentTime, List<CPUProcess> scheduledProcesses) {
        process.setTurnAroundTime(currentTime - process.getArrivalTime());
        process.setWaitingTime(process.getTurnAroundTime() - process.getBurstTime());
        output.add(String.format("Time %d: Process %s completed\n",
                currentTime, process.getName()));
        scheduledProcesses.add(process);
    }

    public List<String> getOutputMessages() {
        return output;
    }

}