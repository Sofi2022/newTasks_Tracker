package Http;

import Client.KVTaskClient;
import Exceptions.ManagerLoadException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.FileBackedTasksManager;
import manager.Managers;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {

    private KVTaskClient client;
    private Gson gson;

    private static final String TASKS_KEY = "tasks";
    private static final String SUBTASKS_KEY = "subtasks";
    private static final String EPICS_KEY = "epics";
    private static final String HISTORY_KEY = "history";


    int port;

    public HttpTaskManager(int port) throws ManagerLoadException {
        this(port, false);
    }

    public HttpTaskManager(int port, boolean isNeedToLoad) throws ManagerLoadException {
        super(null);
        gson = Managers.getGson();
        client = new KVTaskClient(port);
        if(isNeedToLoad){
            load();
        }
    }

    protected void save(){
        String tasksJson = gson.toJson(getTaskList());
        client.put(TASKS_KEY, tasksJson);

        String subtasksJson = gson.toJson(getSubtaskList());
        client.put(SUBTASKS_KEY, subtasksJson);


        String epicsJson = gson.toJson(getEpicList());
        client.put(EPICS_KEY, epicsJson);

        List<Integer> history = getHistory().stream()
                .map(Task::getId)
                .collect(Collectors.toList());

        String historyJson = gson.toJson(history);
        client.put(HISTORY_KEY, historyJson);
    }

    protected void load() throws ManagerLoadException {
        Type tasksType = new TypeToken<List<Task>>(){}.getType();
        String str = client.load(TASKS_KEY);
        List<Task> tasks = gson.fromJson(str, tasksType);
        for(Task task : tasks){
            int id = task.getId();
            this.tasks.put(id, task);
            searchForCrosses(task);
            this.prioritizedTasks.add(task);

            Type subtasksType = new TypeToken<List<Subtask>>()
            {}.getType();
            List<Subtask> subtasks = gson.fromJson(client.load(SUBTASKS_KEY), subtasksType);
            for(Subtask subtask : subtasks){
                int subtaskId = subtask.getId();
                this.subtasks.put(subtaskId, subtask);
                searchForCrosses(subtask);
                this.prioritizedTasks.add(subtask);

                Type epicsType = new TypeToken<List<Epic>>()
                {}.getType();
                List<Epic> epics = gson.fromJson(client.load(EPICS_KEY), epicsType);
                for(Epic epic : epics){
                    int epicId = epic.getId();
                    this.epics.put(epicId, epic);

                    Type historyType = new TypeToken<List<Integer>>(){
                    }.getType();
                    List<Integer> history = gson.fromJson(client.load(HISTORY_KEY), historyType);
                    for(Integer historyId : history){
                        historyManager.add(this.findTask(historyId));
                    }
                }
            }
        }
    }

}