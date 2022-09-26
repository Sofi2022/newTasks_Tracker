package Test;

    import Exceptions.ManagerLoadException;
    import com.google.gson.Gson;
    import com.google.gson.reflect.TypeToken;
    import Http.HttpTaskServer;
    import manager.InMemoryTaskManager;
    import manager.Managers;
    import manager.TaskManager;
    import org.junit.jupiter.api.AfterEach;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import tasks.Epic;
    import tasks.Subtask;
    import tasks.Task;
    import tasks.TaskStatus;

    import java.io.IOException;
    import java.lang.reflect.Type;
    import java.net.URI;
    import java.net.http.HttpClient;
    import java.net.http.HttpRequest;
    import java.net.http.HttpResponse;
    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;

    import static org.junit.jupiter.api.Assertions.*;
    import static tasks.TaskStatus.NEW;

class HttpTaskServerTest {

        protected HttpTaskServer taskServer;
        protected TaskManager taskManager;
        protected Task task;
        protected Epic epic;
        protected Subtask subtask;
        Gson gson = Managers.getGson();

        @BeforeEach
        void init() throws IOException, ManagerLoadException {
            taskManager = new InMemoryTaskManager();
            taskServer = new HttpTaskServer(taskManager);

            task = new Task("Task", "Task description",0,  NEW, LocalDateTime.now(), 15);
            taskManager.createTask(task);

            subtask = new Subtask("Subtask", "Subtask description", TaskStatus.NEW, 0,
                    0, LocalDateTime.of(2023, 2, 1, 5, 0), 1);
            taskManager.createSubtask(subtask);

            epic = new Epic("Epic", "Epic Description", 0, new ArrayList<>(), TaskStatus.NEW,
                    LocalDateTime.of(2023, 1, 1, 7, 0), 2);
            taskManager.createEpic(epic);

            taskServer.start();
        }

        @AfterEach
        void stop() {
            taskServer.stop();
        }

        @Test
        void getTaskListTest() throws IOException, InterruptedException {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/tasks/task");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            Type taskType = new TypeToken<ArrayList<Task>>() {
            }.getType();

            final List<Task> tasks = gson.fromJson(response.body(), taskType);
            System.out.println("Задачи" + tasks.size());

            assertNotNull(tasks, "Задачи не возвращаются");
            assertEquals(1, tasks.size(), "Не верное количество задач");
            assertEquals(task, tasks.get(0), "Задачи не совпадают");
        }

        @Test
        void getSubtaskListTest() throws IOException, InterruptedException {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/tasks/subtask");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            final List<Subtask> subtasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Subtask>>() {
            }.getType());

            assertNotNull(subtasks, "Подзадачи на возвращаются");
            assertEquals(1, subtasks.size(), "Не верное количество подзадач");
            assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают");
        }

        @Test
        void getEpicListTest() throws IOException, InterruptedException {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/tasks/epic");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            final List<Epic> epics = gson.fromJson(response.body(), new TypeToken<ArrayList<Epic>>() {
            }.getType());

            assertNotNull(epics, "Эпик на возвращаются");
            assertEquals(1, epics.size(), "Не верное количество эпиков");
            assertEquals(epic, epics.get(0), "Эпики не совпадают");
        }

        @Test
        void getEpicSubtasksTest() throws IOException, InterruptedException {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/tasks/subtask/epic?id=" + epic.getId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            final List<Subtask> epicSubtasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Subtask>>() {
            }.getType());

            assertNotNull(epicSubtasks, "Подзадачи на возвращаются");
            assertEquals(1, epicSubtasks.size(), "Не верное количество подзадач");
            assertEquals(subtask, epicSubtasks.get(0), "Подзадачи не совпадают");
        }

        @Test
        void getTaskByIdTest() {
            final Task savedTask = taskManager.getTaskById(task.getId());

            assertNotNull(savedTask, "Задача не найдена");
            assertEquals(task, savedTask, "Задачи не совпадают");
        }

        @Test
        void getSubtaskByIdTest() {
            final Task savedSubtask = taskManager.getSubtaskById(subtask.getId());

            assertNotNull(savedSubtask, "Подзадача не найдена");
            assertEquals(subtask, savedSubtask, "Подзадачи не совпадают");
        }

        @Test
        void getEpicByIdTest() {
            final Task savedEpic = taskManager.getEpicById(epic.getId());

            assertNotNull(savedEpic, "Эпик не найдена");
            assertEquals(epic, savedEpic, "Эпики не совпадают");
        }

        @Test
        void createTaskTest() {
            Task newTask = new Task("Create Task", "New description", 0, NEW,
                    LocalDateTime.of(2023, 4, 1, 2, 0), 15);
            taskManager.createTask(newTask);

            final Task savedTask = taskManager.getTaskById(newTask.getId());

            assertNotNull(savedTask, "Задача не найдена");
            assertEquals(newTask, savedTask, "Задачи не совпадают");

            final List<Task> tasks = taskManager.getTaskList();

            assertNotNull(tasks, "Задачи на возвращаются");
            assertEquals(2, tasks.size(), "Не верное количество задач");
        }

        @Test
        void createEpicTest() {
            Epic newEpic = new Epic("Create epic", "New description", 0, new ArrayList<>(), TaskStatus.NEW,
                    LocalDateTime.of(2023, 5, 1, 7, 0), 1);
            taskManager.createEpic(newEpic);

            final Epic savedEpic = taskManager.getEpicById(newEpic.getId());

            assertNotNull(savedEpic, "Эпик не найдена");
            assertEquals(newEpic, savedEpic, "Эпики не совпадают");

            final List<Epic> epics = taskManager.getEpicList();

            assertNotNull(epics, "Эпик на возвращаются");
            assertEquals(2, epics.size(), "Не верное количество эпиков");
        }

        @Test
        void createSubtaskTest() {
            Subtask newsSubtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                    TaskStatus.NEW, 0, 0, LocalDateTime.of(2023, 5, 1, 5, 0), 1);
            taskManager.createSubtask(newsSubtask);
            final Subtask savedSubtask = taskManager.getSubtaskById(newsSubtask.getId());

            assertNotNull(newsSubtask, "Подзадача не создается");
            assertNotNull(savedSubtask, "Подзадача не найдена");
            assertEquals(newsSubtask, savedSubtask, "Подзадачи не совпадают");

            final List<Subtask> subtasks = taskManager.getSubtaskList();

            assertNotNull(subtasks, "Подзадачи на возвращаются");
            assertEquals(2, subtasks.size(), "Не верное количество подзадач");
        }

        @Test
        void updateTask() {
            task.setStatus(TaskStatus.DONE);
            task.setName("Update updateTask");
            task.setDescription("Update updateTask description");
            taskManager.updateTask(task);


            final Task savedTask = taskManager.getTaskById(task.getId());

            assertNotNull(savedTask, "Задача не найдена");
            assertEquals(task, savedTask, "Задачи не совпадают");
        }

        @Test
        void updateEpic() {
            epic.setName("Update updateEpic");
            epic.setDescription("Update updateEpic description");
            taskManager.updateEpic(epic);

            final Task savedEpic = taskManager.getEpicById(epic.getId());

            assertNotNull(savedEpic, "Эпик не найдена");
            assertEquals(epic, savedEpic, "Эпик не совпадают");
        }

        @Test
        void epicWithEmptySubtasks() {
            Epic epic = new Epic("Create newEpic", "New description1", 0, new ArrayList<>(), TaskStatus.NEW,
                    LocalDateTime.of(2023, 6, 1, 7, 0), 1);
            taskManager.createEpic(epic);

            final Epic savedEpic = taskManager.getEpicById(epic.getId());

            assertNotNull(savedEpic, "Эпик не найден");
            assertEquals(NEW, savedEpic.getStatus(), "Пустой эпик статус");
        }

        @Test
        void epicWithNewSubtasks() {
            Subtask newSubtask =  new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                    TaskStatus.NEW, 0, 0, LocalDateTime.of(2023, 7, 1, 5, 0), 1);
            taskManager.createTask(newSubtask);
            taskManager.setEpicSubtask(epic.getId(), newSubtask.getId());
            taskManager.setEpicSubtask(epic.getId(), subtask.getId());

            final Epic savedEpic = taskManager.getEpicById(epic.getId());

            assertNotNull(savedEpic, "Эпик не найдена");
            assertEquals(NEW, savedEpic.getStatus(), "Пустой эпик статус");
        }

        @Test
        void epicWithInProgressNewSubtasks() {

            Epic newEpic = new Epic("Empty epic", "Test epic description", 0, new ArrayList<>(), TaskStatus.NEW,
                    LocalDateTime.of(2023, 8, 1, 7, 0), 1);
            taskManager.createEpic(epic);
            Subtask subtask1 = new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                    TaskStatus.IN_PROGRESS, 0, 0, LocalDateTime.of(2023, 9, 1, 5, 0), 1);
            taskManager.createSubtask(subtask1);
            Subtask subtask2 = new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                    TaskStatus.IN_PROGRESS, 0, 0, LocalDateTime.of(2023, 10, 1, 5, 0), 1);
            taskManager.createSubtask(subtask2);
            taskManager.setEpicSubtask(newEpic.getId(), subtask1.getId());
            taskManager.setEpicSubtask(newEpic.getId(), subtask2.getId());

            final Epic savedEpic = taskManager.getEpicById(newEpic.getId());

            assertNotNull(savedEpic, "Эпик не найдена");
            assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getStatus(), "Пустой эпик статус");
            assertEquals(subtask1.getDuration() + subtask2.getDuration(), savedEpic.getDuration(), "Длительности эпика сумма длительностей подзадач");
            assertEquals(subtask1.getStartTime(), savedEpic.getStartTime(), "Начало эпика - начало ранней подзадачи");
            assertEquals(subtask2.getEndTime(), savedEpic.getEndTime(), "Завершение эпика - завершение последней подзадачи");
        }

        @Test
        void epicWithDoneSubtasks() {
            //final LocalDateTime endTime = getLastEndTime();
            Epic newEpic = new Epic("Empty epic", "Test epic description", 0, new ArrayList<>(), TaskStatus.NEW,
                    LocalDateTime.of(2023, 11, 1, 7, 0), 1);
            taskManager.createEpic(newEpic);

            Subtask subtask1 = new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                    TaskStatus.DONE, 0, 0, LocalDateTime.of(2023, 12, 1, 5, 0), 1);
            taskManager.createSubtask(subtask1);
            Subtask subtask2 = new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                    TaskStatus.DONE, 0, 0, LocalDateTime.of(2024, 1, 1, 5, 0), 1);
            taskManager.createSubtask(subtask2);
            taskManager.setEpicSubtask(newEpic.getId(), subtask1.getId());
            taskManager.setEpicSubtask(newEpic.getId(), subtask2.getId());

            final Epic savedEpic = taskManager.getEpicById(epic.getId());

            assertNotNull(savedEpic, "Эпик не найден");
            assertEquals(TaskStatus.DONE, savedEpic.getStatus(), "Пустой эпик статус");
        }

        @Test
        void updateSubtask() {
            subtask.setStatus(TaskStatus.IN_PROGRESS);
            subtask.setName("Update updateEpic");
            subtask.setDescription("Update updateEpic description");
            taskManager.updateSubtask(subtask);

            final Subtask savedTask = taskManager.getSubtaskById(subtask.getId());

            assertNotNull(savedTask, "Эпик не найдена");
            assertEquals(subtask, savedTask, "Эпик не совпадают");
        }

        @Test
        void deleteTask() {
            taskManager.deleteTaskById(task.getId());

            final Task savedTask = taskManager.getTaskById(task.getId());
            assertNull(savedTask, "Задачи нет после удаления");

            final List<Task> tasks = taskManager.getTaskList();
            assertNotNull(tasks, "Задачи нет после удаления");
            assertTrue(tasks.isEmpty(), "Задачи нет после удаления");
        }

        @Test
        void deleteEpic() {
            taskManager.deleteEpicById(epic.getId());

            final Epic savedEpic = taskManager.getEpicById(epic.getId());
            assertNull(savedEpic, "Эпика нет после удаления");

            final List<Epic> epics = taskManager.getEpicList();
            assertNotNull(epics, "Эпика нет после удаления");
            assertTrue(epics.isEmpty(), "Эпика нет после удаления");

            final List<Subtask> epicSubtasks = taskManager.getEpicSubtasks(epic);
            assertNull(epicSubtasks, "Подзадач нет после удаления эпика");

            final List<Subtask> subtasks = taskManager.getSubtaskList();
            assertNotNull(subtasks, "Подзадач нет после удаления эпика");
            assertTrue(subtasks.isEmpty(), "Подзадач нет после удаления эпика");
        }

        @Test
        void deleteSubtask() {
            taskManager.deleteSubtaskById(subtask.getId());

            final Subtask savedSubtask = taskManager.getSubtaskById(subtask.getId());
            assertNull(savedSubtask, "Подзадач нет после удаления");

            final List<Subtask> subtasks = taskManager.getSubtaskList();
            assertNotNull(subtasks, "Подзадач нет после удаления");
            assertTrue(subtasks.isEmpty(), "Подзадач нет после удаления");


            final List<Subtask> epicSubtasks = taskManager.getEpicSubtasks(epic);
            assertTrue(epicSubtasks.isEmpty(), "Подзадач нет после удаления");

            final Epic epic = taskManager.getEpicById(subtask.getEpicId());
            assertNotNull(epic, "Эпик остался");
            assertEquals(NEW, epic.getStatus(), "Обновился статус эпика");
        }

        @Test
        void getHistory() {
            List<Task> history = taskManager.getHistory();
            assertNotNull(history, "История возвращается");
            assertTrue(history.isEmpty(), "История пустая");

            taskManager.getTaskById(task.getId());
            history = taskManager.getHistory();
            assertFalse(history.isEmpty(), "История не пустая");
            assertEquals(task, history.get(0), "История вернула последнюю задачу");

            taskManager.getEpicById(epic.getId());
            history = taskManager.getHistory();
            assertEquals(2, history.size(), "История не пустая");
            assertEquals(epic, history.get(1), "История вернула последнюю задачу");

            taskManager.getSubtaskById(subtask.getId());
            history = taskManager.getHistory();
            assertEquals(3, history.size(), "История не пустая");
            assertEquals(subtask, history.get(2), "История вернула последнюю задачу");


            taskManager.deleteTaskById(task.getId());
            history = taskManager.getHistory();
            assertEquals(2, history.size(), "История не пустая");
            assertEquals(epic, history.get(0), "История вернула последнюю задачу");

            taskManager.deleteEpicById(epic.getId());
            history = taskManager.getHistory();
            assertEquals(0, history.size(), "История пустая");

            taskManager.deleteSubtaskById(subtask.getId());
            history = taskManager.getHistory();
            assertEquals(0, history.size(), "История пустая");
        }

        @Test
        void getPrioritizedTasks() throws IOException, InterruptedException {
            HttpClient client = HttpClient.newHttpClient();
            URI url = URI.create("http://localhost:8080/tasks/");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());

            List<Task> tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
            }.getType());
            assertNotNull(tasks, "Задачи в порядке приоритета возвращаются");
            assertEquals(2, tasks.size(), "Все задачи в порядке приоритета кроме эпика");
            assertEquals(task.getId(), tasks.get(0).getId(), "Первая - обычная задача");
            assertEquals(subtask.getId(), tasks.get(1).getId(), "Вторая - подзадача");

            //Task updateTask = new Task(task);
            task.setStartTime(subtask.getEndTime());
            taskManager.updateTask(task);

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            tasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
            }.getType());

            assertNotNull(tasks, "Задачи в порядке приоритета возвращаются");
            assertEquals(2, tasks.size(), "Все задачи в порядке приоритета");
            assertEquals(subtask.getId(), tasks.get(0).getId(), "Первая - подзадача");
            assertEquals(task.getId(), tasks.get(1).getId(), "Второй  - задача");
        }

    }
