package ru.educationmm.taskmanager.main.service;

import ru.educationmm.taskmanager.main.model.Epic;
import ru.educationmm.taskmanager.main.model.Subtask;
import ru.educationmm.taskmanager.main.model.Task;
import ru.educationmm.taskmanager.main.util.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private int id = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();


    private int generateId() {
        return ++id;
    }

    // GET LIST
    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    // ADD
    @Override
    public boolean addTask(Task task) {
        int taskId = generateId();
        task.setId(taskId);
        tasks.put(taskId, task);
        return true;
    }

    @Override
    public boolean addEpic(Epic epic) {
        int epicId = generateId();
        epic.setId(epicId);
        epics.put(epicId, epic);
        return true;
    }

    @Override
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
    @Override
    public boolean deleteAllTasks() {
        tasks.clear();
        return true;
    }

    @Override
    public boolean deleteAllEpics() {
        epics.clear();
        subtasks.clear();
        return true;
    }

    @Override
    public boolean deleteAllSubtasks() {
        subtasks.clear();
        epics.forEach((id, epic) -> {
            epic.clearSubtasks();
            epic.setStatus();
        });
        return true;
    }

    // GET BY ID
    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        historyManager.add(new Task(task));
        return task;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        historyManager.add(new Epic(epic));
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        historyManager.add(new Subtask(subtask));
        return subtask;
    }

    // UPDATE
    @Override
    public boolean updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return false;
        }
        tasks.put(task.getId(), task);
        return true;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            return false;
        }
        epic.setStatus();
        epics.put(epic.getId(), epic);
        return true;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            return false;
        }
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).updateSubtaskInEpic(subtask);
        return true;
    }

    // DELETE BY ID
    @Override
    public boolean deleteTaskById(int taskId) {
        if (!tasks.containsKey(taskId)) {
            return false;
        }
        tasks.remove(taskId);
        return true;
    }

    @Override
    public boolean deleteEpicById(int epicId) {
        if (!epics.containsKey(epicId)) {
            return false;
        }
        epics.get(epicId).getSubtaskIds().forEach(id -> subtasks.remove(id));
        epics.remove(epicId);
        return true;
    }

    @Override
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
    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        if (!epics.containsKey(epicId)) {
            return null;
        }
        return epics.get(epicId).getEpicSubtasks();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
