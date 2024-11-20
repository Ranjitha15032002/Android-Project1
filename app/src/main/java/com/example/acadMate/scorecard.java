package com.example.acadMate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

//import com.example.profile_page.R;
import com.google.android.material.navigation.NavigationView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class scorecard extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private Spinner semesterSpinner;
    private FirebaseFirestore db;
    private TableLayout scoreTable;
    private TextView cgpaTextView;
    private TextView gpaTextView;
    private ImageView bk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scorecard);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        scoreTable = findViewById(R.id.scoreTable);
        cgpaTextView = findViewById(R.id.cgpa);
        gpaTextView = findViewById(R.id.gpa);

        // toolbar
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
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
                Intent intent = new Intent(scorecard.this, timetable.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_coursedetails) {
                Intent intent = new Intent(scorecard.this, coursedetails.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_attendance) {
                Intent intent = new Intent(scorecard.this, attendance.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_faculty) {
                Intent intent = new Intent(scorecard.this, faculty.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_profile) {
                Intent intent = new Intent(scorecard.this, profile.class);
                startActivity(intent);
            }
            else if (item.getItemId() == R.id.nav_announcements) {
                Intent intent = new Intent(scorecard.this, notification.class);
                startActivity(intent);
            }
            else if (item.getItemId() == R.id.nav_feedback) {
                Intent intent = new Intent(scorecard.this, feedback.class);
                startActivity(intent);
            }
            drawerLayout.closeDrawers();
            return true;
        });

        // Right Navigation Drawer
        NavigationView navViewRight = findViewById(R.id.nav_view_right);
        navViewRight.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_dashboard) {
                Intent intent = new Intent(scorecard.this, dashboard.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(scorecard.this, MainActivity.class));
                finish();
            }
            drawerLayout.closeDrawers();
            return true;
        });

        // Button to open right drawer
        ImageView openRightDrawerButton = findViewById(R.id.profile);
        openRightDrawerButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));

        //back button
        bk = findViewById(R.id.bk1);
        bk.setOnClickListener(view -> {
            onBackPressed();
        });

        // Add Spinner setup code here
        setupSemesterSpinner();
    }

    private void setupSemesterSpinner() {
        semesterSpinner = findViewById(R.id.semesterSpinner);

        // Create an ArrayAdapter using the string array and the custom spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.semester_options, R.layout.spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_item);

        // Apply the adapter to the spinner
        semesterSpinner.setAdapter(adapter);

        // Set the listener for item selection
        semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSemester = parent.getItemAtPosition(position).toString();

                String semester = "";
                if(selectedSemester.equals("Semester 1"))
                    semester = "semester1";
                else if(selectedSemester.equals("Semester 2"))
                    semester = "semester2";
                else if(selectedSemester.equals("Semester 3"))
                    semester = "semester3";

                loadSemesterData(semester);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }


    private void loadSemesterData(String selectedSemester) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            showToast("User is not logged in. Redirecting to login.");
            Intent intent = new Intent(scorecard.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        String userEmail = currentUser.getEmail();



        // Assuming userEmail is used as the document ID in Firestore
        db.collection("scorecard")
                .document(userEmail)  // Directly access the document using the email ID as the document ID
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        showToast("Error loading data: " + error.getMessage());
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        String semesterKey = selectedSemester;
                        Object semesterData = documentSnapshot.get(semesterKey);

                        if (semesterData != null) {
                            Double cgpa = documentSnapshot.getDouble(semesterKey + ".cgpa");
                            Double gpa = documentSnapshot.getDouble(semesterKey + ".gpa");

                            cgpaTextView.setText(cgpa != null ? String.format("%.2f", cgpa) : "N/A");
                            gpaTextView.setText(gpa != null ? String.format("%.2f", gpa) : "N/A");

                            int childCount = scoreTable.getChildCount();
                            if (childCount > 1) {
                                scoreTable.removeViews(1, childCount - 1);
                            }

                            Object subjects = documentSnapshot.get(semesterKey + ".subjects");
                            if (subjects != null) {
                                for (int i = 1; i <= 6; i++) {
                                    String subjectKey = "subject" + i;
                                    if (documentSnapshot.contains(semesterKey + ".subjects." + subjectKey)) {
                                        addSubjectRow(
                                                documentSnapshot.getString(semesterKey + ".subjects." + subjectKey + ".code"),
                                                documentSnapshot.getString(semesterKey + ".subjects." + subjectKey + ".sname"),
                                                documentSnapshot.getString(semesterKey + ".subjects." + subjectKey + ".grade"),
                                                getLongFromDocument(documentSnapshot, semesterKey + ".subjects." + subjectKey + ".credit"),
                                                getLongFromDocument(documentSnapshot, semesterKey + ".subjects." + subjectKey + ".totalcredit")
                                        );
                                    }
                                }
                            }
                        } else {
                            showToast("No data found for this semester");
                        }
                    } else {
                        showToast("No data found for this semester");
                    }
                });
    }


    // Helper function to safely retrieve long values from Firestore
    private Long getLongFromDocument(DocumentSnapshot document, String fieldPath) {
        Object value = document.get(fieldPath);
        if (value instanceof Number) {
            return ((Number) value).longValue();  // Safely convert to long
        } else {
            return null;  // Return null if not a valid number
        }
    }




    private void addSubjectRow(String code, String name, String grade, Long credit, Long totalCredit) {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        // Add cells
        addCell(row, code);
        addCell(row, name);
        addCell(row, grade);
        addCell(row, String.valueOf(credit));
        addCell(row, String.valueOf(totalCredit));

        scoreTable.addView(row);
    }

    private void addCell(TableRow row, String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(16, 16, 16, 16);
        textView.setTextColor(getResources().getColor(R.color.black));
        row.addView(textView);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(findViewById(R.id.nav_view_left)) ||
                drawerLayout.isDrawerOpen(findViewById(R.id.nav_view_right))) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}
