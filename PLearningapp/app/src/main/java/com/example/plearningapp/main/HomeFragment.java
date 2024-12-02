package com.example.plearningapp.main;
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
import com.example.plearningapp.func.AddFilesActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment{
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private FileAdapterMain fileAdapter;
    private List<FileModelMain> fileList;
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userId = mAuth.getCurrentUser().getUid();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        thamChieu(view);
        fetchUploads();
        fileAdapter = new FileAdapterMain(fileList);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddFilesActivity.class);
            startActivity(intent);
        });
        return view;
    }
    private void thamChieu(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        fileList = new ArrayList<>();
        fab = view.findViewById(R.id.fab);
    }
    private void fetchUploads() {
        firestore.collection("userId").document(userId).collection("files")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<FileModelMain> uploads = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String fileName = document.getString("fileName");
                            String date = document.getString("timestamp");
                            String subject = document.getString("subject");
                            Log.d("Firestore", " fileName => " + fileName);
                            Log.d("Firestore", " date => " + date);
                            Log.d("Firestore", " subject => " + subject);
                            FileModelMain fileModel = new FileModelMain(fileName, date, subject);
                            uploads.add(fileModel);
                        }
                        fileAdapter.updateData(uploads);

                        recyclerView.setAdapter(fileAdapter);
                        Log.d("Firestore", "Data fetched successfully");
                        Log.d("Firestore", "Data: " + uploads);
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