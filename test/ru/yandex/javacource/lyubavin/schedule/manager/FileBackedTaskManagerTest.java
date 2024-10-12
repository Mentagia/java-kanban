package ru.yandex.javacource.lyubavin.schedule.manager;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import ru.yandex.javacource.lyubavin.schedule.exceptions.ManagerLoadException;
import ru.yandex.javacource.lyubavin.schedule.task.Task;
import ru.yandex.javacource.lyubavin.schedule.task.Epic;
import ru.yandex.javacource.lyubavin.schedule.task.Subtask;
import ru.yandex.javacource.lyubavin.schedule.enums.TaskStatus;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends AbstractTaskManagerTest<FileBackedTaskManager> {

    @BeforeEach
    void init() {
        taskManager = (FileBackedTaskManager) Managers.getDefault();
    }

    @Test
    public void assureFileSaveAndLoadWithoutTime() throws ManagerLoadException {

        Task task1 = new Task(0,"task 1", "task description 1", TaskStatus .NEW);
        Task task2 = new Task(1,"task 2", "task description 2", TaskStatus .NEW);
        Epic epic1 = new Epic(2,"epic 1", "epic description 1");
        Epic epic2 = new Epic(3,"epic 2", "epic description 2");
        Subtask subtask1 = new Subtask(5,"subtask 1",
                "subtask description 1", TaskStatus .NEW, 2);
        Subtask subtask2 = new Subtask(6,"subtask 2",
                "subtask description 2", TaskStatus .NEW,2);
        Subtask subtask3 = new Subtask(7,"subtask 3",
                "subtask description 3", TaskStatus .NEW,3);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        List<Task> tasksBeforeLoad = taskManager.getAllTasks();
        List<Epic> epicsBeforeLoad = taskManager.getAllEpics();
        List<Subtask> subTasksBeforeLoad = taskManager.getAllSubtasks();
        taskManager = FileBackedTaskManager.loadFromFile(Paths.get("taskLogs/tasks.csv").toFile());
        List<Task> tasksAfterLoad = taskManager.getAllTasks();
        List<Epic> epicsAfterLoad = taskManager.getAllEpics();
        List<Subtask> subTasksAfterLoad = taskManager.getAllSubtasks();

        for (int i=0; i<tasksBeforeLoad.size(); i++) {
            assertEquals(tasksBeforeLoad.get(i),tasksAfterLoad.get(i));
        }
        for (int i=0; i<epicsBeforeLoad.size(); i++) {
            assertEquals(epicsBeforeLoad.get(i),epicsAfterLoad.get(i));
        }
        for (int i=0; i<subTasksBeforeLoad.size(); i++) {
            assertEquals(subTasksBeforeLoad.get(i),subTasksAfterLoad.get(i));
        }
    }

    @Test
    public void assureEmptyFileSaveAndLoad() throws ManagerLoadException {
        taskManager.save();

        assertTrue(taskManager.tasks.isEmpty());
        assertTrue(taskManager.epics.isEmpty());
        assertTrue(taskManager.subtasks.isEmpty());

        taskManager = FileBackedTaskManager.loadFromFile(Paths.get("taskLogs/tasks.csv").toFile());

        assertTrue(taskManager.tasks.isEmpty());
        assertTrue(taskManager.epics.isEmpty());
        assertTrue(taskManager.subtasks.isEmpty());
    }

    @Test
    public void assureThrowExceptionWhenLoadFromWrongFile() {
        assertThrows(ManagerLoadException.class, () ->
                FileBackedTaskManager.loadFromFile(Paths.get("taskLogs/task.csv").toFile()));
    }

    @Test
    public void assureFileSaveAndLoadWithAndWithoutTime() throws ManagerLoadException {

        Task task1 = new Task(0,"task 1", "task description 1", TaskStatus .NEW,
                LocalDateTime.of(2024,10,12,23, 0), Duration.ofHours(1));
        Task task2 = new Task(1,"task 2", "task description 2", TaskStatus .NEW);
        Epic epic1 = new Epic(2,"epic 1", "epic description 1");
        Epic epic2 = new Epic(3,"epic 2", "epic description 2");
        Subtask subtask1 = new Subtask(5,"subtask 1",
                "subtask description 1", TaskStatus .NEW, 2);
        Subtask subtask2 = new Subtask(6,"subtask 2",
                "subtask description 2", TaskStatus .NEW,
                LocalDateTime.of(2024,10,12,12, 0), Duration.ofHours(1),2);
        Subtask subtask3 = new Subtask(7,"subtask 3",
                "subtask description 3", TaskStatus .NEW,3);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        List<Task> tasksBeforeLoad = taskManager.getAllTasks();
        List<Epic> epicsBeforeLoad = taskManager.getAllEpics();
        List<Subtask> subTasksBeforeLoad = taskManager.getAllSubtasks();
        taskManager = FileBackedTaskManager.loadFromFile(Paths.get("taskLogs/tasks.csv").toFile());
        List<Task> tasksAfterLoad = taskManager.getAllTasks();
        List<Epic> epicsAfterLoad = taskManager.getAllEpics();
        List<Subtask> subTasksAfterLoad = taskManager.getAllSubtasks();

        for (int i=0; i<tasksBeforeLoad.size(); i++) {
            assertEquals(tasksBeforeLoad.get(i),tasksAfterLoad.get(i));
        }
        for (int i=0; i<epicsBeforeLoad.size(); i++) {
            assertEquals(epicsBeforeLoad.get(i),epicsAfterLoad.get(i));
        }
        for (int i=0; i<subTasksBeforeLoad.size(); i++) {
            assertEquals(subTasksBeforeLoad.get(i),subTasksAfterLoad.get(i));
        }
    }
}
