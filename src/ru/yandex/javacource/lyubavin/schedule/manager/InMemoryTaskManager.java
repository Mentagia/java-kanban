package ru.yandex.javacource.lyubavin.schedule.manager;

import ru.yandex.javacource.lyubavin.schedule.task.Epic;
import ru.yandex.javacource.lyubavin.schedule.task.Subtask;
import ru.yandex.javacource.lyubavin.schedule.task.Task;
import ru.yandex.javacource.lyubavin.schedule.task.TaskStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {
    private int generatedId = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = new InMemoryHistoryManager();

    @Override
    public List<Task> getHistoryList() {
        return historyManager.getHistory();
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

        if (tasks.containsKey(updatedTaskId)) {
            tasks.put(updatedTaskId, updatedTask);
        }
    }

    @Override
    public Task getTask(int taskId) {
        final Task task = tasks.get(taskId);

        if (task != null) {
            historyManager.add(task);
            return task;
        }

        return null;
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>(tasks.values());

        if (!allTasks.isEmpty()) {
            return allTasks;
        }

        return null;
    }

    @Override
    public void removeAllTasks() {
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }

        tasks.clear();
    }

    @Override
    public void removeTask(int id) {
            historyManager.remove(id);
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
        final Epic savedEpic = epics.get(updatedEpic.getId());

        if (savedEpic == null) {
            return;
        }

        updatedEpic.setSubtaskIds(savedEpic.getSubtaskIds());
        updatedEpic.setTaskStatus(savedEpic.getTaskStatus());
        epics.put(updatedEpic.getId(), updatedEpic);
    }

    @Override
    public Epic getEpic(int epicId) {
        Epic epic = epics.get(epicId);

        if (epic != null) {
            historyManager.add(epic);
            return epic;
        }

        return null;
    }

    @Override
    public List<Epic> getAllEpics() {
        List<Epic> allEpics = new ArrayList<>(epics.values());

        if (!allEpics.isEmpty()) {
            return allEpics;
        }

        return null;
    }

    @Override
    public void removeAllEpics() {
        for (Integer subtaskId : subtasks.keySet()) {
            historyManager.remove(subtaskId);
        }

        for (Integer epicId : epics.keySet()) {
            historyManager.remove(epicId);
        }

        epics.clear();
        removeAllSubtask();
    }

    @Override
    public void removeEpic(int id) {

        List<Integer> subtaskIds = epics.get(id).getSubtaskIds();

        for (int subtaskId : subtaskIds) {
            historyManager.remove(subtaskId);
            subtasks.remove(subtaskId);
        }

        historyManager.remove(id);
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
        Subtask subtask = subtasks.get(subtaskId);

        if (subtask != null) {
            historyManager.add(subtask);
            return subtask;
        }

        return null;
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        List<Subtask> allSubtasks = new ArrayList<>(subtasks.values());

        if (!allSubtasks.isEmpty()) {
            return allSubtasks;
        }

        return null;
    }

    @Override
    public List<Subtask> getAllEpicSubtasks(int epicId) {
        List<Integer> epicSubtasksIds = epics.get(epicId).getSubtaskIds();

        if (!epicSubtasksIds.isEmpty()) {
            List<Subtask> epicSubtasks = new ArrayList<>();

            for (int id : epicSubtasksIds) {
                epicSubtasks.add(subtasks.get(id));
            }

            return epicSubtasks;
        }

        return null;
    }

    @Override
    public void removeAllSubtask() {
        for (Epic epic : epics.values()) {
            epic.clearSubtaskId();
        }

        for (Integer subtaskId : subtasks.keySet()) {
            historyManager.remove(subtaskId);
        }

        subtasks.clear();
    }

    @Override
    public void removeSubtask(int id) {
        int epicId = subtasks.get(id). getEpicId();

        historyManager.remove(id);
        epics.get(epicId).removeSubtaskId(id);
        subtasks.remove(id);
        changeEpicStatus(epicId);
    }

    private void changeEpicStatus(int epicId) {
        Epic epicToChangeStatus = epics.get(epicId);
        List<Integer> subtaskIdS = epicToChangeStatus.getSubtaskIds();
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
