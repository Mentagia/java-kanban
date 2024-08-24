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
        Task task1 = new Task("task 1", "task description 1", TaskStatus .NEW);
        Task task2 = new Task("task 2", "task  description 2", TaskStatus .NEW);
        Epic epic1 = new Epic("epic 1", "epic description 1");
        Epic epic2 = new Epic("epic 2", "epic description 2");
        Subtask subtask1 = new Subtask("subtask 1",
                "subtask description 1", TaskStatus .NEW, 2);
        Subtask subtask2 = new Subtask("subtask 2",
                "subtask description 2", TaskStatus .NEW,2);
        Subtask subtask3 = new Subtask("subtask 3",
                "subtask description 3", TaskStatus .NEW,3);

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
    void assureRemoveFirstTaskInHistoryIfListSizeIsBiggerThan10() {
        Task task;
        int taskId;

        for (int i = 1; i <= 10; i++) {
            task = new Task("task " + i, " task description " + i, TaskStatus .NEW);
            taskId = taskManager.addTask(task);
            historyManager.addTaskToHistory(task);
        }

        final List<Task> history = historyManager.getHistory();

        assertNotNull(history, "Задачи не добавлены в историю");
        assertEquals(10, history.size(),"Не все задачи добавлены в историю");
        assertEquals(0, history.get(0).getId(),"Сохранена неверная задача");
        assertEquals(9, history.get(9).getId(), "Сохранена неверная задача");

        task = new Task("task 11", "task description 11", TaskStatus .NEW);
        taskId = taskManager.addTask(task);
        historyManager.addTaskToHistory(task);

        assertNotNull(history);
        assertEquals(10, history.size(), "Не все задачи добавлены в историю");
        assertEquals(1, history.get(0).getId(),"Самая старая задача не была удалена");
        assertEquals(10, history.get(9).getId(), "Выведена неверная задача");


    }

}