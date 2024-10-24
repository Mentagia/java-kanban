package ru.yandex.javacource.lyubavin.schedule.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import ru.yandex.javacource.lyubavin.schedule.adapters.EpicAdapter;
import ru.yandex.javacource.lyubavin.schedule.enums.TaskStatus;
import ru.yandex.javacource.lyubavin.schedule.manager.InMemoryTaskManager;
import ru.yandex.javacource.lyubavin.schedule.manager.TaskManager;
import ru.yandex.javacource.lyubavin.schedule.task.Epic;
import ru.yandex.javacource.lyubavin.schedule.task.Subtask;
import ru.yandex.javacource.lyubavin.schedule.task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {

    TaskManager taskManager;
    HttpTaskServer taskServer;
    Gson gson = HttpTaskServer.getGson();
    Gson epicGson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Epic.class, new EpicAdapter())
            .create();
    HttpClient client;

    class TaskListTypeToken extends TypeToken<List<Task>> {
    }

    class SubtaskListTypeToken extends TypeToken<List<Subtask>> {
    }

    class EpicListTypeToken extends TypeToken<List<Epic>> {
    }

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(taskManager);
        taskManager.removeAllTasks();
        taskManager.removeAllEpics();
        taskManager.removeAllSubtask();
        taskServer.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(5));

        String taskJson = gson.toJson(task);

        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = taskManager.getAllTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getTaskName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task2 = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(30));

        int taskId = taskManager.addTask(task2);

        URI url = URI.create("http://localhost:8080/tasks");

        Task task3 = new Task(taskId,"Test 3", "Testing task 3",
                TaskStatus.IN_PROGRESS, LocalDateTime.of(2024,9,12,23, 0),
                Duration.ofMinutes(10));

        String taskJson3 = gson.toJson(task3);

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson3))
                .build();

        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksFromManager = taskManager.getAllTasks();

        assertEquals(201, response3.statusCode());

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(task3, tasksFromManager.getFirst(), "Добавлена некорректная задача");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task2 = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(30));

        int taskId = taskManager.addTask(task2);

        URI url = URI.create("http://localhost:8080/tasks/" + taskId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksFromManager = taskManager.getAllTasks();

        assertEquals(200, response.statusCode());
        assertNull(tasksFromManager, "Задача не была удалена");
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        Task task2 = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(30));

        int taskId = taskManager.addTask(task2);

        URI url = URI.create("http://localhost:8080/tasks/" + taskId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task jsonTask = gson.fromJson(response.body(), Task.class);
        assertEquals(task2, jsonTask, "Получена неправильная задача");
    }

    @Test
    public void testGetAllTask() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        Task task2 = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, LocalDateTime.of(2024,10,12,23, 0),
                Duration.ofMinutes(30));
        Task task3 = new Task("Test 3", "Testing task 3",
                TaskStatus.NEW, LocalDateTime.of(2023,9,1,23, 0),
                Duration.ofMinutes(10));
        int taskId1 = taskManager.addTask(task1);
        int taskId2 = taskManager.addTask(task2);
        int taskId3 = taskManager.addTask(task3);

        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Task> jsonTaskList = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        List<Task> taskList = taskManager.getAllTasks();

        assertEquals(taskList.size(), jsonTaskList.size(), "Получены не все задачи");
        for (int i = 0; i < taskList.size(); i++){
            assertEquals(taskList.get(i), jsonTaskList.get(i), "Получены неверные задачи");
        }
        assertEquals(taskList, jsonTaskList, "Получены не все задачи");
    }

    @Test
    public void testTaskIntersection() throws IOException, InterruptedException {
        Task task2 = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(30));

        int taskId = taskManager.addTask(task2);

        URI url = URI.create("http://localhost:8080/tasks");

        Task task3 = new Task("Test 3", "Testing task 3",
                TaskStatus.IN_PROGRESS, LocalDateTime.now(), Duration.ofMinutes(30));

        String taskJson3 = gson.toJson(task3);

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson3))
                .build();

        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        List<Task> tasksFromManager = taskManager.getAllTasks();

        assertEquals(406, response3.statusCode());

        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertNotEquals(task3, tasksFromManager.getFirst(), "Добавлена задача, пересекающаюся с имеющейся");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic newEpic1 = new Epic("Test 1", "Testing task 1");

        String taskJson = epicGson.toJson(newEpic1);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Epic> tasksFromManager = taskManager.getAllEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 1", tasksFromManager.getFirst().getTaskName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        Epic newEpic1 = new Epic("Test 1", "Testing task 1");

        int epicId = taskManager.addEpic(newEpic1);
        Subtask newSubtask1 = new Subtask("Test 2",
                "Testing task 2", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2024,10,12,12, 0), Duration.ofHours(1), epicId);
        taskManager.addSubtask(newSubtask1);

        Epic newEpic2 = new Epic(epicId,"Test 2", "Testing task 2");
        String taskJson = epicGson.toJson(newEpic2);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> tasksFromManager = taskManager.getAllEpics();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(newEpic2, tasksFromManager.getFirst(), "Обновление не было произведено");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic newEpic1 = new Epic("Test 1", "Testing task 1");

        int taskId = taskManager.addEpic(newEpic1);

        URI url = URI.create("http://localhost:8080/epics/" + taskId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> tasksFromManager = taskManager.getAllEpics();

        assertEquals(200, response.statusCode());
        assertNull(tasksFromManager, "Задача не была удалена");
    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        Epic newEpic1 = new Epic("Test 1", "Testing task 1");

        int taskId = taskManager.addEpic(newEpic1);

        URI url = URI.create("http://localhost:8080/epics/" + taskId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Epic jsonTask = epicGson.fromJson(response.body(), Epic.class);
        assertEquals(newEpic1, jsonTask, "Получена неправильная задача");
    }

    @Test
    public void testGetAllEpics() throws IOException, InterruptedException {
        Epic newEpic1 = new Epic("Test 1", "Testing task 1");
        Epic newEpic2 = new Epic("Test 2", "Testing task 2");
        Epic newEpic3 = new Epic("Test 3", "Testing task 3");
        int taskId1 = taskManager.addEpic(newEpic1);
        int taskId2 = taskManager.addEpic(newEpic2);
        int taskId3 = taskManager.addEpic(newEpic3);

        Subtask newSubtask1 = new Subtask("Test 1",
                "Testing task 1", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2024,10,12,12, 0), Duration.ofHours(1), taskId2);
        Subtask newSubtask2 = new Subtask("Test 2",
                "Testing task 2", TaskStatus.NEW,
                LocalDateTime.of(2022,12,2,3, 0), Duration.ofHours(1), taskId2);
        Subtask newSubtask3 = new Subtask("Test 3",
                "Testing task 3", TaskStatus.DONE,
                LocalDateTime.of(2021,3,21,3, 0), Duration.ofHours(1), taskId3);
        taskManager.addSubtask(newSubtask1);
        taskManager.addSubtask(newSubtask2);
        taskManager.addSubtask(newSubtask3);

        URI url = URI.create("http://localhost:8080/epics");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Epic> jsonTaskList = epicGson.fromJson(response.body(), new EpicListTypeToken().getType());
        List<Epic> taskList = taskManager.getAllEpics();

        assertEquals(taskList.size(), jsonTaskList.size(), "Получены не все задачи");
        for (int i = 0; i < taskList.size(); i++){
            assertEquals(taskList.get(i), jsonTaskList.get(i), "Получены неверные задачи");
        }
        assertEquals(taskList, jsonTaskList, "Получены не все задачи");
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic newEpic1 = new Epic("Test 1", "Testing task 1");
        int epicId = taskManager.addEpic(newEpic1);

        Subtask newSubtask1 = new Subtask("Test 2",
                "Testing task 2", TaskStatus.NEW,
                LocalDateTime.of(2024,10,12,12, 0), Duration.ofHours(1), epicId);

        String taskJson = gson.toJson(newSubtask1);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Subtask> tasksFromManager = taskManager.getAllSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getFirst().getTaskName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic newEpic1 = new Epic("Test 1", "Testing task 1");
        int epicId = taskManager.addEpic(newEpic1);

        Subtask newSubtask1 = new Subtask("Test 2",
                "Testing task 2", TaskStatus.NEW,
                LocalDateTime.of(2024,10,12,12, 0), Duration.ofHours(1), epicId);
        int subId = taskManager.addSubtask(newSubtask1);

        Subtask updateSubtask1 = new Subtask(subId,"Test 3",
                "Testing task 4", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofHours(2), epicId);

        String taskJson = gson.toJson(updateSubtask1);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Subtask> tasksFromManager = taskManager.getAllSubtasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(updateSubtask1, tasksFromManager.getFirst(), "Добавлена некорректная задача");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic newEpic1 = new Epic("Test 1", "Testing task 1");
        int epicId = taskManager.addEpic(newEpic1);

        Subtask newSubtask1 = new Subtask("Test 2",
                "Testing task 2", TaskStatus.NEW,
                LocalDateTime.of(2024,10,12,12, 0), Duration.ofHours(1), epicId);
        int subId = taskManager.addSubtask(newSubtask1);

        URI url = URI.create("http://localhost:8080/subtasks/" + subId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Subtask> tasksFromManager = taskManager.getAllSubtasks();

        assertEquals(200, response.statusCode());
        assertNull(tasksFromManager, "Задача не была удалена");
    }

    @Test
    public void testGetSubtask() throws IOException, InterruptedException {
        Epic newEpic1 = new Epic("Test 1", "Testing task 1");
        int epicId = taskManager.addEpic(newEpic1);

        Subtask newSubtask1 = new Subtask("Test 2",
                "Testing task 2", TaskStatus.NEW,
                LocalDateTime.of(2024,10,12,12, 0), Duration.ofHours(1), epicId);
        int subId = taskManager.addSubtask(newSubtask1);

        URI url = URI.create("http://localhost:8080/subtasks/" + subId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task jsonTask = gson.fromJson(response.body(), Subtask.class);
        assertEquals(newSubtask1, jsonTask, "Получена неправильная задача");
    }

    @Test
    public void testGetAllSubtask() throws IOException, InterruptedException {
        Epic newEpic1 = new Epic("Test 1", "Testing task 1");
        int epicId = taskManager.addEpic(newEpic1);

        Subtask newSubtask1 = new Subtask("Test 1",
                "Testing task 1", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2024,10,12,12, 0), Duration.ofHours(1), epicId);
        Subtask newSubtask2 = new Subtask("Test 2",
                "Testing task 2", TaskStatus.NEW,
                LocalDateTime.of(2022,12,2,3, 0), Duration.ofHours(1), epicId);
        Subtask newSubtask3 = new Subtask("Test 3",
                "Testing task 3", TaskStatus.DONE,
                LocalDateTime.of(2021,3,21,3, 0), Duration.ofHours(1), epicId);
        taskManager.addSubtask(newSubtask1);
        taskManager.addSubtask(newSubtask2);
        taskManager.addSubtask(newSubtask3);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Subtask> jsonTaskList = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());
        List<Subtask> taskList = taskManager.getAllSubtasks();

        assertEquals(taskList.size(), jsonTaskList.size(), "Получены не все задачи");
        for (int i = 0; i < taskList.size(); i++){
            assertEquals(taskList.get(i), jsonTaskList.get(i), "Получены неверные задачи");
        }
        assertEquals(taskList, jsonTaskList, "Получены не все задачи");
    }

    @Test
    public void testSubtaskIntersection() throws IOException, InterruptedException {
        Epic newEpic1 = new Epic("Test 1", "Testing task 1");
        int epicId = taskManager.addEpic(newEpic1);

        Subtask newSubtask1 = new Subtask("Test 2",
                "Testing task 2", TaskStatus.NEW,
                LocalDateTime.of(2024,10,12,12, 0), Duration.ofHours(1), epicId);
        int subId = taskManager.addSubtask(newSubtask1);

        URI url = URI.create("http://localhost:8080/subtasks");

        Subtask updSubtask1 = new Subtask("Test 3",
                "Testing task 4", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2024,10,12,12, 0), Duration.ofHours(3), epicId);

        String taskJson3 = gson.toJson(updSubtask1);

        HttpRequest request3 = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson3))
                .build();

        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        List<Subtask> tasksFromManager = taskManager.getAllSubtasks();

        assertEquals(406, response3.statusCode());

        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertNotEquals(updSubtask1, tasksFromManager.getFirst(), "Добавлена задача, пересекающаюся с имеющейся");
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1",
                TaskStatus.NEW, LocalDateTime.now().plusMinutes(30), Duration.ofMinutes(5));
        Task task2 = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(5));
        Task task3 = new Task("Test 3", "Testing task 3",
                TaskStatus.NEW,LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(5));
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertNotNull(tasksFromManager);
        assertEquals(3, tasksFromManager.size(), "Переданы не все задачи");
        assertEquals("Test 3", tasksFromManager.get(0).getTaskName(), "Задачи не совпадают");
        assertEquals("Test 2", tasksFromManager.get(1).getTaskName(), "Задачи не совпадают");
        assertEquals("Test 1", tasksFromManager.get(2).getTaskName(), "Задачи не совпадают");
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task("Test 1", "Testing task 1",
                TaskStatus.NEW, LocalDateTime.now().plusMinutes(30), Duration.ofMinutes(5));
        Task task2 = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, LocalDateTime.now().plusMinutes(20), Duration.ofMinutes(5));
        Task task3 = new Task("Test 3", "Testing task 3",
                TaskStatus.NEW,LocalDateTime.now().plusMinutes(10), Duration.ofMinutes(5));

        int taskId1 = taskManager.addTask(task1);
        int taskId2 =taskManager.addTask(task2);
        int taskId3 =taskManager.addTask(task3);

        taskManager.getTask(taskId1);
        taskManager.getTask(taskId2);
        taskManager.getTask(taskId3);

        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertNotNull(tasksFromManager);
        assertEquals(3, tasksFromManager.size(), "Переданы не все задачи");
        assertEquals("Test 1", tasksFromManager.get(0).getTaskName(), "Задачи не совпадают");
        assertEquals("Test 2", tasksFromManager.get(1).getTaskName(), "Задачи не совпадают");
        assertEquals("Test 3", tasksFromManager.get(2).getTaskName(), "Задачи не совпадают");
    }
}
