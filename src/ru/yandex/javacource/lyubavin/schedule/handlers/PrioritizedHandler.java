package ru.yandex.javacource.lyubavin.schedule.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacource.lyubavin.schedule.manager.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

import static ru.yandex.javacource.lyubavin.schedule.server.HttpTaskServer.getGson;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson = getGson();

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String requestMethod = exchange.getRequestMethod();
            String requestPath = exchange.getRequestURI().getPath();

            if (requestMethod.equals("GET")) {
                if (Pattern.matches("^/prioritized$", requestPath)) {
                    String tasksJson = gson.toJson(taskManager.getPrioritizedTasks());
                    sendText(exchange, tasksJson);
                } else {
                    sendCode(exchange, 404);
                }
            } else {
                sendCode(exchange, 400);
            }
        } catch (Exception e) {
            sendCode(exchange, 500);
        }
    }
}
