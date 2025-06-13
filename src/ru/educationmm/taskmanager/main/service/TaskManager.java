package ru.educationmm.taskmanager.main.service;

import ru.educationmm.taskmanager.main.model.Epic;
import ru.educationmm.taskmanager.main.model.Subtask;
import ru.educationmm.taskmanager.main.model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int epicId = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public static int generateTaskId() {
        return ++epicId;
    }
// GET TASKS
    public ArrayList<Task> getTasks() {
        ArrayList<Task> taskList = new ArrayList<>();
        tasks.forEach((id, task) -> taskList.add(new Task(task)));
        return taskList;
    }

    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> epicList = new ArrayList<>();
        epics.forEach((id, epic) -> epicList.add(new Epic(epic)));
        return epicList;
    }

    public ArrayList<Subtask> getSubtasks() {
    ArrayList<Subtask> subtaskList = new ArrayList<>();
        subtasks.forEach((id, subtask) -> subtaskList.add(new Subtask(subtask)));
        return subtaskList;
    }
// ADD TASKS
    public boolean addTask(Task task) {
        tasks.put(task.getId(), task);
        return true;
    }

    public boolean addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        return true;
    }

    public boolean addSubtask(Subtask subtask) {
        if (epics.isEmpty() || !epics.containsKey(subtask.getEpicId())) {
            return false;
        }
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).addSubtaskToEpic(subtask);
        return true;
    }
// DELETE ALL TASKS
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
// GET TASKS BY ID
    public Task getTaskById(int taskId) {
        if (!tasks.containsKey(taskId)) {
            return null;
        }
        return new Task(tasks.get(taskId));
    }

    public Epic getEpicById(int epicId) {
        if (!epics.containsKey(epicId)) {
            return null;
        }
        return new Epic(epics.get(epicId));
    }

    public Subtask getSubtaskById(int subtaskId) {
        if (!subtasks.containsKey(subtaskId)) {
            return null;
        }
        return new Subtask(subtasks.get(subtaskId));
    }
// UPDATE TASKS
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
