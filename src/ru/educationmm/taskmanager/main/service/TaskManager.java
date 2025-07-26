package ru.educationmm.taskmanager.main.service;

import ru.educationmm.taskmanager.main.model.Epic;
import ru.educationmm.taskmanager.main.model.Subtask;
import ru.educationmm.taskmanager.main.model.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getTasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasks();

    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    Task getTaskById(int taskId);

    Epic getEpicById(int epicId);

    Subtask getSubtaskById(int subtaskId);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteTaskById(int taskId);

    void deleteEpicById(int epicId);

    void deleteSubtaskById(int subtaskId);

    List<Subtask> getEpicSubtasks(int epicId);

    List<Task> getHistory();
}
