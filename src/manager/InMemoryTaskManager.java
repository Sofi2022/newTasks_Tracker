package manager;

import java.util.*;

import Exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import static tasks.TaskStatus.NEW;

public class InMemoryTaskManager implements TaskManager {

    private int id = 0;

    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected TreeSet<Task> prioritizedTasks = new TreeSet<>(new TasksComparator()); //Поле было статическим


    protected HistoryManager historyManager = Managers.getHistoryDefault();

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

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

    // Удаление всех задач 2.2
    public void deleteAllTasks() {
        for (Integer key : tasks.keySet()) {
            historyManager.remove(key);
        }
        tasks.clear();
       prioritizedTasks.clear();
    }

    public void deleteAllSubtasks() {
        for (Integer key : subtasks.keySet()) {
            historyManager.remove(key);
        }
        subtasks.clear();
        prioritizedTasks.clear();
    }

    public void deleteAllEpics() {
        for (Integer key : subtasks.keySet()) {
            historyManager.remove(key);
        }
        for (Integer key : epics.keySet()) {
            historyManager.remove(key);
        }
        epics.clear();
    }

    //Получение по id
    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        getHistoryManager().add(task);
        return task;
    }


    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        getHistoryManager().add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        getHistoryManager().add(epic);
        return epic;
    }

    //Создание
    @Override
    public Task createTask(Task task) {
        id++;
        task.setId(id);
        searchForCrosses(task);
        prioritizedTasks.add(task);
        tasks.put(id, task);
        return task;


    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        id++;
        subtask.setId(id);
        searchForCrosses(subtask);
        prioritizedTasks.add(subtask);
        subtasks.put(id, subtask);
        return subtask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        id++;
        epic.setId(id);
        updateStatusEpic(epic);
        epics.put(id, epic);
        return epic;
    }

    //Обновление задачи
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        //searchForCrosses(task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        //searchForCrosses(subtask);
    }

    @Override
    public void updateEpic(Epic epic) {
        updateStatusEpic(epic);
        epics.put(epic.getId(), epic);
    }

    //Удаление задачи по id
    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);


    }

    @Override
    public void deleteSubtaskById(int id) {
            subtasks.remove(id);
            historyManager.remove(id);
    }

    @Override
    public void deleteEpicById(int id) {
            epics.remove(id);
            historyManager.remove(id);
    }

    //Получение списка эпика
    @Override
    public List<Subtask> getEpicSubtasks(Epic epic) {  //Получение списка подзадач опр. эпика
        return epic.getSubtasks();
    }

    @Override
    public void setEpicSubtask(int epicId, int id) {
        Epic epic = epics.get(epicId);
        Subtask subtask = subtasks.get(id);

        subtask.setEpicId(epicId);
        epic.addSubtask(subtask);
        updateStatusEpic(epic);
        updateEpic(epic);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void updateStatusEpic(Epic epic) {
        int countNew = 0;
        int countDone = 0;

        for (Subtask subtask : epic.getSubtasks()) {
            if (subtask.getStatus() == TaskStatus.NEW) {
                countNew++;
            } else if (subtask.getStatus() == TaskStatus.DONE) {
                {
                    countDone++;
                }
            }
            if (countNew == epic.getSubtasks().size()) {
                epic.setStatus(NEW);
            } else if (countDone == epic.getSubtasks().size()) {
                epic.setStatus(TaskStatus.DONE);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public void searchForCrosses(Task task) {
        List<Task> prioritizedTask = getPrioritizedTasks();
        for (Task o : prioritizedTask) {
            if (!(task.getStartTime().isBefore(o.getStartTime()) && task.getEndTime().isBefore(o.getStartTime()) ||
                    task.getStartTime().isAfter(o.getEndTime()) && task.getEndTime().isAfter(o.getEndTime()))) {
                throw new ManagerSaveException("Задача не укладывается во временной отрезок");
            }
        }
    }
}
