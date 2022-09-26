package tasks;

import manager.TaskTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Epic extends Task {

    private List<Subtask> subtasksList;
    private LocalDateTime endTime;


    public Epic(String name, String description, int id, ArrayList<Subtask> subtasks, TaskStatus status,
                LocalDateTime start, int duration) {
        super(name, description, id, status, start, duration);
        this.type = TaskTypes.EPIC;
        this.subtasksList = subtasks;

        int sum = 0;
        for(Subtask sub : subtasks){
            sum += sub.getDuration();
        }
        this.duration= sum;
        this.endTime = this.getEpicEndTime(subtasks);
    }
    public Epic(String name, String description, int id, LocalDateTime start,
                int duration){
        super(name, description, id);
        this.type = TaskTypes.EPIC;
        subtasksList = new ArrayList<>();
    }

    public List<Subtask> getSubtasks() {
        return subtasksList;
    }

    public void setSubtasks(List<Subtask> subtasks) {
        this.subtasksList = subtasks;
    }

    public void addSubtask(Subtask subtask) {
        this.subtasksList.add(subtask);
    }

    public LocalDateTime getEpicEndTime(ArrayList<Subtask> subtasks){
        int subtasksDuration = 0;
        for(Subtask sub : subtasks){
            subtasksDuration += sub.getDuration();
        }
        return startTime.plusMinutes(subtasksDuration);
    }
}

