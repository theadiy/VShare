package org.terna.vshare;


import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.Serializable;

public class HomeViewModel implements Serializable {

    public String likeCount;
    public String commentCount;
    public String owner;
    public String videoDescription;
    public String videoId;
    public String videoName;
    public String videoTimeDuration;
    public String videoUploadDate;
    public transient Bitmap videoThumbnailImageView;


    public HomeViewModel(String likeCount,
                         String commentCount,
                         String owner,
                         String videoDescription,
                         String videoId,
                         String videoName,
                         String videoTimeDuration,
                         String videoUploadDate,
                         Bitmap videoThumbnailImageView){

        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.owner = owner;
        this.videoDescription = videoDescription;
        this.videoId = videoId;
        this.videoName = videoName;
        this.videoTimeDuration = videoTimeDuration;
        this.videoUploadDate = videoUploadDate;
        this.videoThumbnailImageView = videoThumbnailImageView;

    }

    public HomeViewModel(){

    }

}