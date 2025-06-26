package ru.educationmm.taskmanager.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ru.educationmm.taskmanager.main.service.HistoryManager;
import ru.educationmm.taskmanager.main.service.InMemoryHistoryManager;
import ru.educationmm.taskmanager.main.service.InMemoryTaskManager;
import ru.educationmm.taskmanager.main.service.TaskManager;
import ru.educationmm.taskmanager.main.util.Managers;

class ManagersTest {

    TaskManager taskManager;

    @Test
    public void getDefault() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Assertions.assertInstanceOf(InMemoryHistoryManager.class, historyManager, "Менеджер должен вернуть " +
                "объект InMemoryHistoryManager");
    }

    @Test
    public void getDefaultHistory() {
        taskManager = Managers.getDefault();
        Assertions.assertInstanceOf(InMemoryTaskManager.class, taskManager, "Менеджер должен вернуть " +
                "объект InMemoryTaskManager");
    }

}