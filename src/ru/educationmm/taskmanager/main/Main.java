package ru.educationmm.taskmanager.main;

import ru.educationmm.taskmanager.main.model.*;
import ru.educationmm.taskmanager.main.service.TaskManager;
import ru.educationmm.taskmanager.main.util.Managers;

import java.time.LocalDateTime;
import java.time.Month;

public class Main {

    public static void main(String[] args) {
        TaskManager fileBackedTaskManager = Managers.getDefault();
        LocalDateTime startTime = LocalDateTime.of(2025, Month.JUNE, 3, 22, 22);
        Task task1 = new Task("test", "test", TaskStatus.NEW, 5, startTime);
        Task task2 = new Task("test", "test", TaskStatus.NEW, 5, startTime.plusHours(1));
        Epic epic1 = new Epic("test", "test");
        Epic epic2 = new Epic("test", "test");
        fileBackedTaskManager.addTask(task1);
        fileBackedTaskManager.addTask(task2);
        fileBackedTaskManager.addEpic(epic1);
        fileBackedTaskManager.addEpic(epic2);
        Subtask subtask1 = new Subtask("test", "test", epic1.getId(), TaskStatus.NEW, 5, startTime.plusHours(2));
        Subtask subtask2 = new Subtask("test", "test", epic1.getId(), TaskStatus.NEW, 5, startTime.plusHours(3));
        Subtask subtask3 = new Subtask("test", "test", epic1.getId(), TaskStatus.NEW, 5, startTime.plusHours(4));
        fileBackedTaskManager.addSubtask(subtask1);
        fileBackedTaskManager.addSubtask(subtask2);
        fileBackedTaskManager.addSubtask(subtask3);

        fileBackedTaskManager.getEpicById(epic1.getId());
        fileBackedTaskManager.getSubtaskById(subtask3.getId());
        fileBackedTaskManager.getTaskById(task1.getId());
        fileBackedTaskManager.getSubtaskById(subtask1.getId());
        fileBackedTaskManager.getSubtaskById(subtask2.getId());
        fileBackedTaskManager.getEpicById(epic1.getId());
        fileBackedTaskManager.getEpicById(epic2.getId());
        fileBackedTaskManager.getEpicById(epic1.getId());
        fileBackedTaskManager.getTaskById(task2.getId());
        fileBackedTaskManager.getTaskById(task2.getId());
        fileBackedTaskManager.getSubtaskById(subtask1.getId());
        fileBackedTaskManager.getSubtaskById(subtask3.getId());
        fileBackedTaskManager.getSubtaskById(subtask2.getId());
        printHistory(fileBackedTaskManager);
        fileBackedTaskManager.deleteTaskById(task2.getId());
        printHistory(fileBackedTaskManager);
        fileBackedTaskManager.deleteEpicById(epic1.getId());
        printHistory(fileBackedTaskManager);
    }

    static void printHistory(TaskManager taskManager) {
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
        System.out.println();
    }
}