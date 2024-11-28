package com.example.plearningapp.func.information;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.plearningapp.ProfileActivity;
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
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    EditText oldPass, newPass, confirmPass;
    Button changePassButton;
    String oldPassword, newPassword, confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);
        thamChieu();

        changePassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doChange();
            }
        });

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
                                    Toast.makeText(ChangePassword.this, "Change Successful", Toast.LENGTH_SHORT).show();
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
    }
}