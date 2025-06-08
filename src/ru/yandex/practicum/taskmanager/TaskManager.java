package ru.yandex.practicum.taskmanager;

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

    boolean checkActiveEpics() {
        for(Epic epic : epics.values()) {
            if (epic.getStatus() != TaskStatus.DONE) {
                return true;
            }
        }
        return false;
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

    void updateTask(Task task) {
        if("Task".equals(task.getClass().getSimpleName())) {
            simpleTasks.put(task.getId(), task);
        }
    }

}
