package com.example.plearningapp.func.information;

import static android.content.ContentValues.TAG;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.plearningapp.ProfileActivity;
import com.example.plearningapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Authenticate extends AppCompatActivity {

    ImageView back;
    EditText email;
    Button btnAuthenticate;
    private final FirebaseAuth  mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser user = mAuth.getCurrentUser();
    private final String emailUser = user.getEmail();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);
        thamChieu();
        back.setOnClickListener(v -> {
            startActivity(new Intent(Authenticate.this, ProfileActivity.class));
        });
        btnAuthenticate.setOnClickListener(v -> {
            if(email.getText().toString().equals(emailUser)){
                doAuthenticate();
            } else {
                email.setError("Email isn't correct");
            }
        });
    }
    private void thamChieu() {
        back = findViewById(R.id.back);
        email = findViewById(R.id.email_edt);
        btnAuthenticate = findViewById(R.id.authenticate_button);
    }

    private void doAuthenticate() {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (user.isEmailVerified()) {
                            Log.d(TAG, "Email is verified.");
                            showBackDialog();
                        }
                        Log.d(TAG, "Email sent.");
                    } else {
                        Toast.makeText(Authenticate.this, "Try later!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "sendEmailVerification", task.getException());
                    }
                });
    }
    private void showBackDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Authenticate.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.show();

        Button buttonBack = dialogView.findViewById(R.id.button);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(Authenticate.this, ProfileActivity.class));
            }
        });
    }
}