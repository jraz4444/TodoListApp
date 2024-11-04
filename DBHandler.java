package com.example.todolistappbasic;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 2; // Incremented version

    // Table name and columns
    private static final String TABLE_TASKS = "tasks";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DUE_DATE = "due_date";
    private static final String COLUMN_NOTES = "notes";
    private static final String COLUMN_COMPLETED = "completed";

    // SQL query to create the table
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_TASKS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                    COLUMN_DUE_DATE + " TEXT, " +
                    COLUMN_NOTES + " TEXT, " +
                    COLUMN_COMPLETED + " INTEGER DEFAULT 0);";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Example: Alter the table to add a new column for priority if needed
            db.execSQL("ALTER TABLE " + TABLE_TASKS + " ADD COLUMN priority INTEGER DEFAULT 0;");
        }
        // Handle more upgrades if you have more versions in the future
    }

    // Method to add a new task
    public void addTask(TaskItem task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DESCRIPTION, task.getTaskDescription());
        values.put(COLUMN_DUE_DATE, task.getDueDate());
        values.put(COLUMN_NOTES, task.getNotes());
        values.put(COLUMN_COMPLETED, task.isCompleted() ? 1 : 0);
        db.insert(TABLE_TASKS, null, values);
        db.close();
    }

    // Method to get all tasks
    @SuppressLint("Range")
    public ArrayList<TaskItem> getAllTasks() {
        ArrayList<TaskItem> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                TaskItem task = new TaskItem();
                task.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID))); // Set the ID here
                task.setTaskDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
                task.setDueDate(cursor.getString(cursor.getColumnIndex(COLUMN_DUE_DATE)));
                task.setNotes(cursor.getString(cursor.getColumnIndex(COLUMN_NOTES)));
                task.setCompleted(cursor.getInt(cursor.getColumnIndex(COLUMN_COMPLETED)) == 1);
                tasks.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tasks;
    }
    // Method to update a task
    // Method to update a task
    public void updateTask(TaskItem task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Check if the original task description exists in the database
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TASKS + " WHERE " + COLUMN_ID + "=?", new String[]{String.valueOf(task.getId())});

        if (cursor.moveToFirst()) {
            // If the task exists, update the values
            values.put(COLUMN_DESCRIPTION, task.getTaskDescription());
            values.put(COLUMN_DUE_DATE, task.getDueDate());
            values.put(COLUMN_NOTES, task.getNotes());
            values.put(COLUMN_COMPLETED, task.isCompleted() ? 1 : 0);

            // Update the task in the database
            db.update(TABLE_TASKS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(task.getId())});
        }

        cursor.close(); // Close the cursor to avoid memory leaks
        db.close(); // Close the database
    }


    // Method to delete a task
    public void deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, COLUMN_ID + " = ?", new String[]{String.valueOf(taskId)});
        db.close();
    }
}
