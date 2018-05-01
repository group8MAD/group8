package it.polito.mad.group8;

import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Book {

    private String title;
    private String authors;
    private String thumbnail;
    private String publisher;
    private String editionYear;

    public Book() {
        this.title = "";
        this.authors = "";
        this.thumbnail = "";
        this.publisher = "";
        this.editionYear = "";
    }

    public Book(String title, String authors, String thumbnail, String publisher, String editionYear, String condition, String ownerID) {
        this.title = title;
        this.authors = authors;
        this.thumbnail = thumbnail;
        this.publisher = publisher;
        this.editionYear = editionYear;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getEditionYear() {
        return editionYear;
    }

    public void setEditionYear(String editionYear) {
        this.editionYear = editionYear;
    }

}