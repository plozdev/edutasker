package com.example.plearningapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.plearningapp.func.information.ChangeAvatar;
import com.example.plearningapp.func.information.ChangePassword;
import com.example.plearningapp.func.information.UpdateActivity;
import com.example.plearningapp.welcome.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;

public class ProfileActivity extends AppCompatActivity {
    Button changeAvatar, changePassword, updateProfile, logout;
    ImageView avatar;
    TextView name, email, phone;
    Uri avatarUri;
    String emailText, nameText, phoneText;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        thamChieu();
        getSetData();

        logout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(ProfileActivity.this, SignUpActivity.class));
        });

        changeAvatar.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, ChangeAvatar.class));
        });

        changePassword.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, ChangePassword.class));
        });

        updateProfile.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, UpdateActivity.class));
        });
    }
    private void thamChieu() {
        avatar = findViewById(R.id.profile_avatar);
        name = findViewById(R.id.profile_name);
        email = findViewById(R.id.profile_email);
        phone = findViewById(R.id.profile_phone);
        changeAvatar = findViewById(R.id.change_avatar_button);
        changePassword = findViewById(R.id.change_password_button);
        updateProfile = findViewById(R.id.update_profile_button);
        logout = findViewById(R.id.logout_profile_button);
    }
    private void getSetData() {

        if (mAuth != null) {
            for (UserInfo profile : mAuth.getCurrentUser().getProviderData()) {
                nameText = profile.getDisplayName();
                emailText = profile.getEmail();
                phoneText = profile.getPhoneNumber();
                avatarUri = profile.getPhotoUrl();

                if (name!=null) {
                    name.setText(nameText);
                }
                if (email!=null) {
                    email.setText(emailText);
                }
                if (phone!=null) {
                    phone.setText(phoneText);
                }
                if (avatarUri != null) {
                    avatar.setImageURI(avatarUri);
                }

            }
        }
    }

}