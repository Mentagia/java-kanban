package ru.yandex.javacource.lyubavin.schedule.task;

import ru.yandex.javacource.lyubavin.schedule.enums.TaskStatus;
import ru.yandex.javacource.lyubavin.schedule.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;


public class Subtask extends Task {
    private int epicId;

    public Subtask(String taskName, String taskDiscr, TaskStatus taskStatus, int epicId) {
        super(taskName, taskDiscr, taskStatus, null, Duration.ZERO);
        this.epicId = epicId;
    }

    public Subtask(int id, String taskName, String taskDiscr, TaskStatus taskStatus, int epicId) {
        super(id, taskName, taskDiscr, taskStatus,null, Duration.ZERO);
        this.epicId = epicId;
    }

    public Subtask(String taskName, String taskDiscr, TaskStatus taskStatus,
                   LocalDateTime startTime, Duration duration, int epicId) {
        super(taskName, taskDiscr, taskStatus, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(int id, String taskName, String taskDiscr, TaskStatus taskStatus,
                   LocalDateTime startTime, Duration duration, int epicId) {
        super(id, taskName, taskDiscr, taskStatus, startTime, duration);
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
        return "Subtask{" +
                "subtaskId=" + getId() +
                ", subtaskName='" + getTaskName() + '\'' +
                ", subtaskDiscr='" + getTaskDiscr() + '\'' +
                ", subtaskStatus=" + getTaskStatus() + '\'' +
                ", startTime='" + getStartTime()+ '\'' +
                ", endTime='" + getEndTime() + '\'' +
                ", duration=" + getDuration().toMinutes() + '\'' +
                ", epicId=" + epicId +
                '}';
    }
}