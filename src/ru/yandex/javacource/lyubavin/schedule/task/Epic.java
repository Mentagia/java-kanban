package ru.yandex.javacource.lyubavin.schedule.task;

import java.util.ArrayList;

public class Epic extends Task{
    private ArrayList<Integer> subtaskIds;

    public Epic(String epicName, String epicDiscr) {
        super(epicName, epicDiscr, TaskStatus.NEW);
        subtaskIds = new ArrayList<>();
    }

    public Epic(int id, String epicName, String epicDiscr) {
        super(id, epicName, epicDiscr, TaskStatus.NEW);
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
    @Override
    public String toString() {
        return "Epic{" +
                "epicId=" + getId() +
                ", epicName='" + getTaskName() + '\'' +
                ", epicDiscr='" + getTaskDiscr() + '\'' +
                ", epicStatus=" + getTaskStatus() +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}