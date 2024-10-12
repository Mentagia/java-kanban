package ru.yandex.javacource.lyubavin.schedule.manager;

import ru.yandex.javacource.lyubavin.schedule.task.Epic;
import ru.yandex.javacource.lyubavin.schedule.task.Subtask;
import ru.yandex.javacource.lyubavin.schedule.task.Task;
import ru.yandex.javacource.lyubavin.schedule.enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int generatedId = 0;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected Set<Task> prioritizedTasks = new TreeSet<>();
    protected final HistoryManager historyManager = new InMemoryHistoryManager();

    @Override
    public List<Task> getHistoryList() {
        return historyManager.getHistory();
    }

    @Override
    public Integer addTask(Task task) {
        if (validateTask(task)) {
            int newId = generatedId++;

            task.setId(newId);
            tasks.put(newId, task);

            addPrioritizedTasks(task);

            return newId;
        }

        return null;
    }

    @Override
    public void updateTask(Task updatedTask) {
        if (validateTask(updatedTask)) {
            int updatedTaskId = updatedTask.getId();

            if (tasks.containsKey(updatedTaskId)) {
                tasks.put(updatedTaskId, updatedTask);
            }

            updatePrioritizedTask(updatedTask);
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
        tasks.keySet().forEach(taskId -> {
            historyManager.remove(taskId);
            removePrioritizedTask(tasks.get(taskId));
        });

        tasks.clear();
    }

    @Override
    public void removeTask(int id) {
        historyManager.remove(id);
        removePrioritizedTask(tasks.get(id));
        tasks.remove(id);

    }

    @Override
    public Integer addEpic(Epic epic) {
        if (validateTask(epic)) {
            int newId = generatedId++;

            epic.setId(newId);
            epics.put(newId, epic);

            return newId;
        }

        return null;
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        if (validateTask(updatedEpic)) {
            final Epic savedEpic = epics.get(updatedEpic.getId());

            if (savedEpic == null) {
                return;
            }

            updatedEpic.setSubtaskIds(savedEpic.getSubtaskIds());
            updatedEpic.setTaskStatus(savedEpic.getTaskStatus());
            epics.put(updatedEpic.getId(), updatedEpic);
        }
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
        subtasks.keySet().forEach(historyManager::remove);

        epics.keySet().forEach(historyManager::remove);

        epics.clear();
        removeAllSubtask();
    }

    @Override
    public void removeEpic(int id) {
        epics.get(id).getSubtaskIds().forEach(subtaskId -> {
            historyManager.remove(subtaskId);
            subtasks.remove(subtaskId);
        });

        historyManager.remove(id);
        epics.remove(id);

    }

    @Override
    public Integer addSubtask(Subtask subtask) {
        if (validateTask(subtask)) {
            int epicId = subtask.getEpicId();

            if (!epics.containsKey(epicId)) {
                return null;
            }

            int newId = generatedId++;

            subtask.setId(newId);
            subtasks.put(newId, subtask);
            addPrioritizedTasks(subtask);

            Epic epicOfSubtask = epics.get(epicId);

            epicOfSubtask.addSubtaskId(subtask.getId());
            changeEpicStatus(epicId);
            changeEpicTime(epicId);

            return newId;
        }

        return null;
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        if (validateTask(updatedSubtask)) {
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
            updatePrioritizedTask(updatedSubtask);
            changeEpicStatus(epicId);
            changeEpicTime(epicId);
        }
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
        if (!epics.get(epicId).getSubtaskIds().isEmpty()) {
            List<Subtask> epicSubtasks = new ArrayList<>();

            epics.get(epicId).getSubtaskIds().forEach(subtaskId -> epicSubtasks.add(subtasks.get(subtaskId)));

            return epicSubtasks;
        }

        return null;
    }

    @Override
    public void removeAllSubtask() {
        epics.values().forEach(Epic::clearSubtaskId);

        subtasks.keySet().forEach(subtaskId -> {
            historyManager.remove(subtaskId);
            removePrioritizedTask(subtasks.get(subtaskId));
        });

        subtasks.clear();
    }

    @Override
    public void removeSubtask(int id) {
        int epicId = subtasks.get(id). getEpicId();

        historyManager.remove(id);
        epics.get(epicId).removeSubtaskId(id);
        removePrioritizedTask(subtasks.get(id));
        subtasks.remove(id);
        changeEpicStatus(epicId);
        changeEpicTime(epicId);
    }

    void changeEpicStatus(int epicId) {
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

    public void changeEpicTime(int epicId) {
        Epic epicToChangeTime = epics.get(epicId);

        List<Subtask> epicSubtasks = getAllEpicSubtasks(epicId);
        boolean isFirstSubtask = true;
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        long minutes = 0L;

        if (epicSubtasks != null){
            for (Subtask subtask : epicSubtasks) {
                if (subtask.getEndTime() != null) {
                    minutes += subtask.getDuration().toMinutes();

                    if (isFirstSubtask) {
                        startTime = subtask.getStartTime();
                        endTime = subtask.getEndTime();
                        isFirstSubtask = false;
                    }

                    if (subtask.getStartTime().isBefore(startTime)) {
                        startTime = subtask.getStartTime();
                    }

                    if (subtask.getEndTime().isAfter(endTime)) {
                        endTime = subtask.getEndTime();
                    }
                }
            }
        }

        if (startTime != null) {
            epicToChangeTime.setStartTime(startTime);
            epicToChangeTime.setEndTime(endTime);
            epicToChangeTime.setDuration(Duration.ofMinutes(minutes));

        } else {
            epicToChangeTime.setStartTime(null);
            epicToChangeTime.setEndTime(null);
            epicToChangeTime.setDuration(Duration.ZERO);
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }

    public boolean validateTask (Task task) {
        if (task.getStartTime() == null){
            return true;
        } else {
            return getPrioritizedTasks().stream()
                    .filter(task1 -> task1.getEndTime().isAfter(task.getStartTime())
                    && task1.getStartTime().isBefore(task.getEndTime())
                    || task1.getStartTime().equals(task.getEndTime ())
                    || task1.getEndTime().equals(task.getStartTime()))
                    .collect(Collectors.toSet()).isEmpty();
        }
    }

    protected void addPrioritizedTasks (Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    protected void updatePrioritizedTask (Task task) {
        if (task.getStartTime() != null) {
            getPrioritizedTasks().forEach(prioritizedTask -> {
                if (prioritizedTask.getId() == task.getId()) {
                    prioritizedTasks.remove(prioritizedTask);
                    prioritizedTasks.add(task);
                }
            });
        }
    }

    protected void removePrioritizedTask (Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.remove(task);
        }
    }
}
