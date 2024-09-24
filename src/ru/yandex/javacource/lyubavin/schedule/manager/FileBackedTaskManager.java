package ru.yandex.javacource.lyubavin.schedule.manager;

import ru.yandex.javacource.lyubavin.schedule.task.Epic;
import ru.yandex.javacource.lyubavin.schedule.task.Subtask;
import ru.yandex.javacource.lyubavin.schedule.task.Task;
import ru.yandex.javacource.lyubavin.schedule.enums.TaskStatus;
import ru.yandex.javacource.lyubavin.schedule.exceptions.ManagerSaveException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;



public class FileBackedTaskManager extends InMemoryTaskManager{
    private final String pathToFile = "taskLogs\\tasks.csv";

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
        int id = super.addSubtask(subtask);

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
                return new Epic(id, name, description);
            default:
                return null;
        }
    }

    public void save() throws ManagerSaveException{
        try (FileWriter writer = new FileWriter(pathToFile)) {
            writer.write("id,type,name,status,description,epicId\n");

            for (Task task : tasks.values()) {
                writer.write(task.toString());
            }

            for (Epic epic : epics.values()) {
                writer.write(epic.toString());
            }

            for (Subtask subTask : subtasks.values()) {
                writer.write(subTask.toString());
            }
        } catch (IOException exc) {
            throw new ManagerSaveException(exc.getMessage());
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            while (br.ready()) {
                String currentLine = br.readLine();
                String[] taskParameters = currentLine.split(",");

                switch (taskParameters[1]) {
                    case "TASK":
                        Task task = fromString(currentLine);

                        if (task !=null) {
                            taskManager.tasks.put(task.getId(), task);
                        }

                        break;
                    case "EPIC":
                        Epic epic = (Epic) fromString(currentLine);

                        if (epic !=null) {
                            taskManager.epics.put(epic.getId(), epic);
                        }

                        break;
                    case "SUBTASK":
                        Subtask subtask = (Subtask) fromString(currentLine);

                        if (subtask != null) {
                            int subtaskId = subtask.getId();
                            int epicId = subtask.getEpicId();

                            taskManager.epics.get(epicId).addSubtaskId(subtaskId);
                            taskManager.subtasks.put(subtaskId, subtask);
                        }

                        break;
                }
            }
            for (Epic epic : taskManager.epics.values()) {
                taskManager.changeEpicStatus(epic.getId());
            }


        } catch (IOException exc) {
            throw new ManagerSaveException(exc.getMessage());
        }

        return taskManager;
    }
}
