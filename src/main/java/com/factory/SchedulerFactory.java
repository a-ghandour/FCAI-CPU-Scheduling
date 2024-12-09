package main.java.com.factory;

import main.java.com.strategy.*;

public class SchedulerFactory {
    public static SchedulingStrategy getScheduler(String type) {
        switch (type.toLowerCase()) {
            case "priority":
                return new PriorityScheduling();
            case "srtf":
                return new SRTFScheduling();
            case "fcai":
                return new FCAIScheduling();
            case "sjf":
                return new SJFScheduling();
            default:
                throw new IllegalArgumentException("Unknown scheduler type: " + type);
        }
    }
}
