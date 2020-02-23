package org.terna.vshare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button loginButton, RegisterButton, signoutButton;
    SignInButton GoogleSigninButton;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    RelativeLayout loginOptionsLayout;

    private AlertDialog.Builder alert;
    private AlertDialog message;
    private static final String TAG = "MainAcitvity";

    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor myEdit;

    Animation fromLeft, fromRight, fromBottom ,fromBottom2 ,fromBottom3 ,fromTop, rotate, zoomIn;

    private static final int RC_SIGN_IN = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.login);
        RegisterButton = findViewById(R.id.register);
        GoogleSigninButton = findViewById(R.id.GoogleSignInButton);
        signoutButton = findViewById(R.id.signOutButton);
        loginOptionsLayout = findViewById(R.id.LoginOptionsLayout);

        loginButton.setVisibility(View.INVISIBLE);
        RegisterButton.setVisibility(View.INVISIBLE);
        GoogleSigninButton.setVisibility(View.INVISIBLE);
        signoutButton.setVisibility(View.INVISIBLE);
        loginOptionsLayout.setVisibility(View.INVISIBLE);


        fromLeft = AnimationUtils.loadAnimation(this,R.anim.from_left);
        fromRight = AnimationUtils.loadAnimation(this,R.anim.from_right);
        fromBottom = AnimationUtils.loadAnimation(this,R.anim.from_bottom);
        fromBottom2 = AnimationUtils.loadAnimation(this,R.anim.from_bottom);
        fromBottom3 = AnimationUtils.loadAnimation(this,R.anim.from_bottom);
        fromTop = AnimationUtils.loadAnimation(this,R.anim.from_top);
        rotate = AnimationUtils.loadAnimation(this,R.anim.rotate);
        zoomIn = AnimationUtils.loadAnimation(this,R.anim.zoom_in);




        loginOptionsLayout.setAnimation(fromBottom);
        loginOptionsLayout.setVisibility(View.VISIBLE);
        loginOptionsLayout.getAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {


                loginButton.setAnimation(fromLeft);
                loginButton.setVisibility(View.VISIBLE);
                loginButton.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                RegisterButton.setAnimation(fromRight);
                RegisterButton.setVisibility(View.VISIBLE);
                RegisterButton.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {


                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {


                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                GoogleSigninButton.setAnimation(fromBottom2);
                GoogleSigninButton.setVisibility(View.VISIBLE);
                GoogleSigninButton.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        signoutButton.setAnimation(fromBottom3);
                        signoutButton.setVisibility(View.VISIBLE);
                        signoutButton.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                                Log.e(TAG,"HERE 3");
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });



                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });



        mAuth = FirebaseAuth.getInstance();

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        myEdit = sharedPreferences.edit();

        loginButton.setOnClickListener(this);
        RegisterButton.setOnClickListener(this);
        GoogleSigninButton.setOnClickListener(this);
        signoutButton.setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(loginIntent);
                break;

            case R.id.register:
                Intent registerIntent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(registerIntent);
                break;

            case R.id.GoogleSignInButton:
                signIn();
                break;

            case R.id.signOutButton:
                googleSignOut();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            getResultOfGetSignedInAccount(task);
        }
    }

    private void getResultOfGetSignedInAccount(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle("Failed To Login");
            alert.setMessage(e.toString());
            message = alert.create();
            message.show();
        }

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        Log.e(TAG,"signIn()");

    }
    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();

                            if(task.getResult().getAdditionalUserInfo().isNewUser()){
                                //Get user email and uid from auth

                                String email = user.getEmail();
                                String uid = user.getUid();

                                //When user is registered store user info in Fb realtime Db too

                                HashMap<Object, String> hashMap = new HashMap<>();
                                // put info in hashmap
                                hashMap.put("email",email);
                                hashMap.put("uid",uid);
                                hashMap.put("name","");
                                hashMap.put("image","");
                                hashMap.put("bio","");
                                //Fb Db instance
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                //path to store data named "Users"
                                DatabaseReference reference = database.getReference("Users");
                                //put data within hashmap in database
                                reference.child(uid).setValue(hashMap);
                            }

                            myEdit.putString("email",user.getEmail());
                            myEdit.putString("uid",user.getUid());
                            myEdit.apply();

                            Intent intent = new Intent(MainActivity.this, HomeFeedActivity.class);
                            startActivity(intent);


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);

                        }

                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();


        if (sharedPreferences.contains("email")){
            Log.e(TAG,"Already signed-in "+sharedPreferences.getString("email",""));
            Toast.makeText(MainActivity.this,"Already signed-in as "+sharedPreferences.getString("email",""),Toast.LENGTH_LONG).show();
            //updateUI(currentUser);
            //intent to home screen
            Intent i = new Intent(MainActivity.this,HomeFeedActivity.class);
            startActivity(i);
        }

    }

    public void googleSignOut()
    {

        myEdit.clear();
        myEdit.apply();


        mGoogleSignInClient.signOut();
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(MainActivity.this,"Sign out",Toast.LENGTH_SHORT).show();
                Log.e(TAG,"---------------"+mAuth.getCurrentUser().getDisplayName());
            }
        });

    }
}
