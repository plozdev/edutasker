package com.example.plearningapp.func.add;
import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.plearningapp.MainActivity;
import com.example.plearningapp.ProfileActivity;
import com.example.plearningapp.R;
import com.example.plearningapp.file.add.FileAdapterAdd;
import com.example.plearningapp.func.information.Authenticate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddFilesActivity extends AppCompatActivity {

    private static final String[] OBJECTS = new String[] { "Toán", "Ngữ Văn", "Anh", "Lý", "Hóa", "Sinh", "Sử", "Địa", "GDCD", "Công nghệ", "Tin học", "Thể dục", "Âm nhạc", "Mỹ thuật", "Ngoại ngữ", "GDQP", "GDTC"};

//    Button saveButton, tryButton;
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;

    String subject;
    ImageView uploadFile, tryUploadFile, back;
    RecyclerView listFileRecyclerView;
    FileAdapterAdd fileAdapterAdd;
    List<String> selectedFiles;
    ProgressBar progressBar;
    //AUTH
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    //FireStore
    private final String userId = mAuth.getCurrentUser().getUid();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final StorageReference storageRef = storage.getReference("users").child(userId).child("files");
    //Storage

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_files);
        thamChieu();
        setAutoCompleteTextView();

        back.setOnClickListener(v -> startActivity(new Intent(AddFilesActivity.this, MainActivity.class)));

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> subject = adapterItems.getItem(position));
        uploadFile.setOnClickListener(v -> {
                if (subject==null && autoCompleteTextView.getText().toString().isEmpty()) {
                    autoCompleteTextView.setError("Fill subject");
                    return;
                }
                subject = autoCompleteTextView.getText().toString();
//                tryUploadFile.setVisibility(View.GONE);
//                uploadFile.setVisibility(View.VISIBLE);
                openFilePicker();
//                setSelectedFiles();

        });

//        tryButton.setOnClickListener(v -> {
//
//        });
//        saveButton.setOnClickListener(v -> startActivity(new Intent(AddFilesActivity.this, MainActivity.class)));

//        uploadFile.setOnClickListener(v -> {
//            if (subject==null) {
//                autoCompleteTextView.setError("Fill subject");
//                return;
//            }
//            openFilePicker();
//        });
    }
    //HIEN THI FILE
    private void setSelectedFiles() {
        selectedFiles = new ArrayList<>();
        fileAdapterAdd = new FileAdapterAdd(selectedFiles);
        listFileRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        listFileRecyclerView.setAdapter(fileAdapterAdd);
    }
    private void setAutoCompleteTextView() {
        adapterItems = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, OBJECTS);
        autoCompleteTextView.setAdapter(adapterItems);
    }


    //THAM CHIEU
    private void thamChieu() {
//        saveButton = findViewById(R.id.saveButton);
//        tryButton = findViewById(R.id.tryButton);
        uploadFile = findViewById(R.id.upload_file);
        back = findViewById(R.id.back);
//        tryUploadFile = findViewById(R.id.try_upload_file);
        progressBar = findViewById(R.id.progess_bar);
        autoCompleteTextView = findViewById(R.id.auto_complete_txt);
        listFileRecyclerView = findViewById(R.id.file_list);
    }


    //UPLOAD FILE
    private ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri uri = data.getData();
                        doUploadTasks(uri);
                    }
                }
            }
    );
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        String[] mimeTypes = {
                "application/pdf", // PDF
                "application/msword", // DOC
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // DOCX
                "application/vnd.ms-excel", // XLS
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // XLSX
                "application/vnd.ms-powerpoint", // PPT
                "application/vnd.openxmlformats-officedocument.presentationml.presentation" // PPTX
        };
        intent.setType("*/*"); // Allow all types of files
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        filePickerLauncher.launch(Intent.createChooser(intent, "Select a File"));
    }
    private void doUploadTasks(Uri uriFile) {
        progressBar.setVisibility(View.VISIBLE);
        storageRef.putFile(uriFile).addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnCompleteListener(task -> {
               if (task.isSuccessful()) {
                   Map<String, Object> metadata = new HashMap<>();
                   metadata.put("fileName", uriFile.getLastPathSegment());
                   metadata.put("date", System.currentTimeMillis()); // Store current timestamp
                   metadata.put("subject", subject);
                   metadata.put("downloadUrl", uriFile.toString());
                   Log.d("Firebase Storage", "doUploadTasks: " + task.getResult());

                   // Add metadata to Firestore
                   db.collection("sampleData").document(userId).collection("files")
                           .add(metadata)
                           .addOnSuccessListener(documentReference -> {
//                                tryButton.setVisibility(View.GONE);
//                                saveButton.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                uploadFile.setVisibility(View.GONE);
                                listFileRecyclerView.setVisibility(View.VISIBLE);
                                setSelectedFiles();
                                fetchRecentUploads();

                                showSuccessDialog();
                                Log.d("Firestore", "Metadata added with ID: " + documentReference.getId());
                           })
                           .addOnFailureListener(e -> {
                               progressBar.setVisibility(View.GONE);
                               Log.w("Firestore", "Error adding metadata", e);
                           });
               } else {
                   progressBar.setVisibility(View.GONE);
                    Log.e("Firebase Storage", "doUploadTasks: ", task.getException());
               }
            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e("Firebase Storage", "doUploadTasks: ", e);
                });
    }
    private void fetchRecentUploads() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("sampleData").document(userId).collection("files")
                .orderBy("timestamp", Query.Direction.DESCENDING) // Sort by timestamp
                .limit(10) // Limit to the 10 most recent uploads
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<String> uploads = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String fileName = document.getString("fileName");
                            uploads.add(fileName);
                        }
                        fileAdapterAdd.updateData(uploads); // Update the adapter with new data
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error fetching recent uploads: " + e.getMessage());
                        Toast.makeText(AddFilesActivity.this, "Failed to load uploads", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddFilesActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.show();
        TextView title = dialogView.findViewById(R.id.textview_dialog);
        title.setText("Upload Successfully!");
        Button buttonBack = dialogView.findViewById(R.id.button);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(AddFilesActivity.this, MainActivity.class));
            }
        });
    }
}