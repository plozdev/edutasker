package com.example.plearningapp.func;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.plearningapp.R;
import com.example.plearningapp.main.MainActivity;
import com.example.plearningapp.welcome.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class ProfileActivity extends AppCompatActivity {
    ImageView avatar, back;
    TextView name, email, changePassword, authenticate, logout, a,b,c,d,rate,share;
    String emailText, nameText, phoneText;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser user = mAuth.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        thamChieu();
        getSetData();
        back.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, MainActivity.class)));
        logout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(ProfileActivity.this, SignUpActivity.class));
        });
        changePassword.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, ChangePassword.class)));
        if (user.isEmailVerified()) {
            authenticate.setText(R.string.already_verified);
            authenticate.setEnabled(false);
        }
        authenticate.setOnClickListener(v -> doAuthenticate());
        a.setOnClickListener(v -> Toast.makeText(ProfileActivity.this, "Demo", Toast.LENGTH_SHORT).show());
        b.setOnClickListener(v -> Toast.makeText(ProfileActivity.this, "Demo", Toast.LENGTH_SHORT).show());
        c.setOnClickListener(v -> Toast.makeText(ProfileActivity.this, "Demo", Toast.LENGTH_SHORT).show());
        d.setOnClickListener(v -> Toast.makeText(ProfileActivity.this, "Demo", Toast.LENGTH_SHORT).show());
        rate.setOnClickListener(v -> Toast.makeText(ProfileActivity.this, "Demo", Toast.LENGTH_SHORT).show());
        share.setOnClickListener(v -> Toast.makeText(ProfileActivity.this, "Demo", Toast.LENGTH_SHORT).show());

    }
    private void thamChieu() {
        back = findViewById(R.id.back);
        avatar = findViewById(R.id.profile_avatar);
        name = findViewById(R.id.profile_name);
        email = findViewById(R.id.profile_email);
        changePassword = findViewById(R.id.change_password);
        authenticate = findViewById(R.id.authenticate);
        logout = findViewById(R.id.logout);
        a = findViewById(R.id.a);
        b = findViewById(R.id.b);
        c = findViewById(R.id.c);
        d = findViewById(R.id.d);
        rate = findViewById(R.id.rate);
        share = findViewById(R.id.share);
    }
    private void getSetData() {
        if (mAuth != null) {
            for (UserInfo profile : mAuth.getCurrentUser().getProviderData()) {
                nameText = profile.getDisplayName();
                emailText = profile.getEmail();
                phoneText = profile.getPhoneNumber();
                if (name!=null) {
                    name.setText(nameText);
                }
                if (email!=null) {
                    email.setText(emailText);
                }
            }
        }
    }

    private void doAuthenticate() {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (user.isEmailVerified()) {
                            Log.d(TAG, "Email is verified.");
                            showSuccessDialog();
                        }
                        Log.d(TAG, "Email sent.");
                    } else {
                        Toast.makeText(ProfileActivity.this, "Try later!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "sendEmailVerification", task.getException());
                    }
                });
    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        dialog.show();

        Button buttonBack = dialogView.findViewById(R.id.button);
        buttonBack.setOnClickListener(v ->dialog.dismiss());
    }
}