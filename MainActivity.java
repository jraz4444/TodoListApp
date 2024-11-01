package com.example.todolistappbasic;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private ArrayList<TaskItem> tasks; // List to hold TaskItems
    private TaskAdapter taskAdapter; // Adapter to bind tasks to ListView
    private ListView listViewTasks; // ListView for displaying tasks
    private String selectedDueDate = "";  // To store the selected due date

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        initUIComponents();

        // Load and apply dark mode preference
        applyDarkModePreference();
    }

    private void initUIComponents() {
        EditText editTextTask = findViewById(R.id.editTextTask);
        EditText editTextNotes = findViewById(R.id.editTextNotes);
        Button buttonAddTask = findViewById(R.id.buttonAddTask);
        Button buttonDueDate = findViewById(R.id.buttonDueDate);
        Button buttonSort = findViewById(R.id.buttonSort);
        listViewTasks = findViewById(R.id.listViewTasks);
        Switch switchDarkMode = findViewById(R.id.switchDarkMode);

        // Initialize the tasks list
        tasks = new ArrayList<>();
        taskAdapter = new TaskAdapter(this, tasks);
        listViewTasks.setAdapter(taskAdapter);

        // Due Date button functionality
        buttonDueDate.setOnClickListener(v -> showDatePickerDialog());

        // Add Task button functionality
        buttonAddTask.setOnClickListener(v -> addTask(editTextTask, editTextNotes));

        // Sort Tasks button functionality
        buttonSort.setOnClickListener(v -> sortTasksByDueDate());

        // Dark Mode toggle functionality
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            toggleDarkMode(isChecked);
            saveDarkModePreference(isChecked);
        });
    }

    private void applyDarkModePreference() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("DarkMode", false);
        toggleDarkMode(isDarkMode);
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                MainActivity.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDueDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    Toast.makeText(MainActivity.this, "Due Date: " + selectedDueDate, Toast.LENGTH_SHORT).show();
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void addTask(EditText editTextTask, EditText editTextNotes) {
        String taskDescription = editTextTask.getText().toString();
        String notes = editTextNotes.getText().toString();

        if (!taskDescription.isEmpty()) {
            TaskItem taskItem = new TaskItem(taskDescription, selectedDueDate, notes);
            tasks.add(taskItem);
            taskAdapter.notifyDataSetChanged();
            clearInputFields(editTextTask, editTextNotes);
            selectedDueDate = ""; // Reset due date after adding task
        } else {
            Toast.makeText(this, "Task description cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputFields(EditText editTextTask, EditText editTextNotes) {
        editTextTask.setText("");
        editTextNotes.setText("");
    }

    private void saveDarkModePreference(boolean isChecked) {
        SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
        editor.putBoolean("DarkMode", isChecked);
        editor.apply();
    }

    private void toggleDarkMode(boolean enableDarkMode) {
        AppCompatDelegate.setDefaultNightMode(
                enableDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    private void sortTasksByDueDate() {
        Collections.sort(tasks, new Comparator<TaskItem>() {
            @Override
            public int compare(TaskItem t1, TaskItem t2) {
                return parseDate(t1.getDueDate()).compareTo(parseDate(t2.getDueDate()));
            }
        });
        taskAdapter.notifyDataSetChanged();
    }

    private Calendar parseDate(String dateString) {
        String[] parts = dateString.split("/");
        Calendar calendar = Calendar.getInstance();
        if (parts.length == 3) {
            int day = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]) - 1;  // Month is 0-based in Calendar
            int year = Integer.parseInt(parts[2]);
            calendar.set(year, month, day);
        }
        return calendar;
    }
}

