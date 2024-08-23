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
        HistoryManager historyManager = Managers.getDefaultHistory();

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
                "Изучить JavaCore за 6 месяцев", TaskStatus.NEW, 2);
        Subtask newSubtask2 = new Subtask("Обучиться SpringBoot",
                "Изучить SpringBoot за 6 месяцев", TaskStatus.NEW, 2);

        int idSubtask1 = taskManager.addSubtask(newSubtask1);
        int idSubtask2 =  taskManager.addSubtask(newSubtask2);

        Epic newEpic2 = new Epic("Научится работе с БД",
                "Обучиться работе с БД за последующие полгода");

        int idEpic2 = taskManager.addEpic(newEpic2);

        Subtask newSubtask3 = new Subtask("Обучиться SQL, Hibernate, Docker",
                "Изучить SQL, Hibernate, Docker за 3 месяца", TaskStatus.NEW, 5);

        int idSubtask3 = taskManager.addSubtask(newSubtask3);

        /*System.out.println("Список task-ов: " + taskManager.getAllTasks());
        System.out.println("Список epic-ов: " + taskManager.getAllEpics());
        System.out.println("Список subtask-ов: " +taskManager.getAllSubtasks());

        Task updatedTask = new Task(0, "Прогулка",
                "Проходить по 10000 шагов в день на протяжении недели", TaskStatus.IN_PROGRESS);
        taskManager.updateTask(updatedTask);*/

        Subtask updatedSubtask2 = new Subtask(4, "Обучиться SpringBoot",
                "Изучить SpringBoot за 6 месяцев", TaskStatus.IN_PROGRESS, 2);
        taskManager.updateSubtask(updatedSubtask2);
/*
        System.out.println("-".repeat(15));
        System.out.println("Список task-ов: " + taskManager.getAllTasks());
        System.out.println("Список epic-ов: " + taskManager.getAllEpics());
        System.out.println("Список subtask-ов: " + taskManager.getAllSubtasks());*/

        System.out.println("Задачи:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : taskManager.getAllEpics()) {
            System.out.println(epic);

            for (Task task : taskManager.getAllEpicSubtasks(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : taskManager.getAllSubtasks()) {
            System.out.println(subtask);
        }


        System.out.println(taskManager.getTask(1));
        System.out.println(taskManager.getEpic(2));
        System.out.println(taskManager.getTask(1));
        System.out.println(taskManager.getSubtask(4));
        System.out.println(taskManager.getSubtask(3));
        System.out.println(taskManager.getTask(1));
        System.out.println(taskManager.getEpic(5));
        System.out.println(taskManager.getSubtask(6));
        System.out.println(taskManager.getSubtask(3));
        System.out.println(taskManager.getSubtask(4));

        System.out.println("История:");
        for (Task task : taskManager.getHistoryManager().getHistory()) {
            System.out.println(task);
        }

        System.out.println(taskManager.getSubtask(6));

        System.out.println("История:");
        for (Task task : taskManager.getHistoryManager().getHistory()) {
            System.out.println(task);
        }
    }


       /* taskManager.removeTask(idTask2);
        taskManager.removeSubtask(idSubtask3);

        System.out.println("-".repeat(15));
        System.out.println("Список task-ов: " + taskManager.getAllTasks());
        System.out.println("Список epic-ов: " + taskManager.getAllEpics());
        System.out.println("Список subtask-ов: " + taskManager.getAllSubtasks());
        System.out.println("Список subtask-ов epic-а с id 2" + taskManager.getAllEpicSubtasks(2));

        taskManager.removeEpic(idEpic1);

        System.out.println("-".repeat(15));
        System.out.println("Список task-ов: " + taskManager.getAllTasks());
        System.out.println("Список epic-ов: " + taskManager.getAllEpics());
        System.out.println("Список subtask-ов: " + taskManager.getAllSubtasks());*/
}

