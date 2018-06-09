package it.polito.mad.group8;

public class Review {
    private String reviewerUid;
    private String reviewerNickname;
    private String reviewerImageUri;
    private String title;
    private String comment;
    private float rating;

    public Review(){
        reviewerUid = "";
        reviewerNickname = "";
        reviewerImageUri = "";
        title = "";
        comment = "";
        rating = 0;
    }

    public String getReviewerUid() {
        return reviewerUid;
    }

    public void setReviewerUid(String reviewerUid) {
        this.reviewerUid = reviewerUid;
    }

    public String getReviewerNickname() {
        return reviewerNickname;
    }

    public void setReviewerNickname(String reviewerNickname) {
        this.reviewerNickname = reviewerNickname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getReviewerImageUri() {
        return reviewerImageUri;
    }

    public void setReviewerImageUri(String reviewerImageUri) {
        this.reviewerImageUri = reviewerImageUri;
    }
}
