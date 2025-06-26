package ru.educationmm.taskmanager.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import ru.educationmm.taskmanager.main.model.Task;
import ru.educationmm.taskmanager.main.model.TaskStatus;

class TaskTest {

    @Test
    public void tasksShouldBeEqualWhenEqualTheirIds() {
        Task task1 = new Task("name1", "description1", TaskStatus.NEW);
        Task task2 = new Task("name2", "description2", TaskStatus.DONE);
        task1.setId(1);
        task2.setId(1);
        Assertions.assertEquals(task1, task2);
    }
}