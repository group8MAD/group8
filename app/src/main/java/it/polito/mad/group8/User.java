package it.polito.mad.group8;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by alvaro on 13/4/18.
 */

public class User {

    private String name;
    private String email;
    private String biography;
    private String imageUri;

    public User() {
        this.name = "";
        this.email = "";
        this.biography = "";
        this.imageUri = "";
    }

    public User(String name, String email, String biography) {
        this.name = name;
        this.email = email;
        this.biography = biography;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
