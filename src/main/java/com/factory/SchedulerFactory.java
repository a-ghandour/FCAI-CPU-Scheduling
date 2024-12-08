package main.java.com.factory;

import main.java.com.strategy.PriorityScheduling;
import main.java.com.strategy.SRTFScheduling;
import main.java.com.strategy.SchedulingStrategy;

public class SchedulerFactory {
    public static SchedulingStrategy getScheduler(String type) {
        switch (type.toLowerCase()) {
            case "priority":
                return new PriorityScheduling();
            case "srtf":
                return new SRTFScheduling();
            default:
                throw new IllegalArgumentException("Unknown scheduler type: " + type);
        }
    }
}
