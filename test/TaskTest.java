import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import ru.educationmm.taskmanager.main.model.Task;
import ru.educationmm.taskmanager.main.model.TaskStatus;

import java.time.LocalDateTime;

class TaskTest {

    @Test
    public void tasksShouldBeEqualWhenEqualTheirIds() {
        Task task1 = new Task("name1", "description1", TaskStatus.NEW, 15, LocalDateTime.now());
        Task task2 = new Task("name2", "description2", TaskStatus.DONE, 15, LocalDateTime.now());
        task1.setId(1);
        task2.setId(1);
        Assertions.assertEquals(task1, task2);
    }
}