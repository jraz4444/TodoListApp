package com.example.todolistappbasic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.example.todolistapp.TaskItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class TaskAdapter extends ArrayAdapter<TaskItem> {

    private ArrayList<TaskItem> tasks; // Keep a reference to the task list
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US); // Define date format

    public TaskAdapter(@NonNull Context context, ArrayList<TaskItem> tasks) {
        super(context, 0, tasks);
        this.tasks = tasks; // Initialize the task list
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get the task item at this position
        TaskItem taskItem = getItem(position);

        // Inflate the task_item layout if it's not already inflated
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_item, parent, false);
        }

        // Get the views from task_item.xml
        CheckBox checkBoxComplete = convertView.findViewById(R.id.checkBoxComplete);
        TextView textViewTaskDescription = convertView.findViewById(R.id.textViewTaskDescription);
        TextView textViewDueDate = convertView.findViewById(R.id.textViewDueDate);
        TextView textViewNotes = convertView.findViewById(R.id.textViewNotes);
        Button buttonMoveUp = convertView.findViewById(R.id.buttonMoveUp);
        Button buttonMoveDown = convertView.findViewById(R.id.buttonMoveDown);
        Button buttonEdit = convertView.findViewById(R.id.buttonEdit); // Edit button
        Button buttonDelete = convertView.findViewById(R.id.buttonDelete); // Delete button

        // Set the task description, due date, and notes
        textViewTaskDescription.setText(taskItem.getTaskDescription());
        textViewDueDate.setText(taskItem.getDueDate());
        textViewNotes.setText(taskItem.getNotes());

        // Set the checkbox state based on whether the task is completed
        checkBoxComplete.setChecked(taskItem.isCompleted());

        // Handle checkbox change event
        checkBoxComplete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            taskItem.setCompleted(isChecked);
            Toast.makeText(getContext(), isChecked ? "Task completed!" : "Task incomplete", Toast.LENGTH_SHORT).show();
        });

        // Handle "Move Up" button click
        buttonMoveUp.setOnClickListener(v -> {
            if (position > 0) { // Only move up if it's not the first item
                // Swap the task with the one above it
                Collections.swap(tasks, position, position - 1);
                notifyDataSetChanged(); // Notify the adapter of the change
            }
        });

        // Handle "Move Down" button click
        buttonMoveDown.setOnClickListener(v -> {
            if (position < tasks.size() - 1) { // Only move down if it's not the last item
                // Swap the task with the one below it
                Collections.swap(tasks, position, position + 1);
                notifyDataSetChanged(); // Notify the adapter of the change
            }
        });

        // Handle "Edit" button click
        buttonEdit.setOnClickListener(v -> {
            // Inflate a dialog layout to enter updated task details
            View editView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_task, null);
            EditText editDescription = editView.findViewById(R.id.editTaskDescription);
            EditText editDueDate = editView.findViewById(R.id.editDueDate);
            EditText editNotes = editView.findViewById(R.id.editNotes);

            // Set current values in the edit fields
            editDescription.setText(taskItem.getTaskDescription());
            editDueDate.setText(taskItem.getDueDate());
            editNotes.setText(taskItem.getNotes());

            // Build an AlertDialog for editing task
            new AlertDialog.Builder(getContext())
                    .setTitle("Edit Task")
                    .setView(editView)
                    .setPositiveButton("Save", (dialog, which) -> {
                        // Update the task with new details
                        taskItem.setTaskDescription(editDescription.getText().toString());
                        taskItem.setDueDate(editDueDate.getText().toString());
                        taskItem.setNotes(editNotes.getText().toString());
                        notifyDataSetChanged(); // Refresh the list
                        Toast.makeText(getContext(), "Task updated", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Handle "Delete" button click
        buttonDelete.setOnClickListener(v -> {
            // Confirm deletion
            new AlertDialog.Builder(getContext())
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        tasks.remove(position); // Remove the task from the list
                        notifyDataSetChanged(); // Notify the adapter of the change
                        Toast.makeText(getContext(), "Task deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        return convertView;
    }

    // Method to sort tasks by due date
    public void sortTasksByDueDate() {
        Collections.sort(tasks, new Comparator<TaskItem>() {
            @Override
            public int compare(TaskItem t1, TaskItem t2) {
                // Parse the due dates
                try {
                    Date date1 = dateFormat.parse(t1.getDueDate());
                    Date date2 = dateFormat.parse(t2.getDueDate());

                    // Compare dates
                    if (date1 != null && date2 != null) {
                        return date1.compareTo(date2);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0; // In case of parsing error, treat dates as equal
            }
        });

        // Notify the adapter to update the list view
        notifyDataSetChanged();
    }
}
