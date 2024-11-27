package com.example.plearningapp.func.information;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.plearningapp.R;
import com.example.plearningapp.welcome.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    EditText gmail;
    Button change;
    TextView returnButton;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        thamChieu();
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gmailText = gmail.getText().toString();
                changePass(gmailText);
            }
        });
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPassword.this, LoginActivity.class));
            }
        });
    }
    private void thamChieu() {
        gmail = findViewById(R.id.forgot_password_email_edittext);
        change = findViewById(R.id.forgot_pass_button);
        returnButton = findViewById(R.id.forgot_pass_return);
    }
    private void changePass(String emailAddress) {
        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPassword.this, "Email sent!", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "Email sent.");
                            returnButton.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(ForgotPassword.this, "Failed to sent\n Try later!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}