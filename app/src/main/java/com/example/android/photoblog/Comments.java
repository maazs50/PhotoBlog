package com.example.android.photoblog;

import java.util.Date;

public class Comments {
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUser_d() {
        return user_d;
    }

    public void setUser_d(String user_d) {
        this.user_d = user_d;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Comments(String message, String user_d, Date timeStamp) {
        this.message = message;
        this.user_d = user_d;
        this.timeStamp = timeStamp;
    }

    private String message,user_d;
    private Date timeStamp;
}
