package it.polito.mad.group8;

import java.util.ArrayList;

/**
 * Created by alvaro on 13/4/18.
 */

public class User {

    private String name;
    private String email;
    private String biography;
    private String imageUri;
    private String direction;
    private String province;
    private String city;
    private String cap;

    private String nickname;

    private ArrayList<Book> booksSaved; //if we create an arraylist of books that the user saves and each time, the user saves a new book in the arraylist

    public User() {
        this.name = "";
        this.email = "";
        this.biography = "";
        this.imageUri = "";
        this.cap = "";
        this.city = "";
        this.direction="";
        this.province="";
        this.nickname ="";

    }


    public User(String name, String email, String biography, String direction, String province, String city, String cap, String nickname) {
        this.name = name;
        this.email = email;
        this.biography = biography;

        this.direction=direction;
        this.province = province;
        this.city= city;
        this.nickname = nickname;
        this.cap=cap;


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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getImageUri() {
        return imageUri;

    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }


    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public ArrayList<Book> getBooksSaved() {
        return booksSaved;
    }

    public void setBooksSaved(ArrayList<Book> booksSaved) {

        for(Book book : booksSaved){
            this.booksSaved.add(book);
        }

    }

    public String printBooksSaved(){
        String list = "";
        for(Book book: booksSaved){
            list = list.concat(book.getTitle().toString() +",");
        }
        return list;
    }

    public ArrayList<Book> getBookList(){
        ArrayList<Book> list = new ArrayList<Book>();
        if(booksSaved !=null)
        for(Book book : booksSaved){
            list.add(book);
        }
        return list;
    }

}
