package com.example.acadMate;

import android.content.SharedPreferences;
import android.widget.ProgressBar;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
//import com.example.profile_page.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class coursedetails extends AppCompatActivity {

    private final String[] semesters = {"Semester 1", "Semester 2", "Semester 3"};
    private String imageURL;
    private String user;

    private ImageView semesterImageView, back;

    private FirebaseFirestore db;
    private ProgressBar progress;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coursedetails);

        Intent receive = getIntent();
//        user = receive.getStringExtra("LEARNER_ID");

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        user = sharedPreferences.getString("LEARNER_ID", null); // Default value is null

        db = FirebaseFirestore.getInstance();

        progress = findViewById(R.id.progress_bar);

        Spinner semesterSpinner = findViewById(R.id.spinner_semesters);
        semesterImageView = findViewById(R.id.semester_image);
        back = findViewById(R.id.back_course); // Back button from Course page to Dashboard

        // Create an ArrayAdapter using the semester array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, semesters);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        semesterSpinner.setAdapter(adapter);

        semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0)
                    fetchImageUrl(0, user);
                else if (position == 1)
                    fetchImageUrl(1, user);
                else if (position == 2)
                    fetchImageUrl(2, user);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optionally handle the case where no semester is selected
            }
        });

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

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
                Intent intent = new Intent(coursedetails.this, timetable.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_profile) {
                Intent intent = new Intent(coursedetails.this, profile.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_attendance) {
                Intent intent = new Intent(coursedetails.this, attendance.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_faculty) {
                Intent intent = new Intent(coursedetails.this, faculty.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_scorecard) {
                Intent intent = new Intent(coursedetails.this, scorecard.class);
                startActivity(intent);
            }
            else if (item.getItemId() == R.id.nav_announcements) {
                Intent intent = new Intent(coursedetails.this, notification.class);
                startActivity(intent);
            }
            else if (item.getItemId() == R.id.nav_feedback) {
                Intent intent = new Intent(coursedetails.this, feedback.class);
                startActivity(intent);
            }
            drawerLayout.closeDrawers();
            return true;
        });

        // Right Navigation Drawer
        NavigationView navViewRight = findViewById(R.id.nav_view_right);
        navViewRight.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_dashboard) {
                Intent intent = new Intent(coursedetails.this, dashboard.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(coursedetails.this, MainActivity.class));
                finish();
            }
            drawerLayout.closeDrawers();
            return true;
        });

        // Button to open right drawer
        ImageView openRightDrawerButton = findViewById(R.id.open_right_drawer_button);
        openRightDrawerButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void fetchImageUrl(int pos, String userid)
    {
        db.collection("courseDetails").document(userid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            DocumentSnapshot document = task.getResult();
                            if(document.exists())
                            {
                                if(pos == 0)
                                    imageURL = document.getString("semester1");
                                else if(pos == 1)
                                    imageURL = document.getString("semester2");
                                else if(pos == 2)
                                    imageURL = document.getString("semester3");

                                if(imageURL != null)
                                {
                                    progress.setVisibility(View.VISIBLE);
                                    Glide.with(getApplicationContext())
                                            .load(imageURL)
                                            .fitCenter()
                                            .listener(new RequestListener<Drawable>() {
                                                @Override
                                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                    // Hide ProgressBar when image load fails
                                                    progress.setVisibility(View.GONE);
                                                    showToast("Failed to load image");
                                                    return false; // Pass the error to Glide
                                                }

                                                @Override
                                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                    // Hide ProgressBar when the image is successfully loaded
                                                    progress.setVisibility(View.GONE);
                                                    return false; // Pass the result to Glide
                                                }
                                            })
                                            .into(semesterImageView);
                                }
                            }
                        }
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
