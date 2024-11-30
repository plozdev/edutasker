package com.example.plearningapp.file.main;

public class FileModelMain {
    private String name, date, subject, downloadUrl;

    public FileModelMain(String name, String date, String subject, String downloadUrl) {
        this.name = name;
        this.date = date;
        this.subject = subject;
        this.downloadUrl = downloadUrl;
    }
    public String getDownloadUrl() {
        return downloadUrl;
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
