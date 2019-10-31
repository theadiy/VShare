package org.terna.vshare;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    private ListView mVideosListView;
    private List<Video> mVideosList = new ArrayList<>();
    private VideoAdapter mVideoAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //assign video
        mVideosListView = (ListView) findViewById(R.id.videoListView);

        //create videos

        Video v1 = new Video("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4");
        Video v2 = new Video("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4");
        Video v3 = new Video("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4");
        Video v4 = new Video("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4");
        Video v5 = new Video("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4");

        mVideosList.add(v1);
        mVideosList.add(v2);
        mVideosList.add(v3);
        mVideosList.add(v4);
        mVideosList.add(v5);


        /***populate video list to adapter**/
        mVideoAdapter = new VideoAdapter(this, mVideosList);
        mVideosListView.setAdapter(mVideoAdapter);
    }
}
