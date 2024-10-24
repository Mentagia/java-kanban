package ru.yandex.javacource.lyubavin.schedule.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.yandex.javacource.lyubavin.schedule.exceptions.TaskValidationException;
import ru.yandex.javacource.lyubavin.schedule.task.Task;
import ru.yandex.javacource.lyubavin.schedule.task.Epic;
import ru.yandex.javacource.lyubavin.schedule.task.Subtask;
import ru.yandex.javacource.lyubavin.schedule.enums.TaskStatus;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends AbstractTaskManagerTest<InMemoryTaskManager>{

    //private InMemoryTaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = (InMemoryTaskManager) Managers.getDefault();;
    }


    @Test
    void assureNotAddSubtaskWithoutEpic() {
        Subtask subtask = new Subtask("subtask ", " subtask  description ",
                TaskStatus.NEW, 1);

        Integer subtaskId = taskManager.addSubtask(subtask);

        assertNull(subtaskId, "Подзадача была добавлена без сопутствующего Эпика");
    }

    @Test
    void assureEpicStatusUpdate() {
        Epic epic = new Epic("epic ", " epic description ");

        int epicId = taskManager.addEpic(epic);

        Epic savedEpic = taskManager.getEpic(epicId);

        assertEquals(TaskStatus.NEW, savedEpic.getTaskStatus(), "Неверный статус Эпика");

        Subtask subtask1 = new Subtask("subtask 1",
                " subtask 1 description ", TaskStatus.NEW, epicId);

        int subtaskId1 = taskManager.addSubtask(subtask1);

        assertEquals(TaskStatus.NEW, savedEpic.getTaskStatus(),
                "Неверный статус Эпика");

        Subtask subtask2 = new Subtask("subtask 2",
                " subtask 2 description ", TaskStatus.IN_PROGRESS, epicId);

        int subtaskId2 = taskManager.addSubtask(subtask2);

        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getTaskStatus(), "Неверный статус Эпика");

        Subtask updatedSubtask1 = new Subtask(subtaskId1, " updated 1 Subtask ",
                " updated 1 Subtask description", TaskStatus.IN_PROGRESS, epicId);

        taskManager.updateSubtask(updatedSubtask1);

        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getTaskStatus(), "Неверный статус Эпика");

        updatedSubtask1 = new Subtask(subtaskId1, " updated 1 Subtask ",
                " updated 1 Subtask description", TaskStatus.DONE, epicId);

        taskManager.updateSubtask(updatedSubtask1);

        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getTaskStatus(), "Неверный статус Эпика");

        Subtask updatedSubtask2 = new Subtask(subtaskId2, " updated 2 Subtask ",
                " updated 2 Subtask description", TaskStatus.DONE, epicId);

        taskManager.updateSubtask(updatedSubtask2);

        assertEquals(TaskStatus.DONE, savedEpic.getTaskStatus(), "Неверный статус Эпика");
    }

    @Test
    void assureEpicStatusUpdateToInProgressIfSubtaskStatusChangedToDone() {
        Epic epic = new Epic("epic ", " epic description ");

        int epicId = taskManager.addEpic(epic);

        Epic savedEpic = taskManager.getEpic(epicId);

        Subtask subtask1 = new Subtask("subtask 1", " subtask 1 description ",
                TaskStatus.NEW, epicId);
        Subtask subtask2 = new Subtask("subtask 2", " subtask 2 description ",
                TaskStatus.NEW, epicId);

        int subtaskId1 = taskManager.addSubtask(subtask1);
        int subtaskId2 = taskManager.addSubtask(subtask2);

        Subtask updatedSubtask1 = new Subtask(subtaskId1, " updated 1 Subtask ",
                " updated 1 Subtask description",TaskStatus.DONE, epicId);

        taskManager.updateSubtask(updatedSubtask1);

        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getTaskStatus(), "Неверный статус Эпика");
    }

    @Test
    public void assureTasksWithGeneratedAndAssignedIdsDoNotConflict() {
        Task task1 = new Task(1, "task 1", "task description 1", TaskStatus .NEW);
        Task task2 = new Task("task 2", "task description 2", TaskStatus .NEW);

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        assertNotEquals(task1.getId(),task2.getId(), "Id задач не отличается");
    }

    @Test
    public void assureEpicsWithGeneratedAndAssignedIdsDoNotConflict() {
        Epic epic1 = new Epic(1, "Test epic1",
                "Test epic1 description");
        Epic epic2 = new Epic("Test epic2",
                "Test epic2 description");


        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);

        assertNotEquals(epic1.getId(),epic2.getId(), "Id эпиков не отличается");
    }

    @Test
    public void assureSubtasksWithGeneratedAndAssignedIdsDoNotConflict() {
        Epic epic1 = new Epic("Test epic1",
                "Test epic1 description");
        int epicId =  taskManager.addEpic(epic1);

        Subtask sub1 = new Subtask(1, "Test sub1",
                "Test NewTask1 description", TaskStatus.NEW, epicId);
        Subtask sub2 = new Subtask( "Test sub2",
                "Test NewTask2 description", TaskStatus.NEW, epicId);

        taskManager.addSubtask(sub1);
        taskManager.addSubtask(sub1);

        assertNotEquals(sub1.getId(), sub2.getId(), "Id подзадач не отличается");
    }

    @Test
    void assureDeletedSubtasksShouldNotKeepOldId() {
        Epic epic1 = new Epic("Test epic1", "Test epic1 description");
        int epicId = taskManager.addEpic(epic1);
        Subtask subtask = new Subtask ("Test sub1",
                "Test NewTask1 description",TaskStatus.NEW, epicId);
        int subtaskId = taskManager.addSubtask(subtask);

        taskManager.removeSubtask(subtaskId);
        Subtask deletedSubtask = taskManager.getSubtask(subtaskId);

        assertNull(deletedSubtask, "Удаленная подзадача сохранила свой ID");
    }

    @Test
    void assureChangesInEveryFieldOfTaskByUsingSetter() {
        Task task1 = new Task("task 1", "task description 1", TaskStatus .NEW);
        int taskId = taskManager.addTask(task1);

        task1.setTaskName("New name");
        task1.setTaskDiscr("New description");
        task1.setTaskStatus(TaskStatus.DONE);

        Task updatedTask = taskManager.getTask(taskId);

        assertEquals("New name", updatedTask.getTaskName());
        assertEquals("New description", updatedTask.getTaskDiscr());
        assertEquals(TaskStatus.DONE, updatedTask.getTaskStatus());
    }

    @Test
    void assureChangesInEveryFieldOfEpicByUsingSetter() {
        Epic epic1 = new Epic(1, "Test epic1",
                "Test NewTask1 description");

        int epicId1 = taskManager.addEpic(epic1);

        Subtask sub1 = new Subtask(2, "Test sub1",
                "Test NewTask1 description", TaskStatus.NEW, epicId1);
        Subtask sub2 = new Subtask(3, "Test sub2",
                "Test NewTask2 description", TaskStatus.NEW, epicId1);

        int subtaskId1 = taskManager.addSubtask(sub1);
        int subtaskId2 = taskManager.addSubtask(sub2);

        epic1.setTaskName("New name");
        epic1.setTaskDiscr("New description");
        Epic updatedEpic = taskManager.getEpic(epicId1);

        assertEquals("New name", updatedEpic.getTaskName());
        assertEquals("New description", updatedEpic.getTaskDiscr());
        assertEquals(2, updatedEpic.getSubtaskIds().size());
        assertEquals(Arrays.asList(sub1.getId(),sub2.getId()), updatedEpic.getSubtaskIds());
    }

    @Test
    void assureChangesInEveryFieldOfSubtaskByUsingSetter() {
        Epic epic = new Epic ("Test epic1",
                "Test NewTask1 description");

        int epicId = taskManager.addEpic(epic);

        Subtask sub1 = new Subtask("Test sub1", " Test sub1 description", TaskStatus.NEW, epicId);

        int subtaskId = taskManager.addSubtask(sub1);

        sub1.setTaskName("New name");
        sub1.setTaskDiscr("New description");
        sub1.setTaskStatus(TaskStatus.DONE);

        Subtask updatedSubtask = taskManager.getSubtask(subtaskId);

        assertEquals("New name", updatedSubtask.getTaskName());
        assertEquals("New description", updatedSubtask.getTaskDiscr());
        assertEquals(TaskStatus.DONE, updatedSubtask.getTaskStatus());
    }

    @Test
    void assureRemoveSubtaskIds() {
        Epic epic1 = new Epic(1, "Test epic1",
                "Test epic1 description");
        int epicId1 = taskManager.addEpic(epic1);

        Subtask sub1 = new Subtask(2, "Test sub1",
                "Test sub1 description", TaskStatus.NEW, epicId1);
        Subtask sub2 = new Subtask(3, "Test sub2",
                "Test sub2 description", TaskStatus.NEW, epicId1);

        int subtaskId1 = taskManager.addSubtask(sub1);

        assertNotNull(epic1.getSubtaskIds(),"ID не добавлен.");

        int subtaskId2 = taskManager.addSubtask(sub2);

        ArrayList<Integer> checkList = new ArrayList<>();

        checkList.add(subtaskId1);
        checkList.add(subtaskId2);

        assertEquals(checkList, epic1.getSubtaskIds(), "ID не совпадают.");

        checkList.remove(Integer.valueOf(subtaskId2));
        epic1.removeSubtaskId(subtaskId2);

        assertEquals(checkList, epic1.getSubtaskIds(), "ID не совпадают.");
    }

    @Test
    void assureNotSaveTaskIfIntersection() {
        Task task1 = new Task("task 1", "task description 1", TaskStatus.NEW, time1 , duration);
        Task task2 = new Task("task 2", "task description 2", TaskStatus.NEW, time1 , duration);

        assertThrows(TaskValidationException.class, () -> {
            taskManager.addTask(task1);
            taskManager.addTask(task2);
        },"Неправильная работа валидатора." + "Обе задачи были добавлены.");
    }

    @Test
    void assureSaveTaskIfNotIntersection() {
        Task task1 = new Task("task 1", "task description 1", TaskStatus.NEW, time1 , duration);
        Task task2 = new Task("task 2", "task description 2", TaskStatus.NEW, time2 , duration);

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        assertEquals(2, taskManager.getAllTasks().size(),"Неправильная работа валидатора." +
                "Не все задачи были добавлены.");
    }
}