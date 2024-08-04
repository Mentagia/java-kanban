package ru.yandex.javacource.lyubavin.schedule.manager;

import ru.yandex.javacource.lyubavin.schedule.task.Epic;
import ru.yandex.javacource.lyubavin.schedule.task.Subtask;
import ru.yandex.javacource.lyubavin.schedule.task.Task;
import ru.yandex.javacource.lyubavin.schedule.task.TaskStatus;

import java.util.HashMap;
import java.util.ArrayList;


public class TaskManager {
    private int generatedId = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    public int addTask(Task task) {
        int newId = generatedId++;

        task.setId(newId);
        tasks.put(newId, task);

        return newId;
    }

    public void updateTask(Task updatedTask) {
        int updatedTaskId = updatedTask.getId();

        if(tasks.containsKey(updatedTaskId)) {
            tasks.put(updatedTaskId, updatedTask);
        }
    }

    public Task getTask(int taskId) {
            return tasks.get(taskId);
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeTask(int id) {
            tasks.remove(id);
    }

    public int addEpic(Epic epic) {
        int newId = generatedId++;

        epic.setId(newId);
        epics.put(newId, epic);

        return newId;
    }

    public void updateEpic(Epic updatedEpic) {
        Epic savedEpic = epics.get(updatedEpic.getId());
        if (savedEpic == null) {
            return;
        }
        savedEpic.setTaskName(updatedEpic.getTaskName());
        savedEpic.setTaskDiscr(updatedEpic.getTaskDiscr());

        // Не совсем понял почему у эпика должно меняться именно назавние и описание
        // просмотрел по тз четко не увидел такого :(
        // Вроде в подсказке "Обновление данных" сказано о записи нового эпика в хэшмапу
        // Хотя по логике действительно проще менять только название и описание

        /*int updatedEpicId = updatedEpic.getId();

        if(epics.containsKey(updatedEpicId)){
            ArrayList<Integer> updatedSubtaskIds = epics.get(updatedEpicId).getSubtaskIds();

            epics.put(updatedEpicId, updatedEpic);
            epics.get(updatedEpicId).setSubtaskIds(updatedSubtaskIds);
            changeEpicStatus(updatedEpicId);
        }*/
    }

    public Epic getEpic(int epicId) {
            return epics.get(epicId);
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void removeAllEpics() {
        epics.clear();
        removeAllSubtask();
    }

    public void removeEpic(int id) {
            ArrayList<Integer> subtaskIds = epics.get(id).getSubtaskIds();

            for(int subtaskId : subtaskIds) {
                subtasks.remove(subtaskId);
            }

            epics.remove(id);
    }

    public Integer addSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();

        if (!epics.containsKey(epicId)) {
            return null;
        }

        int newId = generatedId++;

        subtask.setId(newId);
        subtasks.put(newId, subtask);

        Epic epicOfSubtask = epics.get(epicId);
        // тут имелся ввиду Эпик сабтаска, но все равно название переменной неправильное,
        // если непонятно другому)

        epicOfSubtask.addSubtaskId(subtask.getId());
        changeEpicStatus(epicId);

        return newId;
    }

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

    public Task getSubtask(int subtaskId) {
            return subtasks.get(subtaskId);
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public ArrayList<Subtask> getAllEpicSubtasks(int epicId) {
        ArrayList<Integer> epicSubtasksIds = epics.get(epicId).getSubtaskIds();
        ArrayList<Subtask> epicSubtasks = new ArrayList<>();

        for (int id : epicSubtasksIds) {
            epicSubtasks.add(subtasks.get(id));
        }

        return epicSubtasks;
    }

    public void removeAllSubtask() {
        subtasks.clear();
    }

    public void removeSubtask(int id) {
            int epicId = subtasks.get(id). getEpicId();

            subtasks.remove(id);
            epics.get(epicId).removeSubtaskId(id);
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

                // if (subtaskStatus == ru.yandex.javacource.lyubavin.schedule.task.TaskStatus.IN_PROGRESS){
                //     epicToChangeStatus.setTaskStatus(ru.yandex.javacource.lyubavin.schedule.task.TaskStatus.IN_PROGRESS);
                //     return;
                // } else
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
