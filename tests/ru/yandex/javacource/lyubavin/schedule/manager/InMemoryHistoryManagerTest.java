package ru.yandex.javacource.lyubavin.schedule.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ru.yandex.javacource.lyubavin.schedule.task.Task;
import ru.yandex.javacource.lyubavin.schedule.task.Epic;
import ru.yandex.javacource.lyubavin.schedule.task.Subtask;
import ru.yandex.javacource.lyubavin.schedule.task.TaskStatus;

import java.util.ArrayList;



class InMemoryHistoryManagerTest {
    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
        historyManager = taskManager.getHistoryManager();
    }

    @Test
    void assureSavePreviousVersionOfTask() {

        Task task = new Task(1, "task", "task description", TaskStatus.NEW);

        historyManager.addTaskToHistory(task);

        assertEquals(1, task.getId(), "Добавлена неверная задача");

        task.setId(2);

        assertEquals(1, historyManager.getHistory().get(0).getId(),
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

        ArrayList<Task> history = historyManager.getHistory();

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

        final ArrayList<Task> history = historyManager.getHistory();

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