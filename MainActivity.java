package com.example.todolistapp;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

    private ArrayList<TaskItem> tasks;
    private TaskAdapter taskAdapter;
    private ListView listViewTasks;
    private String selectedDueDate = "";  // To store the selected due date

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize components
        EditText editTextTask = findViewById(R.id.editTextTask);
        EditText editTextNotes = findViewById(R.id.editTextNotes);
        Button buttonAddTask = findViewById(R.id.buttonAddTask);
        Button buttonDueDate = findViewById(R.id.buttonDueDate);  // Button for selecting due date
        Button buttonSort = findViewById(R.id.buttonSort);  // Sort button
        listViewTasks = findViewById(R.id.listViewTasks);
        Switch switchDarkMode = findViewById(R.id.switchDarkMode);  // Switch for dark mode

        // Initialize the tasks list
        tasks = new ArrayList<>();

        // Set up the custom adapter to display tasks in ListView
        taskAdapter = new TaskAdapter(this, tasks);
        listViewTasks.setAdapter(taskAdapter);

        // Load and apply dark mode preference
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean("DarkMode", false);
        toggleDarkMode(isDarkMode);
        switchDarkMode.setChecked(isDarkMode);

        // Due Date button functionality
        buttonDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use Calendar to get the current date
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Create a DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // Handle selected date
                                selectedDueDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                                Toast.makeText(MainActivity.this, "Due Date: " + selectedDueDate, Toast.LENGTH_SHORT).show();
                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });

        // Add Task button functionality
        buttonAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskDescription = editTextTask.getText().toString();
                String notes = editTextNotes.getText().toString();
                if (!taskDescription.isEmpty()) {
                    // Create a new TaskItem with the task description, selected due date, and notes
                    TaskItem taskItem = new TaskItem(taskDescription, selectedDueDate, notes);
                    tasks.add(taskItem);  // Add the task to the list
                    taskAdapter.notifyDataSetChanged();  // Notify the adapter to refresh the ListView
                    editTextTask.setText("");  // Clear the input field
                    editTextNotes.setText("");  // Clear the notes field
                    selectedDueDate = "";  // Reset due date after adding task
                }
            }
        });

        // Sort Tasks button functionality
        buttonSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortTasksByDueDate();  // Call the method to sort tasks
            }
        });

        // Dark Mode toggle functionality
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            toggleDarkMode(isChecked);
            // Save preference
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("DarkMode", isChecked);
            editor.apply();
        });
    }

    // Method to toggle Dark Mode
    public void toggleDarkMode(boolean enableDarkMode) {
        AppCompatDelegate.setDefaultNightMode(
                enableDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    // Method to sort tasks by due date
    public void sortTasksByDueDate() {
        Collections.sort(tasks, new Comparator<TaskItem>() {
            @Override
            public int compare(TaskItem t1, TaskItem t2) {
                // Compare dates in the format "day/month/year"
                return parseDate(t1.getDueDate()).compareTo(parseDate(t2.getDueDate()));
            }
        });

        // Notify the adapter that the data has changed
        taskAdapter.notifyDataSetChanged();
    }

    // Utility method to parse the date string in the format "day/month/year"
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
