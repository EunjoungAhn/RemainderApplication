package com.example.remainderapplication;

public class Reminder {
    String content;
    String time;

    public Reminder() {

    }

    public Reminder(String content, String time) {
        this.content = content;
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
