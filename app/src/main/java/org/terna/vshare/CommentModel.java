package org.terna.vshare;

public class CommentModel {

    String com_id, com_Des, createdOn, uid, uEmail, uDp, uName;

    public CommentModel() {


    }

    public CommentModel(String com_id, String com_Des, String createdOn, String uid, String uEmail, String uDp, String uName) {

        this.com_id = com_id;
        this.com_Des = com_Des;
        this.createdOn = createdOn;
        this.uid = uid;
        this.uEmail = uEmail;
        this.uDp = uDp;
        this.uName = uName;
    }

    public String getCom_id() {
        return com_id;
    }

    public void setCom_id(String com_id) {
        this.com_id = com_id;
    }

    public String getCom_Des() {
        return com_Des;
    }

    public void setCom_Des(String com_Des) {
        this.com_Des = com_Des;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }
}