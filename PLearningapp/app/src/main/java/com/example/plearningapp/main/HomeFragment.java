package com.example.plearningapp.main;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.plearningapp.R;
import com.example.plearningapp.func.AddFilesActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    Button fab;
    TextView textTask, TEXT;
    ProgressBar progressBar;
    RecyclerView recyclerView, recyclerViewTodo;
    private FileAdapterMain fileAdapter;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userId = mAuth.getCurrentUser().getUid();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fileAdapter = new FileAdapterMain();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
//        https://developer.android.com/reference/android/arch/lifecycle/LiveData
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        started(view);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddFilesActivity.class);
            startActivity(intent);
        });
        fetchTasks();
        fetchUploads();
    }

    // bindView
    private void started(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(fileAdapter);
        fab = view.findViewById(R.id.fab);
        textTask = view.findViewById(R.id.text_task);
        TEXT = view.findViewById(R.id.TEXT);
        progressBar = view.findViewById(R.id.progess_bar_main);
        recyclerViewTodo = view.findViewById(R.id.recycler_view_todo_main);
        recyclerViewTodo.setLayoutManager(new LinearLayoutManager(recyclerViewTodo.getContext()));
    }
    // mvvm, mvc
    // https://developer.android.com/topic/architecture/intro
    // https://viblo.asia/p/android-viewmodel-firebase-database-924lJEpYZPM
    // singleton design pattern
//observer design pattern
//    https://guides.codepath.com/android/Understanding-the-Android-Application-Class
    private void fetchUploads() {
        progressBar.setVisibility(View.VISIBLE);
        firestore.collection("userId").document(userId).collection("files")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<FileModelMain> uploads = new ArrayList<>();
                        if (queryDocumentSnapshots.isEmpty()) return;
                        for (int i=queryDocumentSnapshots.size()-1;i>=0; i--) {
                            String fileName = queryDocumentSnapshots.getDocuments().get(i).getString("fileName");
                            String date = queryDocumentSnapshots.getDocuments().get(i).getString("timestamp");
                            String subject = queryDocumentSnapshots.getDocuments().get(i).getString("subject");
                            Log.d("Firestore", " fileName => " + fileName);
                            Log.d("Firestore", " date => " + date);
                            Log.d("Firestore", " subject => " + subject);
                            FileModelMain fileModel = new FileModelMain(fileName, date, subject);
                            uploads.add(fileModel);
                        }
                        // dunhf live data cập nhật dữ liệu sau khi fetch
                        fileAdapter.updateData(uploads);
                        progressBar.setVisibility(View.GONE);
                        Log.d("Firestore", "Data fetched successfully");
                        Log.d("Firestore", "Data: " + uploads);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Log.e("Firestore", "Error getting documents.", e);
                    }
                });
    }

    private void fetchTasks() {
        firestore.collection("userId").document(userId).collection("tasks")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<String> tasks = new ArrayList<>();
                        if (queryDocumentSnapshots.isEmpty()) return;
                        for (int i=queryDocumentSnapshots.size()-1;i>=0; i--) {
                            String task = queryDocumentSnapshots.getDocuments().get(i).getString("task");
                            tasks.add(task);
                        }
                        if (tasks.isEmpty()) {
                            Log.d("Firestore", "Tasks is empty");
                            return ;
                        }
                        FileAdapterTodo fileAdapterTodo = new FileAdapterTodo();
                        fileAdapterTodo.setUpFileList(tasks);
                        recyclerViewTodo.setAdapter(fileAdapterTodo);
                        textTask.setVisibility(View.GONE);
                        TEXT.setVisibility(View.VISIBLE);
                        recyclerViewTodo.setVisibility(View.VISIBLE);
                        Log.d("Firestore", "Tasks fetched successfully");
                        Log.d("Firestore", "Tasks: " + tasks);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error getting documents.", e);
                    }
                });
    }
}