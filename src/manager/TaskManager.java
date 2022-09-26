package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    HistoryManager getHistoryManager();

    //Получение списка задач
    List<Task> getTaskList();

    List<Subtask> getSubtaskList();

    List<Epic> getEpicList();

    //Получение по id
    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);

    //Создание
    Task createTask(Task task);

    Subtask createSubtask(Subtask subtask);

    Epic createEpic(Epic epic);

    //Обновление
    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);


    //Удаление всех задач
    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();

    //Удаление задачи по id
    void deleteTaskById(int id);

    void deleteSubtaskById(int id);

    void deleteEpicById(int id);

    // Установление id
    void setEpicSubtask(int epicId, int id);

    List<Subtask> getEpicSubtasks(Epic epic);

    List<Task> getHistory();

    //Обновление статуса эпика
    void updateStatusEpic(Epic epic);
     List<Task> getPrioritizedTasks();
}

