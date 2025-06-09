package ru.yandex.practicum.taskmanager;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int taskId = 0;

    private HashMap<Integer, Task> simpleTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, ArrayList<Integer>> epicSubtaskMapping = new HashMap<>(); //связь эпиков с подзадачами

    public HashMap<Integer, Task> getSimpleTasks() {
        return simpleTasks;
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

    boolean epicsIsEmpty() {
        return epics.isEmpty();
    }

    Task getTaskById(int id) {
        return simpleTasks.get(id);
    }

    Epic getEpicById(int id) {
        return epics.get(id);
    }

    Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    boolean checkEpicId(int id) {
        return epics.containsKey(id);
    }

    void addTask(Task task, TaskType taskType) {
        switch (taskType) {
            case TASK:
                simpleTasks.put(task.getId(), task);
                break;
            case EPIC:
                epics.put(task.getId(), (Epic)task);
                break;
            case SUBTASK:
                Subtask subtask = (Subtask)task;
                subtasks.put(subtask.getId(), subtask);
                Epic epic = epics.get(subtask.getEpicId());
                if (epic.getStatus() == TaskStatus.DONE) {
                    Epic newEpic = new Epic(epic.getName(), epic.getDescription(), epic.getId(), TaskStatus.NEW);
                    updateTask(newEpic);
                }
                epicSubtaskMapping.computeIfAbsent(subtask.getEpicId(), k ->
                        new ArrayList<>()).add(subtask.getId());

                break;
        }
    }

    void deleteAllTasks(){
        simpleTasks = new HashMap<>();
        subtasks = new HashMap<>();
        epicSubtaskMapping = new HashMap<>();
        epics = new HashMap<>();
    }

    //удалить метод, функционал перенести в связанный метод
    ArrayList<Subtask> getEpicSubtasks (int epicId) {
        ArrayList<Subtask>  epicSubtasks = new ArrayList<>();
        epicSubtaskMapping.get(epicId).forEach(subtaskId -> epicSubtasks.add(subtasks.get(subtaskId)));
        return epicSubtasks;
    }

    void updateTask(Task task) {
        String taskType = task.getClass().getSimpleName();
        if ("Task".equals(taskType)) {
            simpleTasks.put(task.getId(), task);
        } else if ("Epic".equals(taskType)) {
            epics.put(task.getId(), (Epic)task);
        } else if ("Subtask".equals(taskType)) {
            Subtask subtask = (Subtask)task;
            subtasks.put(subtask.getId(), subtask);
            TaskStatus currentEpicStatus = epics.get(subtask.getEpicId()).getStatus();
            TaskStatus calculatedEpicStatus = defineEpicStatus(subtask.getEpicId());

            if (currentEpicStatus != calculatedEpicStatus) {
                Epic epic = epics.get(subtask.getEpicId());
                Epic newEpic = new Epic(epic.getName(), epic.getDescription(), epic.getId(), calculatedEpicStatus);
                epics.put(newEpic.getId(), newEpic);
            }
        }
    }

    TaskStatus defineEpicStatus (int epicId) {
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

    void deleteTaskById (int taskId) {

    }

}
