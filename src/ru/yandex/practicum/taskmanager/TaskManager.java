package ru.yandex.practicum.taskmanager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int taskId = 0;

    private HashMap<Integer, Task> simpleTasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, ArrayList<Integer>> epicSubtaskMapping = new HashMap<>(); //для хранения связей между эпиком и его подзадачами

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

    int getTaskId() {
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

    HashMap<Integer, Subtask> getEpicSubtasks (int epicId) {
        HashMap<Integer, Subtask>  epicSubtasks = new HashMap<>();
        epicSubtaskMapping.get(epicId).forEach(subtaskId -> epicSubtasks.put(subtaskId, subtasks.get(subtaskId)));
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
            HashMap<Integer, Subtask> epicSubtasks = getEpicSubtasks(subtask.getEpicId());
            if(subtask.getStatus() == subtasks.get(subtask.getId()).getStatus()) {
                subtasks.put(subtask.getId(), subtask);
            } else {
                TaskStatus status = subtask.getStatus();
                ArrayList<TaskStatus> epicSubtasksStatuses = new ArrayList<>();
                epicSubtasks.forEach((k, v) -> epicSubtasksStatuses.add(v.getStatus()));

            }

        }
    }

}
