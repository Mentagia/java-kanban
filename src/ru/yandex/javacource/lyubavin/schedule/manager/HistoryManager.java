package ru.yandex.javacource.lyubavin.schedule.manager;

import ru.yandex.javacource.lyubavin.schedule.task.Task;

import java.util.ArrayList;

public interface HistoryManager {
    ArrayList<Task> getHistory();
    void addTaskToHistory(Task task);
}
