package com.example.acadMate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
//import com.example.profile_page.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class profile extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private ImageView back, profile;
    private FirebaseFirestore db;
    private String user;

    private TextView nameText, phoneText, emailText, dobText, learnerText, regText, nationalityText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
//        FirebaseApp.initializeApp(this);

        nameText = findViewById(R.id.nameText);
        phoneText = findViewById(R.id.phoneText);
        dobText = findViewById(R.id.dobText);
        learnerText = findViewById(R.id.learnerText);
        regText = findViewById(R.id.regText);
        nationalityText = findViewById(R.id.nationalityText);
        emailText = findViewById(R.id.emailText);

        profile = findViewById(R.id.profile_image);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        db = FirebaseFirestore.getInstance();

        Intent receive = getIntent();
//        user = receive.getStringExtra("LEARNER_ID");

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String user = sharedPreferences.getString("LEARNER_ID", null); // Default value is null


        if (user != null && !user.isEmpty()) {
            fetchProfileData(user);
        } else {
            showToast("No Learner ID found!");
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        back = findViewById(R.id.backBtn);
        profile = findViewById(R.id.profile_image); // Profile image attribute

        // Setting up the navigation toggle
        toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Left Navigation Drawer
        NavigationView navViewLeft = findViewById(R.id.nav_view_left);
        navViewLeft.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_timetable) {
                Intent intent = new Intent(profile.this, timetable.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_coursedetails) {
                Intent intent = new Intent(profile.this, coursedetails.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_attendance) {
                Intent intent = new Intent(profile.this, attendance.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_faculty) {
                Intent intent = new Intent(profile.this, faculty.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_scorecard) {
                Intent intent = new Intent(profile.this, scorecard.class);
                startActivity(intent);
            }
            else if (item.getItemId() == R.id.nav_announcements) {
                Intent intent = new Intent(profile.this, notification.class);
                startActivity(intent);
            }
            else if (item.getItemId() == R.id.nav_feedback) {
                Intent intent = new Intent(profile.this, feedback.class);
                startActivity(intent);
            }
            drawerLayout.closeDrawers();
            return true;
        });

        // Right Navigation Drawer
        NavigationView navViewRight = findViewById(R.id.nav_view_right);
        navViewRight.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_dashboard) {
                Intent intent = new Intent(profile.this, dashboard.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(profile.this, MainActivity.class));
                finish();
            }
            drawerLayout.closeDrawers();
            return true;
        });

        // Button to open right drawer
        ImageView openRightDrawerButton = findViewById(R.id.open_right_drawer_button);
        openRightDrawerButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));

        // Back button functionality
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void fetchProfileData(String userid)
    {
        db.collection("profileDetails").document(userid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists())
                            {
                                String name = document.getString("name");
                                String reg = document.getString("regno");
                                String dob = document.getString("dob");
                                String email = document.getString("email");
                                String phone = document.getString("phone");
                                String learner = document.getString("learnerid");
                                String nationality = document.getString("nationality");
                                String imageUrl = document.getString("image");

                                nameText.setText(name);
                                regText.setText(reg);
                                dobText.setText(dob);
                                emailText.setText(email);
                                phoneText.setText(phone);
                                learnerText.setText(learner);
                                nationalityText.setText(nationality);

                                if(imageUrl != null)
                                {
                                    Glide.with(getApplicationContext())
                                            .load(imageUrl)
                                            .transform(new CircleCrop())
                                            .into(profile);
                                }
                            }
                            else
                            {
                                showToast("No data found!");
                            }
                        } else{
                            showToast("Failed to retrieve data: "+task.getException());
                        }
                    }
                });
    }



    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(findViewById(R.id.nav_view_left)) || drawerLayout.isDrawerOpen(findViewById(R.id.nav_view_right))) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}
