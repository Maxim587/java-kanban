package ru.educationmm.taskmanager.main.util;

import ru.educationmm.taskmanager.main.service.*;

import java.io.File;

public class Managers {

    private Managers() {
    }

    public static TaskManager getDefault() {

        return new FileBackedTaskManager(new File(System.getProperty("user.home") +
                File.separator + "FileBackedTaskManager.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
