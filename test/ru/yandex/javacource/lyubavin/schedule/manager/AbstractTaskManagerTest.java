package ru.yandex.javacource.lyubavin.schedule.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacource.lyubavin.schedule.enums.TaskStatus;
import ru.yandex.javacource.lyubavin.schedule.task.Epic;
import ru.yandex.javacource.lyubavin.schedule.task.Subtask;
import ru.yandex.javacource.lyubavin.schedule.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

public abstract class AbstractTaskManagerTest <T extends TaskManager> {
    protected T taskManager;
    protected Duration duration;
    protected LocalDateTime time1;
    protected LocalDateTime time2;
    protected LocalDateTime time3;
    protected LocalDateTime time4;

    @BeforeEach
    void init() {
        duration = Duration.ofMinutes(10);
        time1 = LocalDateTime.of(2024, 10, 12, 12, 0);
        time2 = LocalDateTime.of(2024, 10, 12, 13, 0);
        time3 = LocalDateTime.of(2024, 10, 12, 14, 0);
        time4 = LocalDateTime.of(2024, 10, 12, 15, 0);
    }

    @Test
    void assureTaskAddAndFindByID() {
        Task task = new Task("task ", "task description ", TaskStatus.NEW, time1 , duration);

        int taskId = taskManager.addTask(task);
        Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не была добавлена");
        assertEquals(task, savedTask, "Задачи не совпадают");

        List<Task> taskList = taskManager.getAllTasks();

        assertNotNull(taskList, "Задача не была добавлена");
        assertEquals(1, taskList.size(), "Неверное количество задач");
        assertEquals(task, taskList.getFirst(), "Задачи не совпадают");
    }

    @Test
    void assureEpicAddAndFindByID() {
        Epic epic = new Epic("epic ", " epic description ");

        int epicId = taskManager.addEpic(epic);
        Task savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Задача не была добавлена");
        assertEquals(epic, savedEpic, "Задачи не совпадают");

        List<Epic> epicList = taskManager.getAllEpics();

        assertNotNull(epicList, "Задача не была добавлена");
        assertEquals(1, epicList.size(), "Неверное количество задач");
        assertEquals(epic, epicList.getFirst(), "Задачи не совпадают");
    }

    @Test
    void assureSubtaskAddAndFindByID() {
        Epic epic = new Epic("epic ", " epic description ");

        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("subtask ",
                " subtask  description ", TaskStatus.NEW, time1 , duration, epicId);

        int subtaskId = taskManager.addSubtask(subtask);
        Task savedSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(savedSubtask, "Задача не была добавлена");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают");

        List<Subtask> subtaskList = taskManager.getAllSubtasks();

        assertNotNull(subtaskList, "Задача не была добавлена");
        assertEquals(1, subtaskList.size(), "Неверное количество задач");
        assertEquals(savedSubtask, subtaskList.getFirst(), "Задачи не совпадают");
    }

    @Test
    void assureGetAllSubtaskOfSpecifiedEpic() {
        Epic epic1 = new Epic("epic 1", " epic 1 description ");
        Epic epic2 = new Epic("epic 2", " epic 2 description ");

        int epicId1 = taskManager.addEpic(epic1);
        int epicId2 = taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask("subtask 1",
                " subtask 1 description ", TaskStatus.NEW, time1 , duration, epicId1);
        Subtask subtask2 = new Subtask("subtask 2",
                " subtask 2 description ", TaskStatus.NEW, time2 , duration, epicId1);
        Subtask subtask3 = new Subtask("subtask 3",
                " subtask 3 description ", TaskStatus.NEW, time3 , duration, epicId2);
        Subtask subtask4 = new Subtask("subtask 4",
                " subtask 4 description ", TaskStatus.NEW, time4 , duration, epicId2);

        int subtaskId1 = taskManager.addSubtask(subtask1);
        int subtaskId2 = taskManager.addSubtask(subtask2);
        int subtaskId3 = taskManager.addSubtask(subtask3);
        int subtaskId4 = taskManager.addSubtask(subtask4);

        List<Subtask> epic1Subtasks = taskManager.getAllEpicSubtasks(epicId1);

        assertNotNull(epic1Subtasks, "Задачи не были добавлены");
        assertEquals(2, epic1Subtasks.size(), "Неверное количество задач");
        assertEquals(subtask1, epic1Subtasks.get(0), "Задачи не совпадают");
        assertEquals(subtask2, epic1Subtasks.get(1), "Задачи не совпадают");

        List<Subtask> epic2Subtasks = taskManager.getAllEpicSubtasks(epicId2);

        assertNotNull(epic2Subtasks, "Задачи не были добавлены");
        assertEquals(2, epic2Subtasks.size(), "Неверное количество задач");
        assertEquals(subtask3, epic2Subtasks.get(0), "Задачи не совпадают");
        assertEquals(subtask4, epic2Subtasks.get(1), "Задачи не совпадают");
    }

    @Test
    void assureTaskUpdate() {
        Task task = new Task("task ", "task description ", TaskStatus.NEW);
        int taskId = taskManager.addTask(task);

        Task updatedTask = new Task(taskId, "updatedTask ", " updatedTask description ",
                TaskStatus.IN_PROGRESS, time2 , Duration.ofMinutes(10));
        taskManager.updateTask(updatedTask);
        Task savedUpdatedTask = taskManager.getTask(taskId);

        assertNotNull(savedUpdatedTask, "Задача не была добавлена");
        assertEquals(updatedTask, savedUpdatedTask, "Задачи не совпадают");

        List<Task> taskList = taskManager.getAllTasks();

        assertNotNull(taskList, "Задача не была добавлена");
        assertEquals(1, taskList.size(), "Неверное количество задач");
        assertEquals(savedUpdatedTask, taskList.getFirst(), "Задачи не совпадают");
        assertEquals(Duration.ofMinutes(10), taskList.getFirst().getDuration(), "Продолжительность не изменилась");
        assertEquals(time2, taskList.getFirst().getStartTime(), "Время начала не изменилось");
    }

    @Test
    void assureEpicUpdate() {
        Epic epic = new Epic("epic ", " epic description ");
        int epicId = taskManager.addEpic(epic);

        Epic updatedEpic = new Epic(epicId, " updated Epic ", " updated Epic description");
        taskManager.updateEpic(updatedEpic);

        Epic savedUpdatedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedUpdatedEpic, "Задача не была добавлена");
        assertEquals(updatedEpic, savedUpdatedEpic, "Задачи не совпадают");

        List<Epic> epicList = taskManager.getAllEpics();

        assertNotNull(epicList, "Задача не была добавлена");
        assertEquals(1, epicList.size(), "Неверное количество задач");
        assertEquals(updatedEpic, epicList.getFirst(), "Задачи не совпадают");
    }

    @Test
    void assureSubtaskUpdate() {
        Epic epic = new Epic("epic ", " epic description ");

        int epicId = taskManager.addEpic(epic);

        Subtask subtask = new Subtask("subtask ", " subtask  description ", TaskStatus.NEW, epicId);

        int subtaskId = taskManager.addSubtask(subtask);

        Subtask updatedSubtask = new Subtask(subtaskId, " updated Subtask ",
                "updated Subtask description", TaskStatus.DONE, time1, Duration.ofMinutes(10), epicId);

        taskManager.updateSubtask(updatedSubtask);

        Task savedUpdatedSubtask = taskManager.getSubtask(subtaskId);

        assertNotNull(savedUpdatedSubtask, "Задача не была добавлена");
        assertEquals(updatedSubtask, savedUpdatedSubtask, "Задачи не совпадают");

        List<Subtask> subtaskList = taskManager.getAllSubtasks();

        assertNotNull(subtaskList, "Задача не была добавлена");
        assertEquals(1, subtaskList.size(), "Неверное количество задач");
        assertEquals(updatedSubtask, subtaskList.getFirst(), "Задачи не совпадают");
        assertEquals(Duration.ofMinutes(10), subtaskList.getFirst().getDuration(), "Продолжительность не изменилась");
        assertEquals(time1, subtaskList.getFirst().getStartTime(), "Время начала не изменилось");
    }

    @Test
    void assureDeleteTask() {
        Task task1 = new Task("task 1", "task 1 description ", TaskStatus.NEW);
        Task task2 = new Task("task 2", "task 2 description ", TaskStatus.NEW);
        Task task3 = new Task("task 3", "task 3 description ", TaskStatus.NEW);

        int taskId1 = taskManager.addTask(task1);
        int taskId2 = taskManager.addTask(task2);
        int taskId3 = taskManager.addTask(task3);

        assertNotNull(taskManager.getTask(taskId1), "Задача не была добавлена");
        assertNotNull(taskManager.getTask(taskId2), "Задача не была добавлена");
        assertNotNull(taskManager.getTask(taskId3), "Задача не была добавлена");

        taskManager.removeTask(taskId1);

        assertNull(taskManager.getTask(taskId1), "Задача не была удалена");

        taskManager.removeAllTasks();

        assertNull(taskManager.getTask(taskId2), "Задача не была удалена");
        assertNull(taskManager.getTask(taskId3), "Задача не была удалена");
    }


    @Test
    void assureDeleteEpic() {
        Epic epic1 = new Epic("epic 1", " epic 1 description ");
        Epic epic2 = new Epic("epic 2", " epic 1 description ");
        Epic epic3 = new Epic("epic 3", " epic 1 description ");

        int epicId1 = taskManager.addEpic(epic1);
        int epicId2 = taskManager.addEpic(epic2);
        int epicId3 = taskManager.addEpic(epic3);

        assertNotNull(taskManager.getEpic(epicId1), "Эпик не был добавлен ");
        assertNotNull(taskManager.getAllEpics(), " Эпики не была добавлены ");


        Subtask subtask1 = new Subtask("subtask 1",
                " subtask 1 description ", TaskStatus.NEW, epicId1);
        Subtask subtask2 = new Subtask("subtask 2",
                " subtask 2 description ", TaskStatus.NEW, epicId1);
        Subtask subtask3 = new Subtask("subtask 2",
                " subtask 2 description ", TaskStatus.NEW, epicId2);
        Subtask subtask4 = new Subtask("subtask 2",
                " subtask 2 description ", TaskStatus.NEW, epicId3);

        int subtaskId1 = taskManager.addSubtask(subtask1);
        int subtaskId2 = taskManager.addSubtask(subtask2);
        int subtaskId3 = taskManager.addSubtask(subtask3);
        int subtaskId4 = taskManager.addSubtask(subtask4);

        assertNotNull(taskManager.getAllEpicSubtasks(epicId1), "Подзадачи не были добавлены");
        assertNotNull(taskManager.getAllEpicSubtasks(epicId2), "Подзадачи не были добавлены");

        assertNotNull(taskManager.getAllEpicSubtasks(epicId3), "Подзадачи не были добавлены");

        taskManager.removeEpic(epicId1);

        assertNull(taskManager.getEpic(epicId1), " Эпик не был удален ");
        assertNotNull(taskManager.getAllEpics(), " Эпики были удалены ");
        assertEquals(2, taskManager.getAllSubtasks().size(), "Подзадачи не была удалены");
        assertNotNull(taskManager.getAllEpicSubtasks(epicId2), "Подзадачи были удалены ");
        assertNotNull(taskManager.getAllEpicSubtasks(epicId3), "Подзадачи были удалены ");

        taskManager.removeAllEpics();
        assertNull(taskManager.getAllEpics(), " Эпики не были удалены ");
        assertNull(taskManager.getAllSubtasks(), "Подзадачи не была удалены");
    }

    @Test
    void assureDeleteSubtask() {
        Epic epic1 = new Epic("epic 1", " epic 1 description ");
        int epicId1 = taskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("subtask 1",
                " subtask 1 description ", TaskStatus.NEW, epicId1);
        Subtask subtask2 = new Subtask("subtask 2",
                " subtask 2 description ", TaskStatus.NEW, epicId1);
        Subtask subtask3 = new Subtask("subtask 3",
                " subtask 3 description ", TaskStatus.NEW, epicId1);

        int subtaskId1 = taskManager.addSubtask(subtask1);
        int subtaskId2 = taskManager.addSubtask(subtask2);
        int subtaskId3 = taskManager.addSubtask(subtask3);

        assertNotNull(taskManager.getAllEpicSubtasks(epicId1), "Подзадачи не была добавлены");

        taskManager.removeSubtask(subtaskId1);

        assertNotNull(taskManager.getAllEpicSubtasks(epicId1), "Все подзадачи были удалены");
        assertNull(taskManager.getSubtask(subtaskId1), "Подзадача не была удалена");

        taskManager.removeAllSubtask();
        assertEquals(new ArrayList<>(), epic1.getSubtaskIds(),"Подзадачи не были удалены");
    }
}
