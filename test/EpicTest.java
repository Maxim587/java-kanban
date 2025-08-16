import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import ru.educationmm.taskmanager.main.model.Epic;
import ru.educationmm.taskmanager.main.model.Subtask;
import ru.educationmm.taskmanager.main.model.TaskStatus;

import java.time.LocalDateTime;
import java.time.Month;


class EpicTest {

    Epic epic1;
    public LocalDateTime startTime = LocalDateTime.of(2025, Month.JUNE, 15, 10, 50);

    @BeforeEach
    public void prepare() {
        epic1 = new Epic("name1", "description1");
        epic1.setId(1);
    }

    @Test
    public void epicsShouldBeEqualWhenEqualTheirIds() {
        Epic epic2 = new Epic("name2", "description2");
        epic2.setId(1);
        Assertions.assertEquals(epic1, epic2, "Эпики не равны при одинаковых id");
    }

    @Test
    public void checkEpicStatusCalculation() {
        Assertions.assertSame(TaskStatus.NEW, epic1.getStatus(), "Новый эпик не в статусе NEW");

        Subtask subtask1 = new Subtask("subtask1", "description", 1, TaskStatus.NEW, 30, startTime);
        Subtask subtask2 = new Subtask("subtask2", "description", 1, TaskStatus.NEW, 30, startTime.plusHours(1));
        subtask1.setId(2);
        subtask2.setId(3);

        //подзадачи в статусе NEW
        epic1.addSubtaskToEpic(subtask1);
        epic1.addSubtaskToEpic(subtask2);
        Assertions.assertSame(TaskStatus.NEW, epic1.getStatus(), "Эпик не в статусе NEW");

        //подзадачи в статусе IN_PROGRESS
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        epic1.setCalculatedFields();
        Assertions.assertSame(TaskStatus.IN_PROGRESS, epic1.getStatus(), "Эпик не в статусе IN_PROGRESS");

        //подзадачи в статусе DONE
        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        epic1.setCalculatedFields();
        Assertions.assertSame(TaskStatus.DONE, epic1.getStatus(), "Эпик не в статусе DONE");

        //подзадачи в статусе NEW и DONE
        subtask2.setStatus(TaskStatus.NEW);
        epic1.setCalculatedFields();
        Assertions.assertSame(TaskStatus.IN_PROGRESS, epic1.getStatus(), "Эпик не в статусе IN_PROGRESS");

        //удаление всех подзадач из эпика
        epic1.deleteSubtaskInEpic(subtask1);
        epic1.deleteSubtaskInEpic(subtask2);
        epic1.setCalculatedFields();
        Assertions.assertSame(TaskStatus.NEW, epic1.getStatus(), "Эпик не в статусе NEW");
    }
}