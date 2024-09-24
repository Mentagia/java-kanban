package ru.yandex.javacource.lyubavin.schedule.task;

import ru.yandex.javacource.lyubavin.schedule.enums.TaskStatus;
import ru.yandex.javacource.lyubavin.schedule.enums.TaskType;

import java.util.Objects;

public class Task {
    private int id;
    private String taskName;
    private String taskDiscr;
    private TaskStatus taskStatus;

    public Task(String taskName, String taskDiscr, TaskStatus taskStatus) {
        this.taskName = taskName;
        this.taskDiscr = taskDiscr;
        this.taskStatus = taskStatus;
    }

    public Task(int id, String taskName, String taskDiscr, TaskStatus taskStatus) {
        this.id = id;
        this.taskName = taskName;
        this.taskDiscr = taskDiscr;
        this.taskStatus = taskStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDiscr() {
        return taskDiscr;
    }

    public void setTaskDiscr(String taskDiscr) {
        this.taskDiscr = taskDiscr;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s\n",(getId()), getType(),
                getTaskName(), getTaskStatus(), getTaskDiscr());
    }

}
