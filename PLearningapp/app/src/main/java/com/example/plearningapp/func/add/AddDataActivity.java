package com.example.plearningapp.func.add;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.plearningapp.MainActivity;
import com.example.plearningapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
public class AddDataActivity extends AppCompatActivity {

    Button saveButton;
    EditText uploadName, uploadSubject, uploadDescription;
    String name, subject, description;
    ImageView uploadFile;
    ProgressBar progressBar;
    //OpenFile
    private ActivityResultLauncher<Intent> openFileLauncher;
    //AUTH
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    //FireStore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Storage



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);
        thamChieu();
        saveButton.setEnabled(false);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataDatabase();
            }
        });
        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
                saveButton.setEnabled(true);
            }
        });

    }
    private void thamChieu() {
        saveButton = findViewById(R.id.saveButton);
        uploadDescription = findViewById(R.id.uploadDescription);
        uploadName = findViewById(R.id.uploadName);
        uploadSubject = findViewById(R.id.uploadSubject);
        uploadFile = findViewById(R.id.upload_file);
    }

    private void saveDataDatabase() {
        name = uploadName.getText().toString();
        subject = uploadSubject.getText().toString();
        description = uploadDescription.getText().toString();
        DataClass dataClass = new DataClass(name, subject, description);
        Map<String, Object> data = new HashMap<>();
        data.put(subject, dataClass);
        db.collection("sampleData")
                .document(mAuth.getCurrentUser().getUid())
                .update(data)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "DocumentSnapshot added with ID");
                    Toast.makeText(AddDataActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddDataActivity.this, MainActivity.class));
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddDataActivity.this, "Something wrong\nTry again!", Toast.LENGTH_SHORT).show();
                    Log.w("Firestore", "Error adding document", e);
                });

    }

    private void uploadFileToStorage(Uri fileUri) {
        if (fileUri == null) {
            Log.e("FileUpload", "No file selected");
            return;
        }

        // Create a reference in Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String fileName = System.currentTimeMillis() + "_" + getFileName(fileUri);
        StorageReference fileRef = storageRef.child("files/" + fileName);

        // Start file upload
        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL of the uploaded file
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Log.d("FileUpload", "File uploaded successfully: " + uri.toString());
                        saveFileMetadataToFirestore(fileName, uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("FileUpload", "File upload failed", e);
                })
                .addOnProgressListener(snapshot -> {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();

                    Log.d("FileUpload", "Upload is " + progress + "% done");
                });
    }


    private void saveFileMetadataToFirestore(String fileName, String fileUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a map to store file details
        Map<String, Object> fileDetails = new HashMap<>();
        fileDetails.put("fileName", fileName);
        fileDetails.put("fileUrl", fileUrl);
        fileDetails.put("timestamp", System.currentTimeMillis());

        // Save to Firestore
        db.collection("uploadedFiles").document(fileName)
                .set(fileDetails)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "File metadata saved successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Failed to save file metadata", e);
                });
    }


    private ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri fileUri = result.getData().getData();
                    uploadFileToStorage(fileUri); // Upload selected file
                }
            }
    );

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // Accept all file types
        filePickerLauncher.launch(intent);
    }

    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }





}