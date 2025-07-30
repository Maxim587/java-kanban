package ru.educationmm.taskmanager.main.util;

import ru.educationmm.taskmanager.main.service.*;

import java.io.File;

public class Managers {

    private Managers() {
    }

    private static final String TASK_MANAGER_FILE_PATH = System.getProperty("user.home") +
            File.separator + "FileBackedTaskManager.csv";

    public static TaskManager getDefault() {

        return new FileBackedTaskManager(new File(TASK_MANAGER_FILE_PATH));
    }

    public static HistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }
}
