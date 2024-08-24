package ru.yandex.javacource.lyubavin.schedule.manager;

import ru.yandex.javacource.lyubavin.schedule.task.Task;

import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();
    public static final int MAX_SIZE = 10;

    @Override
    public List<Task> getHistory() {
        return history;
    }

    @Override
    public void addTaskToHistory(Task task) {
        if (task == null) {
            return;
        }

        if(history.size() >= 10){
            history.remove(0);
        }

        history.add(task);
    }
}
