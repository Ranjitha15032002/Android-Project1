package com.example.acadMate;

public class FacultyMember {
    private String name;
    private String designation;
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDepartment(String designation) {
        this.designation = designation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    // Default constructor required for Firestore
    public FacultyMember() {}

    public FacultyMember(String name, String designation, String email) {
        this.name = name;
        this.designation = designation;
        this.email = email;
    }

    // Getters and setters
    // ... (same as before)

}