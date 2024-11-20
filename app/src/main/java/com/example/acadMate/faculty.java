package com.example.acadMate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

//import com.example.profile_page.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class faculty extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    ImageView bk1;
    ListView listView;
    ArrayList<FacultyMember> facultyList;
    ListAdapter adapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty);

//navigation and toolbar
        //back button
        bk1 = findViewById(R.id.bk1);
        bk1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        // toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.tool_bar);
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
            if (item.getItemId() == R.id.profile) {
                Intent intent = new Intent(faculty.this, profile.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.attendance) {
                Intent intent = new Intent(faculty.this, attendance.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.announcement) {
                Intent intent = new Intent(faculty.this, notification.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.scorecard) {
                Intent intent = new Intent(faculty.this, scorecard.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.timetable) {
                Intent intent = new Intent(faculty.this, timetable.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.feedback) {
                Intent intent = new Intent(faculty.this, feedback.class);
                startActivity(intent);
            }
            else if (item.getItemId() == R.id.coursedetails) {
                Intent intent = new Intent(faculty.this, coursedetails.class);
                startActivity(intent);
            }
            drawerLayout.closeDrawers();
            return true;
        });

        // Right Navigation Drawer
        NavigationView navViewRight = findViewById(R.id.nav_view_right);
        navViewRight.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_dashboard) {
                Intent intent = new Intent(faculty.this, dashboard.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(faculty.this, MainActivity.class));
                finish();
            }
            drawerLayout.closeDrawers();
            return true;
        });

        // Button to open right drawer
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        ImageView openRightDrawerButton = findViewById(R.id.profile);
        openRightDrawerButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Setup ListView
        listView = findViewById(R.id.listview);
        facultyList = new ArrayList<>();
        adapter = new ListAdapter(this,facultyList );
        listView.setAdapter(adapter);
        fetchFacultyData();
    }
    private void fetchFacultyData() {
        db.collection("facultyDetails")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(faculty.this, "Error fetching data: " + error, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    facultyList.clear();
                    for (QueryDocumentSnapshot doc : value) {
                        FacultyMember faculty = doc.toObject(FacultyMember.class);
                        facultyList.add(faculty);
                    }
                    adapter.notifyDataSetChanged();
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