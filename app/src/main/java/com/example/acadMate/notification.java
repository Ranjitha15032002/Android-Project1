package com.example.acadMate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;

//import com.example.profile_page.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class notification extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private ListView listView;
    private List<String> titles = new ArrayList<>();
    private List<String> contents = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ImageView back, profile;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        back = findViewById(R.id.backBtn);
        drawerLayout = findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navViewLeft = findViewById(R.id.nav_view_left);
        navViewLeft.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_profile) {
                Intent intent = new Intent(notification.this, profile.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_coursedetails) {
                Intent intent = new Intent(notification.this, coursedetails.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_timetable) {
                Intent intent = new Intent(notification.this, timetable.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_faculty) {
                Intent intent = new Intent(notification.this, faculty.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_scorecard) {
                Intent intent = new Intent(notification.this, scorecard.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_attendance) {
                Intent intent = new Intent(notification.this, attendance.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_feedback) {
                Intent intent = new Intent(notification.this, feedback.class);
                startActivity(intent);
            }
            drawerLayout.closeDrawers();
            return true;
        });

        NavigationView navViewRight = findViewById(R.id.nav_view_right);
        navViewRight.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_dashboard) {
                Intent intent = new Intent(notification.this, dashboard.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(notification.this, MainActivity.class));
                finish();
            }
            drawerLayout.closeDrawers();
            return true;
        });

        ImageView openRightDrawerButton = findViewById(R.id.open_right_drawer_button);
        openRightDrawerButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        listView = findViewById(R.id.announcement_list);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles);
        listView.setAdapter(adapter);

        // Initialize Firestore Database
        db = FirebaseFirestore.getInstance();

        fetchAnnouncements();

        listView.setOnItemClickListener((parent, view, position, id) -> showAnnouncementDialog(titles.get(position), contents.get(position)));
    }

    private void fetchAnnouncements() {
        db.collection("notificationDetails")
                .document("announcements")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    titles.clear();
                    contents.clear();

                    if (documentSnapshot.exists()) {
                        // Loop through the notification fields (ann1, ann2, etc.)
                        for (int i = 1; i <= 10; i++) {
                            String key = "ann" + i;
                            String content = documentSnapshot.getString(key);

                            if (content != null && !content.isEmpty()) {
                                titles.add("Announcement "+i);
                                contents.add(content);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        showToast("No announcements found.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Notification", "Failed to fetch announcements.", e);
                    showToast("Failed to fetch announcements.");
                });
    }

    private void showAnnouncementDialog(String title, String content) {
        DialogFragment dialogFragment = AnnouncementDialogFragment.newInstance(title, content);
        dialogFragment.show(getSupportFragmentManager(), "announcement_dialog");
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
