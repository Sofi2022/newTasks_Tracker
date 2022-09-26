package Test;

import manager.FileBackedTasksManager;
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

class FileBackedTasksManagerTest extends ManagerTest {

    @BeforeEach
    @Override
    void init() {
        manager = new FileBackedTasksManager(new File("resources\\test.txt"));
        task = new Task("newTask1", "newDescription", 0, TaskStatus.NEW,
                LocalDateTime.of(2000, 1, 1, 2, 0), 15);
        manager.createTask(task);

        subtask = new Subtask("Собрать коробки", "Расфасовать их по категориям", TaskStatus.NEW, 0,
                0, LocalDateTime.of(2000, 1, 1, 5, 0), 1);
        manager.createSubtask(subtask);

        epic = new Epic("epic1", "EpicDescription1", 0, new ArrayList<>(), TaskStatus.NEW,
                LocalDateTime.of(2000, 1, 1, 7, 0), 2);
        manager.createEpic(epic);

        prioritizedTasks = new TreeSet<>(new TasksComparator());
    }


    @Test
    void saveTest() {
        final List<Task> tasks = new ArrayList<>();
        final List<Subtask> subtaskList = new ArrayList<>();
        manager.deleteTaskById(task.getId());
        manager.deleteSubtaskById(subtask.getId());
        manager.deleteEpicById(epic.getId());

        assertEquals(tasks, manager.getTaskList(), "список задач пуст");
        assertEquals(subtaskList, manager.getSubtaskList(), "список подзадач пуст");
        assertTrue(manager.getTaskList().isEmpty(), "возвращает пустой список задач");
    }


    @Test
    void historyFromStringTest() {
        List<Integer> result = FileBackedTasksManager.historyFromString("1,2");

        assertNotNull(result);
        assertEquals(2, result.size(), "размер истории совпадает");
    }
}