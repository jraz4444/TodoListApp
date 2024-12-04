package com.example.todolistappbasic;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDAOUpdate {

    @Insert
    void insert(TaskItem taskItem);

    @Update
    void update(TaskItem taskItem);

    @Delete
    void delete(TaskItem taskItem);

    @Query("SELECT * FROM tasks")
    List<TaskItem> getAllTasks();

    @Query("SELECT * FROM tasks WHERE taskDescription = :description LIMIT 1")
    TaskItem getTaskByDescription(String description);
}
