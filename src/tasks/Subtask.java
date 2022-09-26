package tasks;

import manager.TaskTypes;

import java.time.LocalDateTime;

public class Subtask extends Task {

    int epicId;

    /**
     * Создание подзадачи со статусом "NEW"
     *
     * @param name        - название подзадачи
     * @param description - описание подзадачи
     * @param id          - уникальный номер подзадачи
     * @param epicId      - номер Эпика, которому подзадача принадлежит
     */
    public Subtask(String name, String description, int id, int epicId, TaskStatus status, LocalDateTime start
            , int duration){
        super(name, description, id, status, start, duration);
        this.type = TaskTypes.SUBTASK;
        this.epicId = epicId;
    }

    /**
     * Создание подзадачи с указанным статусом
     *
     * @param name        - название подзадачи
     * @param description - описание подзадачи
     * @param status      - новый статус подзадачи
     * @param id          - уникальный номер подзадачи
     * @param epicId      - номер Эпика, которому подзадача принадлежит
     */
    public Subtask(String name, String description, TaskStatus status, int id, int epicId,LocalDateTime start
            , int duration) {
        super(name, description, id, status, start, duration);
        this.epicId = epicId;
        this.type = TaskTypes.SUBTASK;
        this.setStatus(status); //Вместо NEW будет другой статус
    }

    public int getEpicId() {
        return epicId;
    }


    public int setEpicId(int epicId) {
        this.epicId = epicId;
        return epicId;
    }

    @Override

    public String toString(){
     return getId() + "," +  getType() + "," + getName() + "," + getStatus() + "," + getDescription() + "," + epicId + "," +
             startTime + "," + duration;
    }


}

