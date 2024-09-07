package ru.yandex.javacource.lyubavin.schedule.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.yandex.javacource.lyubavin.schedule.task.Task;
import ru.yandex.javacource.lyubavin.schedule.task.Epic;
import ru.yandex.javacource.lyubavin.schedule.task.Subtask;
import ru.yandex.javacource.lyubavin.schedule.task.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class InMemoryHistoryManagerTest {
    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void assureSavePreviousVersionOfTask() {
        Task task = new Task(1, "task", "task description", TaskStatus.NEW);

        historyManager.addTaskToHistory(task);

        assertEquals(task, historyManager.getHistory().get(0), "Добавлена неверная задача");

        Task updatedTask = new Task(1, "updated Task",
                "updated Task description", TaskStatus.IN_PROGRESS);

        taskManager.updateTask(updatedTask);

        assertNotEquals(updatedTask.getTaskName(), historyManager.getHistory().get(0).getTaskName(),
                "Сохранена новая версия задачи");
        assertNotEquals(updatedTask.getTaskDiscr(), historyManager.getHistory().get(0).getTaskDiscr(),
                "Сохранена новая версия задачи");
        assertNotEquals(updatedTask.getTaskStatus(), historyManager.getHistory().get(0).getTaskStatus(),
                "Сохранена новая версия задачи");
    }

    @Test
    void assureAddTaskToHistory() {
        Task task1 = new Task(1,"task 1", "task description 1", TaskStatus .NEW);
        Task task2 = new Task(2,"task 2", "task  description 2", TaskStatus .NEW);
        Epic epic1 = new Epic(3,"epic 1", "epic description 1");
        Epic epic2 = new Epic(4,"epic 2", "epic description 2");
        Subtask subtask1 = new Subtask(5,"subtask 1",
                "subtask description 1", TaskStatus .NEW, 3);
        Subtask subtask2 = new Subtask(6,"subtask 2",
                "subtask description 2", TaskStatus .NEW,3);
        Subtask subtask3 = new Subtask(7,"subtask 3",
                "subtask description 3", TaskStatus .NEW,4);

        historyManager.addTaskToHistory(task1);
        historyManager.addTaskToHistory(task2);
        historyManager.addTaskToHistory(epic1);
        historyManager.addTaskToHistory(epic2);
        historyManager.addTaskToHistory(subtask1);
        historyManager.addTaskToHistory(subtask2);
        historyManager.addTaskToHistory(subtask3);

        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "Задачи не добавлены в историю");
        assertEquals(7, history.size(), "Не все задачи добавлены в историю");
    }

    @Test
    void assureAddNewTaskAtTheEndOfHistoryList() {
        Task task1 = new Task(1,"task 1", "task description 1", TaskStatus .NEW);
        Task task2 = new Task(2,"task 2", "task  description 2", TaskStatus .NEW);

        historyManager.addTaskToHistory(task1);
        historyManager.addTaskToHistory(task2);

        final List<Task> history = historyManager.getHistory();

        assertEquals(task2, history.get(1), "Задача не была добавлена в конец просмотра истории");
    }

    @Test
    void assureAddSameTaskOneTimeAndOnlyTheLatestVersionToHistory () {
        Task task1 = new Task(1,"task 1", "task description 1", TaskStatus .NEW);
        Task task2 = new Task(2,"task 2", "task  description 2", TaskStatus .NEW);


        historyManager.addTaskToHistory(task1);
        historyManager.addTaskToHistory(task2);
        historyManager.addTaskToHistory(task1);

        final List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "Были добавлены дубликаты задачи");
        assertEquals(task1, history.get(1), "В истории сохранен не последний просмотр дублированной задачи ");
    }

    @Test
    void assureDeleteTaskFromHistoryWhenTaskIsDeletedFromManager() {
        Task task1 = new Task("task 1", "task description 1", TaskStatus .NEW);
        Task task2 = new Task("task 1", "task description 1", TaskStatus .NEW);
        Task task3 = new Task("task 1", "task description 1", TaskStatus .NEW);

        int taskId1 = taskManager.addTask(task1);
        int taskId2 = taskManager.addTask(task2);
        int taskId3 = taskManager.addTask(task3);

        taskManager.getTask(taskId1);
        taskManager.getTask(taskId2);
        taskManager.getTask(taskId3);

        List<Task> history1 = taskManager.getHistoryList();

        assertEquals(3, history1.size(), "Задачи не были добавлены");

        taskManager.removeTask(taskId1);

        final List<Task> history2 = taskManager.getHistoryList();

        assertEquals(2, history2.size(), "Задача не была удалена из истории");
        assertNotEquals(history1, history2, "Задача не была удалена из истории");
    }
}