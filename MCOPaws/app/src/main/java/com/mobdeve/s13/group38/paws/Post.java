package com.mobdeve.s13.group38.paws;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Post {
    private String user;
    private String photo;
    private ArrayList<String> likes;
    private ArrayList<String> comments;
    private String datePosted;
    private String description;

    public Post(String user, String photo, ArrayList<String> likes, ArrayList<String> comments, String datePosted, String description){
        this.user = user;
        this.photo = photo;
        this.likes = likes;
        this.comments = comments;
        this.datePosted = datePosted;
        this.description = description;
    }
    public String getUser(){
        return this.user;
    }

    public String getPhoto() {
        return this.photo;
    }

    public ArrayList<String> getLikes(){
        return this.likes;
    }

    public ArrayList<String> getComments(){
        return this.comments;
    }

    public String getDatePosted(){

        return datePosted;
    }

    public String getDescription(){
        return this.description;
    }

//    public String getFormattedDate() {
//        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
//
//        String formattedDate = formatter.format(this.datePosted);
//        return formattedDate;
//    }
}
