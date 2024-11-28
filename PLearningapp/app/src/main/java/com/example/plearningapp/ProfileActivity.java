package com.example.plearningapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.plearningapp.func.information.Authenticate;
import com.example.plearningapp.func.information.ChangeAvatar;
import com.example.plearningapp.func.information.ChangeEmail;
import com.example.plearningapp.func.information.ChangePassword;
import com.example.plearningapp.welcome.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class ProfileActivity extends AppCompatActivity {
    ImageView avatar, back;
    TextView name, email, changeAvatar, changePassword, changeEmail, authenticate, logout;
    Uri avatarUri;
    String emailText, nameText, phoneText;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser user = mAuth.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        thamChieu();
        getSetData();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });
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
        changeEmail.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, ChangeEmail.class));
        });
        if (user.isEmailVerified()) {
            authenticate.setText(R.string.already_verified);
            authenticate.setEnabled(false);
        }
        authenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, Authenticate.class));
            }
        });

    }
    private void thamChieu() {
        back = findViewById(R.id.back);
        avatar = findViewById(R.id.profile_avatar);
        name = findViewById(R.id.profile_name);
        email = findViewById(R.id.profile_email);
        changeAvatar = findViewById(R.id.change_avatar);
        changePassword = findViewById(R.id.change_password);
        changeEmail = findViewById(R.id.change_mail);
        authenticate = findViewById(R.id.authenticate);
        logout = findViewById(R.id.logout);
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
                if (avatarUri != null) {
                    avatar.setImageURI(avatarUri);
                }

            }
        }
    }

}