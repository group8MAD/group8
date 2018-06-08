package it.polito.mad.group8;

public class OngoingTransaction {

    private String requesterUid;
    private String requesterNickname;
    private String requesterImageUri;
    private String bookIsbn;
    private String BookTitle;
    private String startDate;
    private String endDate;
    private String city;
    private String province;
    private String bookOwnerUid;

    public OngoingTransaction(){
        requesterUid = "";
        requesterNickname = "";
        requesterImageUri = "";
        bookIsbn = "";
        BookTitle = "";
        startDate = "";
        endDate = "";
        city = "";
        province = "";
        bookOwnerUid = "";
    }

    public String getRequesterUid() {
        return requesterUid;
    }

    public void setRequesterUid(String requesterUid) {
        this.requesterUid = requesterUid;
    }

    public String getRequesterNickname() {
        return requesterNickname;
    }

    public void setRequesterNickname(String requesterNickname) {
        this.requesterNickname = requesterNickname;
    }

    public String getRequesterImageUri() {
        return requesterImageUri;
    }

    public void setRequesterImageUri(String requesterImageUri) {
        this.requesterImageUri = requesterImageUri;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    public String getBookTitle() {
        return BookTitle;
    }

    public void setBookTitle(String bookTitle) {
        BookTitle = bookTitle;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getBookOwnerUid() {
        return bookOwnerUid;
    }

    public void setBookOwnerUid(String bookOwnerUid) {
        this.bookOwnerUid = bookOwnerUid;
    }
}
