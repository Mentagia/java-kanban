package ru.yandex.javacource.lyubavin.schedule.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {
    @Test
    void assureSubtaskObjectsAreEqualIfIdEqual(){
        Subtask sub1 = new Subtask(1, "Test sub1",
                "Test NewTask1 description", TaskStatus.NEW, 2);
        Subtask sub2 = new Subtask(1, "Test sub2",
                "Test NewTask2 description", TaskStatus.NEW, 2);
        assertEquals(sub1, sub2, "Задачи не совпадают.");
    }

    @Test
    void assureGetCorrectEpicId() {
        Subtask sub = new Subtask(1, "Test NewTask1",
                "Test NewTask1 description", TaskStatus.NEW, 2);

        assertEquals(2, sub.getEpicId(),"EpicID не совпадают.");
    }

    @Test
    void assureSetNewId(){
        Subtask sub = new Subtask(1, "Test NewTask1",
                "Test NewTask1 description", TaskStatus.NEW, 2);

        sub.setEpicId(3);

        assertEquals(3, sub.getEpicId(), "ID не изменился");
    }
}