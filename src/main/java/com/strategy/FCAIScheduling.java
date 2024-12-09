package main.java.com.strategy;

import main.java.com.model.*;
import main.java.com.utils.SchedulingUtils;
import java.util.*;

public class FCAIScheduling implements SchedulingStrategy {
    List<String> output = new ArrayList<>();
    List<String> ganttTimeline = new ArrayList<>();

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
            // Add arriving processes to the ready queue
            Iterator<CPUProcess> iterator = processes.iterator();
            while (iterator.hasNext()) {
                CPUProcess process = iterator.next();
                if (process.getArrivalTime() <= currentTime) {
                    readyQueue.addLast(process);
                    iterator.remove();
                }
            }

            if (!readyQueue.isEmpty()) {
                CPUProcess currentProcess = readyQueue.pollFirst();
                ganttTimeline.add("Time " + currentTime + ": " + currentProcess.getName() + " starts execution.");

                double quantum = currentProcess.getQuantum();
                double execTime = Math.ceil(quantum * 0.4);
                double unusedQuantum = quantum - execTime;
                execTime = Math.min(execTime, currentProcess.getRemainingTime());
                currentTime += execTime;
                currentProcess.setRemainingTime(currentProcess.getRemainingTime() - (int)execTime);
                currentProcess.setFCAIFactor(Math.ceil(SchedulingUtils.calculateFCAIFactor(currentProcess, v1, v2)));

                iterator = processes.iterator();
                while (iterator.hasNext()) {
                    CPUProcess newProcess = iterator.next();
                    if (newProcess.getArrivalTime() <= currentTime) {
                        readyQueue.addLast(newProcess);
                        iterator.remove();
                    }
                }

                CPUProcess preemptive = null ;
                for (CPUProcess process : readyQueue) {
                    if (process.getFCAIFactor() <= currentProcess.getFCAIFactor()) {
                        preemptive = process;
                    }
                }

                if (preemptive != null) {
                    ganttTimeline.add("Time " + currentTime + ": " + currentProcess.getName() + " starts execution.");
                    currentProcess.setQuantum(currentProcess.getQuantum() + (int)unusedQuantum);
                    readyQueue.addLast(currentProcess);
                    currentProcess.setFCAIFactor(Math.ceil(SchedulingUtils.calculateFCAIFactor(currentProcess, v1, v2)));

                    CPUProcess process = readyQueue.stream()
                            .min(Comparator.comparingDouble(CPUProcess::getFCAIFactor))
                            .orElse(null);

                    readyQueue.remove(process);
                    readyQueue.addFirst(process);
                    output.add(("Time " +currentTime + ": Process "+currentProcess.getName()+
                                    " executed for 1 unit\nRemaining Burst Time: "+currentProcess.getRemainingTime()+
                                    ", Remaining Quantum: "+currentProcess.getQuantum()+", FCAI Factor: "+currentProcess.getFCAIFactor() +"\n"));
                    output.add(("Process " + preemptive.getName() + " preempted " + currentProcess.getName()));
                    continue;
                }
                if (currentProcess.getRemainingTime() <= 0) {
                    ganttTimeline.add("Time " + currentTime + ": " + currentProcess.getName() + " starts execution.");
                    currentProcess.setTurnAroundTime(currentTime - currentProcess.getArrivalTime());
                    currentProcess.setWaitingTime(currentProcess.getTurnAroundTime() - currentProcess.getBurstTime());
                    output.add(("Time " +currentTime + ": Process "+currentProcess.getName()+
                            " executed for 1 unit\nRemaining Burst Time: "+currentProcess.getRemainingTime()+
                            ", Remaining Quantum: "+currentProcess.getQuantum()+", FCAI Factor: Completed" +"\n"));
                    output.add(("Time "+currentTime+": Process "+currentProcess.getName()+" completed.\n\n"));
                    scheduledProcesses.add(currentProcess);
                    continue;
                }

                while (currentProcess.getRemainingTime() > 0 && unusedQuantum > 0) {
                    // Execute the process for 1 unit of time
                    int remainTime = currentProcess.getRemainingTime() - 1;
                    currentProcess.setRemainingTime(remainTime);
                    unusedQuantum--;
                    currentTime++;

                    // Update FCAI factor for the current process
                    currentProcess.setFCAIFactor(Math.ceil(SchedulingUtils.calculateFCAIFactor(currentProcess, v1, v2)));
                    ganttTimeline.add("Time " + currentTime + ": " + currentProcess.getName() + " starts execution.");

                    //print completed
                    if (currentProcess.getRemainingTime() <= 0) {
                        currentProcess.setTurnAroundTime(currentTime - currentProcess.getArrivalTime());
                        currentProcess.setWaitingTime(currentProcess.getTurnAroundTime() - currentProcess.getBurstTime());
                        output.add(("Time " +currentTime + ": Process "+currentProcess.getName()+
                                " executed for 1 unit\nRemaining Burst Time: "+currentProcess.getRemainingTime()+
                                ", Remaining Quantum: "+currentProcess.getQuantum()+", FCAI Factor: Completed " +"\n"));
                        output.add(("Time "+currentTime+": Process "+currentProcess.getName()+" completed.\n\n"));
                        scheduledProcesses.add(currentProcess);
                        break;
                    }
                    output.add(("Time " +currentTime + ": Process "+currentProcess.getName()+
                            " executed for 1 unit\nRemaining Burst Time: "+currentProcess.getRemainingTime()+
                            ", Remaining Quantum: "+currentProcess.getQuantum()+", FCAI Factor: "+currentProcess.getFCAIFactor() +"\n"));

                    // Check for newly arriving processes and add them to the ready queue
                    iterator = processes.iterator();
                    while (iterator.hasNext()) {
                        CPUProcess newProcess = iterator.next();
                        if (newProcess.getArrivalTime() <= currentTime) {
                            readyQueue.addLast(newProcess);
                            iterator.remove();
                        }
                    }

                    preemptive = null ;
                    for (CPUProcess process : readyQueue) {
                        if (process.getFCAIFactor() <= currentProcess.getFCAIFactor()) {
                            preemptive = process;
                        }
                    }

                    if (preemptive != null) {
                        ganttTimeline.add("Time " + currentTime + ": " + currentProcess.getName() + " starts execution.");
                        currentProcess.setQuantum(currentProcess.getQuantum() + (int)unusedQuantum);
                        readyQueue.addLast(currentProcess);
                        currentProcess.setFCAIFactor(Math.ceil(SchedulingUtils.calculateFCAIFactor(currentProcess, v1, v2)));

                        CPUProcess process = readyQueue.stream()
                                .min(Comparator.comparingDouble(CPUProcess::getFCAIFactor))
                                .orElse(null);

                        readyQueue.remove(process);
                        readyQueue.addFirst(process);
                        break;
                    }

                    //print completed
                    if (currentProcess.getRemainingTime() <= 0) {
                        ganttTimeline.add("Time " + currentTime + ": " + currentProcess.getName() + " starts execution.");
                        currentProcess.setTurnAroundTime(currentTime - currentProcess.getArrivalTime());
                        currentProcess.setWaitingTime(currentProcess.getTurnAroundTime() - currentProcess.getBurstTime());
                        output.add(("Time " +currentTime + ": Process "+currentProcess.getName()+
                                " executed for 1 unit\nRemaining Burst Time: "+currentProcess.getRemainingTime()+
                                ", Remaining Quantum: "+currentProcess.getQuantum()+", FCAI Factor: Completed" +"\n"));
                        output.add(("Time "+currentTime+": Process "+currentProcess.getName()+" completed.\n\n"));
                        scheduledProcesses.add(currentProcess);
                        break;
                    }
                    else if (unusedQuantum <= 0){
                        currentProcess.setQuantum(currentProcess.getQuantum() + 2);
                        readyQueue.addLast(currentProcess);
                        break;
                    }
                }
            }
            else {
                // If no process is ready, increment time
                ganttTimeline.add("Time " + currentTime + ": Idle");
                currentTime++;
            }
        }
        return scheduledProcesses;
    }

    public List<String> getOutputMessages() {
        return output;
    }

    public List<String> getGanttTimeline() {
        return ganttTimeline;
    }
}
