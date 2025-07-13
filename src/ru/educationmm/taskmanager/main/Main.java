package ru.educationmm.taskmanager.main;

import ru.educationmm.taskmanager.main.model.Epic;
import ru.educationmm.taskmanager.main.model.Subtask;
import ru.educationmm.taskmanager.main.model.Task;
import ru.educationmm.taskmanager.main.model.TaskStatus;
import ru.educationmm.taskmanager.main.service.TaskManager;
import ru.educationmm.taskmanager.main.util.Managers;


public class Main {

    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = Managers.getDefault();
        Task task1 = new Task("test", "test", TaskStatus.NEW);
        Task task2 = new Task("test", "test", TaskStatus.NEW);
        Epic epic1 = new Epic("test", "test");
        Epic epic2 = new Epic("test", "test");
        inMemoryTaskManager.addTask(task1);
        inMemoryTaskManager.addTask(task2);
        inMemoryTaskManager.addEpic(epic1);
        inMemoryTaskManager.addEpic(epic2);
        Subtask subtask1 = new Subtask("test", "test", epic1.getId(), TaskStatus.NEW);
        Subtask subtask2 = new Subtask("test", "test", epic1.getId(), TaskStatus.NEW);
        Subtask subtask3 = new Subtask("test", "test", epic1.getId(), TaskStatus.NEW);
        inMemoryTaskManager.addSubtask(subtask1);
        inMemoryTaskManager.addSubtask(subtask2);
        inMemoryTaskManager.addSubtask(subtask3);

        inMemoryTaskManager.getEpicById(epic1.getId());
        inMemoryTaskManager.getSubtaskById(subtask3.getId());
        inMemoryTaskManager.getTaskById(task1.getId());
        inMemoryTaskManager.getSubtaskById(subtask1.getId());
        inMemoryTaskManager.getSubtaskById(subtask2.getId());
        inMemoryTaskManager.getEpicById(epic1.getId());
        inMemoryTaskManager.getEpicById(epic2.getId());
        inMemoryTaskManager.getEpicById(epic1.getId());
        inMemoryTaskManager.getTaskById(task2.getId());
        inMemoryTaskManager.getTaskById(task2.getId());
        inMemoryTaskManager.getSubtaskById(subtask1.getId());
        inMemoryTaskManager.getSubtaskById(subtask3.getId());
        inMemoryTaskManager.getSubtaskById(subtask2.getId());
        printHistory(inMemoryTaskManager);
        inMemoryTaskManager.deleteTaskById(task2.getId());
        printHistory(inMemoryTaskManager);
        inMemoryTaskManager.deleteEpicById(epic1.getId());
        printHistory(inMemoryTaskManager);
    }
    static void printHistory(TaskManager inMemoryTaskManager){
        for(Task task : inMemoryTaskManager.getHistory()) {
            System.out.println(task);
        }
        System.out.println();
    }
}