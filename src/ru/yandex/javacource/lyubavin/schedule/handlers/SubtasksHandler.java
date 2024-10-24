package ru.yandex.javacource.lyubavin.schedule.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import ru.yandex.javacource.lyubavin.schedule.exceptions.TaskValidationException;
import ru.yandex.javacource.lyubavin.schedule.manager.TaskManager;
import ru.yandex.javacource.lyubavin.schedule.task.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static ru.yandex.javacource.lyubavin.schedule.server.HttpTaskServer.getGson;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson = getGson();

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String requestMethod = exchange.getRequestMethod();
            String requestPath = exchange.getRequestURI().getPath();

            switch (requestMethod) {
                case "GET":
                    if (Pattern.matches("^/subtasks$", requestPath)) {
                        try {
                            String response = gson.toJson(taskManager.getAllSubtasks());
                            sendText(exchange, response);
                        } catch (NullPointerException e) {
                            sendCode(exchange, 404);
                        }
                    } else if (Pattern.matches("^/subtasks/\\d+$", requestPath)) {
                        int id = getId(requestPath);

                        if (id != -1) {
                            try {
                                Subtask subtaskToGet = taskManager.getSubtask(id);

                                if (subtaskToGet != null) {
                                    sendText(exchange, gson.toJson(subtaskToGet));
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
                    if (Pattern.matches("^/subtasks$", requestPath)) {
                        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Subtask subtask;

                        try {
                            subtask = gson.fromJson(body, Subtask.class);
                        } catch (JsonSyntaxException e) {
                            sendCode(exchange, 400);
                            return;
                        }

                        if (subtask != null) {
                            if (subtask.getId() == 0) {
                                try {
                                    taskManager.addSubtask(subtask);
                                    sendCode(exchange, 200);
                                } catch (TaskValidationException e) {
                                    sendCode(exchange, 406);
                                }
                            } else {
                                try {
                                    taskManager.updateSubtask(subtask);
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
                    if (Pattern.matches("^/subtasks/\\d+$", requestPath)) {
                        int id = getId(requestPath);

                        if (id != -1) {
                            try {
                                Subtask subtaskToRemove = taskManager.getSubtask(id);

                                if (subtaskToRemove != null) {
                                    taskManager.removeSubtask(id);
                                    sendText(exchange, gson.toJson(subtaskToRemove));
                                } else {
                                    sendCode(exchange, 404);
                                }
                            } catch (Exception e) {
                                sendCode(exchange, 500);
                            }
                        }
                    } else {
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
