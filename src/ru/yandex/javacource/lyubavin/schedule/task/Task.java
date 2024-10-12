package ru.yandex.javacource.lyubavin.schedule.task;

import ru.yandex.javacource.lyubavin.schedule.enums.TaskStatus;
import ru.yandex.javacource.lyubavin.schedule.enums.TaskType;

import java.util.Comparator;
import java.util.Objects;
import java.time.Duration;
import java.time.LocalDateTime;

public class Task implements Comparable<Task> {
    private int id;
    private String taskName;
    private String taskDiscr;
    private TaskStatus taskStatus;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(String taskName, String taskDiscr, TaskStatus taskStatus) {
        this.taskName = taskName;
        this.taskDiscr = taskDiscr;
        this.taskStatus = taskStatus;
        this.startTime = null;
        this.duration = Duration.ZERO;
    }

    public Task(int id, String taskName, String taskDiscr, TaskStatus taskStatus) {
        this.id = id;
        this.taskName = taskName;
        this.taskDiscr = taskDiscr;
        this.taskStatus = taskStatus;
        this.startTime = null;
        this.duration = Duration.ZERO;
    }

    public Task(String taskName, String taskDiscr, TaskStatus taskStatus,
                LocalDateTime startTime, Duration duration) {
        this.taskName = taskName;
        this.taskDiscr = taskDiscr;
        this.taskStatus = taskStatus;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(int id, String taskName, String taskDiscr, TaskStatus taskStatus,
                LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.taskName = taskName;
        this.taskDiscr = taskDiscr;
        this.taskStatus = taskStatus;
        this.startTime = startTime;
        this.duration = duration;
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

    public Duration getDuration() {
        if (duration == null){
            return Duration.ZERO;
        }

        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if ((duration != null) && (startTime != null)) {
            return startTime.plus(duration);
        } else {
            return null;
        }
    }

    @Override
    public int compareTo(Task task) {
        if (this.getStartTime().isBefore(task.getStartTime())) {
            return -1;
        } else if (this.getStartTime().isAfter(task.getStartTime())) {
            return 1;
        } else {
            return this.getId() - task.getId();
        }
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
        return "Task{" +
                "taskId=" + id +
                ", taskName='" + taskName + '\'' +
                ", taskDiscr='" + taskDiscr + '\'' +
                ", taskStatus=" + taskStatus + '\''  +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + getEndTime() + '\'' +
                ", duration=" + duration.toMinutes() +
                '}';
    }

}
