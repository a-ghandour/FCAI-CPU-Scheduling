package main.java.com.model;
import java.awt.Color;

public class CPUProcess {
    private String name;
    private Color color;
    private int burstTime;
    private int arrivalTime;
    private int fakeArrivalTime;
    private int priority;
    private int turnAroundTime;
    private int waitingTime;
    private int remainingTime;
    private int agingPriority = 0;
    private int quantum;
    private double FCAIFactor;

    private CPUProcess(String name, int arrivalTime, int burstTime, int priority, int quantum, Color color, int waitingTime, int turnAroundTime)
    {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
        this.turnAroundTime = turnAroundTime;
        this.fakeArrivalTime = arrivalTime;
        this.color = color;
        this.waitingTime = waitingTime;
        this.quantum = quantum;
    }
    
    public CPUProcess(String name, int arrivalTime, int burstTime, int priority)
    {
        this(name, arrivalTime, burstTime, priority, 0, Color.DARK_GRAY, 0, 0);
    }

    public CPUProcess(String name, int arrivalTime, int burstTime, int priority, int quantum)
    {
        this(name, arrivalTime, burstTime, priority, quantum, Color.DARK_GRAY, 0, 0);
    }
    
    public CPUProcess(String name, int arrivalTime, int burstTime)
    {
        this(name, arrivalTime, burstTime, 0, 0,Color.DARK_GRAY, 0, 0);
    }
    
    public void setBurstTime(int burstTime)
    {
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
    }

    public void setFakeArrivalTime(int currnetTime)
    {
        this.fakeArrivalTime = currnetTime;
    }

    public void setWaitingTime(int waitingTime)
    {
        this.waitingTime = waitingTime;
    }
    public void setColor(Color color)
    {
        this.color = color;
    }

    public void setTurnAroundTime(int turnAroundTime)
    {
        this.turnAroundTime = turnAroundTime;
    }
    public void setRemainingTime(int remainingTime)
    {
        this.remainingTime = remainingTime;
    }
    public void setAgingPriority(int agingPriority) {
        this.agingPriority = agingPriority;
    }
    public int getAgingPriority() {
        return agingPriority;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    public void setFCAIFactor(double FCAIFactor) {
        this.FCAIFactor = FCAIFactor;
    }

    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }
    public double getFCAIFactor() {
        return FCAIFactor;
    }

    public String getName()
    {
        return this.name;
    }

    public int getQuantum() {
        return quantum;
    }

    public int getArrivalTime()
    {
        return this.arrivalTime;
    }

    public int getFakeArrivalTime()
    {
        return this.fakeArrivalTime;
    }

    public int getBurstTime()
    {
        return this.burstTime;
    }
    
    public int getPriority()
    {
        return this.priority;
    }
    
    public int getWaitingTime()
    {
        return this.waitingTime;
    }
    
    public int getTurnAroundTime()
    {
        return this.turnAroundTime;
    }
    public int getRemainingTime()
    {
        return this.remainingTime;
    }

    public Color getColor() {
        return color; // Getter for color
    }
}
