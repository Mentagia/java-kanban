package ru.yandex.javacource.lyubavin.schedule.manager;

import ru.yandex.javacource.lyubavin.schedule.task.Epic;
import ru.yandex.javacource.lyubavin.schedule.task.Subtask;
import ru.yandex.javacource.lyubavin.schedule.task.Task;

import java.util.List;

public interface TaskManager {
    Integer addTask(Task task);

    void updateTask(Task updatedTask);

    Task getTask(int taskId);

    List<Task> getAllTasks();

    void removeAllTasks();

    void removeTask(int id);

    Integer addEpic(Epic epic);

    void updateEpic(Epic updatedEpic);

    Epic getEpic(int epicId);

    List<Epic> getAllEpics();

    void removeAllEpics();

    void removeEpic(int id);

    Integer addSubtask(Subtask subtask);

    void updateSubtask(Subtask updatedSubtask);

    Subtask getSubtask(int subtaskId);

    List<Subtask> getAllSubtasks();

    List<Subtask> getAllEpicSubtasks(int epicId);

    void removeAllSubtask();

    void removeSubtask(int id);

    List<Task> getHistoryList();

    List<Task> getPrioritizedTasks();
}
