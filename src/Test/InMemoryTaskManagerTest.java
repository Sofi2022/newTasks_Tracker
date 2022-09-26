package Test;

import Exceptions.ManagerLoadException;
import manager.InMemoryTaskManager;
import manager.TasksComparator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class InMemoryTaskManagerTest extends ManagerTest {


    @BeforeEach
    @Override
    void init() throws IOException, ManagerLoadException {
        manager = new InMemoryTaskManager();
        super.init();
        prioritizedTasks = new TreeSet<>(new TasksComparator());
    }

    @Test
    void test() {
        manager = new InMemoryTaskManager();

        assertEquals(0, manager.getTaskList().size(), "задач нет");
        assertEquals(0, manager.getSubtaskList().size(), "подзадач нет");
        assertEquals(0, manager.getEpicList().size(), "эпиков нет");
        assertEquals(0, manager.getHistory().size(), "история пустая");
    }

    @Test
    void searchForCrossesTest() {
        Task crossedTask = new Task("crossedTask", "crossedDescription", 0, TaskStatus.NEW,
                LocalDateTime.of(2020, 1, 1, 2, 0), 15);
        List<Task> tasks = manager.getTaskList();

        assertFalse(tasks.contains(crossedTask), "В списке задач нет новой задачи");
    }

    @Test
    void updateStatusEpicTest() {
        manager.updateStatusEpic(epic);
        assertEquals(TaskStatus.NEW, epic.getStatus(), "пустой список подзадач у эпика");


        Subtask subtaskNew1 = new Subtask("s1", "d1", TaskStatus.NEW, 0,
                0, LocalDateTime.of(2000, 1, 1, 5, 0), 1);
        Subtask subtaskNew2 = new Subtask("s1", "d1", TaskStatus.NEW, 0,
                0, LocalDateTime.of(2000, 2, 1, 5, 0), 1);
        ArrayList<Subtask> testSubtasks = new ArrayList<>();
        Collections.addAll(testSubtasks, subtask, subtaskNew1, subtaskNew2);

        epic.setSubtasks(testSubtasks);
        manager.updateStatusEpic(epic);
        assertEquals(TaskStatus.NEW, epic.getStatus(), "все подзадачи со статусом NEW у эпика");


        subtask.setStatus(TaskStatus.DONE);
        subtaskNew1.setStatus(TaskStatus.DONE);
        subtaskNew2.setStatus(TaskStatus.DONE);
        testSubtasks.clear();
        Collections.addAll(testSubtasks, subtask, subtaskNew1, subtaskNew2);
        epic.setSubtasks(testSubtasks);
        manager.updateStatusEpic(epic);
        assertEquals(TaskStatus.DONE, epic.getStatus(), "все подзадачи со статусом DONE");


        subtask.setStatus(TaskStatus.NEW);
        testSubtasks.clear();
        Collections.addAll(testSubtasks, subtask, subtaskNew1);
        epic.setSubtasks(testSubtasks);
        manager.updateStatusEpic(epic);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "подзадачи со статусом NEW и DONE");


        subtask.setStatus(TaskStatus.IN_PROGRESS);
        subtaskNew1.setStatus(TaskStatus.IN_PROGRESS);
        testSubtasks.clear();
        Collections.addAll(testSubtasks, subtask, subtaskNew1);
        epic.setSubtasks(testSubtasks);
        manager.updateStatusEpic(epic);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "подзадачи со статусом IN_PROGRESS");
    }
}