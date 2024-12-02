package com.example.plearningapp.func;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.plearningapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    EditText oldPass, newPass, confirmPass;
    Button changePassButton;
    String oldPassword, newPassword, confirmPassword;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        thamChieu();
        back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        changePassButton.setOnClickListener(v -> doChange());
    }
    private void doChange() {
         oldPassword = oldPass.getText().toString();
         newPassword = newPass.getText().toString();
         confirmPassword = confirmPass.getText().toString();

        if (oldPassword.isEmpty()) {
            oldPass.setError("Required!");
        } else if (newPassword.isEmpty()) {
            newPass.setError("Required!");
        } else if (confirmPassword.isEmpty()) {
            confirmPass.setError("Required!");
        } else if (!newPassword.equals(confirmPassword)) {
            confirmPass.setError("Not match!");
        } else if (newPassword.length() < 6) {
            newPass.setError("Password must be at least 6 characters");
        } else {
            updatePassword(oldPassword, newPassword);
        }
    }
    private void updatePassword(String oldPassword, String newPassword) {
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), oldPassword);

        user.reauthenticate(credential)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    showSuccessDialog();
                                    startActivity(new Intent(ChangePassword.this, ProfileActivity.class));
                                    Log.d(TAG, "User password updated.");
                                } else {
                                    Toast.makeText(ChangePassword.this, "Failed", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Error: " + task.getException());
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error: " + e);
                        Toast.makeText(ChangePassword.this, "Try later", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void thamChieu() {
        oldPass = findViewById(R.id.current_password_edittext);
        newPass = findViewById(R.id.new_password_edittext);
        confirmPass = findViewById(R.id.confirm_password_edittext);
        changePassButton = findViewById(R.id.change_button);
        back = findViewById(R.id.back);
    }
    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassword.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.show();
        TextView title = dialogView.findViewById(R.id.textview_dialog);
        title.setText("Change Successfully!");
        Button buttonBack = dialogView.findViewById(R.id.button);
        buttonBack.setOnClickListener(v -> {
            dialog.dismiss();
            startActivity(new Intent(ChangePassword.this, ProfileActivity.class));
        });
    }
}