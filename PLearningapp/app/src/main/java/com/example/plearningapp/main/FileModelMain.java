package com.example.plearningapp.main;
public class FileModelMain {
    private String name, date, subject;

    public FileModelMain(String name, String date, String subject) {
        this.name = name;
        this.date = date;
        this.subject = subject;
    }
    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getSubject() {
        return subject;
    }
}
