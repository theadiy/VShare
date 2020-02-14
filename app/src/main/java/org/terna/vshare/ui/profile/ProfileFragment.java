package org.terna.vshare.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.terna.vshare.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    Button profileVideoUploadButton;
    Intent fileIntent;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();




    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        Log.e("ProfileFragment","path : "+resultCode);



        switch (requestCode){
            case 10:
                if(resultCode == -1){


                    Log.e("ProfileFragment","path : "+data.getData().getPath());
                    Toast.makeText(this.getContext(),"SELECTED FILE"+data.getData(),Toast.LENGTH_SHORT).show();

                    Uri file = data.getData();
                    String path = file.getPath();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                    String format = simpleDateFormat.format(new Date());

                    StorageReference videoRef = storageRef.child("videos/"+file.getLastPathSegment()+format);
                    UploadTask uploadTask = videoRef.putFile(file);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Toast.makeText(getContext(),"Video uploaded succesfully.",Toast.LENGTH_LONG).show();


                        }
                    });
                }
        }

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_profile, container, false);
        //final TextView textView = root.findViewById(R.id.text_gallery);
        profileVideoUploadButton = root.findViewById(R.id.profile_uploads_Button);
        profileViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);

                profileVideoUploadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        fileIntent.setType("*/*");
                        startActivityForResult(fileIntent,10);
                        Toast.makeText(root.getContext(),"Uploading video",Toast.LENGTH_SHORT).show();


                    }
                });



            }



        });


        return root;
    }


}
