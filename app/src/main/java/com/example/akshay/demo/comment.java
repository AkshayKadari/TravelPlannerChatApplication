package com.example.akshay.demo;

import java.util.Date;

/**
 * Created by akshay on 4/18/2017.
 */


public class comment {

    public comment(){}

    public comment(String comment, Date date) {
        this.comment = comment;
        this.date = date;
    }

    String comment;
    Date date;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
