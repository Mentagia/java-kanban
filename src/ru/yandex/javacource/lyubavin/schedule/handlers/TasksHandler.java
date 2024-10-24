package ru.yandex.javacource.lyubavin.schedule.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import ru.yandex.javacource.lyubavin.schedule.exceptions.TaskValidationException;
import ru.yandex.javacource.lyubavin.schedule.manager.TaskManager;
import ru.yandex.javacource.lyubavin.schedule.task.Subtask;
import ru.yandex.javacource.lyubavin.schedule.task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static ru.yandex.javacource.lyubavin.schedule.server.HttpTaskServer.getGson;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson = getGson();

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String requestMethod = exchange.getRequestMethod();
            String requestPath = exchange.getRequestURI().getPath();


            switch (requestMethod) {
                case "GET":
                    if (Pattern.matches("^/tasks$", requestPath)) {
                        try {
                            String response = gson.toJson(taskManager.getAllTasks());
                            sendText(exchange, response);
                        } catch (NullPointerException e) {
                            sendCode(exchange, 404);
                        }
                    } else if (Pattern.matches("^/tasks/\\d+$", requestPath)) {
                        int id = getId(requestPath);

                        if (id != -1) {
                            try {
                                Task taskToGet = taskManager.getTask(id);

                                if (taskToGet != null) {
                                    sendText(exchange, gson.toJson(taskToGet));
                                } else {
                                    sendCode(exchange, 404);
                                }
                            } catch (Exception e) {
                                sendCode(exchange, 500);
                            }
                        }
                    } else {
                        sendCode(exchange, 404);
                    }
                    break;
                case "POST":
                    if (Pattern.matches("^/tasks$", requestPath)) {
                        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Task task;

                        try {
                            task = gson.fromJson(body, Task.class);
                        } catch (JsonSyntaxException e) {
                            sendCode(exchange, 400);
                            return;
                        }

                        if (task != null && !(task instanceof Subtask)) {
                            if (task.getId() == 0) {
                                try {
                                    taskManager.addTask(task);
                                    sendCode(exchange, 200);
                                } catch (TaskValidationException e) {
                                    sendCode(exchange, 406);
                                }
                            } else {
                                try {
                                    taskManager.updateTask(task);
                                    sendCode(exchange, 201);
                                } catch (TaskValidationException e) {
                                    sendCode(exchange, 406);
                                }
                            }
                        } else {
                            sendCode(exchange, 400);
                        }
                    } else {
                        sendCode(exchange, 404);
                    }
                    break;
                case "DELETE":
                    if (Pattern.matches("^/tasks/\\d+$", requestPath)) {
                        int id = getId(requestPath);

                        if (id != -1) {
                            try {
                                Task taskToRemove = taskManager.getTask(id);

                                if (taskToRemove != null) {
                                    taskManager.removeTask(id);
                                    sendText(exchange, gson.toJson(taskToRemove));
                                } else {
                                    sendCode(exchange, 404);
                                }
                            } catch (Exception e) {
                                sendCode(exchange, 500);
                            }
                        }
                    } else {
                        System.out.println(2);
                        sendCode(exchange, 400);
                    }
                    break;
                default:
                    sendCode(exchange, 400);

            }
        } catch (Exception e) {
            sendCode(exchange, 500);
        }
    }

    protected Integer getId(String requestPath) {
        String[] pathParts = requestPath.split("/");

        try {
            return Integer.parseInt(pathParts[2]);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }
}
