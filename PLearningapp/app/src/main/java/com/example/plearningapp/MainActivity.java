package com.example.plearningapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.plearningapp.navigator.HomeFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        //NAV_SIDE
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
        changeNameAndGmail(navigationView);
        profileClick(navigationView);

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int check = item.getItemId();
        if (check == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    private void changeNameAndGmail(NavigationView navigationView) {
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.nav_name);
        TextView navGmail = headerView.findViewById(R.id.nav_gmail);
        ImageView navAvatar = headerView.findViewById(R.id.nav_avatar);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user!=null) {
            String name, gmail;
            Uri photoUrl;
            for (UserInfo profile : user.getProviderData()) {
                 name = profile.getDisplayName();
                 gmail = profile.getEmail();
                photoUrl = profile.getPhotoUrl();
                if (photoUrl!=null) {
                    navAvatar.setImageURI(photoUrl);
                }
                if (name!=null) {
                    navUsername.setText(name);
                }
                if (gmail!=null) navGmail.setText(gmail);
            }

        }

    }
    private void profileClick(NavigationView navigationView) {
        View headerView = navigationView.getHeaderView(0);
        ImageView avatar = headerView.findViewById(R.id.nav_avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });
    }
}