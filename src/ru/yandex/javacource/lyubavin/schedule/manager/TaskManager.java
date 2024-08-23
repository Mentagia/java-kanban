package ru.yandex.javacource.lyubavin.schedule.manager;

import ru.yandex.javacource.lyubavin.schedule.task.Epic;
import ru.yandex.javacource.lyubavin.schedule.task.Subtask;
import ru.yandex.javacource.lyubavin.schedule.task.Task;

import java.util.ArrayList;

public interface TaskManager {
    int addTask(Task task);

    void updateTask(Task updatedTask);

    Task getTask(int taskId);

    ArrayList<Task> getAllTasks();

    void removeAllTasks();

    void removeTask(int id);

    int addEpic(Epic epic);

    void updateEpic(Epic updatedEpic);

    Epic getEpic(int epicId);

    ArrayList<Epic> getAllEpics();

    void removeAllEpics();

    void removeEpic(int id);

    Integer addSubtask(Subtask subtask);

    void updateSubtask(Subtask updatedSubtask);

    Subtask getSubtask(int subtaskId);

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Subtask> getAllEpicSubtasks(int epicId);

    void removeAllSubtask();

    void removeSubtask(int id);

    HistoryManager getHistoryManager();
}
