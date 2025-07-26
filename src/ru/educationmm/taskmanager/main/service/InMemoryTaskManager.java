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
    protected int id;
    protected Map<Integer, Task> tasks;
    protected Map<Integer, Epic> epics;
    protected Map<Integer, Subtask> subtasks;
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        id = 0;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

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
    public void addTask(Task task) {
        int taskId = generateId();
        task.setId(taskId);
        tasks.put(taskId, task);
    }

    @Override
    public void addEpic(Epic epic) {
        int epicId = generateId();
        epic.setId(epicId);
        epics.put(epicId, epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (epics.isEmpty() || !epics.containsKey(subtask.getEpicId())) {
            return;
        }
        int subtaskId = generateId();
        subtask.setId(subtaskId);
        subtasks.put(subtaskId, subtask);
        epics.get(subtask.getEpicId()).addSubtaskToEpic(subtask);
    }

    // DELETE ALL
    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();
        epics.forEach((id, epic) -> {
            epic.clearSubtasks();
            epic.setStatus();
        });
    }

    // GET BY ID
    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        if (task == null) {
            return null;
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask == null) {
            return null;
        }
        historyManager.add(subtask);
        return subtask;
    }

    // UPDATE
    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            return;
        }
        epic.setStatus();
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            return;
        }
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).updateSubtaskInEpic(subtask);
    }

    // DELETE BY ID
    @Override
    public void deleteTaskById(int taskId) {
        if (!tasks.containsKey(taskId)) {
            return;
        }
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void deleteEpicById(int epicId) {
        if (!epics.containsKey(epicId)) {
            return;
        }
        epics.get(epicId).getSubtaskIds().forEach(id -> {
            subtasks.remove(id);
            historyManager.remove(id);
        });
        epics.remove(epicId);
        historyManager.remove(epicId);
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        if (!subtasks.containsKey(subtaskId)) {
            return;
        }
        int epicId = subtasks.get(subtaskId).getEpicId();
        epics.get(epicId).deleteSubtaskInEpic(subtasks.get(subtaskId));
        subtasks.remove(subtaskId);
        epics.get(epicId).setStatus();
        historyManager.remove(subtaskId);
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
