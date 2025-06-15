package ru.educationmm.taskmanager.main.service;

import ru.educationmm.taskmanager.main.model.Epic;
import ru.educationmm.taskmanager.main.model.Subtask;
import ru.educationmm.taskmanager.main.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int id = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    private int generateId() {
        return ++id;
    }

    // GET LIST
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // ADD
    public boolean addTask(Task task) {
        int taskId = generateId();
        task.setId(taskId);
        tasks.put(taskId, task);
        return true;
    }

    public boolean addEpic(Epic epic) {
        int epicId = generateId();
        epic.setId(epicId);
        epics.put(epicId, epic);
        return true;
    }

    public boolean addSubtask(Subtask subtask) {
        if (epics.isEmpty() || !epics.containsKey(subtask.getEpicId())) {
            return false;
        }
        int subtaskId = generateId();
        subtask.setId(subtaskId);
        subtasks.put(subtaskId, subtask);
        epics.get(subtask.getEpicId()).addSubtaskToEpic(subtask);
        return true;
    }

    // DELETE ALL
    public boolean deleteAllTasks() {
        tasks.clear();
        return true;
    }

    public boolean deleteAllEpics() {
        epics.clear();
        subtasks.clear();
        return true;
    }

    public boolean deleteAllSubtasks() {
        subtasks.clear();
        epics.forEach((id, epic) -> {
            epic.clearSubtasks();
            epic.setStatus();
        });
        return true;
    }

    // GET BY ID
    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    public Epic getEpicById(int epicId) {
        return epics.get(epicId);
    }

    public Subtask getSubtaskById(int subtaskId) {
        return subtasks.get(subtaskId);
    }

    // UPDATE
    public boolean updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return false;
        }
        tasks.put(task.getId(), task);
        return true;
    }

    public boolean updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            return false;
        }
        epic.setStatus();
        epics.put(epic.getId(), epic);
        return true;
    }

    public boolean updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            return false;
        }
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).updateSubtaskInEpic(subtask);
        return true;
    }

    // DELETE BY ID
    public boolean deleteTaskById(int taskId) {
        if (!tasks.containsKey(taskId)) {
            return false;
        }
        tasks.remove(taskId);
        return true;
    }

    public boolean deleteEpicById(int epicId) {
        if (!epics.containsKey(epicId)) {
            return false;
        }
        epics.get(epicId).getSubtaskIds().forEach(id -> subtasks.remove(id));
        epics.remove(epicId);
        return true;
    }

    public boolean deleteSubtaskById(int subtaskId) {
        if (!subtasks.containsKey(subtaskId)) {
            return false;
        }
        int epicId = subtasks.get(subtaskId).getEpicId();
        epics.get(epicId).deleteSubtaskInEpic(subtasks.get(subtaskId));
        subtasks.remove(subtaskId);
        epics.get(epicId).setStatus();
        return true;
    }

    // EPIC SUBTASKS
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        if (!epics.containsKey(epicId)) {
            return null;
        }
        return epics.get(epicId).getEpicSubtasks();
    }
}
