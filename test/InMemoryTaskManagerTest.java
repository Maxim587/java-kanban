import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.educationmm.taskmanager.main.model.Epic;
import ru.educationmm.taskmanager.main.model.Subtask;
import ru.educationmm.taskmanager.main.model.Task;
import ru.educationmm.taskmanager.main.model.TaskStatus;
import ru.educationmm.taskmanager.main.service.InMemoryTaskManager;


class InMemoryTaskManagerTest {

    InMemoryTaskManager taskManager;
    Task task;
    Epic epic;
    Subtask subtask;

    @BeforeEach
    public void prepare() {
        taskManager = new InMemoryTaskManager();
        task = new Task("test task", "test description", TaskStatus.NEW);
        taskManager.addTask(task);
        epic = new Epic("test epic", "test description");
        taskManager.addEpic(epic);
        subtask = new Subtask("test subtask", "test description", epic.getId(), TaskStatus.NEW);
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
        Subtask subtask1 = new Subtask("st", "desc", epic.getId(), TaskStatus.NEW);
        taskManager.addSubtask(subtask1);
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
        Task taskUpdated = new Task("updated", "updated", TaskStatus.IN_PROGRESS);
        taskUpdated.setId(task.getId());
        Epic epicUpdated = new Epic("updated", "updated");
        epicUpdated.setId(epic.getId());
        Subtask subtaskUpdated = new Subtask("updated", "updated", epicUpdated.getId(), TaskStatus.DONE);
        subtaskUpdated.setId(subtask.getId());
        epicUpdated.addSubtaskToEpic(subtaskUpdated);

        taskManager.updateTask(taskUpdated);
        taskManager.updateEpic(epicUpdated);
        taskManager.updateSubtask(subtaskUpdated);

        Task taskToCompare = taskManager.getTaskById(task.getId());
        Epic epicToCompare = taskManager.getEpicById(epic.getId());
        Subtask subtaskToCompare = taskManager.getSubtaskById(subtask.getId());

        Assertions.assertEquals("updated", taskToCompare.getName(), "Название задачи не обновлено");
        Assertions.assertEquals("updated", taskToCompare.getDescription(), "Описание задачи не обновлено");
        Assertions.assertSame(TaskStatus.IN_PROGRESS, taskToCompare.getStatus(), "Статус задачи не обновлен");
        Assertions.assertEquals("updated", epicToCompare.getName(), "Название эпика не обновлено");
        Assertions.assertEquals("updated", epicToCompare.getDescription(), "Описание эпика не обновлено");
        Assertions.assertEquals("updated", subtaskToCompare.getName(), "Название подзадачи не обновлено");
        Assertions.assertEquals("updated", subtaskToCompare.getDescription(), "Описание подзадачи не обновлено");
        Assertions.assertSame(TaskStatus.DONE, subtaskToCompare.getStatus(), "Статус подзадачи не обновлен");
    }
}