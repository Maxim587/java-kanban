import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ru.educationmm.taskmanager.main.model.*;
import ru.educationmm.taskmanager.main.service.InMemoryTaskManager;


class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    public void setManager() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void isIntersectingTasks() {
        //Пересекающиеся задачи, у которых совпадает время старта и время окончания
        Task task2 = new Task(task);
        Assertions.assertTrue(taskManager.isIntersectingTasks(task, task2), "Должно быть выявлено пересечение задач");

        //Пересекающиеся задачи, у которых отличается время старта и время окончания
        task2 = new Task("task2", "task2", TaskStatus.NEW, 30, startTime.plusMinutes(10));
        Assertions.assertTrue(taskManager.isIntersectingTasks(task, task2), "Должно быть выявлено пересечение задач");

        //Пересекающиеся задачи, у которых совпадает время старта и отличается время окончания
        task2 = new Task("task2", "task2", TaskStatus.NEW, 30, startTime);
        Assertions.assertTrue(taskManager.isIntersectingTasks(task, task2), "Должно быть выявлено пересечение задач");

        //Пересекающиеся задачи, у которых отличается время старта и совпадает время окончания
        task2 = new Task("task2", "task2", TaskStatus.NEW, 15, startTime.plusMinutes(5));
        Assertions.assertTrue(taskManager.isIntersectingTasks(task, task2), "Должно быть выявлено пересечение задач");

        //Пересекающиеся задачи, у которых время окончания первой задачи совпадает с временем начала второй
        task2 = new Task("task2", "task2", TaskStatus.NEW, 30, task.getEndTime());
        Assertions.assertTrue(taskManager.isIntersectingTasks(task, task2), "Должно быть выявлено пересечение задач");

        //Непересекающиеся задачи
        task2 = new Task("task2", "task2", TaskStatus.NEW, 30, task.getEndTime().plusMinutes(5));
        Assertions.assertFalse(taskManager.isIntersectingTasks(task, task2), "Должно быть выявлено отсутствие пересечения задач");
    }
}