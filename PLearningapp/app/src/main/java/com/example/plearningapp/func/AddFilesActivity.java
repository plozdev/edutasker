package com.example.plearningapp.func;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.plearningapp.main.MainActivity;
import com.example.plearningapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddFilesActivity extends AppCompatActivity {

    private static final String[] OBJECTS = new String[] { "Toán", "Ngữ Văn", "Anh", "Lý", "Hóa", "Sinh", "Sử", "Địa", "GDCD", "Công nghệ", "Tin học", "Thể dục", "Âm nhạc", "Mỹ thuật", "Ngoại ngữ", "GDQP", "GDTC"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    String subject;
    ImageView uploadFile, back;
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
        back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> subject = adapterItems.getItem(position));
        uploadFile.setOnClickListener(v -> {
                if (subject==null && autoCompleteTextView.getText().toString().isEmpty()) {
                    autoCompleteTextView.setError("Fill subject");
                    return;
                }
                subject = autoCompleteTextView.getText().toString();
                openFilePicker();
        });
    }

    private void setAutoCompleteTextView() {
        adapterItems = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, OBJECTS);
        autoCompleteTextView.setAdapter(adapterItems);
    }

    //THAM CHIEU
    private void thamChieu() {
        uploadFile = findViewById(R.id.upload_file);
        back = findViewById(R.id.back);
        progressBar = findViewById(R.id.progess_bar);
        autoCompleteTextView = findViewById(R.id.auto_complete_txt);
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
    private String getFileName(Uri fileUri) {
        String result = null;

        // Query the file's display name
        Cursor cursor = getContentResolver().query(fileUri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (nameIndex != -1) {
                result = cursor.getString(nameIndex);
            }
            cursor.close();
        }

        if (result == null) {
            result = fileUri.getLastPathSegment();
        }
        return result;
    }
    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
    private void doUploadTasks(Uri uriFile) {
        progressBar.setVisibility(View.VISIBLE);
        storageRef.putFile(uriFile).addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnCompleteListener(task -> {
               if (task.isSuccessful()) {
                   Map<String, Object> metadata = new HashMap<>();
                   metadata.put("fileName", getFileName(uriFile));
                   metadata.put("date",System.currentTimeMillis());
                   metadata.put("timestamp", formatTimestamp(System.currentTimeMillis()));
                   metadata.put("subject", subject);
                   Log.d("Firebase Storage", "doUploadTasks: " + task.getResult());

                   // Add metadata
                   db.collection("userId").document(userId).collection("files")
                           .add(metadata)
                           .addOnSuccessListener(documentReference -> {
                                progressBar.setVisibility(View.GONE);
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
        buttonBack.setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(AddFilesActivity.this, MainActivity.class));
        });
    }
}