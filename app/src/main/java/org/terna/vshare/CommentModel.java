package org.terna.vshare;

public class CommentModel {

    String  cid, comment, createdOn, uid, uEmail, uDp, uName;

    public CommentModel() {


    }

    public CommentModel( String cid, String comment, String timestamp, String uid, String uEmail, String uDp, String uName) {

        this.cid = cid;
        this.comment = comment;
        this.createdOn = timestamp;
        this.uid = uid;
        this.uEmail = uEmail;
        this.uDp = uDp;
        this.uName = uName;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimestamp() {
        return createdOn;
    }

    public void setTimestamp(String timestamp) {
        this.createdOn = timestamp;
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