package org.terna.vshare.ui.home;


import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.Serializable;

public class HomeViewModel implements Serializable {

    String likeCount;
    String owner;
    String videoDescription;
    String videoId;
    String videoName;
    String videoTimeDuration;
    String videoUploadDate;
    Bitmap videoThumbnailImageView;

    public HomeViewModel(String likeCount,
                         String owner,
                         String videoDescription,
                         String videoId,
                         String videoName,
                         String videoTimeDuration,
                         String videoUploadDate,
                         Bitmap videoThumbnailImageView){

        this.likeCount = likeCount;
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