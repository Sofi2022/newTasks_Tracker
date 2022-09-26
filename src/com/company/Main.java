package com.company;


import Exceptions.ManagerLoadException;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;


public class Main {

    public static void main(String[] args) throws ManagerLoadException {


        TaskManager manager = Managers.getDefault();

        Task task = new Task("Покормить кота", "Насыпать 10г корма", 0, TaskStatus.NEW, LocalDateTime.now(), 1);
        manager.createTask(task);
        System.out.println(task);

        Task task1 = new Task("task1", "description1", 0, TaskStatus.NEW, LocalDateTime.now(), 1);
        manager.createTask(task1);

        Subtask subtask = new Subtask("Собрать коробки", "Расфасовать их по категориям", 0, 0,
                TaskStatus.NEW, null, 1);
        manager.createSubtask(subtask);
        ArrayList<Subtask> test = new ArrayList<>();


        Epic epic = new Epic("Переезд", "Переехать до 12.05", 0, test, TaskStatus.NEW, null, 1);
        manager.createEpic(epic);


        Subtask subtask1 = new Subtask("Купить новую мебель", "Сделать замеры", TaskStatus.NEW, 0,
                0, LocalDateTime.of(2000, 1, 1, 0, 0), 1);
        manager.createSubtask(subtask1);
        System.out.println(subtask1.toString());
        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subtask1);
        test.add(subtask1);
        System.out.println(subtask1);


        Subtask subtask2 = new Subtask("Поклеить новые обои", "Снять старые, съездить за новыми",
                0, 0,TaskStatus.NEW, null, 1);
        manager.createSubtask(subtask2);
        test.add(subtask2);

        Epic epic3 = new Epic("Ремнот", "Начать ремонтировать кухню", 0, test, TaskStatus.NEW,
                null, 1);
        manager.createEpic(epic3);
        manager.setEpicSubtask(epic3.getId(), subtask.getId());
        manager.setEpicSubtask(epic3.getId(), subtask1.getId());
        manager.setEpicSubtask(epic3.getId(), subtask2.getId());

        manager.updateEpic(epic3);


        manager.getTaskById(task.getId());
        manager.getTaskById(task.getId());
        manager.getTaskById(task1.getId());
        manager.getEpicById(epic3.getId());

        manager.getHistory().forEach(System.out::println);
        System.out.println(manager.getHistory());
    }
}


