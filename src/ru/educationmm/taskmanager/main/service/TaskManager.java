package ru.educationmm.taskmanager.main.service;

import ru.educationmm.taskmanager.main.model.Epic;
import ru.educationmm.taskmanager.main.model.Subtask;
import ru.educationmm.taskmanager.main.model.Task;

import java.util.ArrayList;

public interface TaskManager {
    ArrayList<Task> getTasks();
    ArrayList<Epic> getEpics();
    ArrayList<Subtask> getSubtasks();
    boolean addTask(Task task);
    boolean addEpic(Epic epic);
    boolean addSubtask(Subtask subtask);
    boolean deleteAllTasks();
    boolean deleteAllEpics();
    boolean deleteAllSubtasks();
    Task getTaskById(int taskId);
    Epic getEpicById(int epicId);
    Subtask getSubtaskById(int subtaskId);
    boolean updateTask(Task task);
    boolean updateEpic(Epic epic);
    boolean updateSubtask(Subtask subtask);
    boolean deleteTaskById(int taskId);
    boolean deleteEpicById(int epicId);
    boolean deleteSubtaskById(int subtaskId);
    ArrayList<Subtask> getEpicSubtasks(int epicId);
    ArrayList<Task> getHistory();
}
