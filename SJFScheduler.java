import java.util.*;

class Process {
    String name;
    int burstTime;
    int arrivalTime;
    int waitingTime = 0;
    int turnaroundTime = 0;
    int priority = 0; // Used to solve starvation problem

    Process(String name, int burstTime, int arrivalTime) {
        this.name = name;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
    }
}

public class SJFScheduler {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of processes: ");
        int n = scanner.nextInt();
        List<Process> processes = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            System.out.print("Enter process name, burst time, and arrival time for process " + (i + 1) + ": ");
            String name = scanner.next();
            int burstTime = scanner.nextInt();
            int arrivalTime = scanner.nextInt();
            processes.add(new Process(name, burstTime, arrivalTime));
        }
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        // SJF Scheduling with Aging
        List<Process> readyQueue = new ArrayList<>();
        int currentTime = 0, completed = 0, cur = 0;
        double totalWaitingTime = 0, totalTurnaroundTime = 0;

        System.out.println("\nExecution Order:");
        while (completed < n) {
            // Add processes that have arrived by the current time to the ready queue
            while (cur < n && processes.get(cur).arrivalTime <= currentTime) {
                readyQueue.add(processes.get(cur));
                cur++;
            }

            // Increase priority for waiting processes
            for (Process process : readyQueue)
                process.priority++;

            // Select the process with the shortest burst time or highest priority
            readyQueue.sort((p1, p2) -> {
                if (p1.burstTime == p2.burstTime)
                    return Integer.compare(p2.priority, p1.priority); // Higher priority first
                return Integer.compare(p1.burstTime, p2.burstTime); // Shorter burst time first
            });

            if (!readyQueue.isEmpty()) {
                Process currentProcess = readyQueue.removeFirst();
                System.out.println("Time " + currentTime + " -> Process " + currentProcess.name);

                currentTime += currentProcess.burstTime;
                currentProcess.turnaroundTime = currentTime - currentProcess.arrivalTime;
                currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;

                totalWaitingTime += currentProcess.waitingTime;
                totalTurnaroundTime += currentProcess.turnaroundTime;
                completed++;
            } else {
                // If no process is ready, increment time
                currentTime++;
            }
        }

        System.out.println("\nProcess Details:");
        System.out.printf("%-10s%-15s%-15s%-15s%-15s\n",
                "Process", "Burst Time", "Arrival Time", "Waiting Time", "Turnaround Time");
        for (Process process : processes) {
            System.out.printf("%-10s%-15d%-15d%-15d%-15d\n",
                    process.name, process.burstTime, process.arrivalTime,
                    process.waitingTime, process.turnaroundTime);
        }

        System.out.printf("\nAverage Waiting Time: %.2f\n", totalWaitingTime / n);
        System.out.printf("Average Turnaround Time: %.2f\n", totalTurnaroundTime / n);

    }
}