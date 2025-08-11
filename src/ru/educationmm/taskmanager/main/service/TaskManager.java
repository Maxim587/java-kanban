package ru.educationmm.taskmanager.main.service;

import ru.educationmm.taskmanager.main.model.Epic;
import ru.educationmm.taskmanager.main.model.Subtask;
import ru.educationmm.taskmanager.main.model.Task;

import java.util.List;
import java.util.Optional;

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

    Optional<Task> getTaskById(int taskId);

    Optional<Epic> getEpicById(int epicId);

    Optional<Subtask> getSubtaskById(int subtaskId);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteTaskById(int taskId);

    void deleteEpicById(int epicId);

    void deleteSubtaskById(int subtaskId);

    List<Subtask> getEpicSubtasks(int epicId);

    List<Task> getHistory();

    //void addToPrioritizedSet(Task task);

    //List<Task> getPrioritizedTasks();

    //boolean isIntersectingTasks(Task task1, Task task2);

    //boolean checkExistingIntersections(Task task);
}
