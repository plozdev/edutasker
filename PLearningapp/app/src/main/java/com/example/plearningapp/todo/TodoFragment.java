package com.example.plearningapp.todo;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.plearningapp.R;
import com.example.plearningapp.main.FileModelMain;
import com.example.plearningapp.main.MainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TodoFragment extends Fragment {
    FloatingActionButton fab;
    ImageView back;
    Button buttonAdd, buttonBack;
    RecyclerView recyclerViewTask;
    AutoCompleteTextView inputSubject;
    private static final String[] OBJECTS = new String[] { "Toán", "Ngữ Văn", "Anh", "Lý", "Hóa", "Sinh", "Sử", "Địa", "GDCD", "Công nghệ", "Tin học", "Thể dục", "Âm nhạc", "Mỹ thuật", "Ngoại ngữ", "GDQP", "GDTC"};
    private static ArrayList<Task> taskList = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser user = mAuth.getCurrentUser();
    private final String userId = user.getUid();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        started(view);
        fetchTask();
        fab.setOnClickListener(v -> showDialog());
        back.setOnClickListener(v -> startActivity(new Intent(getActivity(), MainActivity.class)));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void started(View v) {
        fab = v.findViewById(R.id.add_task_fab);
        recyclerViewTask = v.findViewById(R.id.recycler_view_tasks);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerViewTask.getContext());
        recyclerViewTask.setLayoutManager(linearLayoutManager);
        back = v.findViewById(R.id.back);

    }
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_tasks_dialog, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.show();
        inputSubject = dialogView.findViewById(R.id.input_subject);
        setAutoCompleteTextView();

        EditText title_input = dialogView.findViewById(R.id.input_title);
        buttonAdd = dialogView.findViewById(R.id.button_add_task);
        buttonBack = dialogView.findViewById(R.id.back_button);


        buttonAdd.setOnClickListener(v -> doAddTask(title_input));
        buttonBack.setOnClickListener(v -> dialog.dismiss());
    }
    private void setAutoCompleteTextView() {
        ArrayAdapter<String> adapterItems = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, OBJECTS);
        inputSubject.setAdapter(adapterItems);
    }
    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
    private void doAddTask(EditText editTextTask) {
        String task = editTextTask.getText().toString();
        String subject = inputSubject.getText().toString();
        String date = formatTimestamp(System.currentTimeMillis());
        if (!task.isEmpty() && !subject.isEmpty()) {
            Task ok = new Task(task, subject, date);
            taskList.add(0, ok);
            TaskAdapter taskAdapter = new TaskAdapter(taskList);
            recyclerViewTask.setAdapter(taskAdapter);
            editTextTask.getText().clear();
            inputSubject.getText().clear();
            doUpload( task);
            Log.d("Task", "doAddTask: " + taskList);
        } else {
            if (subject.isEmpty()) inputSubject.setError("Fill subject");
            else if (task.isEmpty()) editTextTask.setError("Fill task");
            Toast.makeText(getActivity(), "Please enter a task", Toast.LENGTH_SHORT).show();
        }
    }
    private void doUpload(String task) {
        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("task", task);
        db.collection("userId").document(userId).collection("tasks")
                .add(taskMap)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Task added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error adding task", e);
                });
    }

    private void fetchTask() {
        db.collection("userId").document(userId).collection("tasks")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) return ;
                    for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                        String task = queryDocumentSnapshots.getDocuments().get(i).getString("task");
                        String subject = queryDocumentSnapshots.getDocuments().get(i).getString("subject");
                        String date = queryDocumentSnapshots.getDocuments().get(i).getString("date");
                        Task ok = new Task(task, subject, date);
                        taskList.add(ok);
                    }
                    TaskAdapter taskAdapter = new TaskAdapter(taskList);
                    recyclerViewTask.setAdapter(taskAdapter);
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error getting tasks", e);
                });
    }
}