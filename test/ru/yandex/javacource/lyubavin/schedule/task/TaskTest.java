package ru.yandex.javacource.lyubavin.schedule.task;

import org.junit.jupiter.api.Test;
import ru.yandex.javacource.lyubavin.schedule.enums.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    @Test
    void assureTaskObjectsAreEqualIfIdEqual() {
        Task task1 = new Task(1, "Test NewTask1",
                "Test NewTask1 description", TaskStatus.NEW);
        Task task2 = new Task(1, "Test NewTask2",
                "Test NewTask2 description", TaskStatus.NEW);

        assertEquals(task1, task2, "Задачи не совпадают.");
    }

    @Test
    void assureSetNewId() {
        Task task = new Task(1, "Test NewTask1",
                "Test NewTask description", TaskStatus.NEW);

        task.setId(2);

        assertEquals(2, task.getId(), "ID не изменился");
    }

    @Test
    void assureSetNewName() {
        Task task = new Task(1, "Test NewTask1",
                "Test NewTask description", TaskStatus.NEW);

        task.setTaskName("New");

        assertEquals("New", task.getTaskName(), "Имя не изменилось");
    }

    @Test
    void assureSetNewDescription() {
        Task task = new Task(1, "Test NewTask1",
                "Test NewTask description", TaskStatus.NEW);

        task.setTaskDiscr("New");

        assertEquals("New", task.getTaskDiscr(), "Описание не изменилось");
    }

    @Test
    void assureSetNewStatus() {
        Task task = new Task(1, "Test NewTask1",
                "Test NewTask description", TaskStatus.NEW);

        task.setTaskStatus(TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, task.getTaskStatus(), "Статус не изменился");
    }
}