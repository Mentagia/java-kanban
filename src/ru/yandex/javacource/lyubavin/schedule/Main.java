package ru.yandex.javacource.lyubavin.schedule;

import ru.yandex.javacource.lyubavin.schedule.manager.HistoryManager;
import ru.yandex.javacource.lyubavin.schedule.manager.TaskManager;
import ru.yandex.javacource.lyubavin.schedule.manager.Managers;
import ru.yandex.javacource.lyubavin.schedule.task.Epic;
import ru.yandex.javacource.lyubavin.schedule.task.Subtask;
import ru.yandex.javacource.lyubavin.schedule.task.Task;
import ru.yandex.javacource.lyubavin.schedule.task.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task newTask1 = new Task("Прогулка",
                "Проходить по 10000 шагов в день на протяжении недели", TaskStatus.NEW);
        Task newTask2 = new Task("Сон", "Спать по 8 часов на протяжении недели",
                TaskStatus.IN_PROGRESS);

        int idTask1 = taskManager.addTask(newTask1);
        int idTask2 = taskManager.addTask(newTask2);

        Epic newEpic1 = new Epic("Обучиться Java",
                "Обучиться Java на достаточном для трудоустройства уровне за последующий год");

        int idEpic1 = taskManager.addEpic(newEpic1);

        Subtask newSubtask1 = new Subtask("Обучиться JavaCore",
                "Изучить JavaCore за 6 месяцев", TaskStatus.NEW, idEpic1);
        Subtask newSubtask2 = new Subtask("Обучиться SpringBoot",
                "Изучить SpringBoot за 6 месяцев", TaskStatus.NEW, idEpic1);
        Subtask newSubtask3 = new Subtask("Обучиться SQL, Hibernate, Docker",
                "Изучить SQL, Hibernate, Docker за 3 месяца", TaskStatus.NEW, idEpic1);

        int idSubtask1 = taskManager.addSubtask(newSubtask1);
        int idSubtask2 =  taskManager.addSubtask(newSubtask2);
        int idSubtask3 = taskManager.addSubtask(newSubtask3);

        Epic newEpic2 = new Epic("Научится работе с БД",
                "Обучиться работе с БД за последующие полгода");

        int idEpic2 = taskManager.addEpic(newEpic2);

        taskManager.getEpic(idEpic1);
        taskManager.getTask(idTask2);
        taskManager.getSubtask(idSubtask3);
        taskManager.getSubtask(idSubtask2);
        printHistory(taskManager);

        taskManager.getTask(idTask2);
        taskManager.getSubtask(idSubtask1);
        taskManager.getEpic(idEpic2);
        taskManager.getSubtask(idSubtask2);
        taskManager.getTask(idTask1);
        taskManager.getTask(idTask1);
        taskManager.getTask(idTask1);
        taskManager.getTask(idTask1);
        printHistory(taskManager);

        taskManager.removeEpic(idEpic2);
        printHistory(taskManager);

        taskManager.removeEpic(idEpic1);
        printHistory(taskManager);

        taskManager.removeAllTasks();
        printHistory(taskManager);
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("История:");

        for (Task task : manager.getHistoryList()) {
            System.out.println(task);
        }
        System.out.println("-".repeat(15));
    }
}

