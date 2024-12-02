package com.example.plearningapp.todo;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.plearningapp.R;
import com.example.plearningapp.main.MainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TodoFragment extends Fragment {
    FloatingActionButton fab;
    ImageView back;
    Button buttonAdd, buttonBack;
    RecyclerView recyclerViewTask;
    private TaskAdapter taskAdapter;
    private static ArrayList<String> taskList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);
        thamChieu(view);
        fab.setOnClickListener(v -> showDialog());
        back.setOnClickListener(v -> startActivity(new Intent(getActivity(), MainActivity.class)));
        return view;
    }
    private void thamChieu(View v) {
        fab = v.findViewById(R.id.add_task_fab);
        recyclerViewTask = v.findViewById(R.id.recycler_view_tasks);
        recyclerViewTask.setLayoutManager(new LinearLayoutManager(getActivity()));
        back = v.findViewById(R.id.back);
    }
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_tasks_dialog, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.show();
        EditText title_input = dialogView.findViewById(R.id.input_title);
        buttonAdd = dialogView.findViewById(R.id.button_add_task);
        buttonBack = dialogView.findViewById(R.id.back_button);
        buttonAdd.setOnClickListener(v -> {
            doAddTask(title_input);
        });
        buttonBack.setOnClickListener(v -> dialog.dismiss());
    }
    private void doAddTask(EditText editTextTask) {
        String task = editTextTask.getText().toString();
        if (!task.isEmpty()) {
            taskList.add(task);
            taskAdapter = new TaskAdapter(taskList);
//            taskAdapter.notifyDataSetChanged();
            recyclerViewTask.setAdapter(taskAdapter);
            editTextTask.getText().clear();
            Log.d("Task", "doAddTask: " + taskList);
        } else {
            Toast.makeText(getActivity(), "Please enter a task", Toast.LENGTH_SHORT).show();
        }
    }
}