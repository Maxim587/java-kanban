import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.educationmm.taskmanager.main.model.Task;
import ru.educationmm.taskmanager.main.model.TaskStatus;
import ru.educationmm.taskmanager.main.service.Prioritized;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

public class PrioritizedTest {

    private final LocalDateTime startTime = LocalDateTime.of(2025, Month.JUNE, 15, 10, 0);
    Prioritized prioritized;
    private Task task1;
    private Task task2;


    @BeforeEach
    void prepare() {
        prioritized = new Prioritized();
        task1 = new Task("name1", "desc1", TaskStatus.NEW, 10, startTime);
        task2 = new Task("name2", "desc2", TaskStatus.NEW, 10, task1.getEndTime().plusMinutes(30));
        task1.setId(1);
        task2.setId(2);
        prioritized.addToPrioritizedSet(task1);
        prioritized.addToPrioritizedSet(task2);
    }

    @Test
    public void prioritizedTaskListShouldContainTaskOrderedByStartTime() {
        Task taskBefore = new Task("task before", "", TaskStatus.NEW, 5, startTime.minusMinutes(20));
        Task taskBetween = new Task("task between", "", TaskStatus.NEW, 5, task1.getEndTime().plusMinutes(5));
        taskBefore.setId(3);
        taskBetween.setId(3);
        prioritized.addToPrioritizedSet(taskBefore);
        prioritized.addToPrioritizedSet(taskBetween);
        List<Task> target = Arrays.asList(taskBefore, task1, taskBetween, task2);
        Assertions.assertIterableEquals(prioritized.getPrioritizedTasks(), target, "Задачи не отсортированы по времени");
    }

    @Test
    public void checkExistingIntersections() {
        Task task1Copy = new Task(task1);
        Task task3 = new Task("not intersecting task", "", TaskStatus.NEW, 1, startTime.plusHours(1));
        Task task4 = new Task("intersecting task", "", TaskStatus.NEW, 20, startTime.plusMinutes(10));

        task1Copy.setId(3);
        task3.setId(4);
        task4.setId(5);

        Assertions.assertTrue(prioritized.checkExistingIntersections(task1Copy).isPresent(), "Должно быть выявлено пересечение задач");
        Assertions.assertTrue(prioritized.checkExistingIntersections(task3).isEmpty(), "Должно быть выявлено отсутствие пересечения задач");
        Assertions.assertTrue(prioritized.checkExistingIntersections(task4).isPresent(), "Должно быть выявлено пересечение задач");
    }

    @Test
    public void isIntersectingTasks() {
        //Пересекающиеся задачи, у которых совпадает время старта и время окончания
        Task task1Copy = new Task(task1);
        Assertions.assertTrue(prioritized.isIntersectingTasks(task1, task1Copy), "Должно быть выявлено пересечение задач");

        //Пересекающиеся задачи, у которых отличается время старта и время окончания
        Task task3 = new Task("task3", "task3", TaskStatus.NEW, 30, startTime.plusMinutes(5));
        Assertions.assertTrue(prioritized.isIntersectingTasks(task1, task3), "Должно быть выявлено пересечение задач");

        //Пересекающиеся задачи, у которых совпадает время старта и отличается время окончания
        task3 = new Task("task3", "task3", TaskStatus.NEW, 30, startTime);
        Assertions.assertTrue(prioritized.isIntersectingTasks(task1, task3), "Должно быть выявлено пересечение задач");

        //Пересекающиеся задачи, у которых отличается время старта и совпадает время окончания
        task3 = new Task("task3", "task3", TaskStatus.NEW, 5, startTime.plusMinutes(5));
        Assertions.assertTrue(prioritized.isIntersectingTasks(task1, task3), "Должно быть выявлено пересечение задач");

        //Пересекающиеся задачи, у которых время окончания первой задачи совпадает с временем начала второй
        task3 = new Task("task3", "task3", TaskStatus.NEW, 30, task1.getEndTime());
        Assertions.assertTrue(prioritized.isIntersectingTasks(task1, task3), "Должно быть выявлено пересечение задач");

        //Непересекающиеся задачи
        task3 = new Task("task3", "task3", TaskStatus.NEW, 30, task1.getEndTime().plusMinutes(5));
        Assertions.assertFalse(prioritized.isIntersectingTasks(task1, task3), "Должно быть выявлено отсутствие пересечения задач");
    }
}
