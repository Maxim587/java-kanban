package ru.educationmm.taskmanager.test;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.educationmm.taskmanager.main.model.Task;
import ru.educationmm.taskmanager.main.model.TaskStatus;
import ru.educationmm.taskmanager.main.service.HistoryManager;
import ru.educationmm.taskmanager.main.service.InMemoryHistoryManager;
import ru.educationmm.taskmanager.main.service.InMemoryTaskManager;
import ru.educationmm.taskmanager.main.service.TaskManager;


class InMemoryHistoryManagerTest {

    HistoryManager historyManager;
    Task task;

    @BeforeEach
    public void prepare() {
        historyManager = new InMemoryHistoryManager();
        task = new Task("test", "test", TaskStatus.NEW);
    }

    @Test
    public void add() {
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        Assertions.assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        Assertions.assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
    }

    @Test
    public void managerShouldSavePreviousTaskVersions() {
        TaskManager taskManager = new InMemoryTaskManager();
        taskManager.addTask(task);
        taskManager.getTaskById(1); //добавление задачи в историю

        task.setName("New name");
        task.setDescription("New description");
        task.setStatus(TaskStatus.IN_PROGRESS);

        Assertions.assertNotEquals(task.getName(), taskManager.getHistory().getFirst().getName(), "В истории " +
                "не сохраняется предыдущая версия названия задачи");
        Assertions.assertNotEquals(task.getDescription(), taskManager.getHistory().getFirst().getDescription(), "В истории " +
                "не сохраняется предыдущая версия описания задачи");
        Assertions.assertNotEquals(task.getStatus(), taskManager.getHistory().getFirst().getStatus(), "В истории " +
                "не сохраняется предыдущая версия статуса задачи");
    }

    @Test
    public void historySizeShouldNotBeMoreThan10() {
        for (int i = 0; i < 11; i++) {
            historyManager.add(new Task(task));
        }
        Assertions.assertEquals(10, historyManager.getHistory().size(), "История просмотров " +
                "содержит более 10 элементов");
    }
}