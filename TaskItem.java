package com.example.todolistappbasic;

public class TaskItem {
    private String taskDescription;
    private boolean isCompleted;
    private String dueDate;
    private String notes;
    private int id;
    private String importance;


    // Constructor with completed status
    public TaskItem(String taskDescription, String dueDate, String notes, String medium) {
        this.taskDescription = taskDescription;
        this.isCompleted = isCompleted;
        this.dueDate = dueDate;
        this.notes = notes;
        this.importance = importance;
    }

    // Default constructor
    public TaskItem() {
    }

    // Getters and Setters
    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getImportance() {
        return importance;
    }

    public void setImportance(String importance) {
        this.importance = importance;
    }
}
