package com.example.plearningapp.todo;

public class Task {
    private String name, subject, date;
    private boolean isExpanded;

    public Task(String name, String subject, String date) {
        this.name = name;
        this.subject = subject;
        this.date = date;
        this.isExpanded = false;
    }

    public String getName() {
        return name;
    }

    public String getSubject() {
        return subject;
    }

    public String getDate() {
        return date;
    }
    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}
