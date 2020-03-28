package org.terna.vshare.ui.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.ybq.android.spinkit.SpriteFactory;
import com.github.ybq.android.spinkit.Style;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.terna.vshare.HomeViewModel;
import org.terna.vshare.R;
import org.terna.vshare.UploadVideoActivity;

import java.io.File;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;


public class ProfileFragment extends Fragment {

    String TAG = "ProfileFragment";
    private ProfileViewModel profileViewModel;
    Button profileVideoUploadButton;
    Intent fileIntent;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage storage = getInstance();
    StorageReference storageRef = storage.getReference();

    //path where images of pp will be stored
    String storagePath= "Users_Profile_Picture/";

    //views from xml
    ImageView profile_imageView;
    TextView profile_name_textView, profile_email_textView, profile_username_textView;
    FloatingActionButton edit;

    ProgressDialog pd;

    //permissions
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    private static final int UPLOAD_VIDEO = 500;
    //arrays of permissions to be requested
    String cameraPermission[];
    String storagePermission[];

    //uri of picked image
    Uri image_uri;
    String profilepicture;

    //own feeds
    private HomeViewModel homeViewModel;
    RecyclerView feedRecyclerView;
    FeedAdapter myAdapter;
    ArrayList<HomeViewModel> homeViewModels = new ArrayList<>();
    private DatabaseReference feedsdatabaseReference;
    private StorageReference feedsstorageReference;
    private DatabaseReference postDatabase;
    private FirebaseStorage postStorage;
    Bitmap thumbnail;
    ProgressBar spinKitView;
    SwipeRefreshLayout videoRefreshlayout;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //Inflate the layout
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        storageRef = getInstance().getReference();

        //init arrays of permissions
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};



        //init views
         profile_imageView = view.findViewById(R.id.profile_imageView);
         profile_name_textView = view.findViewById(R.id.profile_name_textView);
         profile_email_textView = view.findViewById(R.id.profile_email_textView);
         profile_username_textView = view.findViewById(R.id.profile_username_textView);
         edit = view.findViewById(R.id.editbutton);

         //init progress dialog
         pd = new ProgressDialog(getActivity());


        //recycler view
        feedRecyclerView = view.findViewById(R.id.profileFeedRecyclerView);


        feedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myAdapter = new FeedAdapter();
        feedRecyclerView.setAdapter(myAdapter);

        //Loading screen
        spinKitView = view.findViewById(R.id.profileSpin_kit);
        final Sprite sprite = SpriteFactory.create(Style.MULTIPLE_PULSE_RING);
        //sprite.setAnimationDelay(50000);
        sprite.setColor(android.graphics.Color.parseColor("#fa0842"));
        spinKitView.setIndeterminateDrawable(sprite);

        videoRefreshlayout = view.findViewById(R.id.profile_swipeRefreshrelativeLayout);
        videoRefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                myAdapter.notifyDataSetChanged();
                if(myAdapter.getItemCount() > 0){

                    videoRefreshlayout.setRefreshing(false);
                }

            }
        });

        //retrieving feeds
        postStorage = FirebaseStorage.getInstance();
        postDatabase = FirebaseDatabase.getInstance().getReference();

        feedsdatabaseReference = FirebaseDatabase.getInstance().getReference("Videos");



        feedsstorageReference = FirebaseStorage.getInstance().getReference("VideosThumbnail");
        Query ownFeedsquery = feedsdatabaseReference.orderByChild("owner").equalTo(user.getUid());



        ownFeedsquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                homeViewModels.clear();
                for (DataSnapshot feedsSnapshot : dataSnapshot.getChildren()){



                    final HashMap feedHashmasp = ((HashMap) feedsSnapshot.getValue());
                    final HomeViewModel newFeed = new HomeViewModel();
                    thumbnail = null;


                    newFeed.videoName = feedHashmasp.get("videoName").toString();
                    newFeed.videoDescription = feedHashmasp.get("videoDescription").toString();
                    newFeed.videoUploadDate = feedHashmasp.get("videoUploadDate").toString();
                    newFeed.videoTimeDuration = feedHashmasp.get("videoTimeDuration").toString();
                    newFeed.owner = feedHashmasp.get("owner").toString();
                    newFeed.likeCount = feedHashmasp.get("likeCount").toString();
                    newFeed.commentCount = feedHashmasp.get("commentCount").toString();
                    newFeed.videoName = feedHashmasp.get("videoName").toString();
                    newFeed.videoId = feedHashmasp.get("videoId").toString();
                    Log.e(TAG,"VIDEO ID -----------"+newFeed.videoId);


                    feedsstorageReference.child(feedHashmasp.get("videoId").toString()+".jpg").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {

                            thumbnail= BitmapFactory.decodeByteArray(bytes,0,bytes.length);


                            //Log.e(TAG,"Bitmap is not null -----"+newFeed.videoThumbnailImageView.getByteCount());
                            newFeed.videoThumbnailImageView = thumbnail;

                            if(newFeed != null && newFeed.videoThumbnailImageView != null){
                                homeViewModels.add(newFeed);

                                //sorting
                                Collections.reverse(homeViewModels);

                                myAdapter.notifyDataSetChanged();
                                if(myAdapter.getItemCount() > 0) {
                                    sprite.stop();
                                    videoRefreshlayout.setRefreshing(false);
                                    spinKitView.setVisibility(View.INVISIBLE);
                                }
                            }else
                            {
                                Log.e(TAG,"newfeed prolem occured");
                            }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG,"-----------------------------thumbnail problem"+e.getMessage());
                        }
                    });




                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

         //retrieve info using email saved in Db

        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //check until required data is received
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    //get data
                    String name = ""+ds.child("name").getValue();
                    String email = ""+ds.child("email").getValue();
                    String bio = ""+ds.child("bio").getValue();
                    String image = ""+ds.child("image").getValue();

                    //set data
                    profile_email_textView.setText(email);
                    profile_name_textView.setText(name);
                    profile_username_textView.setText(bio);

                    try{
                        Picasso.get().load(image).into(profile_imageView);
                    }
                    catch (Exception e){
                        Picasso.get().load(R.drawable.ic_profile_photo).into(profile_imageView);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // edit button click
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });

        //Upload Button Code

        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_profile, container, false);
        //final TextView textView = root.findViewById(R.id.text_gallery);
        profileVideoUploadButton = view.findViewById(R.id.profile_uploads_Button);

        profileVideoUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent uploadIntent = new Intent(getContext(), UploadVideoActivity.class);
                startActivity(uploadIntent);

            }
        });

        profileViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);




            }



        });


        return view ;
    }

    private boolean checkStoragePermission(){
        //check if permission is enabled or not
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }
    private void requestStoragePermission(){
        //request runtime storage permission
        requestPermissions(storagePermission, STORAGE_REQUEST_CODE);
    }


    private boolean checkCameraPermission(){
        //check if permission is enabled or not
        boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);


        boolean result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void requestCameraPermission(){
        //request runtime storage permission
        requestPermissions(cameraPermission, CAMERA_REQUEST_CODE);
    }


    private void showEditProfileDialog() {

        //Showing Dialog edit options here
        String options[] = {"Edit Profile Picture", "Edit Username", "Edit Bio"};
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //setTitle
        builder.setTitle("Choose Action");
        //set items to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle dialog item clicks
                if (which == 0){
                    //Edit Profile picture clicked
                    pd.setMessage("Updating Profile Picture");
                    profilepicture = "image";
                    showImagePicDialog();
                }
                else if(which == 1){
                    //Edit Username clicked
                    pd.setMessage("Updating Username");
                    //calling method and pass key "name" as parameter to update value
                    showNamePhoneUpdateDialog("name");

                }
                else if(which == 2){
                    //Edit Bio clicked
                    pd.setMessage("Updating Bio");
                    showNamePhoneUpdateDialog("bio");
                }
            }
        });
        //create and show
        builder.create().show();

    }

    private void showNamePhoneUpdateDialog(final String key) {
        //parameter key contains value
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Update "+ key); //for eg Update name or bio
        //set layout of dialog
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);
        //add edit text
        final EditText editText = new EditText(getActivity());
        editText.setHint("Enter "+ key); //eg name or bio edit
        linearLayout.addView(editText);

        builder.setView(linearLayout);
        //add buttons in dialog to update

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input text
                String value = editText.getText().toString().trim();
                //validate if user has entered something or not
                if(!TextUtils.isEmpty(value)){
                    pd.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key, value);

                    databaseReference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });


                }
                else{
                    Toast.makeText(getActivity(), "Please enter username"+key, Toast.LENGTH_SHORT).show();
                }

            }
        });
        //add button to cancel
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        //create and show dialog
        builder.create().show();
    }

    private void showImagePicDialog() {
        //showing dialog containing options Camera and Gallery to pick image
        String options[] = {"Camera", "Gallery"};
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //setTitle
        builder.setTitle("Pick Image From");
        //set items to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle dialog item clicks
                if (which == 0){
                    //Camera clicked

                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else{
                        pickFromCamera();
                    }
                }
                else if(which == 1){
                    //Gallery clicked
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else{
                        pickFromGallery();
                    }
                }
            }
        });
        //create and show
        builder.create().show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //This method called when user press Allow or deny from permission dialog

        switch (requestCode){
            case CAMERA_REQUEST_CODE: {
                if(grantResults.length >0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted){
                        //permissions enabled
                        pickFromCamera();
                    }
                    else {
                        //permission denied
                        Toast.makeText(getActivity(), "Please enable camera & storage permission", Toast.LENGTH_SHORT);
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if(grantResults.length >0){

                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted){
                        //permissions enabled
                        pickFromGallery();
                    }
                    else {
                        //permission denied
                        Toast.makeText(getActivity(), "Please enable storage permission", Toast.LENGTH_SHORT);
                    }
                }

            }
            break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        // This method will be called after picking image from Camera or Gallery
        // Upload button CODE


        switch (requestCode){


            case IMAGE_PICK_GALLERY_CODE:
                //get uri of image picked from gallery
                image_uri = data.getData();

                uploadProfileCoverPhoto(image_uri);
                break;

            case IMAGE_PICK_CAMERA_CODE:
                //get uri of image picked from camera

                uploadProfileCoverPhoto(image_uri);
                break;

            case UPLOAD_VIDEO:
                Log.e("ProfileFragment","UPLOADBUTTON CLICKED.....................................");
                Toast.makeText(getContext(),"UPLOAD video button", Toast.LENGTH_SHORT).show();

                Uri fileUri = data.getData();

                Intent uploadIntent = new Intent(getContext(), UploadVideoActivity.class);
                uploadIntent.putExtra("videofileUri", fileUri.toString());
                startActivity(uploadIntent);

/*                    Log.e("ProfileFragment","path : "+data.getData().getPath());
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

                            Toast.makeText(getContext(),"video uploaded succesfully.",Toast.LENGTH_LONG).show();


                        }
                    });*/

        }

    }

    private void uploadProfileCoverPhoto(Uri uri) {

        pd.show();

        //path and name of image to be stored in Fb
        String filePathAndName = storagePath+ ""+ profilepicture +"_"+ user.getUid();

        StorageReference storageReference2nd = storageRef.child(filePathAndName);
        storageReference2nd.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //image is uploaded to storage, now get it's url & store in users Db
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();

                        //check if image is uploaded or not and uri is received
                        if (uriTask.isSuccessful()){
                            //image uploaded
                            //add/update url in user's Db
                            HashMap<String, Object> results= new HashMap<>();
                            results.put(profilepicture, downloadUri.toString());

                            databaseReference.child(user.getUid()).updateChildren(results)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //url in database of user is added successfully
                                            pd.dismiss();
                                            Toast.makeText(getActivity(), "Image Updated", Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            //error adding url
                                            pd.dismiss();
                                            Toast.makeText(getActivity(), "Error Updating Image", Toast.LENGTH_SHORT).show();


                                        }
                                    });

                        }
                        else {
                            //error
                            pd.dismiss();
                            Toast.makeText(getActivity(), "Some error occured", Toast.LENGTH_SHORT).show();
                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //there were some errors, get and show msg & dismiss progress dialog
                        pd.dismiss();
                        Toast.makeText(getActivity(),e.getMessage(), Toast.LENGTH_SHORT).show();


                    }
                });

    }


    private void pickFromCamera() {
        //intent of picking image from device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
        //put image uri
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //intent to start camera
        Intent cameraIntent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
        //intent pick from gallery
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }


    public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


        class FeedItem extends RecyclerView.ViewHolder{

            ImageView postCellThumbnailImageView;
            TextView postCellDurationTextView, postCellVideoTitleTextView, postCellVideouploadDateTextView, postCellLikeTextView, postCellCommentTextView;


            public FeedItem(@NonNull View itemView) {
                super(itemView);

                postCellThumbnailImageView = itemView.findViewById(R.id.postCellThumbnailImageView);
                postCellDurationTextView = itemView.findViewById(R.id.postCellDurationTextView);
                postCellVideoTitleTextView = itemView.findViewById(R.id.postCellVideoTitleTextView);
                postCellVideouploadDateTextView = itemView.findViewById(R.id.postCellVideouploadDateTextView);
                postCellLikeTextView = itemView.findViewById(R.id.postCellLikeTextView);
                postCellCommentTextView = itemView.findViewById(R.id.postCellCommentTextView);


            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_cell,parent,false);


            return new FeedItem(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

            final HomeViewModel feed = homeViewModels.get(position);
            ((FeedItem)holder).postCellVideoTitleTextView.setText(feed.videoName);
            ((FeedItem)holder).postCellVideouploadDateTextView.setText("Uploaded on  "+feed.videoUploadDate);
            ((FeedItem)holder).postCellDurationTextView.setText(feed.videoTimeDuration);
            ((FeedItem)holder).postCellLikeTextView.setText("Likes "+feed.likeCount);
            ((FeedItem)holder).postCellCommentTextView.setText("Comments "+feed.commentCount);
            ((FeedItem)holder).postCellThumbnailImageView.setImageBitmap(feed.videoThumbnailImageView);

            ((FeedItem)holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Toast.makeText(getContext()," you clicked "+((FeedItem)holder).postCellVideoTitleTextView.getText(),Toast.LENGTH_LONG).show();
                    showEditVideoDialog(feed.videoId);

                }
            });

        }

        @Override
        public int getItemCount() {
            return homeViewModels.size();
        }
    }


    public void RemoveMyVideo(final String videoid){

        FirebaseDatabase.getInstance().getReference().child("Videos").child(videoid).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        FirebaseStorage.getInstance().getReference().child("videos").child(videoid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                FirebaseStorage.getInstance().getReference().child("VideosThumbnail").child(videoid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(),"Deleted Successfully.",Toast.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(),"Error encountered !  ",Toast.LENGTH_LONG).show();
                                    }
                                });


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {


                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }

    private void showEditVideoDialog(final String videoId) {

        //Showing Dialog edit options here
        String options[] = {"view", "Delete"};
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //setTitle
        builder.setTitle("Choose Action");
        //set items to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle dialog item clicks
                if (which == 0){
                    //view
                }
                else if(which == 1){
                    //delete
                    Toast.makeText(getContext(),"please wait...",Toast.LENGTH_SHORT).show();
                    RemoveMyVideo(videoId);
                }
            }
        });
        //create and show
        builder.create().show();

    }
}
