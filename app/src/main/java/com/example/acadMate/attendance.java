package com.example.acadMate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

//import com.example.profile_page.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;


public class attendance extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private FirebaseFirestore db;

    private ImageView back;
    private TextView code1, sub1, totclss1, classp1, classab1, per1;
    private TextView code2, sub2, totclss2, classp2, classab2, per2;
    private TextView code3, sub3, totclss3, classp3, classab3, per3;
    private TextView code4, sub4, totclss4, classp4, classab4, per4;
    private TextView code5, sub5, totclss5, classp5, classab5, per5;
    private String profileId;  // To store logged-in profile ID
    //private String[] semesters = {"First Semester", "Second Semester", "Third Semester"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        Intent receive = getIntent();

        Spinner yearSemesterSpinner = findViewById(R.id.spinner_semesters);

        // Initialize all TextViews for 5 subjects
        code1 = findViewById(R.id.code1);
        sub1 = findViewById(R.id.sub1);
        totclss1 = findViewById(R.id.totclass1);
        classp1 = findViewById(R.id.clssp1);
        classab1 = findViewById(R.id.clssab1);
        per1 = findViewById(R.id.per1);

        code2 = findViewById(R.id.code2);
        sub2 = findViewById(R.id.sub2);
        totclss2 = findViewById(R.id.totclass2);
        classp2 = findViewById(R.id.clssp2);
        classab2 = findViewById(R.id.clssab2);
        per2 = findViewById(R.id.per2);

        code3 = findViewById(R.id.code3);
        sub3 = findViewById(R.id.sub3);
        totclss3 = findViewById(R.id.totclass3);
        classp3 = findViewById(R.id.clssp3);
        classab3 = findViewById(R.id.clssab3);
        per3 = findViewById(R.id.per3);

        code4 = findViewById(R.id.code4);
        sub4 = findViewById(R.id.sub4);
        totclss4 = findViewById(R.id.totclass4);
        classp4 = findViewById(R.id.clssp4);
        classab4 = findViewById(R.id.clssab4);
        per4 = findViewById(R.id.per4);

        code5 = findViewById(R.id.code5);
        sub5 = findViewById(R.id.sub5);
        totclss5 = findViewById(R.id.totclass5);
        classp5 = findViewById(R.id.clssp5);
        classab5 = findViewById(R.id.clssab5);
        per5 = findViewById(R.id.per5);

        back = findViewById(R.id.backBtn);

        // Firebase Firestore instance
        db = FirebaseFirestore.getInstance();

        // Spinner adapter setup
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.year_semester_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSemesterSpinner.setAdapter(adapter);

        // Toolbar and Drawer setup
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);

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
            handleLeftNavClick(item.getItemId());
            drawerLayout.closeDrawers();
            return true;
        });

        // Right Navigation Drawer
        NavigationView navViewRight = findViewById(R.id.nav_view_right);
        navViewRight.setNavigationItemSelectedListener(item -> {
            handleRightNavClick(item.getItemId());
            drawerLayout.closeDrawers();
            return true;
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Button to open the right drawer
        ImageView openRightDrawerButton = findViewById(R.id.open_right_drawer_button);
        openRightDrawerButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));

        yearSemesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        fetchFirestoreData("attendanceDetails", "lalit.mitmpl2023@learner.manipal.edu", "semester1");  // Fetch Semester 1 data
                        break;
                    case 1:
                        fetchFirestoreData("attendanceDetails", "lalit.mitmpl2023@learner.manipal.edu", "semester2");  // Fetch Semester 2 data
                        break;
                    case 2:
                        fetchFirestoreData("attendanceDetails", "lalit.mitmpl2023@learner.manipal.edu", "semester3");  // Fetch Semester 3 data
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optionally handle the case where no semester is selected
            }
        });

    }

    private void fetchFirestoreData(String collectionName, String profile, String semesterField) {
        DocumentReference docRef = db.collection(collectionName).document(profile);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Retrieve the specific semester map from the document
                        Map<String, Object> semesterData = (Map<String, Object>) document.get(semesterField);

                        if (semesterData != null) {
                            // Safely cast or convert Long to Double
                            Double clssab1 = convertToDouble(semesterData.get("clssab1"));
                            Double clssab2 = convertToDouble(semesterData.get("clssab2"));
                            Double clssab3 = convertToDouble(semesterData.get("clssab3"));
                            Double clssab4 = convertToDouble(semesterData.get("clssab4"));
                            Double clssab5 = convertToDouble(semesterData.get("clssab5"));

                            Double clssp1 = convertToDouble(semesterData.get("clssp1"));
                            Double clssp2 = convertToDouble(semesterData.get("clssp2"));
                            Double clssp3 = convertToDouble(semesterData.get("clssp3"));
                            Double clssp4 = convertToDouble(semesterData.get("clssp4"));
                            Double clssp5 = convertToDouble(semesterData.get("clssp5"));

                            String code1Val = (String) semesterData.get("code1");
                            String code2Val = (String) semesterData.get("code2");
                            String code3Val = (String) semesterData.get("code3");
                            String code4Val = (String) semesterData.get("code4");
                            String code5Val = (String) semesterData.get("code5");

                            Double per1Val = convertToDouble(semesterData.get("per1"));
                            Double per2Val = convertToDouble(semesterData.get("per2"));
                            Double per3Val = convertToDouble(semesterData.get("per3"));
                            Double per4Val = convertToDouble(semesterData.get("per4"));
                            Double per5Val = convertToDouble(semesterData.get("per5"));

                            String sub1Val = (String) semesterData.get("sub1");
                            String sub2Val = (String) semesterData.get("sub2");
                            String sub3Val = (String) semesterData.get("sub3");
                            String sub4Val = (String) semesterData.get("sub4");
                            String sub5Val = (String) semesterData.get("sub5");

                            Double totclass1Val = convertToDouble(semesterData.get("totclss1"));
                            Double totclass2Val = convertToDouble(semesterData.get("totclss2"));
                            Double totclass3Val = convertToDouble(semesterData.get("totclss3"));
                            Double totclass4Val = convertToDouble(semesterData.get("totclss4"));
                            Double totclass5Val = convertToDouble(semesterData.get("totclss5"));

                            // Update the UI with the retrieved data
                            updateUI(clssab1, clssab2, clssab3, clssab4, clssab5,
                                    clssp1, clssp2, clssp3, clssp4, clssp5,
                                    code1Val, code2Val, code3Val, code4Val, code5Val,
                                    per1Val, per2Val, per3Val, per4Val, per5Val,
                                    sub1Val, sub2Val, sub3Val, sub4Val, sub5Val,
                                    totclass1Val, totclass2Val, totclass3Val, totclass4Val, totclass5Val);
                        } else {
                            Toast.makeText(attendance.this, "Semester data does not exist", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(attendance.this, "Profile does not exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("MainActivity", "Failed to retrieve data: ", task.getException());
                }
            }
        });
    }

    // Helper method to convert Long to Double if needed
    private Double convertToDouble(Object value) {
        if (value instanceof Long) {
            return ((Long) value).doubleValue();  // Convert Long to Double
        } else if (value instanceof Double) {
            return (Double) value;  // Return Double directly
        } else {
            return 0.0;  // Return 0.0 if value is null or of another type
        }
    }




    private void updateUI(Double clssab1, Double clssab2, Double clssab3, Double clssab4, Double clssab5,
                          Double clssp1, Double clssp2, Double clssp3, Double clssp4, Double clssp5,
                          String code1Val, String code2Val, String code3Val, String code4Val, String code5Val,
                          Double per1Val, Double per2Val, Double per3Val, Double per4Val, Double per5Val,
                          String sub1Val, String sub2Val, String sub3Val, String sub4Val, String sub5Val,
                          Double totclass1Val, Double totclass2Val, Double totclass3Val, Double totclass4Val, Double totclass5Val) {

        code1.setText(code1Val);
        code2.setText(code2Val);
        code3.setText(code3Val);
        code4.setText(code4Val);
        code5.setText(code5Val);

        sub1.setText(sub1Val);
        sub2.setText(sub2Val);
        sub3.setText(sub3Val);
        sub4.setText(sub4Val);
        sub5.setText(sub5Val);

        totclss1.setText(String.valueOf(totclass1Val));
        totclss2.setText(String.valueOf(totclass2Val));
        totclss3.setText(String.valueOf(totclass3Val));
        totclss4.setText(String.valueOf(totclass4Val));
        totclss5.setText(String.valueOf(totclass5Val));

        classp1.setText(String.valueOf(clssp1));
        classp2.setText(String.valueOf(clssp2));
        classp3.setText(String.valueOf(clssp3));
        classp4.setText(String.valueOf(clssp4));
        classp5.setText(String.valueOf(clssp5));

        classab1.setText(String.valueOf(clssab1));
        classab2.setText(String.valueOf(clssab2));
        classab3.setText(String.valueOf(clssab3));
        classab4.setText(String.valueOf(clssab4));
        classab5.setText(String.valueOf(clssab5));

        per1.setText(String.valueOf(per1Val));
        per2.setText(String.valueOf(per2Val));
        per3.setText(String.valueOf(per3Val));
        per4.setText(String.valueOf(per4Val));
        per5.setText(String.valueOf(per5Val));
    }

    private void handleLeftNavClick(int itemId) {
        if (itemId == R.id.nav_timetable) {
            Intent intent = new Intent(attendance.this, timetable.class);
            startActivity(intent);
        } else if (itemId == R.id.nav_coursedetails) {
            Intent intent = new Intent(attendance.this, coursedetails.class);
            startActivity(intent);
        } else if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(attendance.this, profile.class);
                startActivity(intent);
        } else if (itemId == R.id.nav_faculty) {
            Intent intent = new Intent(attendance.this, faculty.class);
            startActivity(intent);
        } else if (itemId== R.id.nav_scorecard) {
            Intent intent = new Intent(attendance.this, scorecard.class);
            startActivity(intent);
        }
        else if (itemId == R.id.nav_announcements) {
            Intent intent = new Intent(attendance.this, notification.class);
            startActivity(intent);
        }
        else if (itemId == R.id.nav_feedback) {
            Intent intent = new Intent(attendance.this, feedback.class);
            startActivity(intent);
        }
        // Handle left navigation drawer clicks here
    }

    private void handleRightNavClick(int itemId) {
        if (itemId == R.id.nav_dashboard) {
            Intent intent = new Intent(attendance.this, dashboard.class);
            startActivity(intent);
        } else if (itemId == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(attendance.this, MainActivity.class));
            finish();
        }
    }

    // Handle right navigation drawer clicks here
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



