package ru.yandex.javacource.lyubavin.schedule;

import ru.yandex.javacource.lyubavin.schedule.exceptions.ManagerLoadException;
import ru.yandex.javacource.lyubavin.schedule.manager.FileBackedTaskManager;
import ru.yandex.javacource.lyubavin.schedule.manager.TaskManager;
import ru.yandex.javacource.lyubavin.schedule.manager.Managers;
import ru.yandex.javacource.lyubavin.schedule.task.Epic;
import ru.yandex.javacource.lyubavin.schedule.task.Subtask;
import ru.yandex.javacource.lyubavin.schedule.task.Task;
import ru.yandex.javacource.lyubavin.schedule.enums.TaskStatus;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws ManagerLoadException {
        TaskManager taskManager = Managers.getDefault();

        Task newTask1 = new Task("Прогулка",
                "Проходить по 10000 шагов в день на протяжении недели", TaskStatus.NEW,
                LocalDateTime.of(2024,10,12,23, 0), Duration.ofMinutes(10));
        Task newTask2 = new Task("Сон", "Спать по 8 часов на протяжении недели",
                TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2024,10,12,21, 0), Duration.ofHours(1));

        Integer idTask1 = taskManager.addTask(newTask1);
        Integer idTask2 = taskManager.addTask(newTask2);

        Epic newEpic1 = new Epic("Обучиться Java",
                "Обучиться Java на достаточном для трудоустройства уровне за последующий год");
        Epic newEpic2 = new Epic("Научится работе с БД",
                "Обучиться работе с БД за последующие полгода");

        Integer idEpic1 = taskManager.addEpic(newEpic1);
        Integer idEpic2 = taskManager.addEpic(newEpic2);

        printTasks(taskManager);

        Subtask newSubtask1 = new Subtask("Обучиться JavaCore",
                "Изучить JavaCore за 6 месяцев", TaskStatus.NEW,
                LocalDateTime.of(2024,10,12,12, 0), Duration.ofHours(1),idEpic1);
        Subtask newSubtask2 = new Subtask("Обучиться SpringBoot",
                "Изучить SpringBoot за 6 месяцев", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2024,10,12,14, 0), Duration.ofHours(2), idEpic1);
        Subtask newSubtask3 = new Subtask("Обучиться SQL",
                "Изучить SQL за 3 месяца", TaskStatus.NEW, idEpic2);

        Integer idSubtask1 = taskManager.addSubtask(newSubtask1);

        printTasks(taskManager);

        Integer idSubtask2 = taskManager.addSubtask(newSubtask2);
        Integer idSubtask3 = taskManager.addSubtask(newSubtask3);

        printTasks(taskManager);

        TaskManager newTaskManager =
                FileBackedTaskManager.loadFromFile(Paths.get("taskLogs\\tasks.csv").toFile());

        Task newTask3 = new Task("new Task3",
                "new Task3 description", TaskStatus.NEW);
        Task newTask4 = new Task("new Task4",
                "new Task4 description", TaskStatus.IN_PROGRESS);

        Integer idTask3 = newTaskManager.addTask(newTask3);
        Integer idTask4 = newTaskManager.addTask(newTask4);

        printTasks(newTaskManager);
    }

    private static void printTasks(TaskManager manager) {
        if (manager.getAllTasks() != null) {
            System.out.println("Задачи:");
            manager.getAllTasks().forEach(System.out::println);
            System.out.println("-".repeat(15));
        }


        if (manager.getAllEpics() != null) {
            System.out.println("Эпики:");
            manager.getAllEpics().forEach(System.out::println);
            System.out.println("-".repeat(15));
        }

        if (manager.getAllSubtasks() != null) {
            System.out.println("Подзадачи:");
            manager.getAllSubtasks().forEach(System.out::println);
            System.out.println("-".repeat(15));
        }

        if (manager.getPrioritizedTasks() != null) {
            System.out.println("Сортированные задачи:");
            manager.getPrioritizedTasks().forEach(System.out::println);
            System.out.println("-".repeat(15));
        }
    }
}
