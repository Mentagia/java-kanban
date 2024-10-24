package ru.yandex.javacource.lyubavin.schedule.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import ru.yandex.javacource.lyubavin.schedule.adapters.EpicAdapter;
import ru.yandex.javacource.lyubavin.schedule.exceptions.TaskValidationException;
import ru.yandex.javacource.lyubavin.schedule.manager.TaskManager;
import ru.yandex.javacource.lyubavin.schedule.task.Epic;
import ru.yandex.javacource.lyubavin.schedule.task.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Epic.class, new EpicAdapter())
            .create();

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String requestMethod = exchange.getRequestMethod();
            String requestPath = exchange.getRequestURI().getPath();

            switch (requestMethod) {
                case "GET":
                    if (Pattern.matches("^/epics$", requestPath)) {
                        try {
                            String response = gson.toJson(taskManager.getAllEpics());
                            sendText(exchange, response);
                        } catch (NullPointerException e) {
                            sendCode(exchange, 404);
                        }
                    } else if (Pattern.matches("^/epics/\\d+$", requestPath)) {
                        int id = getId(requestPath);

                        if (id != -1) {
                            try {
                                Epic epicToGet = taskManager.getEpic(id);

                                if (epicToGet != null) {
                                    sendText(exchange, gson.toJson(epicToGet));
                                } else {
                                    sendCode(exchange, 404);
                                }
                            } catch (Exception e) {
                                sendCode(exchange, 500);
                            }
                        }
                    } else if (Pattern.matches("^/epics/\\d+/subtasks$", requestPath)) {
                        int id = getId(requestPath);

                        if (id != -1) {
                            try {
                                List<Subtask> epicSubtasks = taskManager.getAllEpicSubtasks(id);

                                if (epicSubtasks != null) {
                                    sendText(exchange, gson.toJson(epicSubtasks));
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
                    if (Pattern.matches("^/epics$", requestPath)) {
                        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                        Epic epic;

                        try {
                            epic = gson.fromJson(body, Epic.class);
                        } catch (JsonSyntaxException e) {
                            System.out.println(0);
                            sendCode(exchange, 400);
                            return;
                        }

                        if (epic != null) {
                            if (epic.getId() == 0) {
                                try {
                                    taskManager.addEpic(epic);
                                    sendCode(exchange, 200);
                                } catch (TaskValidationException e) {
                                    sendCode(exchange, 406);
                                }
                            } else {
                                try {
                                    taskManager.updateEpic(epic);
                                    sendCode(exchange, 201);
                                } catch (TaskValidationException e) {
                                    sendCode(exchange, 406);
                                }
                            }
                        } else {
                            System.out.println(1);
                            sendCode(exchange, 400);
                        }
                    } else {
                        sendCode(exchange, 404);
                    }
                    break;
                case "DELETE":
                    if (Pattern.matches("^/epics/\\d+$", requestPath)) {
                        int id = getId(requestPath);

                        if (id != -1) {
                            try {
                                Epic epicToRemove = taskManager.getEpic(id);

                                if (epicToRemove != null) {
                                    taskManager.removeEpic(id);
                                    sendText(exchange, gson.toJson(epicToRemove));
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
                    System.out.println(3);
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

