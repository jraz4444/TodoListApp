package com.example.todolistappbasic;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
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
    private DBHandler dbHandler; // Database handler
    private String selectedDueDate = "";  // To store the selected due date

    private int percentage = 0; // Variable to store the current percentage
    private TextView textViewPercentage; // TextView to display percentage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        initUIComponents();

        // Load and apply dark mode preference
        loadDarkModePreference();

        // Initialize the DBHandler
        dbHandler = new DBHandler(this);

        // Load tasks from the database
        loadTasksFromDatabase();
    }

    private void loadDarkModePreference() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isDarkMode = preferences.getBoolean("DarkMode", false); // Default to false
        toggleDarkMode(isDarkMode);
    }

    private void initUIComponents() {
        EditText editTextTask = findViewById(R.id.editTextTask);
        EditText editTextNotes = findViewById(R.id.editTextNotes);
        Button buttonAddTask = findViewById(R.id.buttonAddTask);
        Button buttonDueDate = findViewById(R.id.buttonDueDate);
        Button buttonSortByDueDate = findViewById(R.id.buttonSort);
        Button buttonSortAlphabetically = findViewById(R.id.buttonAlphabet); // Button for alphabetical sort
        Button buttonImportance = findViewById(R.id.buttonImportance); // Button for importance
        Button buttonSortImportance = findViewById(R.id.buttonImportance1);
        // Initialize percentage display and control buttons
        textViewPercentage = findViewById(R.id.textViewPercentage);
        Button buttonIncrease = findViewById(R.id.buttonIncrement);
        Button buttonDecrease = findViewById(R.id.buttonDecrement);

        listViewTasks = findViewById(R.id.listViewTasks);
        Switch switchDarkMode = findViewById(R.id.switchDarkMode);

        // Set the initial percentage display
        updatePercentageDisplay();

        // Increase button functionality
        buttonIncrease.setOnClickListener(v -> {
            if (percentage < 100) {  // Limit the percentage to 100
                percentage++;
                updatePercentageDisplay();
            }
        });

        // Decrease button functionality
        buttonDecrease.setOnClickListener(v -> {
            if (percentage > 0) {  // Limit the percentage to 0
                percentage--;
                updatePercentageDisplay();
            }
        });

        // Initialize the tasks list
        tasks = new ArrayList<>();
        taskAdapter = new TaskAdapter(this, tasks);
        listViewTasks.setAdapter(taskAdapter);

        // Due Date button functionality
        buttonDueDate.setOnClickListener(v -> showDatePickerDialog());

        // Add Task button functionality
        buttonAddTask.setOnClickListener(v -> addTask(editTextTask, editTextNotes));

        // Sort Tasks by Due Date button functionality
        buttonSortByDueDate.setOnClickListener(v -> sortTasksByDueDate());

        // Sort Tasks Alphabetically button functionality
        buttonSortAlphabetically.setOnClickListener(v -> sortTasksAlphabetically());

        buttonSortImportance.setOnClickListener(v -> sortTasksByImportance());


        // Importance button functionality
        buttonImportance.setOnClickListener(v -> setTaskImportance());



        // Dark Mode toggle functionality
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            toggleDarkMode(isChecked);
            saveDarkModePreference(isChecked);
        });
    }

    private void updatePercentageDisplay() {
        textViewPercentage.setText(percentage + "%");
    }

    private void loadTasksFromDatabase() {
        tasks.clear(); // Clear current task list
        tasks.addAll(dbHandler.getAllTasks()); // Retrieve all tasks from the database
        taskAdapter.notifyDataSetChanged(); // Notify the adapter of data change
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
            TaskItem taskItem = new TaskItem(taskDescription, selectedDueDate, notes, "Medium"); // Default to "Medium" importance
            tasks.add(taskItem);
            taskAdapter.notifyDataSetChanged();
            dbHandler.addTask(taskItem); // Save task to database
            clearInputFields(editTextTask, editTextNotes);
            selectedDueDate = ""; // Reset due date after adding task
        } else {
            Toast.makeText(this, "Task description cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void setTaskImportance() {
        // For simplicity, we're just toggling between "Low", "Medium", and "High"
        String newImportance = "High".equals(tasks.isEmpty() ? "" : tasks.get(tasks.size() - 1).getImportance()) ? "Low" : "High";
        if (!tasks.isEmpty()) {
            TaskItem lastTask = tasks.get(tasks.size() - 1);
            lastTask.setImportance(newImportance);
            taskAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Task importance set to: " + newImportance, Toast.LENGTH_SHORT).show();
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

    private void sortTasksAlphabetically() {
        Collections.sort(tasks, new Comparator<TaskItem>() {
            @Override
            public int compare(TaskItem t1, TaskItem t2) {
                return t1.getTaskDescription().compareToIgnoreCase(t2.getTaskDescription());
            }
        });
        taskAdapter.notifyDataSetChanged();
    }

    public void sortTasksByImportance() {
        Collections.sort(tasks, (t1, t2) -> {
            String importance1 = t1.getImportance() != null ? t1.getImportance() : "Medium"; // Default to "Medium"
            String importance2 = t2.getImportance() != null ? t2.getImportance() : "Medium"; // Default to "Medium"

            // If one of the tasks is "Low", it should stay where it is
            if (importance1.equals("Low") && !importance2.equals("Low")) {
                return 1; // "Low" tasks should be sorted to the end
            } else if (!importance1.equals("Low") && importance2.equals("Low")) {
                return -1; // Non-"Low" tasks should be sorted before "Low" tasks
            }

            // Otherwise, compare the importance values (alphabetical sort)
            return importance1.compareTo(importance2);
        });
        taskAdapter.notifyDataSetChanged();  // Refresh the list view after sorting
    }


    private int getImportanceValue(String importance) {
        switch (importance) {
            case "High":
                return 3;
            case "Low":
                return 1;
            default:
                return 2;  // Default to "Medium" if somehow the importance is invalid
        }
    }
}

