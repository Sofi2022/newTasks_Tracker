package manager;

import Exceptions.ManagerLoadException;
import Exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.String.valueOf;


public class FileBackedTasksManager extends InMemoryTaskManager {

    private static File file;

    public FileBackedTasksManager(File file) {
        this(file, false);
    }

    public FileBackedTasksManager(File file, boolean isLoad) {
        this.file = file;
        if (isLoad == true) {
            load();
        }
    }

    //ЗАДАЧИ

    //Создание задач
    @Override
    public Task createTask(Task task) {
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic;
    }

    //Получение списка задач
    @Override
    public List<Task> getTaskList() {  //Получение списка задач 2.1
        return List.copyOf(tasks.values());
    }

    @Override
    public List<Subtask> getSubtaskList() {
        return List.copyOf(subtasks.values());
    }

    @Override
    public List<Epic> getEpicList() {
        return List.copyOf(epics.values());
    }


    //Удаление задач
    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }


    //Удаление задач по id
    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }


    //Получение задачи по id
    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    //Обновление задач
    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void setEpicSubtask(int epicId, int id) {
        super.setEpicSubtask(epicId, id);
        save();
    }

    //Обновление статуса
    @Override
    public void updateStatusEpic(Epic epic) {
        super.updateStatusEpic(epic);
        save();
    }


    private static Task fromString(String value) {
        final String[] string = value.split(",");

        TaskTypes type = TaskTypes.valueOf(valueOf(string[1]));
        switch (type) {
            case TASK:
                return new Task(string[2], string[4], Integer.parseInt(string[0]), TaskStatus.valueOf(string[3]),
                        LocalDateTime.parse(string[5]),
                        Integer.parseInt(string[6]));
            case SUBTASK:
                return new Subtask(string[2], string[4], TaskStatus.valueOf(string[3]), Integer.parseInt(string[0]),
                        Integer.parseInt(string[5]), LocalDateTime.parse(string[6]), Integer.parseInt(string[7]));
            case EPIC:
                return new Epic(string[2], string[4], Integer.parseInt(string[0]), LocalDateTime.parse(string[5]),
                        Integer.parseInt(string[6]));

        }
        return null;
    }

    private static String toString(HistoryManager manager) {
        StringBuilder br = new StringBuilder();
        for (Task task : manager.getHistory()) {
            br.append(task.getId());
            br.append(" , ");
        }
        return br.toString();
    }

    //создание истории из строки
    public static List<Integer> historyFromString(String value) {
        final String[] ids = value.split(",");
        List<Integer> history = new ArrayList<>();
        for (String v : ids) {
            history.add(Integer.valueOf(v));
        }
        return history;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.append(String.join(",", List.of("id", "type", "name", "status", "description", "epic",
                    "start", "duration")));
            writer.newLine();

            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                writer.append(entry.getValue().toString());
                writer.newLine();
            }
            for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
                writer.append(entry.getValue().toString());
                writer.newLine();
            }
            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                writer.append(entry.getValue().toString());
                writer.newLine();
            }
            writer.newLine();

            List<String> ids = new ArrayList<>();
            for (Task task : historyManager.getHistory()) {
                ids.add(String.valueOf(task.getId()));
            }
            writer.append(String.join(",", ids));
        } catch (IOException e) {
            throw new ManagerSaveException("При записи в файл произошла ошибка");
        }
    }


    // восстановление из файла
    private void load() {
        try (final BufferedReader read = new BufferedReader(new FileReader(file))) {
            read.readLine();// пропускаем заголовок
            while (true) {
                String line = read.readLine();
                if (!line.isEmpty()) {
                    final String[] string = line.split(",");
                    TaskTypes type = TaskTypes.valueOf(valueOf(string[1]));
                    switch (type) {
                        case TASK:
                            tasks.put(Integer.parseInt(string[0]), new Task(string[2], string[4],
                                    Integer.parseInt(string[0]), TaskStatus.valueOf(string[3]),
                                    LocalDateTime.parse(string[5]), Integer.parseInt(string[6])));
                            break;
                        case SUBTASK:
                            subtasks.put(Integer.parseInt(string[0]), new Subtask(string[2], string[4],
                                    TaskStatus.valueOf(string[3]), Integer.parseInt(string[0]),
                                    Integer.parseInt(string[5]), LocalDateTime.parse(string[6]),
                                    Integer.parseInt(string[7])));
                            break;
                        case EPIC:
                            epics.put(Integer.parseInt(string[0]), new Epic(string[2], string[4],
                                    Integer.parseInt(string[0]), LocalDateTime.parse(string[5]),
                                    Integer.parseInt(string[6])));
                            if (line.isEmpty()) {
                                historyFromString(line);
                                break;
                            }
                    }
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("При чтении файла произошла ошибка");
        }
    }

    //восстанавливает данные менеджера из файла при запуске программы.
    public static FileBackedTasksManager loadFromFile(File file) {
        final FileBackedTasksManager manager = new FileBackedTasksManager(file, true);
        return manager;
    }

    protected Task findTask(Integer id){
        final Task task = tasks.get(id);
        if (task != null) {
            return task;
        }

        final Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            return subtask;
        }

        return epics.get(id);
    }

    public void main(String[] args) throws ManagerLoadException {

        FileBackedTasksManager fileManager = (FileBackedTasksManager) Managers.getDefault();
        Task task2 = new Task("task2", "description1", 0, TaskStatus.NEW,
                LocalDateTime.of(2022, 1, 1, 10, 0, 1), 1);
        fileManager.createTask(task2);

        ArrayList<Subtask> subtasksList = new ArrayList<>();
        Subtask subtask1 = new Subtask("subtask1", "subDescription1", TaskStatus.NEW, 0,
                0, LocalDateTime.of(2022, 1, 1, 8, 0), 1);
        fileManager.createSubtask(subtask1);
        subtasksList.add(subtask1);

        Task task3 = new Task("task3", "description3", 0, TaskStatus.NEW,
                LocalDateTime.of(2022, 1, 1, 15, 0), 3);
        fileManager.createTask(task3);

        Epic epic = new Epic("epic1", "EpicDescription1", 0, subtasksList, TaskStatus.NEW,
                LocalDateTime.now(), 1);
        fileManager.createEpic(epic);
        fileManager.setEpicSubtask(epic.getId(), subtask1.getId());

        fileManager.getEpicById(epic.getId());

        System.out.println(fileManager.getHistory());

        for (Task t : prioritizedTasks) {
            System.out.println("sorted" + t);
        }

        FileBackedTasksManager newManager = FileBackedTasksManager.loadFromFile(file);

        System.out.println("задачи " + newManager.getTaskList());
    }
}



