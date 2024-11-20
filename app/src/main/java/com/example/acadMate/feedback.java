package com.example.acadMate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

//import com.example.profile_page.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class feedback extends AppCompatActivity {

    private Spinner courseSpinner;
    private Button submitButton;
    private Button resetButton;
    private RatingBar[] ratingBars = new RatingBar[6]; // Array to hold the RatingBars
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        Intent receive = getIntent();

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String userId = sharedPreferences.getString("LEARNER_ID", null); // Default value is null

        back = findViewById(R.id.backBtn);

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
                Intent intent = new Intent(feedback.this, timetable.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_coursedetails) {
                Intent intent = new Intent(feedback.this, coursedetails.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_attendance) {
                Intent intent = new Intent(feedback.this, attendance.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_faculty) {
                Intent intent = new Intent(feedback.this, faculty.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_scorecard) {
                Intent intent = new Intent(feedback.this, scorecard.class);
                startActivity(intent);
            }
            else if (item.getItemId() == R.id.nav_announcements) {
                Intent intent = new Intent(feedback.this, notification.class);
                startActivity(intent);
            }
            else if (item.getItemId() == R.id.nav_profile) {
                Intent intent = new Intent(feedback.this, profile.class);
                startActivity(intent);
            }
            drawerLayout.closeDrawers();
            return true;
        });

        // Right Navigation Drawer
        NavigationView navViewRight = findViewById(R.id.nav_view_right);
        navViewRight.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_dashboard) {
                Intent intent = new Intent(feedback.this, dashboard.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(feedback.this, MainActivity.class));
                finish();
            }
            drawerLayout.closeDrawers();
            return true;
        });

        ImageView openRightDrawerButton = findViewById(R.id.open_right_drawer_button);
        openRightDrawerButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

        courseSpinner = findViewById(R.id.course);
        submitButton = findViewById(R.id.submit);
        resetButton = findViewById(R.id.reset);

        // Set the color of the submit button to #D97769
        submitButton.setBackgroundColor(android.graphics.Color.parseColor("#D97769"));

        // Sample data for the spinner
        String[] courses = {"Computational Mathematics", "Data Structures & Algorithms", "Operating Systems", "Linux Programming"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(adapter);

        // Initialize RatingBars
        ratingBars[0] = findViewById(R.id.rb_q1);
        ratingBars[1] = findViewById(R.id.rb_q2);
        ratingBars[2] = findViewById(R.id.rb_q3);
        ratingBars[3] = findViewById(R.id.rb_q4);
        ratingBars[4] = findViewById(R.id.rb_q5);
        ratingBars[5] = findViewById(R.id.rb_q6);

        // Set listener for Reset button
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAll();
            }
        });

        // Set listener for Submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if all ratings are given
                StringBuilder missingRatings = new StringBuilder();
                String selectedCourse = courseSpinner.getSelectedItem().toString();

                for (int i = 0; i < ratingBars.length; i++) {
                    if (ratingBars[i].getRating() == 0) {
                        missingRatings.append("Question ").append(i + 1).append(" rating is not given.\n");
                    }
                }

                // If there are missing ratings, show an alert
                if (missingRatings.length() > 0) {
                    new android.app.AlertDialog.Builder(feedback.this)
                            .setTitle("Missing Ratings")
                            .setMessage("All questions need to be answered!")
                            .setPositiveButton("OK", null)
                            .show();
                } else {
                    // Prepare data to submit to Firestore
                    String courseKey = selectedCourse.toLowerCase().replace(" ", ""); // e.g., "course1"

                    // Create a map to hold the ratings
                    Map<String, Object> ratings = new HashMap<>();
                    for (int i = 0; i < ratingBars.length; i++) {
                        ratings.put("q" + (i + 1), (int) ratingBars[i].getRating());
                    }

                    // Update Firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("feedbackDetails")
                            .document(userId)
                            .update(courseKey + "." + courseKey, ratings)
                            .addOnSuccessListener(aVoid -> {
                                // Submission successful
                                new android.app.AlertDialog.Builder(feedback.this)
                                        .setTitle("Submission Successful")
                                        .setMessage("Feedback for " + selectedCourse + " has been submitted successfully.")
                                        .setPositiveButton("OK", null)
                                        .show();
                                resetAll();
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure
                                new android.app.AlertDialog.Builder(feedback.this)
                                        .setTitle("Error")
                                        .setMessage("Failed to submit feedback: " + e.getMessage())
                                        .setPositiveButton("OK", null)
                                        .show();
                            });
                }
            }
        });

    }

    // Method to reset all RatingBars and Spinner
    private void resetAll() {
        for (RatingBar ratingBar : ratingBars) {
            ratingBar.setRating(0);
        }
//        courseSpinner.setSelection(0);
        showToast("All inputs have been reset");
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
