package ru.yandex.javacource.lyubavin.schedule.task;

import ru.yandex.javacource.lyubavin.schedule.enums.TaskStatus;
import ru.yandex.javacource.lyubavin.schedule.enums.TaskType;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String taskName, String taskDiscr, TaskStatus taskStatus, int epicId) {
        super(taskName, taskDiscr, taskStatus);
        this.epicId = epicId;
    }

    public Subtask(int id, String taskName, String taskDiscr, TaskStatus taskStatus, int epicId) {
        super(id, taskName, taskDiscr, taskStatus);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s\n",(getId()), getType(), getTaskName(),
                getTaskStatus(), getTaskDiscr(), getEpicId());
    }
}
