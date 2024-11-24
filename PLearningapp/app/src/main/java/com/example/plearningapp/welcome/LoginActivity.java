package com.example.plearningapp.welcome;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.plearningapp.MainActivity;
import com.example.plearningapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView notMember;
    private EditText email, password;
    private Button signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        thamChieu();
        notMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
        mAuth = FirebaseAuth.getInstance();
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithEmail();
            }
        });

    }
    private void thamChieu() {
        notMember = findViewById(R.id.not_member_text);
        email = findViewById(R.id.emailEditText_login);
        password = findViewById(R.id.passwordEditText_login);
        signIn = findViewById(R.id.login_button);
    }
    private void signInWithEmail() {
        String gmail, pass;
        gmail = email.getText().toString();
        pass = password.getText().toString();
        if (gmail.isEmpty()) {
            email.setError("Email is required");
        } else if (pass.isEmpty()) {
            password.setError("Password is required");
        } else {
            mAuth.signInWithEmailAndPassword(gmail, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            } else {
                                Toast.makeText(LoginActivity.this, "Check again!", Toast.LENGTH_LONG).show();
                                email.setError("Does is correct?");
                                password.setError("Does is correct?");
                            }
                        }
                    });
        }
    }
}