package main.java.com.main;

import javax.swing.SwingUtilities;
import main.java.com.view.CPUSchedulerGUI;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CPUSchedulerGUI().setVisible(true);
        });
    }
}
