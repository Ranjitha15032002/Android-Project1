package com.example.acadMate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

//import com.example.profile_page.R;
import com.google.firebase.auth.FirebaseAuth;

public class dashboard extends AppCompatActivity {
    private ImageView attendance, announcement, timetable, faculty, course, feedback, profile, scorecard, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        attendance = findViewById(R.id.attendanceBtn);
        announcement = findViewById(R.id.announcementBtn);
        timetable = findViewById(R.id.timetableBtn);
        faculty = findViewById(R.id.facultyBtn);
        course = findViewById(R.id.courseBtn);
        feedback = findViewById(R.id.feedbackBtn);
        profile = findViewById(R.id.profileBtn);
        scorecard = findViewById(R.id.scorecardBtn);
        logoutButton = findViewById(R.id.logoutBtn);

        // Initialize toolbar without title and home button
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Button listeners
        attendance.setOnClickListener(view -> startActivity(new Intent(dashboard.this, attendance.class)));
        announcement.setOnClickListener(view -> startActivity(new Intent(dashboard.this, notification.class)));
        timetable.setOnClickListener(view -> startActivity(new Intent(dashboard.this, timetable.class)));
        faculty.setOnClickListener(view -> startActivity(new Intent(dashboard.this, faculty.class)));
        course.setOnClickListener(view -> startActivity(new Intent(dashboard.this, coursedetails.class)));
        feedback.setOnClickListener(view -> startActivity(new Intent(dashboard.this, feedback.class)));
        profile.setOnClickListener(view -> startActivity(new Intent(dashboard.this, profile.class)));
        scorecard.setOnClickListener(view -> startActivity(new Intent(dashboard.this, scorecard.class)));

        // Direct Logout button
        if (logoutButton != null) {
            logoutButton.setOnClickListener(view -> {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
        } else {
            Log.e("DashboardActivity", "Logout ImageView is null");
        }

        Log.d("DashboardActivity", "Layout inflated");
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
