package ru.yandex.practicum.taskmanager;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int taskId = 0;
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
        return ++taskId;
    }

    void addTaskNew(Task task) {
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
            updateTask(newEpic);
        }
        epicSubtaskMapping.computeIfAbsent(subtask.getEpicId(), k ->
                new ArrayList<>()).add(subtask.getId());
    }

    void addTask(Task task) {
        if (task.getTaskType() == TaskType.TASK) {
            tasks.put(task.getId(), task);
        } else if (task.getTaskType() == TaskType.EPIC) {
            epics.put(task.getId(), (Epic) task);
        } else if (task.getTaskType() == TaskType.SUBTASK) {
            Subtask subtask = (Subtask) task;
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic.getStatus() == TaskStatus.DONE) {
                Epic newEpic = new Epic(epic.getId(), epic.getName(), epic.getDescription(), TaskStatus.NEW, TaskType.EPIC);
                updateTask(newEpic);
            }
            epicSubtaskMapping.computeIfAbsent(subtask.getEpicId(), k ->
                    new ArrayList<>()).add(subtask.getId());
        }
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
        if (task.getTaskType() == TaskType.TASK) {
            tasks.put(task.getId(), task);
        } else if (task.getTaskType() == TaskType.EPIC) {
            epics.put(task.getId(), (Epic) task);
        } else if (task.getTaskType() == TaskType.SUBTASK) {
            Subtask subtask = (Subtask) task;
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

    void deleteTaskById(int taskId, TaskType taskType) {
        switch (taskType) {
            case TASK:
                tasks.remove(taskId);
                break;
            case EPIC:
                epics.remove(taskId);
                if (!epicSubtaskMapping.containsKey(taskId)) {
                    break;
                }
                epicSubtaskMapping.get(taskId).forEach(subtaskId -> subtasks.remove(subtaskId));
                epicSubtaskMapping.remove(taskId);
                break;
            case SUBTASK:
                int epicId = subtasks.remove(taskId).getEpicId();
                epicSubtaskMapping.get(epicId).remove((Integer) taskId);
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
                break;
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
}
