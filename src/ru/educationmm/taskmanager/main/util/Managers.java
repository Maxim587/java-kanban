package ru.educationmm.taskmanager.main.util;

import ru.educationmm.taskmanager.main.service.HistoryManager;
import ru.educationmm.taskmanager.main.service.InMemoryHistoryManager;
import ru.educationmm.taskmanager.main.service.InMemoryTaskManager;
import ru.educationmm.taskmanager.main.service.TaskManager;


public class Managers {

    private Managers() {}

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
