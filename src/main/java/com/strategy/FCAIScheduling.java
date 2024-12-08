package main.java.com.strategy;
import java.util.List;
import main.java.com.model.CPUProcess;
package org.os;
import java.util.*;

class Process {
    String name;
    double burstTime, arrivalTime, priority, quantum, remainingBurstTime, waitingTime , TurnaroundTime;
    double fcaiFactor;

    public Process(String name, int burstTime, int arrivalTime, int priority, int quantum) {
        this.name = name;
        this.burstTime = burstTime;
        this.remainingBurstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.priority = priority;
        this.quantum = quantum;
        waitingTime = 0 ;
        TurnaroundTime = 0;
    }

    public double getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(double waitingTime) {
        this.waitingTime = waitingTime;
    }

    public double getTurnaroundTime() {
        return TurnaroundTime;
    }

    public void setTurnaroundTime(double turnaroundTime) {
        TurnaroundTime = turnaroundTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBurstTime() {
        return burstTime;
    }

    public void setBurstTime(double burstTime) {
        this.burstTime = burstTime;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getPriority() {
        return priority;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }

    public double getQuantum() {
        return quantum;
    }

    public void setQuantum(double quantum) {
        this.quantum = quantum;
    }

    public double getRemainingBurstTime() {
        return remainingBurstTime;
    }

    public void setRemainingBurstTime(double remainingBurstTime) {
        this.remainingBurstTime = remainingBurstTime;
    }

    public double getFcaiFactor() {
        return fcaiFactor;
    }

    public void setFcaiFactor(double fcaiFactor) {
        this.fcaiFactor = fcaiFactor;
    }

    public void calculateFCAIFactor(double v1, double v2) {
        this.fcaiFactor = Math.ceil((10 - priority) + Math.ceil(arrivalTime / v1) + Math.ceil(remainingBurstTime / v2));
    }

    @Override
    public String toString() {
        return String.format(
                "Process %s -> Burst Time: %.2f, Arrival Time: %.2f, Priority: %.2f, Quantum: %.2f, FCAI Factor: %.2f",
                name, burstTime, arrivalTime, priority, quantum, fcaiFactor
        );
    }
}

public class FCAIScheduler {
    public static void main(String[] args) {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process("P1", 17, 0, 4, 4));
        processes.add(new Process("P2", 6, 3, 9, 3));
        processes.add(new Process("P3", 10, 4, 3, 5));
        processes.add(new Process("P4", 4, 29, 10, 2));

        // Calculate V1 and V2
        double lastArrivalTime = processes.stream().mapToDouble(p -> p.arrivalTime).max().orElse(1);
        double maxBurstTime = processes.stream().mapToDouble(p -> p.burstTime).max().orElse(1);
        double v1 = Math.ceil(lastArrivalTime / 10);
        double v2 = Math.ceil(maxBurstTime / 10);

        // Initialize FCAI factors
        for (Process process : processes) {
            process.calculateFCAIFactor(v1, v2);
        }

        // Priority Queue for scheduling based on FCAI factor
        Deque<Process> readyQueue = new LinkedList<>() ;
        int currentTime = 0;

        while (!processes.isEmpty() || !readyQueue.isEmpty()) {
            // Add arriving processes to the ready queue
            Iterator<Process> iterator = processes.iterator();
            while (iterator.hasNext()) {
                Process process = iterator.next();
                if (process.arrivalTime <= currentTime) {
                    readyQueue.addLast(process);
                    iterator.remove();
                }
            }

            // Execute the process with the lowest FCAI factor
            if (!readyQueue.isEmpty()) {
                Process currentProcess = readyQueue.pollFirst();
                double quantum = currentProcess.getQuantum() ;
                double execTime = Math.ceil(quantum * 0.4);
                double unusedQuantum = currentProcess.getQuantum() - execTime ;

                execTime = Math.min(execTime, currentProcess.getRemainingBurstTime());
                currentTime += execTime ;

                currentProcess.setRemainingBurstTime(currentProcess.getRemainingBurstTime() - execTime);
                currentProcess.calculateFCAIFactor(v1,v2);

                iterator = processes.iterator();
                while (iterator.hasNext()) {
                    Process newProcess = iterator.next();
                    if (newProcess.arrivalTime <= currentTime) {
                        readyQueue.addLast(newProcess);
                        iterator.remove();
                        System.out.printf("Time %d: Process %s arrived and added to the ready queue.\n", currentTime, newProcess.name);
                    }
                }


                Process preemptive = null ;
                for (Process process : readyQueue) {
                    if (process.getFcaiFactor() <= currentProcess.getFcaiFactor()) {
                        preemptive = process;
                    }
                }

                if (preemptive != null) {
                    currentProcess.setQuantum(currentProcess.getQuantum() + unusedQuantum);
                    readyQueue.addLast(currentProcess);
                    currentProcess.calculateFCAIFactor(v1, v2);

                    Process process = readyQueue.stream()
                            .min(Comparator.comparingDouble(Process::getFcaiFactor))
                            .orElse(null);

                    readyQueue.remove(process);
                    readyQueue.addFirst(process);

                    System.out.println("Process " + preemptive.getName() + " preemptived " + currentProcess.getName());
                    System.out.printf(
                            "Time %d: Process %s executed for 1 unit\nRemaining Burst Time: %.2f, Remaining Quantum: %.2f, FCAI Factor: %.2f\n",
                            currentTime, currentProcess.name, currentProcess.remainingBurstTime, currentProcess.quantum, currentProcess.fcaiFactor);
                    continue;
                }
            //print completed
                if (currentProcess.remainingBurstTime <= 0) {
                    currentProcess.setTurnaroundTime(currentProcess.getBurstTime() - currentProcess.getArrivalTime());
                    currentProcess.setWaitingTime(currentProcess.getTurnaroundTime() - currentProcess.getBurstTime());
                    System.out.printf(
                            "Time %d: Process %s executed for 1 unit\nRemaining Burst Time: %.2f, Remaining Quantum: %.2f, FCAI Factor: %.2f\n",
                            currentTime, currentProcess.name, currentProcess.remainingBurstTime, currentProcess.quantum, currentProcess.fcaiFactor);
                    System.out.printf("Time %d: Process %s completed.\n\n", currentTime, currentProcess.name);
                    continue;
                }


                while (currentProcess.remainingBurstTime > 0 && unusedQuantum > 0) {
                    // Execute the process for 1 unit of time
                    double remainTime = currentProcess.getRemainingBurstTime() - 1;
                    currentProcess.setRemainingBurstTime(remainTime);
                    unusedQuantum--;
                    currentTime++;

                    // Update FCAI factor for the current process
                    currentProcess.calculateFCAIFactor(v1, v2);

                //print completed
                    if (currentProcess.remainingBurstTime <= 0) {
                        currentProcess.setTurnaroundTime(currentProcess.getBurstTime() - currentProcess.getArrivalTime());
                        currentProcess.setWaitingTime(currentProcess.getTurnaroundTime() - currentProcess.getBurstTime());
                        System.out.printf(
                                "Time %d: Process %s executed for 1 unit\nRemaining Burst Time: %.2f, Remaining Quantum: %.2f, FCAI Factor: %.2f\n",
                                currentTime, currentProcess.name, currentProcess.remainingBurstTime, currentProcess.quantum, currentProcess.fcaiFactor);
                        System.out.printf("Time %d: Process %s completed.\n\n", currentTime, currentProcess.name);
                        break;
                    }

                    System.out.printf(
                            "Time %d: Process %s executed for 1 unit\nRemaining Burst Time: %.2f, Remaining Quantum: %.2f, FCAI Factor: %.2f\n",
                            currentTime, currentProcess.name, currentProcess.remainingBurstTime, currentProcess.quantum, currentProcess.fcaiFactor);

                    // Check for newly arriving processes and add them to the ready queue
                    iterator = processes.iterator();
                    while (iterator.hasNext()) {
                        Process newProcess = iterator.next();
                        if (newProcess.arrivalTime <= currentTime) {
                            readyQueue.addLast(newProcess);
                            iterator.remove();
                            System.out.printf("Time %d: Process %s arrived and added to the ready queue.\n", currentTime, newProcess.name);
                        }
                    }

                    preemptive = null ;
                    for (Process process : readyQueue) {
                        if (process.getFcaiFactor() <= currentProcess.getFcaiFactor()) {
                            preemptive = process;
                        }
                    }


                    if (preemptive != null) {
                        currentProcess.setQuantum(currentProcess.getQuantum() + unusedQuantum);
                        readyQueue.addLast(currentProcess);
                        currentProcess.calculateFCAIFactor(v1, v2);

                        Process process = readyQueue.stream()
                                .min(Comparator.comparingDouble(Process::getFcaiFactor))
                                .orElse(null);

                        readyQueue.remove(process);
                        readyQueue.addFirst(process);
                        break;
                    }


                    //print completed
                    if (currentProcess.remainingBurstTime <= 0) {
                        currentProcess.setTurnaroundTime(currentProcess.getBurstTime() - currentProcess.getArrivalTime());
                        currentProcess.setWaitingTime(currentProcess.getTurnaroundTime() - currentProcess.getBurstTime());
                        System.out.printf(
                                "Time %d: Process %s executed for 1 unit\nRemaining Burst Time: %.2f, Remaining Quantum: %.2f, FCAI Factor: %.2f\n",
                                currentTime, currentProcess.name, currentProcess.remainingBurstTime, currentProcess.quantum, currentProcess.fcaiFactor);
                        System.out.printf("Time %d: Process %s completed.\n\n", currentTime, currentProcess.name);
                        break;
                    }
                    else if (unusedQuantum <= 0){
                        currentProcess.setQuantum(currentProcess.getQuantum() + 2);
                        readyQueue.addLast(currentProcess);
                        break;
                    }
                }
            } else {
                // If no process in readyQueue, increment time
                currentTime++;
            }
        }
    }



}
