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

    private final ArrayList<TaskItem> tasks;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private final Context context;

    // ViewHolder pattern to avoid repeated findViewById calls
    private static class ViewHolder {
        CheckBox checkBoxComplete;
        TextView textViewTaskDescription;
        TextView textViewDueDate;
        TextView textViewNotes;
        TextView textViewImportance;
        Button buttonMoveUp;
        Button buttonMoveDown;
        Button buttonEdit;
        Button buttonDelete;
        TextView textViewPercentage;
    }

    public TaskAdapter(@NonNull Context context, ArrayList<TaskItem> tasks) {
        super(context, 0, tasks);
        this.tasks = tasks;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TaskItem taskItem = getItem(position);

        // Using ViewHolder to optimize performance
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.checkBoxComplete = convertView.findViewById(R.id.checkBoxComplete);
            viewHolder.textViewTaskDescription = convertView.findViewById(R.id.textViewTaskDescription);
            viewHolder.textViewDueDate = convertView.findViewById(R.id.textViewDueDate);
            viewHolder.textViewNotes = convertView.findViewById(R.id.textViewNotes);
            viewHolder.textViewImportance = convertView.findViewById(R.id.textViewImportance);
            viewHolder.buttonMoveUp = convertView.findViewById(R.id.buttonMoveUp);
            viewHolder.buttonMoveDown = convertView.findViewById(R.id.buttonMoveDown);
            viewHolder.buttonEdit = convertView.findViewById(R.id.buttonEdit);
            viewHolder.buttonDelete = convertView.findViewById(R.id.buttonDelete);
            viewHolder.textViewPercentage = convertView.findViewById(R.id.textViewPercentage);

            convertView.setTag(viewHolder); // Save ViewHolder in the convertView
        } else {
            viewHolder = (ViewHolder) convertView.getTag(); // Reuse ViewHolder
        }

        // Set task details in the views
        if (taskItem != null) {
            // Set the task description
            viewHolder.textViewTaskDescription.setText(taskItem.getTaskDescription());

            // Handle due date safely
            String dueDate = taskItem.getDueDate();
            if (dueDate != null && !dueDate.isEmpty()) {
                viewHolder.textViewDueDate.setText(dueDate);
            } else {
                viewHolder.textViewDueDate.setText("No due date");
            }

            // Handle notes safely
            String notes = taskItem.getNotes();
            viewHolder.textViewNotes.setText(notes != null ? notes : "No notes");

            // Handle importance
            String importance = taskItem.getImportance();
            if (importance != null) {
                switch (importance.toLowerCase()) {
                    case "high":
                        viewHolder.textViewImportance.setText("High");
                        viewHolder.textViewImportance.setTextColor(ContextCompat.getColor(getContext(), R.color.high_importance));
                        break;
                    case "low":
                        viewHolder.textViewImportance.setText("Low");
                        viewHolder.textViewImportance.setTextColor(ContextCompat.getColor(getContext(), R.color.low_importance));
                        break;
                    default:
                        viewHolder.textViewImportance.setText("Medium");
                        viewHolder.textViewImportance.setTextColor(ContextCompat.getColor(getContext(), R.color.medium_importance));
                        break;
                }
            } else {
                viewHolder.textViewImportance.setText("Medium");
                viewHolder.textViewImportance.setTextColor(ContextCompat.getColor(getContext(), R.color.medium_importance));
            }

            // Set percentage
            int percentage = taskItem.getPercentage();
            viewHolder.textViewPercentage.setText(percentage + "%");

            // Set checkbox based on completion status
            viewHolder.checkBoxComplete.setChecked(taskItem.isCompleted());
            viewHolder.checkBoxComplete.setOnCheckedChangeListener((buttonView, isChecked) -> {
                taskItem.setCompleted(isChecked);
                Toast.makeText(getContext(), isChecked ? "Task completed!" : "Task incomplete", Toast.LENGTH_SHORT).show();
            });

            // Move Up button functionality
            viewHolder.buttonMoveUp.setOnClickListener(v -> {
                if (position > 0) {
                    Collections.swap(tasks, position, position - 1);
                    notifyDataSetChanged();
                }
            });

            // Move Down button functionality
            viewHolder.buttonMoveDown.setOnClickListener(v -> {
                if (position < tasks.size() - 1) {
                    Collections.swap(tasks, position, position + 1);
                    notifyDataSetChanged();
                }
            });

            // Edit button functionality
            viewHolder.buttonEdit.setOnClickListener(v -> {
                View editView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_task, null);
                EditText editDescription = editView.findViewById(R.id.editTaskDescription);
                EditText editDueDate = editView.findViewById(R.id.editDueDate);
                EditText editNotes = editView.findViewById(R.id.editNotes);
                EditText editImportance = editView.findViewById(R.id.editImportance);

                // Populate edit fields with current task values
                editDescription.setText(taskItem.getTaskDescription());
                editDueDate.setText(taskItem.getDueDate());
                editNotes.setText(taskItem.getNotes());
                editImportance.setText(taskItem.getImportance());

                new AlertDialog.Builder(getContext())
                        .setTitle("Edit Task")
                        .setView(editView)
                        .setPositiveButton("Save", (dialog, which) -> {
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

            // Delete button functionality
            viewHolder.buttonDelete.setOnClickListener(v -> {
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
        }

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

    // Sort tasks by importance
    public void sortTasksByImportance() {
        Collections.sort(tasks, (t1, t2) -> {
            String importance1 = t1.getImportance() != null ? t1.getImportance() : "Medium"; // Default to "Medium"
            String importance2 = t2.getImportance() != null ? t2.getImportance() : "Medium";
            return importance1.compareTo(importance2);
        });
        notifyDataSetChanged();
    }

    // Sort tasks by percentage
    public void sortTasksByPercentage() {
        Collections.sort(tasks, (t1, t2) -> Integer.compare(t2.getPercentage(), t1.getPercentage())); // Sort in descending order
        notifyDataSetChanged();
    }
}

