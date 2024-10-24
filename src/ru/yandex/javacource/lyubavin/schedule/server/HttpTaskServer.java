package ru.yandex.javacource.lyubavin.schedule.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.javacource.lyubavin.schedule.adapters.DurationAdapter;
import ru.yandex.javacource.lyubavin.schedule.adapters.LocalDateTimeAdapter;
import ru.yandex.javacource.lyubavin.schedule.handlers.*;
import ru.yandex.javacource.lyubavin.schedule.manager.Managers;
import ru.yandex.javacource.lyubavin.schedule.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    public static final int PORT = 8080;

    private HttpServer httpServer;
    private TaskManager taskManager;

    public HttpTaskServer(TaskManager manager) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler(manager));
        httpServer.createContext("/subtasks", new SubtasksHandler(manager));
        httpServer.createContext("/epics", new EpicsHandler(manager));
        httpServer.createContext("/history", new HistoryHandler(manager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager));
    }

    public void start() {
        httpServer.start();
        System.out.println("Server started at PORT: " + PORT);
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Server stopped at PORT: " + PORT);
    }

    public static Gson getGson() {
        return new GsonBuilder().serializeNulls()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();

        HttpTaskServer server = new HttpTaskServer(manager);
        server.start();
        server.stop();
    }


}
