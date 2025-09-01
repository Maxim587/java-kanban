package ru.educationmm.taskmanager.main.service;

import ru.educationmm.taskmanager.main.exception.NotFoundException;
import ru.educationmm.taskmanager.main.exception.TaskOverlapException;
import ru.educationmm.taskmanager.main.model.Epic;
import ru.educationmm.taskmanager.main.model.Subtask;
import ru.educationmm.taskmanager.main.model.Task;
import ru.educationmm.taskmanager.main.util.Managers;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    protected final Prioritized prioritized = new Prioritized();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    protected int id;

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
    public Task addTask(Task task) throws TaskOverlapException {
        Optional<Task> intersectingTask = prioritized.checkExistingIntersections(task);
        if (intersectingTask.isPresent()) {
            throw new TaskOverlapException("Ошибка добавления задачи. Задача пересекается по времени с задачей id:" + intersectingTask.get().getId());
        }

        int taskId = task.getId();

        if (taskId == 0) {
            taskId = generateId();
        }

        task.setId(taskId);
        tasks.put(taskId, task);
        prioritized.addToPrioritizedSet(task);
        return task;
    }

    @Override
    public Epic addTask(Epic epic) {
        int epicId = epic.getId();

        if (epicId == 0) {
            epicId = generateId();
        }

        epic.setId(epicId);
        epics.put(epicId, epic);
        return epic;
    }

    @Override
    public Subtask addTask(Subtask subtask) throws TaskOverlapException, NotFoundException {
        Optional<Task> intersectingTask = prioritized.checkExistingIntersections(subtask);
        if (intersectingTask.isPresent()) {
            throw new TaskOverlapException("Ошибка добавления подзадачи. Подзадача пересекается по времени с задачей id:" + intersectingTask.get().getId());
        }

        if (epics.isEmpty() || !epics.containsKey(subtask.getEpicId())) {
            throw new NotFoundException("Ошибка добавления подзадачи. Эпик не найден");
        }

        int subtaskId = subtask.getId();

        if (subtaskId == 0) {
            subtaskId = generateId();
        }

        subtask.setId(subtaskId);
        subtasks.put(subtaskId, subtask);
        epics.get(subtask.getEpicId()).addSubtaskToEpic(subtask);
        prioritized.addToPrioritizedSet(subtask);
        return subtask;
    }

    // DELETE ALL
    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        prioritized.removeTasks(tasks.values());
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
        prioritized.removeTasks(subtasks.values());
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        prioritized.removeTasks(subtasks.values());
        subtasks.clear();
        epics.forEach((id, epic) -> {
            epic.clearSubtasks();
            epic.setCalculatedFields();
        });
    }

    // GET BY ID
    @Override
    public Task getTaskById(int taskId) throws NotFoundException {
        Task task = tasks.get(taskId);
        if (task == null) {
            throw new NotFoundException("Задача не найдена");
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int epicId) throws NotFoundException {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            throw new NotFoundException("Эпик не найден");
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) throws NotFoundException {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask == null) {
            throw new NotFoundException("Эпик не найден");
        }
        historyManager.add(subtask);
        return subtask;
    }

    // UPDATE
    @Override
    public void updateTask(Task task) throws TaskOverlapException, NotFoundException {
        if (!tasks.containsKey(task.getId())) {
            throw new NotFoundException("Ошибка обновления задачи. Задачи нет в менеджере");
        } else {
            Optional<Task> intersectingTask = prioritized.checkExistingIntersections(task);
            if (intersectingTask.isPresent()) {
                throw new TaskOverlapException("Ошибка обновления задачи. Задача пересекается по времени с задачей id:" + intersectingTask.get().getId());
            }
        }
        prioritized.removeSingle(task);
        prioritized.addToPrioritizedSet(task);
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateTask(Epic epic) throws NotFoundException {
        if (!epics.containsKey(epic.getId())) {
            throw new NotFoundException("Ошибка обновления эпика. Эпика нет в менеджере");
        }
        epic.setCalculatedFields();
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateTask(Subtask subtask) throws TaskOverlapException, NotFoundException {
        if (!subtasks.containsKey(subtask.getId())) {
            throw new NotFoundException("Ошибка обновления подзадачи. Подзадачи нет в менеджере");
        } else {
            Optional<Task> intersectingTask = prioritized.checkExistingIntersections(subtask);
            if (intersectingTask.isPresent()) {
                throw new TaskOverlapException("Ошибка обновления подзадачи. Задача пересекается по времени с задачей id:" + intersectingTask.get().getId());
            }
        }

        prioritized.removeSingle(subtask);
        prioritized.addToPrioritizedSet(subtask);
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).updateSubtaskInEpic(subtask);
    }

    // DELETE BY ID
    @Override
    public void deleteTaskById(int taskId) throws NotFoundException {
        if (!tasks.containsKey(taskId)) {
            throw new NotFoundException("Ошибка удаления задачи. Задача отсутствует в менеджере");
        }

        prioritized.removeSingle(tasks.get(taskId));
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public void deleteEpicById(int epicId) throws NotFoundException {
        if (!epics.containsKey(epicId)) {
            throw new NotFoundException("Ошибка удаления эпика. Эпик отсутствует в менеджере");
        }
        epics.get(epicId).getSubtaskIds().forEach(id -> {
            prioritized.removeSingle(subtasks.get(id));
            subtasks.remove(id);
            historyManager.remove(id);
        });
        epics.remove(epicId);
        historyManager.remove(epicId);
    }

    @Override
    public void deleteSubtaskById(int subtaskId) throws NotFoundException {
        if (!subtasks.containsKey(subtaskId)) {
            throw new NotFoundException("Ошибка удаления подзадачи. Подзадача отсутствует в менеджере");
        }
        prioritized.removeSingle(subtasks.get(subtaskId));
        int epicId = subtasks.get(subtaskId).getEpicId();
        Epic epic = epics.get(epicId);
        epic.deleteSubtaskInEpic(subtasks.get(subtaskId));
        subtasks.remove(subtaskId);
        epic.setCalculatedFields();
        historyManager.remove(subtaskId);
    }

    // EPIC SUBTASKS
    @Override
    public List<Subtask> getEpicSubtasks(int epicId) throws NotFoundException {
        if (!epics.containsKey(epicId)) {
            throw new NotFoundException("Ошибка. Эпик отсутствует в менеджере");
        }
        return epics.get(epicId).getEpicSubtasks();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritized.getPrioritizedTasks();
    }
}
