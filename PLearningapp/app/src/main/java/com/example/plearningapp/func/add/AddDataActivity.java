package com.example.plearningapp.func.add;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.plearningapp.R;
import com.example.plearningapp.adapter.FileAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class AddDataActivity extends AppCompatActivity {

    private static final int FILE_REQUEST_CODE = 1;
    Button saveButton;
    AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapterItems;
    private static final String[] OBJECTS = new String[] { "Toán", "Văn", "Anh", "Lý", "Hóa", "Sinh", "Sử", "Địa", "GDCD", "Công nghệ", "Tin học", "Thể dục", "Âm nhạc", "Mỹ thuật", "Ngoại ngữ", "GDQP", "GDTC"};


    String subject;
    ImageView uploadFile;
    //OpenFile
    private ActivityResultLauncher<Intent> openFileLauncher;
    //AUTH
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    //FireStore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId = mAuth.getCurrentUser().getUid();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
    //Storage
    private RecyclerView listFileRecyclerView;
    private FileAdapter fileAdapter;
    private List<String> selectedFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);
        thamChieu();
        saveButton.setEnabled(false);
        setAutoCompleteTextView();
        setSelectedFiles();
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        if (data.getClipData() != null) {
                            // Handle multiple files
                            int count = data.getClipData().getItemCount();
                            for (int i = 0; i < count; i++) {
                                Uri fileUri = data.getClipData().getItemAt(i).getUri();
                                if (getContentResolver().getType(fileUri).equals("application/pdf")) {
                                    selectedFiles.add(fileUri.getLastPathSegment());
                                } else {
                                    Toast.makeText(this, "Only PDF files are allowed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else if (data.getData() != null) {
                            // Handle single file
                            Uri fileUri = data.getData();
                            if (getContentResolver().getType(fileUri).equals("application/pdf")) {
                                selectedFiles.add(fileUri.getLastPathSegment());
                            } else {
                                Toast.makeText(this, "Only PDF files are allowed.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        fileAdapter.notifyDataSetChanged();
                    }
                }
        );
        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();

                saveButton.setEnabled(true);
            }
        });

    }
    private void setSelectedFiles() {
        selectedFiles = new ArrayList<>();
        fileAdapter = new FileAdapter(selectedFiles);
        listFileRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        listFileRecyclerView.setAdapter(fileAdapter);
    }
    private void setAutoCompleteTextView() {
        adapterItems = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, OBJECTS);
        autoCompleteTextView.setAdapter(adapterItems);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                subject = parent.getItemAtPosition(position).toString();
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
//            if (data.getClipData() != null) {
//                int count = data.getClipData().getItemCount();
//                for (int i = 0; i < count; i++) {
//                    Uri fileUri = data.getClipData().getItemAt(i).getUri();
//                    if (getContentResolver().getType(fileUri).equals("application/pdf")) {
//                        selectedFiles.add(fileUri.getLastPathSegment());
//                    } else {
//                        // Show an error if the file is not a PDF
//                        Toast.makeText(this, "Only PDF files are allowed.", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            } else if (data.getData() != null) {
//                Uri fileUri = data.getData();
//                if (getContentResolver().getType(fileUri).equals("application/pdf")) {
//                    selectedFiles.add(fileUri.getLastPathSegment());
//                } else {
//                    Toast.makeText(this, "Only PDF files are allowed.", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            fileAdapter.notifyDataSetChanged();
//        }
//    }

    private void thamChieu() {
        saveButton = findViewById(R.id.saveButton);
        uploadFile = findViewById(R.id.upload_file);
        autoCompleteTextView = findViewById(R.id.auto_complete_txt);
        listFileRecyclerView = findViewById(R.id.file_list);
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
                        uploadFile.setVisibility(View.GONE);
                        listFileRecyclerView.setVisibility(View.VISIBLE);
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


    private ActivityResultLauncher<Intent> filePickerLauncher;


    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/application/pdf*");
        filePickerLauncher.launch(Intent.createChooser(intent, "Choose PDF Files"));
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