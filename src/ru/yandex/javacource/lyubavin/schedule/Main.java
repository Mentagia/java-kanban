package ru.yandex.javacource.lyubavin.schedule;

import ru.yandex.javacource.lyubavin.schedule.manager.FileBackedTaskManager;
import ru.yandex.javacource.lyubavin.schedule.manager.TaskManager;
import ru.yandex.javacource.lyubavin.schedule.manager.Managers;
import ru.yandex.javacource.lyubavin.schedule.task.Epic;
import ru.yandex.javacource.lyubavin.schedule.task.Subtask;
import ru.yandex.javacource.lyubavin.schedule.task.Task;
import ru.yandex.javacource.lyubavin.schedule.enums.TaskStatus;

import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefaultFileBackedTaskManager();

        Task newTask1 = new Task("Прогулка",
                "Проходить по 10000 шагов в день на протяжении недели", TaskStatus.NEW);
        Task newTask2 = new Task("Сон", "Спать по 8 часов на протяжении недели",
                TaskStatus.IN_PROGRESS);

        int idTask1 = taskManager.addTask(newTask1);
        int idTask2 = taskManager.addTask(newTask2);

        Epic newEpic1 = new Epic("Обучиться Java",
                "Обучиться Java на достаточном для трудоустройства уровне за последующий год");
        Epic newEpic2 = new Epic("Научится работе с БД",
                "Обучиться работе с БД за последующие полгода");

        int idEpic1 = taskManager.addEpic(newEpic1);
        int idEpic2 = taskManager.addEpic(newEpic2);

        Subtask newSubtask1 = new Subtask("Обучиться JavaCore",
                "Изучить JavaCore за 6 месяцев", TaskStatus.NEW, idEpic1);
        Subtask newSubtask2 = new Subtask("Обучиться SpringBoot",
                "Изучить SpringBoot за 6 месяцев", TaskStatus.NEW, idEpic1);
        Subtask newSubtask3 = new Subtask("Обучиться SQL",
                "Изучить SQL за 3 месяца", TaskStatus.NEW, idEpic2);

        int idSubtask1 = taskManager.addSubtask(newSubtask1);
        int idSubtask2 =  taskManager.addSubtask(newSubtask2);
        int idSubtask3 = taskManager.addSubtask(newSubtask3);

        printTasks(taskManager);

        TaskManager newTaskManager =
                FileBackedTaskManager.loadFromFile(Paths.get("taskLogs\\tasks.csv").toFile());

        printTasks(newTaskManager);
    }

    private static void printTasks(TaskManager manager) {
        System.out.println("Задачи:");

        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }

        System.out.println("-".repeat(15));

        System.out.println("Эпики:");

        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("-".repeat(15));

        System.out.println("Подзадачи:");

        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("-".repeat(15));
    }
}
