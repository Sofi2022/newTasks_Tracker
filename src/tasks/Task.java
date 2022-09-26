package tasks;

import manager.TaskTypes;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private TaskStatus status = TaskStatus.NEW;
    protected TaskTypes type = TaskTypes.TASK;

    protected LocalDateTime startTime;
    protected LocalDateTime endTime;
    protected int duration;


    public Task() {
    }

    public Task(String name, String description, int id, TaskStatus status, LocalDateTime startTime
            , int duration) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = startTime.plusMinutes(duration);
    }


    public Task(String name, String description, int id, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }


    public Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) && Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s", id, type, name, status, description, startTime, duration);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskTypes getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime(){
        return startTime.plusMinutes(duration);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}

