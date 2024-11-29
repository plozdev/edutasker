package com.example.plearningapp.navigator;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.plearningapp.R;
import com.example.plearningapp.file.FileAdapterMain;
import com.example.plearningapp.file.FileModelMain;
import com.example.plearningapp.func.add.AddFilesActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment{
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private FileAdapterMain fileAdapter;
    private List<FileModelMain> fileList;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userId = mAuth.getCurrentUser().getUid();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        fileList = new ArrayList<>();
//        fetchFile();
        fileAdapter = new FileAdapterMain(fileList);
        recyclerView.setAdapter(fileAdapter);
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddFilesActivity.class);
            startActivity(intent);
        });
        return view;
    }
//    private void fetchFile() {
//        firestore.collection("sampleData").document(userId)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                String name = document.getString("name");
//                                String date = document.getString("date");
//                                String subject = document.getString("subject");
//                                String downloadUrl = document.getString("downloadUrl");
//                                FileModelMain fileModel = new FileModelMain(name, date, subject, downloadUrl);
//                                fileList.add(fileModel);
////                                fileAdapter.notifyDataSetChanged();
//                            }
//                        }
//                        else {
//
//                        }
//                    }
//                });
//    }
}