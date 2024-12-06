package main.java.com.factory;

import main.java.com.strategy.PriorityScheduling;
import main.java.com.strategy.SchedulingStrategy;

public class SchedulerFactory {
    public static SchedulingStrategy getScheduler(String type) {
        switch (type.toLowerCase()) {
            case "priority":
                return new PriorityScheduling();
            // Add other strategies as needed
            default:
                throw new IllegalArgumentException("Unknown scheduler type: " + type);
        }
    }
}
