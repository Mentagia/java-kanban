public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task newTask1 = new Task("Прогулка",
                "Проходить по 10000 шагов в день на протяжении недели", TaskStatus.NEW);
        Task newTask2 = new Task("Сон", "Спать по 8 часов на протяжении недели",
                TaskStatus.IN_PROGRESS);

        taskManager.addTask(newTask1);
        taskManager.addTask(newTask2);

        Epic newEpic1 = new Epic("Обучиться Java",
                "Обучиться Java на достаточном для трудоустройства уровне за последующий год");

        taskManager.addEpic(newEpic1);

        Subtask newSubtask1 = new Subtask("Обучиться JavaCore",
                "Изучить JavaCore за 6 месяцев", TaskStatus.NEW, 2);
        Subtask newSubtask2 = new Subtask("Обучиться SpringBoot",
                "Изучить SpringBoot за 6 месяцев", TaskStatus.NEW, 2);

        taskManager.addSubtask(newSubtask1);
        taskManager.addSubtask(newSubtask2);

        Epic newEpic2 = new Epic("Научится работе с БД",
                "Обучиться работе с БД за последующие полгода");

        taskManager.addEpic(newEpic2);

        Subtask newSubtask3 = new Subtask("Обучиться SQL, Hibernate, Docker",
                "Изучить SQL, Hibernate, Docker за 3 месяца", TaskStatus.NEW, 5);

        taskManager.addSubtask(newSubtask3);

        System.out.println("Список task-ов: " + taskManager.getAllTasks());
        System.out.println("Список epic-ов: " + taskManager.getAllEpics());
        System.out.println("Список subtask-ов: " +taskManager.getAllSubtasks());

        Task updatedTask = new Task(0, "Прогулка",
                "Проходить по 10000 шагов в день на протяжении недели", TaskStatus.IN_PROGRESS);
        taskManager.taskUpdate(updatedTask);

        Subtask updatedSubtask2 = new Subtask(4, "Обучиться SpringBoot",
                "Изучить SpringBoot за 6 месяцев", TaskStatus.IN_PROGRESS, 2);
        taskManager.subtaskUpdate(updatedSubtask2);

        System.out.println("-".repeat(15));
        System.out.println("Список task-ов: " + taskManager.getAllTasks());
        System.out.println("Список epic-ов: " +taskManager.getAllEpics());
        System.out.println("Список subtask-ов: " +taskManager.getAllSubtasks());

        taskManager.removeTaskById(1);
        taskManager.removeSubtaskById(6);

        System.out.println("-".repeat(15));
        System.out.println("Список task-ов: " + taskManager.getAllTasks());
        System.out.println("Список epic-ов: " +taskManager.getAllEpics());
        System.out.println("Список subtask-ов: " +taskManager.getAllSubtasks());

        taskManager.removeEpicById(2);

        System.out.println("-".repeat(15));
        System.out.println("Список task-ов: " + taskManager.getAllTasks());
        System.out.println("Список epic-ов: " +taskManager.getAllEpics());
        System.out.println("Список subtask-ов: " +taskManager.getAllSubtasks());
    }

}
