package com.example.todolistappbasic;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class TaskAdapter extends ArrayAdapter<TaskItem> {

    private final ArrayList<TaskItem> tasks; // Reference to the task list
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US); // Date format

    public TaskAdapter(@NonNull Context context, ArrayList<TaskItem> tasks) {
        super(context, 0, tasks);
        this.tasks = tasks; // Initialize task list
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TaskItem taskItem = getItem(position); // Get task at position

        // Inflate the task_item layout if not already done
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_item, parent, false);
        }

        // Initialize views
        CheckBox checkBoxComplete = convertView.findViewById(R.id.checkBoxComplete);
        TextView textViewTaskDescription = convertView.findViewById(R.id.textViewTaskDescription);
        TextView textViewDueDate = convertView.findViewById(R.id.textViewDueDate);
        TextView textViewNotes = convertView.findViewById(R.id.textViewNotes);
        TextView textViewImportance = convertView.findViewById(R.id.textViewImportance); // Importance display
        Button buttonMoveUp = convertView.findViewById(R.id.buttonMoveUp);
        Button buttonMoveDown = convertView.findViewById(R.id.buttonMoveDown);
        Button buttonEdit = convertView.findViewById(R.id.buttonEdit);
        Button buttonDelete = convertView.findViewById(R.id.buttonDelete);

        // Set task details
        textViewTaskDescription.setText(taskItem.getTaskDescription());
        textViewDueDate.setText(taskItem.getDueDate());
        textViewNotes.setText(taskItem.getNotes());

        // Set importance level with color coding
        String importanceLevel = taskItem.getImportance();
        if (importanceLevel != null) {
            if (importanceLevel.equalsIgnoreCase("high")) {
                textViewImportance.setText("High");
                textViewImportance.setTextColor(ContextCompat.getColor(getContext(), R.color.high_importance)); // Assumes R.color.high_importance exists
            } else if (importanceLevel.equalsIgnoreCase("low")) {
                textViewImportance.setText("Low");
                textViewImportance.setTextColor(ContextCompat.getColor(getContext(), R.color.low_importance)); // Assumes R.color.low_importance exists
            } else {
                textViewImportance.setText(importanceLevel); // Display other values as default
                textViewImportance.setTextColor(ContextCompat.getColor(getContext(), R.color.default_importance)); // Assumes R.color.default_importance exists
            }
        }

        // Set checkbox state based on task completion
        checkBoxComplete.setChecked(taskItem.isCompleted());
        checkBoxComplete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            taskItem.setCompleted(isChecked);
            Toast.makeText(getContext(), isChecked ? "Task completed!" : "Task incomplete", Toast.LENGTH_SHORT).show();
        });

        // "Move Up" functionality
        buttonMoveUp.setOnClickListener(v -> {
            if (position > 0) {
                Collections.swap(tasks, position, position - 1);
                notifyDataSetChanged();
            }
        });

        // "Move Down" functionality
        buttonMoveDown.setOnClickListener(v -> {
            if (position < tasks.size() - 1) {
                Collections.swap(tasks, position, position + 1);
                notifyDataSetChanged();
            }
        });

        // "Edit" functionality
        buttonEdit.setOnClickListener(v -> {
            View editView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_task, null);
            EditText editDescription = editView.findViewById(R.id.editTaskDescription);
            EditText editDueDate = editView.findViewById(R.id.editDueDate);
            EditText editNotes = editView.findViewById(R.id.editNotes);
            EditText editImportance = editView.findViewById(R.id.editImportance); // Field for importance

            // Set existing values in edit fields
            editDescription.setText(taskItem.getTaskDescription());
            editDueDate.setText(taskItem.getDueDate());
            editNotes.setText(taskItem.getNotes());
            editImportance.setText(taskItem.getImportance());

            // Build and show edit dialog
            new AlertDialog.Builder(getContext())
                    .setTitle("Edit Task")
                    .setView(editView)
                    .setPositiveButton("Save", (dialog, which) -> {
                        // Update task details
                        taskItem.setTaskDescription(editDescription.getText().toString());
                        taskItem.setDueDate(editDueDate.getText().toString());
                        taskItem.setNotes(editNotes.getText().toString());
                        taskItem.setImportance(editImportance.getText().toString());
                        notifyDataSetChanged();
                        Toast.makeText(getContext(), "Task updated", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // "Delete" functionality
        buttonDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        tasks.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(getContext(), "Task deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        return convertView;
    }

    // Sort tasks by due date
    public void sortTasksByDueDate() {
        Collections.sort(tasks, (t1, t2) -> {
            try {
                Date date1 = dateFormat.parse(t1.getDueDate());
                Date date2 = dateFormat.parse(t2.getDueDate());
                return date1 != null && date2 != null ? date1.compareTo(date2) : 0;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        });
        notifyDataSetChanged();
    }
}

