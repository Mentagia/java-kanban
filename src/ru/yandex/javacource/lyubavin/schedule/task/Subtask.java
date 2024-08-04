package ru.yandex.javacource.lyubavin.schedule.task;

public class Subtask extends Task{
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
    public String toString() {
        return "Subtask{" +
                "subtaskId=" + getId() +
                ", subtaskName='" + getTaskName() + '\'' +
                ", subtaskDiscr='" + getTaskDiscr() + '\'' +
                ", subtaskStatus=" + getTaskStatus() +
                ", epicId=" + epicId +
                '}';
    }
}
