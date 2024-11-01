package com.example.todolistappbasic;

public class TaskItem {
    private String taskDescription;
    private boolean isCompleted;
    private String dueDate;
    private String notes; // New field for notes

    // Constructor
    public TaskItem(String taskDescription, String dueDate, String notes) {
        this.taskDescription = taskDescription;
        this.isCompleted = false; // Initially, the task is not completed
        this.dueDate = dueDate;
        this.notes = notes; // Initialize notes
    }

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
}
