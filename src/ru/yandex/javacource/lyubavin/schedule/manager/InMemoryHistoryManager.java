package ru.yandex.javacource.lyubavin.schedule.manager;

import ru.yandex.javacource.lyubavin.schedule.task.Task;
import ru.yandex.javacource.lyubavin.schedule.task.TaskStatus;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{
    private final ArrayList<Task> history = new ArrayList<>();

    @Override
    public ArrayList<Task> getHistory(){
        return history;
    }

    @Override
    public void addTaskToHistory(Task task){
        if(history.size() >= 10){
            history.remove(0);
        }

        Task savedTask = new Task(task.getId(), task.getTaskName(), task.getTaskDiscr(),task.getTaskStatus());
        history.add(savedTask);
    }
}
