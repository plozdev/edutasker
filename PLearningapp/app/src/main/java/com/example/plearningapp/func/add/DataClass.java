package com.example.plearningapp.func.add;

public class DataClass {
    private String title, subject, description, userId;



    public DataClass(String title, String description, String subject) {
        this.title = title;
        this.description = description;
        this.subject = subject;
    }

    public String getTitle() {
        return title;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }
}
