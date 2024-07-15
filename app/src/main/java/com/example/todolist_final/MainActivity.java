package com.example.todolist_final;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> tasks;
    private ArrayAdapter<String> adapter;
    private SharedPreferences preferences;
    private EditText editTextTask;
    private EditText editTextDueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("To Do List");

        editTextTask = findViewById(R.id.editTextTask);
        Button btnAddTask = findViewById(R.id.btnAddTask);
        ListView listViewTasks = findViewById(R.id.listViewTasks);
        editTextDueDate = findViewById(R.id.editTextDueDate);

        tasks = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tasks);
        listViewTasks.setAdapter(adapter);

        preferences = getPreferences(MODE_PRIVATE);

        loadTasks();

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = editTextTask.getText().toString().trim();
                String dueDate = editTextDueDate.getText().toString().trim();
                if (!task.isEmpty()) {
                    tasks.add(task + " (Due: " + dueDate + ")");
                    Collections.sort(tasks);
                    saveTasks();
                    adapter.notifyDataSetChanged();
                    editTextTask.getText().clear();
                    editTextDueDate.getText().clear();
                }
            }
        });

        // Long press to remove task
        listViewTasks.setOnItemLongClickListener((parent, view, position, id) -> {
            tasks.remove(position);
            saveTasks();
            adapter.notifyDataSetChanged();
            return true;
        });

        // Short press to mark task as completed
        listViewTasks.setOnItemClickListener((parent, view, position, id) -> {
            String task = tasks.get(position);
            // Your logic here to handle completed tasks, e.g., remove them from the list
            tasks.remove(position);
            saveTasks();
            adapter.notifyDataSetChanged();
        });

        // Set onClickListener for editTextDueDate to show DatePickerDialog
        editTextDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    private void loadTasks() {
        int size = preferences.getInt("tasks_size", 0);
        for (int i = 0; i < size; i++) {
            String task = preferences.getString("task_" + i, null);
            if (task != null) {
                tasks.add(task);
            }
        }
    }

    private void saveTasks() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.putInt("tasks_size", tasks.size());
        for (int i = 0; i < tasks.size(); i++) {
            editor.putString("task_" + i, tasks.get(i));
        }
        editor.apply();
    }

    private void showDatePickerDialog() {
        // Get current date
        final Calendar calendar = Calendar.getInstance();

        // Create DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Set selected date to editTextDueDate
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Format date to desired format (e.g., dd/MM/yyyy)
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        String selectedDate = dateFormat.format(calendar.getTime());
                        editTextDueDate.setText(selectedDate);
                    }
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // Show DatePickerDialog
        datePickerDialog.show();
    }
}
