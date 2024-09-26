package ru.yandex.javacource.lyubavin.schedule.manager;

import ru.yandex.javacource.lyubavin.schedule.enums.TaskType;
import ru.yandex.javacource.lyubavin.schedule.task.Epic;
import ru.yandex.javacource.lyubavin.schedule.task.Subtask;
import ru.yandex.javacource.lyubavin.schedule.task.Task;
import ru.yandex.javacource.lyubavin.schedule.enums.TaskStatus;
import ru.yandex.javacource.lyubavin.schedule.exceptions.ManagerSaveException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;



public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final String HEADER = "id,type,name,status,description,epic";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public int addTask(Task task) {
        int id = super.addTask(task);
        save();
        return id;
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public int addEpic(Epic epic) {
        int id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public Integer addSubtask(Subtask subtask) {
        Integer id = super.addSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        super.updateSubtask(updatedSubtask);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void removeAllSubtask() {
        super.removeAllSubtask();
        save();
    }

    public static String toString(Task task) {
        String str;

        if (task != null) {
            str = task.getId() + "," + task.getType() + "," + task.getTaskName() + "," + task.getTaskStatus() +
                    "," + task.getTaskDiscr();

            if (task.getType().equals(TaskType.SUBTASK)) {
                str = str + "," + ((Subtask) task).getEpicId();
                return str;
            }

            return str;
        }
        return null;
    }

     static Task fromString(String stringTask) {
        String[] taskParameters = stringTask.split(",");
        int id = Integer.parseInt(taskParameters[0]);
        String name = taskParameters[2];
        TaskStatus status = TaskStatus.valueOf(taskParameters[3]);
        String description = taskParameters[4];


        switch (taskParameters[1]) {
            case "TASK":
                return new Task(id, name, description, status);
            case "SUBTASK":
                int epicId = Integer.parseInt(taskParameters[5]);
                return new Subtask(id, name, description, status, epicId);
            case "EPIC":
                return new Epic(id, name, description, status);
            default:
                return null;
        }
    }

    public void save() throws ManagerSaveException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(HEADER);
            writer.newLine();

            for (Task task : tasks.values()) {
                writer.write(toString(task));
                writer.newLine();
            }

            for (Epic epic : epics.values()) {
                writer.write(toString(epic));
                writer.newLine();
            }

            for (Subtask subTask : subtasks.values()) {
                writer.write(toString(subTask));
                writer.newLine();
            }
        } catch (IOException exc) {
            throw new ManagerSaveException(exc.getMessage());
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        int currentId = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            while (br.ready()) {
                String currentLine = br.readLine();
                String[] taskParameters = currentLine.split(",");

                switch (taskParameters[1]) {
                    case "TASK":
                        Task task = fromString(currentLine);

                        if (task != null) {
                            int taskId = task.getId();
                            currentId = Math.max(taskId, currentId);

                            taskManager.tasks.put(taskId, task);
                        }

                        break;
                    case "EPIC":
                        Epic epic = (Epic) fromString(currentLine);

                        if (epic != null) {
                            int epicId = epic.getId();
                            currentId = Math.max(epicId, currentId);

                            taskManager.epics.put(epicId, epic);
                        }

                        break;
                    case "SUBTASK":
                        Subtask subtask = (Subtask) fromString(currentLine);

                        if (subtask != null) {
                            int subtaskId = subtask.getId();
                            int epicId = subtask.getEpicId();
                            currentId = Math.max(subtaskId, currentId);

                            taskManager.epics.get(epicId).addSubtaskId(subtaskId);
                            taskManager.subtasks.put(subtaskId, subtask);
                        }

                        break;
                }
            }
        } catch (IOException exc) {
            throw new ManagerSaveException(exc.getMessage());
        }

        taskManager.generatedId = ++currentId;
        return taskManager;
    }
}
