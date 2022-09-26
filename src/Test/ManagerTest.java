package Test;

import static org.junit.jupiter.api.Assertions.*;

import Exceptions.ManagerLoadException;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

abstract class ManagerTest<T extends TaskManager> {

    protected T manager;
    protected static TreeSet<Task> prioritizedTasks;

    Task task;
    Subtask subtask;
    Epic epic;

    @BeforeEach
    void init() throws IOException, ManagerLoadException {
        task = new Task("Покормить кота", "Насыпать 10г корма", 0, TaskStatus.NEW,
                LocalDateTime.of(2019, 1, 1, 2, 0), 15);
        manager.createTask(task);

        subtask = new Subtask("Собрать коробки", "Расфасовать их по категориям", TaskStatus.NEW, 0,
                0, LocalDateTime.of(2019, 1, 1, 5, 0), 1);
        manager.createSubtask(subtask);

        epic = new Epic("epic1", "EpicDescription1", 0, new ArrayList<>(), TaskStatus.NEW,
                LocalDateTime.of(2019, 1, 1, 7, 0), 1);
        manager.createEpic(epic);
    }


    @Test
    void getTaskListTest() {
        final List<Task> tasks = manager.getTaskList();

        assertNotNull(tasks);
        assertEquals(1, tasks.size(), "одна задача");
        assertEquals(task, tasks.get(0));
    }

    @Test
    void getSubtaskListTest() {
        final List<Subtask> subtasks = manager.getSubtaskList();

        assertNotNull(subtasks);
        assertEquals(1, subtasks.size(), "одна подзадача");
        assertEquals(subtask, subtasks.get(0));
    }

    @Test
    void getEpicListTest() {
        final List<Epic> epics = manager.getEpicList();

        assertNotNull(epics);
        assertEquals(1, epics.size(), "один эпик");
        assertEquals(epic, epics.get(0));
    }

    @Test
    void deleteAllTasksTest() {
        Task task1 = new Task("task1", "description1", 0, TaskStatus.NEW,
                LocalDateTime.of(2018, 1, 1, 2, 0), 15);
        manager.createTask(task1);
        manager.deleteAllTasks();
        final List<Task> tasks = manager.getTaskList();
        boolean isEmpty = tasks.isEmpty();

        assertTrue(isEmpty, "список пуст");
    }

    @Test
    void deleteAllSubtasksTest() {
        Subtask sub1 = new Subtask("sub1", "description1", TaskStatus.NEW, 0,
                0, LocalDateTime.of(2017, 1, 1, 2, 0), 1);
        manager.createSubtask(sub1);
        manager.deleteAllSubtasks();
        final List<Subtask> subtasks = manager.getSubtaskList();
        boolean isEmpty = subtasks.isEmpty();

        assertTrue(isEmpty, "список пуст");
    }

    @Test
    void deleteAllEpicsTest() {
        Epic epic1 = new Epic("Переезд", "Переехать до 12.05", 0, new ArrayList<>(), TaskStatus.NEW,
                LocalDateTime.of(2016, 1, 1, 2, 0), 1);
        manager.createEpic(epic1);
        manager.deleteAllEpics();
        final List<Epic> epics = manager.getEpicList();
        boolean isEmpty = epics.isEmpty();

        assertTrue(isEmpty, "список пуст");
    }

    @Test
    void getTaskByIdTest() {
        final List<Task> tasks = manager.getTaskList();
        Task taskById = manager.getTaskById(task.getId());

        assertNotNull(taskById);
        assertEquals(task, tasks.get(0), "задача найдена");
    }

    @Test
    void getSubtaskByIdTest() {
        final List<Subtask> subtasks = manager.getSubtaskList();
        Subtask subtaskById = manager.getSubtaskById(subtask.getId());

        assertNotNull(subtaskById);
        assertEquals(subtask, subtasks.get(0), "подзадача найдена");
    }

    @Test
    void getEpicByIdTest() {
        final List<Epic> epicList = manager.getEpicList();
        Epic epicById = manager.getEpicById(epic.getId());

        assertNotNull(epicById);
        assertEquals(epic, epicList.get(0), "эпик найдена");
    }

    @Test
    void createTaskTest() {
        final List<Task> tasks = manager.getTaskList();

        assertNotNull(tasks);
        assertEquals(1, tasks.size(), "задача создана");
        assertEquals(task, tasks.get(0));
    }

    @Test
    void createSubtaskTest() {
        final List<Subtask> subtasks = manager.getSubtaskList();

        assertNotNull(subtasks);
        assertEquals(1, subtasks.size(), "подзадача создана");
        assertEquals(subtask, subtasks.get(0));
    }

    @Test
    void createEpicTest() {
        List<Epic> epicList = manager.getEpicList();

        assertNotNull(epicList);
        assertEquals(1, epicList.size(), "эпик создан");
        assertEquals(epic, epicList.get(0));
    }

    @Test
    void updateTaskTest() {
        task.setStatus(TaskStatus.DONE);
        task.setDescription("new description");
        manager.updateTask(task);
        final List<Task> tasks = manager.getTaskList();

        assertNotNull(task);
        assertEquals(task, tasks.get(0), "задача в списке");
        assertEquals(TaskStatus.DONE, task.getStatus(), "задача обновлена");
        assertEquals("new description", task.getDescription(), "задача обновлена");
    }

    @Test
    void updateSubtaskTest() {
        final List<Subtask> subtasks = manager.getSubtaskList();
        subtask.setStatus(TaskStatus.DONE);
        subtask.setDescription("new description");
        manager.updateSubtask(subtask);

        assertNotNull(subtask);
        assertEquals(subtask, subtasks.get(0), "подзадача в списке");
        assertEquals(TaskStatus.DONE, subtask.getStatus(), "подзадача обновлена");
        assertEquals("new description", subtask.getDescription(), "подзадача обновлена");
    }

    @Test
    void updateEpicTest() {
        final List<Epic> epics = manager.getEpicList();
        epic.setStatus(TaskStatus.DONE);
        epic.setName("new epic");
        manager.updateEpic(epic);

        assertNotNull(epic);
        assertEquals(epic, epics.get(0), "эпик в списке");
        assertEquals(TaskStatus.DONE, epic.getStatus(), "эпик обновлен");
        assertEquals("new epic", epic.getName(), "эпик обновлен");
    }

    @Test
    void deleteTaskByIdTest() {
        manager.deleteTaskById(task.getId());
        final List<Task> tasks = manager.getTaskList();

        assertFalse(tasks.contains(task), "задача удалена");
    }

    @Test
    void deleteSubtaskByIdTest() {
        manager.deleteSubtaskById(subtask.getId());
        final List<Subtask> subtasks = manager.getSubtaskList();

        assertFalse(subtasks.contains(subtask), "подзадача удалена");
    }

    @Test
    void deleteEpicByIdTest() {
        manager.deleteEpicById(epic.getId());
        final List<Epic> epics = manager.getEpicList();

        assertFalse(epics.contains(epic), "эпик удален");
    }

    @Test
    void getEpicSubtasksTest() {
        manager.setEpicSubtask(epic.getId(), subtask.getId());
        List<Subtask> epicSubbtasks = manager.getEpicSubtasks(epic);

        assertNotNull(epicSubbtasks);
        assertTrue(epicSubbtasks.contains(subtask), "задача есть в списке");
        assertEquals(1, epicSubbtasks.size(), "список из 2 подзадач");
    }

    @Test
    void setEpicSubtaskTest() {
        final List<Subtask> epicSubtasks = manager.getEpicSubtasks(epic);
        manager.setEpicSubtask(epic.getId(), subtask.getId());

        assertNotNull(epicSubtasks);
        assertTrue(epicSubtasks.contains(subtask), "задача есть в списке");
        assertEquals(1, epicSubtasks.size(), "список из 2 подзадач");
    }

    @Test
    void getHistoryTest() {
        manager.getSubtaskById(subtask.getId());
        manager.getTaskById(task.getId());
        manager.getEpicById(epic.getId());
        List<Task> historyList = manager.getHistory();
        List<Task> TasksList = new ArrayList<>(List.of(
                manager.getSubtaskById(subtask.getId()),
                manager.getTaskById(task.getId()),
                manager.getEpicById(epic.getId())));

        assertNotNull(historyList);
        assertEquals(historyList, TasksList);
    }
}