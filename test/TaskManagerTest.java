import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.educationmm.taskmanager.main.model.Epic;
import ru.educationmm.taskmanager.main.model.Subtask;
import ru.educationmm.taskmanager.main.model.Task;
import ru.educationmm.taskmanager.main.model.TaskStatus;
import ru.educationmm.taskmanager.main.service.TaskManager;

import java.time.LocalDateTime;
import java.time.Month;

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
        taskManager.addTask(epic);
        subtask = new Subtask("test subtask", "test description", epic.getId(), TaskStatus.NEW, 25, startTime.plusMinutes(30));
        taskManager.addTask(subtask);
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
        Assertions.assertEquals(subtask, taskManager.getEpicById(epic.getId()).getEpicSubtasks().getFirst(),
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
        Assertions.assertEquals(task.getId(), taskManager.getTaskById(1).getId(), "Id задач " +
                "не совпадают");
        Assertions.assertEquals(epic.getId(), taskManager.getEpicById(2).getId(), "Id эпиков " +
                "не совпадают");
        Assertions.assertEquals(subtask.getId(), taskManager.getSubtaskById(3).getId(), "Id подзадач " +
                "не совпадают");

        Assertions.assertEquals(task.getName(), taskManager.getTaskById(1).getName(), "Имена задач " +
                "не совпадают");
        Assertions.assertEquals(epic.getName(), taskManager.getEpicById(2).getName(), "Имена эпиков " +
                "не совпадают");
        Assertions.assertEquals(subtask.getName(), taskManager.getSubtaskById(3).getName(), "Имена " +
                "подзадач не совпадают");

        Assertions.assertEquals(task.getDescription(), taskManager.getTaskById(1).getDescription(),
                "Описания задач не совпадают");
        Assertions.assertEquals(epic.getDescription(), taskManager.getEpicById(2).getDescription(),
                "Описания эпиков не совпадают");
        Assertions.assertEquals(subtask.getDescription(), taskManager.getSubtaskById(3).getDescription(),
                "Описания подзадач не совпадают");

        Assertions.assertEquals(task.getStatus(), taskManager.getTaskById(1).getStatus(), "Статусы " +
                "задач не совпадают");
        Assertions.assertEquals(epic.getStatus(), taskManager.getEpicById(2).getStatus(), "Статусы " +
                "эпиков не совпадают");
        Assertions.assertEquals(subtask.getStatus(), taskManager.getSubtaskById(3).getStatus(),
                "Статусы подзадач не совпадают");

        Assertions.assertEquals(epic.getEpicSubtasks(), taskManager.getEpicById(2).getEpicSubtasks(),
                "Списки подзадач эпика не совпадают");

        Assertions.assertEquals(subtask.getEpicId(), taskManager.getSubtaskById(3).getEpicId(),
                "Id эпиков подзадач не совпадают");

        Assertions.assertEquals(task.getDuration(), taskManager.getTaskById(1).getDuration(), "Продолжительность задач " +
                "не совпадает");
        Assertions.assertEquals(epic.getDuration(), taskManager.getEpicById(2).getDuration(), "Продолжительность эпиков " +
                "не совпадает");
        Assertions.assertEquals(subtask.getDuration(), taskManager.getSubtaskById(3).getDuration(), "Продолжительность подзадач " +
                "не совпадает");

        Assertions.assertEquals(task.getStartTime(), taskManager.getTaskById(1).getStartTime(), "Время начала задач " +
                "не совпадает");
        Assertions.assertEquals(epic.getStartTime(), taskManager.getEpicById(2).getStartTime(), "Время начала эпиков " +
                "не совпадает");
        Assertions.assertEquals(subtask.getStartTime(), taskManager.getSubtaskById(3).getStartTime(), "Время начала подзадач " +
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
        taskManager.addTask(subtask1);
        int subtaskId = subtask1.getId();
        taskManager.deleteSubtaskById(subtaskId);
        taskManager.getEpicById(epic.getId()).getEpicSubtasks().forEach(st ->
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
        taskManager.updateTask(epicUpdated);
        taskManager.updateTask(subtaskUpdated);

        Task taskToCompare = taskManager.getTaskById(task.getId());
        Epic epicToCompare = taskManager.getEpicById(epic.getId());
        Subtask subtaskToCompare = taskManager.getSubtaskById(subtask.getId());

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
    public void subtasksShouldHaveEpicIds() {
        Subtask subtask1 = new Subtask("subtask1", "description", 1, TaskStatus.NEW, 30, startTime);
        Subtask subtask2 = new Subtask("subtask2", "description", 1, TaskStatus.NEW, 30, startTime.plusHours(1));
        subtask1.setId(4);
        subtask2.setId(5);
        boolean isEpicIdNotSet = taskManager.getSubtasks().stream().anyMatch(subtask -> subtask.getEpicId() <= 0);
        Assertions.assertFalse(isEpicIdNotSet, "Подзадача должна содержать epicId");
    }
}
