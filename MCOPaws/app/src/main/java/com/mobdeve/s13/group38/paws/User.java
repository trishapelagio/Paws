package com.mobdeve.s13.group38.paws;

import java.util.ArrayList;
import java.util.Date;

public class User {
    private String email;
    private String password;
    private String gender;
    private String name;
    private String breed;
    private String birthday;
    private String description;
    private String profilepic;

    public User(String email, String password, String gender, String name, String breed, String birthday, String description, String profilepic){
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.name = name;
        this.breed = breed;
        this.birthday = birthday;
        this.description = description;
        this.profilepic = profilepic;
    }

    public String getEmail(){
        return this.email;
    }

    public String getPassword(){
        return this.password;
    }

    public String getGender(){
        return this.gender;
    }

    public String getName(){
        return this.name;
    }

    public String getBreed(){
        return this.breed;
    }

    public String getBirthday(){
        return this.birthday;
    }

    public String getDescription(){
        return this.description;
    }

    public String getProfilepic() {
        return this.profilepic;
    }

}
