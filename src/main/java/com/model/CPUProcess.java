package main.java.com.model;

public class CPUProcess {
    private String name;
    private String color;
    private int burstTime;
    private int arrivalTime;
    private int fakeArrivalTime;
    private int priority;
    private int turnAroundTime;
    private int waitingTime;
    private int remainingTime;

    private CPUProcess(String name, int arrivalTime, int burstTime, int priority, int waitingTime, int turnAroundTime)
    {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
        this.turnAroundTime = turnAroundTime;
        this.fakeArrivalTime = arrivalTime;
    }

    public CPUProcess(String name, int arrivalTime, int burstTime, int priority)
    {
        this(name, arrivalTime, burstTime, priority, 0, 0);
    }

    public CPUProcess(String name, int arrivalTime, int burstTime)
    {
        this(name, arrivalTime, burstTime, 0, 0, 0);
    }

    public void setBurstTime(int burstTime)
    {
        this.burstTime = burstTime;
    }

    public void setFakeArrivalTime(int currnetTime)
    {
        this.fakeArrivalTime = currnetTime;
    }

    public void setWaitingTime(int waitingTime)
    {
        this.waitingTime = waitingTime;
    }

    public void setTurnAroundTime(int turnAroundTime)
    {
        this.turnAroundTime = turnAroundTime;
    }

    public String getName()
    {
        return this.name;
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
}