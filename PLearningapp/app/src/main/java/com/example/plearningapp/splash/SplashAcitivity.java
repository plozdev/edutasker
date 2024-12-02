package com.example.plearningapp.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.plearningapp.main.MainActivity;
import com.example.plearningapp.R;
import com.example.plearningapp.welcome.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashAcitivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_acitivity);
        mAuth = FirebaseAuth.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAuth.getCurrentUser() != null) {
                    startActivity(new Intent(SplashAcitivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(SplashAcitivity.this, SignUpActivity.class));
                } finish();
            }
        }, 3000);
    }
}