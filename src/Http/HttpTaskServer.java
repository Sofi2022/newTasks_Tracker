package Http;

import Exceptions.ManagerLoadException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;
import static manager.Managers.getGson;

public class HttpTaskServer {

    public static final int PORT = 8080;
    private HttpServer server;
    private Gson gson;
    private TaskManager taskManager;

    public HttpTaskServer() throws IOException, ManagerLoadException {
        this(Managers.getDefault());
    }

    public HttpTaskServer (TaskManager taskManager) throws IOException, ManagerLoadException {
        this.taskManager = Managers.getDefault();
        gson = getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this :: handler);

    }

    public void handler(HttpExchange httpExchange){
        try{
            System.out.println("/tasks" + httpExchange.getRequestURI());
            final String path = httpExchange.getRequestURI().getPath().replaceFirst("/tasks", "");
            switch (path){
                case "/task" :
                    System.out.println("выбран таск");
                    handleTask(httpExchange);
                    break;
                case "/subtask" :
                    handleSubtask(httpExchange);
                    break;
                case "/epic" :
                    handleEpic(httpExchange);
                    break;
                case "/history" :
                    handleHistory(httpExchange);
                    break;
                case "/ " :
                    String response = gson.toJson(taskManager.getPrioritizedTasks());
                    sendText(httpExchange, response);
                    break;
            }
        } catch(Exception exception){
            System.out.println("Ошибка при обработке запроса");
        } finally {
            httpExchange.close();
        }
    }

    private void handleHistory(HttpExchange httpExchange) throws IOException {
        String requestMethod = httpExchange.getRequestMethod();
        if(requestMethod.equals("GET")){
            List history = taskManager.getHistory();
            String response = gson.toJson(history);
            sendText(httpExchange,response);
            return;
        } else {
            System.out.println("Ошибка запроса");
        }
    }

    private void handleEpic(HttpExchange httpExchange) throws IOException {
        String requestMethod = httpExchange.getRequestMethod();
        String query = httpExchange.getRequestURI().getQuery();
        switch (requestMethod) {
            case "GET":
                if (Objects.nonNull(query)) {
                    String idString = query.substring(2);
                    int id = Integer.parseInt(idString);
                    String response = gson.toJson(taskManager.getEpicById(id));
                    sendText(httpExchange, response);
                    break;
                }else {
                    List epics = taskManager.getEpicList();
                    String response = gson.toJson(epics);
                    sendText(httpExchange, response);
                    break;
                }
            case "POST" :
                if(Objects.nonNull(query)){
                    String body = readText(httpExchange);
                    Epic epic = gson.fromJson(body, Epic.class);
                    if(epic.getId() == 0) {
                        taskManager.createEpic(epic);
                        String response = "Эпик создан";
                        sendText(httpExchange, response);
                        break;
                    }else {
                        taskManager.updateEpic(epic);
                        String response = "Эпик обновлен";
                        sendText(httpExchange, response);
                        break;
                    }
                }
        }
    }

    private void handleSubtask(HttpExchange httpExchange) throws IOException {
        String requestMethod = httpExchange.getRequestMethod();
        String query = httpExchange.getRequestURI().getQuery();
        switch (requestMethod) {
            case "GET":
                if (Objects.nonNull(query)) {
                    String idString = query.substring(2);
                    int id = Integer.parseInt(idString);
                    String response = gson.toJson(taskManager.getSubtaskById(id));
                    sendText(httpExchange, response);
                    break;
                } else {
                    String response = gson.toJson(taskManager.getSubtaskList());
                    sendText(httpExchange, response);
                    return;
                }
            case "POST" :
                String body = readText(httpExchange);
                Subtask subtask = gson.fromJson(body, Subtask.class);
                if(Objects.nonNull(query)) {
                    if (subtask.getId() == 0) {
                        taskManager.createSubtask(subtask);
                        String response = "Задача создана";
                        sendText(httpExchange, response);
                        break;
                    } else {
                        taskManager.updateSubtask(subtask);
                        String response = "Задача обновлена";
                        sendText(httpExchange, response);
                        break;
                    }
                }
            case "DELETE" :
                if(Objects.nonNull(query)){
                    String idString = query.substring(2);
                    int id = Integer.parseInt(idString);
                    taskManager.deleteSubtaskById(id);
                    String response = "Задача удалена";
                    sendText(httpExchange, response);
                    break;
                } else{
                    taskManager.deleteAllSubtasks();
                    String response = "Задачи удалены";
                    sendText(httpExchange, response);
                    break;
                }
        }
    }

    private void handleTask(HttpExchange httpExchange) throws IOException { // выводит id равно 1. Возможно дело в менеджеоре
        String requestMethod = httpExchange.getRequestMethod();
        System.out.println("метод запроса " + requestMethod);//получаем метод запроса
        String query = httpExchange.getRequestURI().getQuery();
        // InMemoryTaskManager taskManager2 = (InMemoryTaskManager) Managers.getDefault();
        // получаем параметр запроса
        System.out.println("Параметры " + query);
        switch (requestMethod){
            case "GET" :
                if(Objects.nonNull(query)){
                    System.out.println("есть параметр");//есть параметры запроса
                    String idString = query.substring(2);
                    int id = Integer.parseInt(idString);
                    System.out.println("id равно " + id);
                    System.out.println("Объект задачи " + taskManager.getTaskById(id));
                    Task task = taskManager.getTaskById(id);
                    System.out.println("taskId" + task.getId());
                    String response = gson.toJson(task);
                    System.out.println("задача с id");
                    sendText(httpExchange, response);
                    break;
                }else{
                    System.out.println(taskManager.getTaskList());
                    String response = gson.toJson(taskManager.getTaskList()); //если нет параметров
                    sendText(httpExchange, response);
                    break;
                }
            case "POST" :
                String body = readText(httpExchange); //читаем переданный текст
                Task task = gson.fromJson(body, Task.class); // упаковываем в объект класс из json
                if(task.getId() == 0){  // если у задачи не задан id, создаем новую задачу
                    taskManager.createTask(task);
                    String response = "Задача создана";
                    sendText(httpExchange, response);
                    break;
                } else{
                    taskManager.updateTask(task); // если id есть, обновляем задачу
                    String response = "Задача обновлена";
                    sendText(httpExchange, response);
                    break;
                }
            case "DELETE" :
                if(Objects.nonNull(query)){
                    String idString = query.substring(2);
                    int id = Integer.parseInt(idString);
                    taskManager.deleteTaskById(id);
                    System.out.println("Задача удалена");
                    httpExchange.sendResponseHeaders(200, 0);
                    break;
                } else{
                    taskManager.deleteAllTasks();
                    System.out.println("Все задачи удалены");
                    httpExchange.sendResponseHeaders(200, 0);
                    break;
                }
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("http://localhost:" + PORT + "/tasks");
        server.start();
    }

    public void stop(){
        server.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }


//    public static Gson getGson(){
//        GsonBuilder gsonBuilder = new GsonBuilder();
//        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
//        return gsonBuilder.create();
//    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    public static void main(String[] args) throws IOException, ManagerLoadException {
        final HttpTaskServer server = new HttpTaskServer();
        TaskManager manager = Managers.getDefault();
        server.start();
        Task task3 = new Task("task3", "description3", 0, TaskStatus.NEW,
                LocalDateTime.of(2022, 1, 1, 15, 0), 3);
        manager.createTask(task3);
        System.out.println(task3.getId());
         server.stop();
        final Gson gson = getGson();
        Task task4 = new Task("task3", "description3", 0, TaskStatus.NEW,
                LocalDateTime.of(2022, 1, 1, 15, 0), 3);
        System.out.println(gson.toJson(task4));
        server.stop();
    }
}
