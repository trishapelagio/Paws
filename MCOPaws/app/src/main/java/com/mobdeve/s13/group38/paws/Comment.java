package com.mobdeve.s13.group38.paws;

public class Comment {
    private String comment;
    private String user;
    private String datePosted;

    public Comment(String comment, String user, String datePosted){
        this.comment = comment;
        this.user = user;
        this.datePosted = datePosted;
    }

    public String getComment() {
        return comment;
    }

    public String getUser() {
        return user;
    }

    public String getDatePosted() {
        return datePosted;
    }
}
