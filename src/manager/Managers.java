package manager;

import Exceptions.ManagerLoadException;
import Http.HttpTaskManager;
import Http.KVServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.time.LocalDateTime;

public class Managers {


    public static TaskManager getDefault() throws ManagerLoadException {
       // return new FileBackedTasksManager(new File("resources\\task.csv"));
        return new HttpTaskManager(KVServer.PORT);
    }

    public static HistoryManager getHistoryDefault() {

        return new InMemoryHistoryManager();
    }

    public static KVServer getDefaultKVServer() throws IOException {
        KVServer kvServer = new KVServer();
        kvServer.start();
        return kvServer;
    }

    public static Gson getGson(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        return gsonBuilder.create();
    }

}
