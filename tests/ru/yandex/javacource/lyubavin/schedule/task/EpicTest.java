package ru.yandex.javacource.lyubavin.schedule.task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;


class EpicTest {
    //проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;
    //Не представляется возможным добавить Epic в качестве Subtask, т.к. Subtask принимает только Subtask :(
    @Test
    void assureEpicObjectsAreEqualIfIdEqual(){
        Epic epic1 = new Epic(1, "Test epic1",
                "Test NewTask1 description");
        Epic epic2 = new Epic(1, "Test epic2",
                "Test NewTask2 description");

        assertEquals(epic1, epic2, "Задачи не совпадают.");
    }

    @Test
    void assureGetCorrectSubtaskIds() {
        Epic epic1 = new Epic(1, "Test epic1",
                "Test NewTask1 description");
        Subtask sub1 = new Subtask(2, "Test sub1",
                "Test NewTask1 description", TaskStatus.NEW, 1);
        Subtask sub2 = new Subtask(3, "Test sub2",
                "Test NewTask2 description", TaskStatus.NEW, 1);

        epic1.addSubtaskId(sub1.getId());

        assertNotNull(epic1.getSubtaskIds(),"ID не добавлен.");

        epic1.addSubtaskId(sub2.getId());

        ArrayList<Integer> checkList = new ArrayList<>();

        checkList.add(2);
        checkList.add(3);

        assertEquals(checkList, epic1.getSubtaskIds(), "ID не совпадают.");
    }

    @Test
    void assureRemoveSubtaskIds() {
        Epic epic1 = new Epic(1, "Test epic1",
                "Test NewTask1 description");
        Subtask sub1 = new Subtask(2, "Test sub1",
                "Test NewTask1 description", TaskStatus.NEW, 1);
        Subtask sub2 = new Subtask(3, "Test sub2",
                "Test NewTask2 description", TaskStatus.NEW, 1);

        epic1.addSubtaskId(sub1.getId());

        assertNotNull(epic1.getSubtaskIds(),"ID не добавлен.");

        epic1.addSubtaskId(sub2.getId());

        ArrayList<Integer> checkList = new ArrayList<>();

        checkList.add(2);
        checkList.add(3);

        assertEquals(checkList, epic1.getSubtaskIds(), "ID не совпадают.");

        checkList.remove(Integer.valueOf(3));
        epic1.removeSubtaskId(3);

        assertEquals(checkList, epic1.getSubtaskIds(), "ID не совпадают.");
    }
}