package ru.educationmm.taskmanager.main.util;

import ru.educationmm.taskmanager.main.service.FileBackedTaskManager;
import ru.educationmm.taskmanager.main.service.HistoryManager;
import ru.educationmm.taskmanager.main.service.InMemoryHistoryManager;
import ru.educationmm.taskmanager.main.service.TaskManager;

import java.io.File;

public class Managers {

    private static final String TASK_MANAGER_FILE_PATH = System.getProperty("user.home") +
            File.separator + "FileBackedTaskManager.csv";

    private Managers() {
    }

    public static TaskManager getDefault() {

        return new FileBackedTaskManager(new File(TASK_MANAGER_FILE_PATH));
    }

    public static HistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }
}
