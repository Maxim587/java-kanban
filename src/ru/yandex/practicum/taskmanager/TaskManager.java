package ru.yandex.practicum.taskmanager;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int epicId = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, ArrayList<Integer>> epicSubtaskMapping = new HashMap<>(); //связь эпиков с подзадачами

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public HashMap<Integer, ArrayList<Integer>> getEpicSubtaskMapping() {
        return epicSubtaskMapping;
    }

    int generateTaskId() {
        return ++epicId;
    }

    void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    void addEpic(Epic epic){
        epics.put(epic.getId(), epic);
    }

    void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic.getStatus() == TaskStatus.DONE) {
            Epic newEpic = new Epic(epic.getId(), epic.getName(), epic.getDescription(), TaskStatus.NEW, TaskType.EPIC);
            updateEpic(newEpic);
        }
        epicSubtaskMapping.computeIfAbsent(subtask.getEpicId(), k ->
                new ArrayList<>()).add(subtask.getId());
    }

    void deleteAllTasks() {
        tasks.clear();
        subtasks.clear();
        epicSubtaskMapping.clear();
        epics.clear();
    }

    Task getTaskById(int id) {
        return tasks.get(id);
    }

    Epic getEpicById(int id) {
        return epics.get(id);
    }

    Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        TaskStatus currentEpicStatus = epics.get(subtask.getEpicId()).getStatus();
        TaskStatus calculatedEpicStatus = defineEpicStatus(subtask.getEpicId());
        if (currentEpicStatus != calculatedEpicStatus) {
            Epic epic = epics.get(subtask.getEpicId());
            Epic newEpic = new Epic(epic.getId(), epic.getName(), epic.getDescription(),
                    calculatedEpicStatus, TaskType.EPIC);
            epics.put(newEpic.getId(), newEpic);
        }
    }

    TaskStatus defineEpicStatus(int epicId) {
        ArrayList<Subtask> epicSubtasks = getEpicSubtasks(epicId);
        boolean isNew = true;
        boolean isDone = true;
        for (Subtask subtask : epicSubtasks) {
            isNew = isNew && (subtask.getStatus() == TaskStatus.NEW); //?все в статусе NEW
            isDone = isDone && (subtask.getStatus() == TaskStatus.DONE); //?все в статусе DONE
        }
        if (isNew) {
            return TaskStatus.NEW;
        } else if (isDone) {
            return TaskStatus.DONE;
        }
        return TaskStatus.IN_PROGRESS;
    }

    void deleteTaskByIdNew(int taskId) {
        tasks.remove(taskId);
    }

    void deleteEpicById(int epicId) {
        epics.remove(epicId);
        if (!epicSubtaskMapping.containsKey(epicId)) {
            return;
        }
        epicSubtaskMapping.get(epicId).forEach(subtaskId -> subtasks.remove(subtaskId));
        epicSubtaskMapping.remove(epicId);
    }

    void deleteSubtaskById(int subtaskId) {
        int epicId = subtasks.remove(subtaskId).getEpicId();
        epicSubtaskMapping.get(epicId).remove((Integer) subtaskId);
        TaskStatus currentEpicStatus = epics.get(epicId).getStatus();
        TaskStatus calculatedEpicStatus = defineEpicStatus(epicId);

        if (currentEpicStatus != calculatedEpicStatus) {
            Epic epic = epics.get(epicId);
            Epic newEpic = new Epic(epic.getId(), epic.getName(), epic.getDescription(),
                    calculatedEpicStatus, TaskType.EPIC);
            epics.put(newEpic.getId(), newEpic);
        }

        if (epicSubtaskMapping.get(epicId).isEmpty()) {
            epicSubtaskMapping.remove(epicId);
        }
    }

    ArrayList<Subtask> getEpicSubtasks(int epicId) {
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();
        if (!epicHasSubtasks(epicId)) {
            return null;
        }
        epicSubtaskMapping.get(epicId).forEach(subtaskId -> epicSubtasks.add(subtasks.get(subtaskId)));
        return epicSubtasks;
    }

    boolean tasksIsEmpty() {
        return tasks.isEmpty();
    }

    boolean epicsIsEmpty() {
        return epics.isEmpty();
    }

    boolean checkEpicId(int id) {
        return epics.containsKey(id);
    }

    boolean epicHasSubtasks(int epicId) {
        return epicSubtaskMapping.containsKey(epicId);
    }

    String getTaskList() {
        String taskList = "";
        int counter = 0;
        String newLine;
        for (Task task : tasks.values()) {
            newLine = counter == (tasks.size() -1) ? "" : "\n";
            taskList += task.toString() + newLine;
            counter++;
        }
        return taskList;
    }

    String getEpicList() {
        String epicList = "";
        int counter = 0;
        String newLine;
        for (Epic epic : epics.values()) {
            newLine = counter == (epics.size() -1) ? "" : "\n";
            epicList += epic.toString() + newLine;
            counter++;
        }
        return epicList;
    }

    String getSubtaskList() {
        String subtaskList = "";
        int counter = 0;
        String newLine;
        for (Subtask subtask : subtasks.values()) {
            newLine = counter == (subtasks.size() -1) ? "" : "\n";
            subtaskList += subtask.toString() + newLine;
            counter++;
        }
        return subtaskList;
    }
}
