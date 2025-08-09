import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.educationmm.taskmanager.main.model.*;
import ru.educationmm.taskmanager.main.service.TaskManager;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;
    protected LocalDateTime startTime = LocalDateTime.of(2025, Month.JUNE, 15, 10, 50);

    abstract public void setManager();

    @BeforeEach
    public void prepare() {
        setManager();
        task = new Task("test task", "test description", TaskStatus.NEW, 20, startTime);
        taskManager.addTask(task);
        epic = new Epic("test epic", "test description");
        taskManager.addEpic(epic);
        subtask = new Subtask("test subtask", "test description", epic.getId(), TaskStatus.NEW, 25, startTime.plusMinutes(30));
        taskManager.addSubtask(subtask);
    }

    @Test
    public void tasksShouldHaveDifferentIds() {
        Assertions.assertFalse(task.getId() == epic.getId() ||
                subtask.getId() == epic.getId() ||
                task.getId() == subtask.getId(), "Задачи имеют одинаковый id");
    }

    @Test
    public void addTasks() {
        Assertions.assertEquals(task, taskManager.getTasks().getFirst(), "Задача не добавлена");
        Assertions.assertEquals(epic, taskManager.getEpics().getFirst(), "Эпик не добавлен");
        Assertions.assertEquals(subtask, taskManager.getSubtasks().getFirst(), "Подзадача не добавлена");
    }

    @Test
    public void subtaskShouldBeAddedToEpic() {
        Assertions.assertEquals(subtask, taskManager.getEpicById(epic.getId()).orElseThrow().getEpicSubtasks().getFirst(),
                "Подзадача не добавлена в эпик");
    }

    @Test
    public void getList() {
        Assertions.assertEquals(1, taskManager.getTasks().size(), "Список задач не получен");
        Assertions.assertEquals(1, taskManager.getEpics().size(), "Список эпиков не получен");
        Assertions.assertEquals(1, taskManager.getSubtasks().size(), "Список подзадач не получен");
    }

    @Test
    public void taskShouldNotBeChangedWhenAddedToManager() {
        Assertions.assertEquals(task.getId(), taskManager.getTaskById(1).orElseThrow().getId(), "Id задач " +
                "не совпадают");
        Assertions.assertEquals(epic.getId(), taskManager.getEpicById(2).orElseThrow().getId(), "Id эпиков " +
                "не совпадают");
        Assertions.assertEquals(subtask.getId(), taskManager.getSubtaskById(3).orElseThrow().getId(), "Id подзадач " +
                "не совпадают");

        Assertions.assertEquals(task.getName(), taskManager.getTaskById(1).orElseThrow().getName(), "Имена задач " +
                "не совпадают");
        Assertions.assertEquals(epic.getName(), taskManager.getEpicById(2).orElseThrow().getName(), "Имена эпиков " +
                "не совпадают");
        Assertions.assertEquals(subtask.getName(), taskManager.getSubtaskById(3).orElseThrow().getName(), "Имена " +
                "подзадач не совпадают");

        Assertions.assertEquals(task.getDescription(), taskManager.getTaskById(1).orElseThrow().getDescription(),
                "Описания задач не совпадают");
        Assertions.assertEquals(epic.getDescription(), taskManager.getEpicById(2).orElseThrow().getDescription(),
                "Описания эпиков не совпадают");
        Assertions.assertEquals(subtask.getDescription(), taskManager.getSubtaskById(3).orElseThrow().getDescription(),
                "Описания подзадач не совпадают");

        Assertions.assertEquals(task.getStatus(), taskManager.getTaskById(1).orElseThrow().getStatus(), "Статусы " +
                "задач не совпадают");
        Assertions.assertEquals(epic.getStatus(), taskManager.getEpicById(2).orElseThrow().getStatus(), "Статусы " +
                "эпиков не совпадают");
        Assertions.assertEquals(subtask.getStatus(), taskManager.getSubtaskById(3).orElseThrow().getStatus(),
                "Статусы подзадач не совпадают");

        Assertions.assertEquals(epic.getEpicSubtasks(), taskManager.getEpicById(2).orElseThrow().getEpicSubtasks(),
                "Списки подзадач эпика не совпадают");

        Assertions.assertEquals(subtask.getEpicId(), taskManager.getSubtaskById(3).orElseThrow().getEpicId(),
                "Id эпиков подзадач не совпадают");

        Assertions.assertEquals(task.getDuration(), taskManager.getTaskById(1).orElseThrow().getDuration(), "Продолжительность задач " +
                "не совпадает");
        Assertions.assertEquals(epic.getDuration(), taskManager.getEpicById(2).orElseThrow().getDuration(), "Продолжительность эпиков " +
                "не совпадает");
        Assertions.assertEquals(subtask.getDuration(), taskManager.getSubtaskById(3).orElseThrow().getDuration(), "Продолжительность подзадач " +
                "не совпадает");

        Assertions.assertEquals(task.getStartTime(), taskManager.getTaskById(1).orElseThrow().getStartTime(), "Время начала задач " +
                "не совпадает");
        Assertions.assertEquals(epic.getStartTime(), taskManager.getEpicById(2).orElseThrow().getStartTime(), "Время начала эпиков " +
                "не совпадает");
        Assertions.assertEquals(subtask.getStartTime(), taskManager.getSubtaskById(3).orElseThrow().getStartTime(), "Время начала подзадач " +
                "не совпадает");
    }

    @Test
    public void deleteTasks() {
        taskManager.deleteAllTasks();
        Assertions.assertEquals(0, taskManager.getTasks().size(), "Задачи не удалены");
        taskManager.deleteAllSubtasks();
        Assertions.assertEquals(0, taskManager.getSubtasks().size(), "Подзадачи не удалены");
        taskManager.deleteAllEpics();
        Assertions.assertEquals(0, taskManager.getEpics().size(), "Эпики не удалены");
    }

    @Test
    public void allSubtasksShouldBeDeletedWhenAllEpicsDeleted() {
        taskManager.deleteAllEpics();
        Assertions.assertEquals(0, taskManager.getSubtasks().size(), "Не все подзадачи удалены");
    }

    @Test
    public void epicShouldNotContainDeletedSubtasks() {
        Subtask subtask1 = new Subtask("st", "desc", epic.getId(), TaskStatus.NEW, 15, startTime.plusHours(1));
        taskManager.addSubtask(subtask1);
        int subtaskId = subtask1.getId();
        taskManager.deleteSubtaskById(subtaskId);
        taskManager.getEpicById(epic.getId()).orElseThrow().getEpicSubtasks().forEach(st ->
                Assertions.assertNotEquals(subtaskId, st.getId(), "Подзадача не удалена из эпика"));
        taskManager.deleteAllSubtasks();
        Assertions.assertEquals(0, taskManager.getEpicSubtasks(epic.getId()).size(), "Подзадачи " +
                "не удалены из эпика");
    }

    @Test
    public void updateTasks() {
        LocalDateTime newTaskStartTime = task.getStartTime().plusMinutes(5);
        long newTaskDuration = task.getDuration().minusMinutes(5).toMinutes();
        Task taskUpdated = new Task("updated", "updated", TaskStatus.IN_PROGRESS,
                newTaskDuration, newTaskStartTime);
        taskUpdated.setId(task.getId());

        Epic epicUpdated = new Epic("updated", "updated");
        epicUpdated.setId(epic.getId());

        LocalDateTime newSubtaskStartTime = subtask.getStartTime().plusMinutes(5);
        long newSubtaskDuration = subtask.getDuration().minusMinutes(5).toMinutes();
        Subtask subtaskUpdated = new Subtask("updated", "updated", epicUpdated.getId(), TaskStatus.DONE,
                newSubtaskDuration, newSubtaskStartTime);
        subtaskUpdated.setId(subtask.getId());
        epicUpdated.addSubtaskToEpic(subtaskUpdated);

        taskManager.updateTask(taskUpdated);
        taskManager.updateEpic(epicUpdated);
        taskManager.updateSubtask(subtaskUpdated);

        Task taskToCompare = taskManager.getTaskById(task.getId()).orElseThrow();
        Epic epicToCompare = taskManager.getEpicById(epic.getId()).orElseThrow();
        Subtask subtaskToCompare = taskManager.getSubtaskById(subtask.getId()).orElseThrow();

        Assertions.assertEquals("updated", taskToCompare.getName(), "Название задачи не обновлено");
        Assertions.assertEquals("updated", taskToCompare.getDescription(), "Описание задачи не обновлено");
        Assertions.assertSame(TaskStatus.IN_PROGRESS, taskToCompare.getStatus(), "Статус задачи не обновлен");
        Assertions.assertEquals(newTaskDuration, taskToCompare.getDuration().toMinutes(), "Продолжительность задачи не обновлена");
        Assertions.assertEquals(newTaskStartTime, taskToCompare.getStartTime(), "Время задачи не обновлено");
        Assertions.assertEquals("updated", epicToCompare.getName(), "Название эпика не обновлено");
        Assertions.assertEquals("updated", epicToCompare.getDescription(), "Описание эпика не обновлено");
        Assertions.assertEquals("updated", subtaskToCompare.getName(), "Название подзадачи не обновлено");
        Assertions.assertEquals("updated", subtaskToCompare.getDescription(), "Описание подзадачи не обновлено");
        Assertions.assertSame(TaskStatus.DONE, subtaskToCompare.getStatus(), "Статус подзадачи не обновлен");
        Assertions.assertEquals(newSubtaskDuration, subtaskToCompare.getDuration().toMinutes(), "Продолжительность подзадачи не обновлена");
        Assertions.assertEquals(newSubtaskStartTime, subtaskToCompare.getStartTime(), "Время подзадачи не обновлено");
    }

    @Test
    public void prioritizedTaskListShouldContainTaskOrderedByStartTime() {
        Task taskBefore = new Task("task before", "", TaskStatus.NEW, 2, startTime.minusMinutes(20));
        Task taskBetween = new Task("task between", "", TaskStatus.NEW, 1, task.getEndTime().plusMinutes(2));
        taskManager.addTask(taskBefore);
        taskManager.addTask(taskBetween);
        List<Task> target = Arrays.asList(taskBefore, task, taskBetween, subtask);
        Assertions.assertIterableEquals(taskManager.getPrioritizedTasks(), target, "Задачи не отсортированы по времени");
    }

    @Test
    public void checkExistingIntersections() {
        Task task0 = new Task(task);
        task0.setId(6);
        Task task1 = new Task("not intersecting task", "", TaskStatus.NEW, 1, startTime.plusHours(1));
        task1.setId(4);
        Task task2 = new Task("intersecting task", "", TaskStatus.NEW, 20, startTime.plusMinutes(10));
        task2.setId(5);
        Assertions.assertTrue(taskManager.checkExistingIntersections(task0), "Должно быть выявлено пересечение задач");
        Assertions.assertFalse(taskManager.checkExistingIntersections(task1), "Должно быть выявлено отсутствие пересечения задач");
        Assertions.assertTrue(taskManager.checkExistingIntersections(task2), "Должно быть выявлено пересечение задач");
    }

    @Test
    void subtasksShouldHaveEpicIds() {
        Subtask subtask1 = new Subtask("subtask1", "description", 1, TaskStatus.NEW, 30, startTime);
        Subtask subtask2 = new Subtask("subtask2", "description", 1, TaskStatus.NEW, 30, startTime.plusHours(1));
        subtask1.setId(4);
        subtask2.setId(5);
        boolean isEpicIdNotSet = taskManager.getSubtasks().stream().anyMatch(subtask -> subtask.getEpicId() <= 0);
        Assertions.assertFalse(isEpicIdNotSet, "Подзадача должна содержать epicId");
    }

}
