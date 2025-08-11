package ru.educationmm.taskmanager.main.service;

import ru.educationmm.taskmanager.main.model.Epic;
import ru.educationmm.taskmanager.main.model.Subtask;
import ru.educationmm.taskmanager.main.model.Task;
import ru.educationmm.taskmanager.main.util.Managers;

import java.util.*;

public class InMemoryTaskManager implements TaskManager, TimePrioritized {
    protected int id;
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

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
        if (checkExistingIntersections(task)) {
            throw new IllegalArgumentException("Ошибка добавления задачи. Задача пересекается по времени с существующей");
        }

        int taskId = task.getId();

        if (taskId == 0) {
            taskId = generateId();
        }

        task.setId(taskId);
        tasks.put(taskId, task);
        addToPrioritizedSet(task);
    }

    @Override
    public void addEpic(Epic epic) {
        int epicId = epic.getId();

        if (epicId == 0) {
            epicId = generateId();
        }

        epic.setId(epicId);
        epics.put(epicId, epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (checkExistingIntersections(subtask)) {
            throw new IllegalArgumentException("Ошибка добавления подзадачи. Подзадача пересекается по времени с существующей");
        }

        if (epics.isEmpty() || !epics.containsKey(subtask.getEpicId())) {
            return;
        }

        int subtaskId = subtask.getId();

        if (subtaskId == 0) {
            subtaskId = generateId();
        }

        subtask.setId(subtaskId);
        subtasks.put(subtaskId, subtask);
        epics.get(subtask.getEpicId()).addSubtaskToEpic(subtask);
        addToPrioritizedSet(subtask);
    }

    // DELETE ALL
    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        prioritizedTasks.removeAll(tasks.values());
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
        prioritizedTasks.removeAll(subtasks.values());
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        prioritizedTasks.removeAll(subtasks.values());
        subtasks.clear();
        epics.forEach((id, epic) -> {
            epic.clearSubtasks();
            epic.setCalculatedFields();
        });
    }

    // GET BY ID
    @Override
    public Optional<Task> getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        if (task == null) {
            return Optional.empty();
        }
        historyManager.add(task);
        return Optional.of(task);
    }

    @Override
    public Optional<Epic> getEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return Optional.empty();
        }
        historyManager.add(epic);
        return Optional.of(epic);
    }

    @Override
    public Optional<Subtask> getSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask == null) {
            return Optional.empty();
        }
        historyManager.add(subtask);
        return Optional.of(subtask);
    }

    // UPDATE
    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            throw new IllegalArgumentException("Ошибка обновления задачи. Задачи нет в менеджере");
        } else if (checkExistingIntersections(task)) {
            throw new IllegalArgumentException("Ошибка обновления задачи. Задача пересекается по времени с существующей");
        }
        prioritizedTasks.remove(task);
        addToPrioritizedSet(task);
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            throw new IllegalArgumentException("Ошибка обновления эпика. Эпика нет в менеджере");
        }
        epic.setCalculatedFields();
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            throw new IllegalArgumentException("Ошибка обновления подзадачи. Подзадачи нет в менеджере");
        } else if (checkExistingIntersections(subtask)) {
            throw new IllegalArgumentException("Ошибка обновления подзадачи. Подзадача пересекается по времени с существующей");
        }

        prioritizedTasks.remove(subtask);
        addToPrioritizedSet(subtask);
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).updateSubtaskInEpic(subtask);
    }

    // DELETE BY ID
    @Override
    public void deleteTaskById(int taskId) {
        if (!tasks.containsKey(taskId)) {
            throw new IllegalArgumentException("Ошибка удаления задачи. Задача отсутствует в менеджере");
        }

        prioritizedTasks.remove(tasks.get(taskId));
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void deleteEpicById(int epicId) {
        if (!epics.containsKey(epicId)) {
            throw new IllegalArgumentException("Ошибка удаления эпика. Эпик отсутствует в менеджере");
        }
        epics.get(epicId).getSubtaskIds().forEach(id -> {
            prioritizedTasks.remove(subtasks.get(id));
            subtasks.remove(id);
            historyManager.remove(id);
        });
        epics.remove(epicId);
        historyManager.remove(epicId);
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        if (!subtasks.containsKey(subtaskId)) {
            throw new IllegalArgumentException("Ошибка удаления подзадачи. Подзадача отсутствует в менеджере");
        }
        prioritizedTasks.remove(subtasks.get(subtaskId));
        int epicId = subtasks.get(subtaskId).getEpicId();
        Epic epic = epics.get(epicId);
        epic.deleteSubtaskInEpic(subtasks.get(subtaskId));
        subtasks.remove(subtaskId);
        epic.setCalculatedFields();
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

    @Override
    public void addToPrioritizedSet(Task task) {
        if (task.getStartTime() == null) {
            return;
        }
        prioritizedTasks.add(task);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    @Override
    public boolean isIntersectingTasks(Task task1, Task task2) {
        TreeSet<Task> cmp = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        cmp.add(task1);
        cmp.add(task2);

        return !cmp.first().getEndTime().isBefore(cmp.last().getStartTime());
    }

    @Override
    public boolean checkExistingIntersections(Task task) {

        return prioritizedTasks.stream()
                .filter(task0 -> task0.getId() != task.getId())
                .anyMatch(task0 -> isIntersectingTasks(task0, task));
    }
}
