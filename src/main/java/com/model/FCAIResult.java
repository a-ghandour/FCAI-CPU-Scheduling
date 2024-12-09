package main.java.com.model;

import java.util.List;

public class FCAIResult {
    private final List<String> messages;
    private final List<CPUProcess> executionOrder;

    public FCAIResult(List<String> messages, List<CPUProcess> executionOrder) {
        this.messages = messages;
        this.executionOrder = executionOrder;
    }

    public List<String> getMessages() {
        return messages;
    }

    public List<CPUProcess> getExecutionOrder() {
        return executionOrder;
    }
}
