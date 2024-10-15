package ru.yandex.javacource.lyubavin.schedule.task;

import ru.yandex.javacource.lyubavin.schedule.enums.TaskStatus;
import ru.yandex.javacource.lyubavin.schedule.enums.TaskType;

import java.util.ArrayList;
import java.time.Duration;
import java.time.LocalDateTime;


public class Epic extends Task {
    private ArrayList<Integer> subtaskIds;
    private LocalDateTime endTime;

    public Epic(String epicName, String epicDiscr) {
        super(epicName, epicDiscr, TaskStatus.NEW,null, Duration.ZERO);
        subtaskIds = new ArrayList<>();
    }

    public Epic(int id, String epicName, String epicDiscr) {
        super(id, epicName, epicDiscr, TaskStatus.NEW,null, Duration.ZERO);
        subtaskIds = new ArrayList<>();
    }

    public Epic(int id, String epicName, String epicDiscr, TaskStatus status) {
        super(id, epicName, epicDiscr, status,null, Duration.ZERO);
        subtaskIds = new ArrayList<>();
    }

    public Epic(int id, String epicName, String epicDiscr, TaskStatus status,
                LocalDateTime startTime, Duration duration) {
        super(id, epicName, epicDiscr, status, startTime, duration);
        subtaskIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIds() {
            return subtaskIds;
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        subtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId) {
        subtaskIds.remove(Integer.valueOf(subtaskId));
    }

    public void clearSubtaskId() {
        subtaskIds.clear();
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        if (getStartTime() != null) {
            return endTime;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "epicId=" + getId() +
                ", epicName='" + getTaskName() + '\'' +
                ", epicDiscr='" + getTaskDiscr() + '\'' +
                ", epicStatus=" + getTaskStatus() + '\'' +
                ", startTime='" + getStartTime() + '\'' +
                ", endTime='" + getEndTime() + '\'' +
                ", duration=" + getDuration().toMinutes() + '\'' +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}
