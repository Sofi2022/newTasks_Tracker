package Test;

import manager.FileBackedTasksManager;
import manager.InMemoryHistoryManager;
import manager.TasksComparator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest extends ManagerTest {


    @BeforeEach
    @Override
    void init() {

        manager = new FileBackedTasksManager(new File("resources\\test1.txt"));

        task = new Task("Покормить кота", "Насыпать 10г корма", 0, TaskStatus.NEW,
                LocalDateTime.of(2020, 1, 1, 2, 0), 15);
        manager.createTask(task);

        subtask = new Subtask("Собрать коробки", "Расфасовать их по категориям", TaskStatus.NEW, 0,
                0, LocalDateTime.of(2020, 1, 1, 5, 0), 1);
        manager.createSubtask(subtask);

        epic = new Epic("epic1", "EpicDescription1", 0, new ArrayList<>(), TaskStatus.NEW,
                LocalDateTime.of(2020, 1, 1, 7, 0), 2);
        manager.createEpic(epic);

        prioritizedTasks = new TreeSet<>( new TasksComparator());
    }


    @Test
    void getHistoryTest() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        assertEquals(0, historyManager.getHistory().size(), "пустая история задач");
        historyManager.add(task);

        assertNotNull(historyManager.getHistory().size(), "список истории не пустой");

        historyManager.add(task);
        assertEquals(2, historyManager.getHistory().size(), "список истории совпадает");
    }

    @Test
    void addTest() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        historyManager.add(task);
        historyManager.add(subtask);
        historyManager.add(epic);

        assertEquals(List.of(task, subtask, epic), historyManager.getHistory(), "история совпадает");
    }

    @Test
    void remove() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        historyManager.add(task);
        historyManager.add(subtask);
        historyManager.add(epic);

        historyManager.remove(task.getId());
        assertEquals(List.of(subtask, epic), historyManager.getHistory(), "удален первый элемент из истории");

        historyManager.add(task);
        historyManager.remove(epic.getId());
        assertEquals(List.of(subtask, task), historyManager.getHistory(), "удален элемент из середины");

        historyManager.add(epic);
        historyManager.remove(epic.getId());
        assertEquals(List.of(subtask, task), historyManager.getHistory(), "удален последний элемент");
    }
}