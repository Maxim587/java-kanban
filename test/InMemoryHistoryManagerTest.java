import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.educationmm.taskmanager.main.model.Epic;
import ru.educationmm.taskmanager.main.model.Subtask;
import ru.educationmm.taskmanager.main.model.Task;
import ru.educationmm.taskmanager.main.model.TaskStatus;
import ru.educationmm.taskmanager.main.service.InMemoryHistoryManager;
import ru.educationmm.taskmanager.main.service.InMemoryTaskManager;
import ru.educationmm.taskmanager.main.service.TaskManager;


class InMemoryHistoryManagerTest {

    TaskManager taskManager;
    Task task;

    @BeforeEach
    public void prepare() {
        taskManager = new InMemoryTaskManager();
        task = new Task("test", "test", TaskStatus.NEW);
    }

    @Test
    public void historyShouldNotContainDuplicateEntries() {
        taskManager.addTask(task);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        Assertions.assertEquals(1, taskManager.getHistory().size(), "В истории не должны дублироваться просмотры задачи");
    }

    @Test
    public void addAndDeleteTasks() {
        Epic epic = new Epic("test", "test");
        taskManager.addTask(task);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("test", "test", epic.getId(), TaskStatus.NEW);
        taskManager.addSubtask(subtask);

        taskManager.getTaskById(task.getId());
        Assertions.assertEquals(1, taskManager.getHistory().size(), "После просмотра задачи, история не должна быть пустой.");

        taskManager.deleteTaskById(task.getId());
        Assertions.assertEquals(0, taskManager.getHistory().size(), "При удалении задачи она должна удаляться из истории");

        taskManager.getTaskById(task.getId());
        taskManager.deleteAllTasks();
        Assertions.assertEquals(0, taskManager.getHistory().size(), "При удалении всех задач они должны удаляться из истории");

        taskManager.getEpicById(epic.getId());
        Assertions.assertEquals(1, taskManager.getHistory().size(), "После просмотра эпика, история не должна быть пустой.");

        taskManager.getSubtaskById(subtask.getId());
        Assertions.assertEquals(2, taskManager.getHistory().size(), "После просмотра подзадачи она должна появиться в истории просмотра.");
        taskManager.deleteEpicById(epic.getId());
        Assertions.assertEquals(0, taskManager.getHistory().size(), "При удалении эпика, он и его подзадачи должны удаляться из истории");

        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(subtask.getId());
        taskManager.deleteAllEpics();
        Assertions.assertEquals(0, taskManager.getHistory().size(), "При удалении всех эпиков, все эпики и их подзадачи должны удаляться из истории");

        taskManager.addEpic(epic);
        subtask = new Subtask("test", "test", epic.getId(), TaskStatus.NEW);
        taskManager.addSubtask(subtask);
        taskManager.getSubtaskById(subtask.getId());
        Assertions.assertEquals(1, taskManager.getHistory().size(), "После просмотра подзадачи, история не должна быть пустой.");

        taskManager.deleteSubtaskById(subtask.getId());
        Assertions.assertEquals(0, taskManager.getHistory().size(), "При удалении подзадачи она должна удаляться из истории");

        taskManager.getSubtaskById(subtask.getId());
        taskManager.deleteAllSubtasks();
        Assertions.assertEquals(0, taskManager.getHistory().size(), "При удалении всех подзадач они должны удаляться из истории");
    }

    @Test
    public void newTaskShouldBeAddedToTheEndOfHistory() {
        Epic epic = new Epic("test", "test");
        taskManager.addTask(task);
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("test", "test", epic.getId(), TaskStatus.NEW);
        taskManager.addSubtask(subtask);

        Assertions.assertEquals(0, taskManager.getHistory().size(), "Первоначально история должна быть пустой");
        taskManager.getTaskById(task.getId());
        taskManager.getSubtaskById(subtask.getId());
        taskManager.getEpicById(epic.getId());
        Assertions.assertSame(subtask, taskManager.getHistory().get(1), "Элементы должны добавляться в историю по порядку");
        Assertions.assertSame(epic, taskManager.getHistory().get(2), "Элементы должны добавляться в историю по порядку");
        taskManager.getTaskById(task.getId());
        Assertions.assertSame(task, taskManager.getHistory().get(2), "Элементы должны добавляться в историю по порядку");
    }

    @Test
    public void afterNodeRemovalHistoryOrderShouldNotBeChanged() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task2 = new Task(task);
        Task task3 = new Task(task);
        Task task4 = new Task(task);
        task.setId(1);
        task2.setId(2);
        task3.setId(3);
        task4.setId(4);
        historyManager.add(task);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task4);
        historyManager.remove(2);
        Assertions.assertEquals(3, historyManager.getHistory().size(), "Задача не удалена из истории");
        Assertions.assertSame(task, historyManager.getHistory().getFirst(), "Первый элемент не соответствует добавленному");
        Assertions.assertSame(task3, historyManager.getHistory().get(1), "Второй элемент не соответствует добавленному");
        Assertions.assertSame(task4, historyManager.getHistory().getLast(), "Последний элемент не соответствует добавленному");
    }
}