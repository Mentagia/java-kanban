package ru.yandex.javacource.lyubavin.schedule.manager;

import ru.yandex.javacource.lyubavin.schedule.task.Epic;
import ru.yandex.javacource.lyubavin.schedule.task.Subtask;
import ru.yandex.javacource.lyubavin.schedule.task.Task;
import ru.yandex.javacource.lyubavin.schedule.task.TaskStatus;

import java.util.HashMap;
import java.util.ArrayList;


public class InMemoryTaskManager implements TaskManager {
    private int generatedId = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    HistoryManager historyManager = new InMemoryHistoryManager();

    @Override
    public HistoryManager getHistoryManager(){
        return historyManager;
    }

    @Override
    public int addTask(Task task) {
        int newId = generatedId++;

        task.setId(newId);
        tasks.put(newId, task);

        return newId;
    }

    @Override
    public void updateTask(Task updatedTask) {
        int updatedTaskId = updatedTask.getId();

        if(tasks.containsKey(updatedTaskId)) {
            tasks.put(updatedTaskId, updatedTask);
        }
    }

    @Override
    public Task getTask(int taskId) {
        if(tasks.get(taskId) != null) {
            historyManager.addTaskToHistory(tasks.get(taskId));
            return tasks.get(taskId);
        }

        return null;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        if(!tasks.values().isEmpty()) {
            return new ArrayList<>(tasks.values());
        }

        return null;
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeTask(int id) {
        tasks.remove(id);
    }

    @Override
    public int addEpic(Epic epic) {
        int newId = generatedId++;

        epic.setId(newId);
        epics.put(newId, epic);

        return newId;
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        Epic savedEpic = epics.get(updatedEpic.getId());

        if (savedEpic == null) {
            return;
        }

        savedEpic.setTaskName(updatedEpic.getTaskName());
        savedEpic.setTaskDiscr(updatedEpic.getTaskDiscr());
    }

    @Override
    public Epic getEpic(int epicId) {
        if(epics.get(epicId) != null){
            historyManager.addTaskToHistory(epics.get(epicId));
            return epics.get(epicId);
        }

        return null;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        if (!epics.values().isEmpty()) {
            return new ArrayList<>(epics.values());
        }

        return null;
    }

    @Override
    public void removeAllEpics() {
        epics.clear();
        removeAllSubtask();
    }

    @Override
    public void removeEpic(int id) {
        ArrayList<Integer> subtaskIds = epics.get(id).getSubtaskIds();

        for(int subtaskId : subtaskIds) {
            subtasks.remove(subtaskId);
        }

        epics.remove(id);
    }

    @Override
    public Integer addSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();

        if (!epics.containsKey(epicId)) {
            return null;
        }

        int newId = generatedId++;

        subtask.setId(newId);
        subtasks.put(newId, subtask);

        Epic epicOfSubtask = epics.get(epicId);

        epicOfSubtask.addSubtaskId(subtask.getId());
        changeEpicStatus(epicId);

        return newId;
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        int id = updatedSubtask.getId();
        int epicId = updatedSubtask.getEpicId();
        Subtask savedSubtask = subtasks.get(id);

        if (savedSubtask == null) {
            return;
        }

        Epic epic = epics.get(epicId);

        if (epic == null) {
            return;
        }

        subtasks.put(id, updatedSubtask);
        changeEpicStatus(epicId);
    }

    @Override
    public Subtask getSubtask(int subtaskId) {
        if(subtasks.get(subtaskId) != null){
            historyManager.addTaskToHistory(subtasks.get(subtaskId));
            return subtasks.get(subtaskId);
        }

        return null;
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        if (!subtasks.values().isEmpty()) {
            return new ArrayList<>(subtasks.values());
        }

        return null;
    }

    @Override
    public ArrayList<Subtask> getAllEpicSubtasks(int epicId) {
        if (!epics.get(epicId).getSubtaskIds().isEmpty()) {
            ArrayList<Integer> epicSubtasksIds = epics.get(epicId).getSubtaskIds();
            ArrayList<Subtask> epicSubtasks = new ArrayList<>();

            for (int id : epicSubtasksIds) {
                epicSubtasks.add(subtasks.get(id));
            }

            return epicSubtasks;
        }

        return null;
    }

    @Override
    public void removeAllSubtask() {
        for (Epic epic : epics.values()){
            epic.clearSubtaskId();
        }

        subtasks.clear();
    }

    @Override
    public void removeSubtask(int id) {
        int epicId = subtasks.get(id). getEpicId();

        epics.get(epicId).removeSubtaskId(id);
        subtasks.remove(id);
        changeEpicStatus(epicId);
    }

    private void changeEpicStatus(int epicId) {
        Epic epicToChangeStatus = epics.get(epicId);
        ArrayList<Integer> subtaskIdS = epicToChangeStatus.getSubtaskIds();
        int amountOfDoneSubtusks = 0;
        int amountOfNewSubtusks = 0;

        if (!subtaskIdS.isEmpty()) {
            for (int subtaskId : subtaskIdS) {
                TaskStatus subtaskStatus = subtasks.get(subtaskId).getTaskStatus();

                if (subtaskStatus == TaskStatus.DONE) {
                    amountOfDoneSubtusks++;
                } else if (subtaskStatus == TaskStatus.NEW) {
                    amountOfNewSubtusks++;
                }
            }

            if (amountOfDoneSubtusks == subtaskIdS.size()) {
                epicToChangeStatus.setTaskStatus(TaskStatus.DONE);
            } else if (amountOfNewSubtusks != subtaskIdS.size()) {
                epicToChangeStatus.setTaskStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }
}
