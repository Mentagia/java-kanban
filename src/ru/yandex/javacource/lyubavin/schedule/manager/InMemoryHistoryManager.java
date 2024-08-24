package ru.yandex.javacource.lyubavin.schedule.manager;

import ru.yandex.javacource.lyubavin.schedule.task.Task;

import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    public static final int MAX_SIZE = 10;
    private final List<Task> history = new ArrayList<>();


    @Override
    public List<Task> getHistory() {
        return history;
    }

    @Override
    public void addTaskToHistory(Task task) {
        if (task == null) {
            return;
        }

        if(history.size() >= MAX_SIZE){
            history.remove(0);
        }

        history.add(task);
    }
}
