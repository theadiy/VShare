package org.terna.vshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    public static String TAG = "LoginActivity";
    Button loginButton;
    TextView logemail,logpassword,forgotPasswordTextView;
    String email,pass;
    LinearLayout loginLinearLayout;


    private FirebaseAuth mAuth;

    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor myEdit;

    Animation fromLeft, fromRight, fromBottom ,fromBottom2 ,fromBottom3 ,fromTop, rotate, zoomIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.LoginButton);
        logemail = findViewById(R.id.LoginEmailEditText);
        logpassword = findViewById(R.id.LoginPasswordEditText);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        loginLinearLayout = findViewById(R.id.loginLinearLayout);

        logemail.setVisibility(View.INVISIBLE);
        logpassword.setVisibility(View.INVISIBLE);
        loginButton.setVisibility(View.INVISIBLE);
        forgotPasswordTextView.setVisibility(View.INVISIBLE);
        loginLinearLayout.setVisibility(View.INVISIBLE);

        fromLeft = AnimationUtils.loadAnimation(this,R.anim.from_left);
        fromRight = AnimationUtils.loadAnimation(this,R.anim.from_right);
        fromBottom = AnimationUtils.loadAnimation(this,R.anim.from_bottom);
        fromBottom2 = AnimationUtils.loadAnimation(this,R.anim.from_bottom);
        fromBottom3 = AnimationUtils.loadAnimation(this,R.anim.from_bottom);
        fromTop = AnimationUtils.loadAnimation(this,R.anim.from_top);
        rotate = AnimationUtils.loadAnimation(this,R.anim.rotate);
        zoomIn = AnimationUtils.loadAnimation(this,R.anim.zoom_in);

        loginLinearLayout.setAnimation(fromBottom);
        loginLinearLayout.setVisibility(View.VISIBLE);
        loginLinearLayout.getAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                logemail.setAnimation(fromLeft);
                logemail.setVisibility(View.VISIBLE);
                logemail.getAnimation().setAnimationListener(new Animation.AnimationListener() {
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

                logpassword.setAnimation(fromRight);
                logpassword.setVisibility(View.VISIBLE);
                logpassword.getAnimation().setAnimationListener(new Animation.AnimationListener() {
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

                loginButton.setAnimation(fromBottom2);
                loginButton.setVisibility(View.VISIBLE);
                loginButton.getAnimation().setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        forgotPasswordTextView.setAnimation(fromBottom3);
                        forgotPasswordTextView.setVisibility(View.VISIBLE);
                        forgotPasswordTextView.getAnimation().setAnimationListener(new Animation.AnimationListener() {
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

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        myEdit = sharedPreferences.edit();

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(logemail.getText().toString().equals("" )){

                    Toast.makeText(LoginActivity.this," Please enter email address ! ",Toast.LENGTH_SHORT).show();


                }else if(logpassword.getText().toString().equals("")){

                    Toast.makeText(LoginActivity.this," Please enter password ! ",Toast.LENGTH_SHORT).show();

                } else {

                    email = logemail.getText().toString();
                    pass = logpassword.getText().toString();
                    Log.e(TAG,"login clicked ");


                    mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.e(TAG, "signInWithEmail:success");
                                Toast.makeText(LoginActivity.this, "Successfull login.",
                                        Toast.LENGTH_SHORT).show();

                                FirebaseUser user = mAuth.getCurrentUser();

                                myEdit.putString("email",user.getEmail());
                                myEdit.putString("uid",user.getUid());
                                myEdit.apply();

                                Intent intent = new Intent(LoginActivity.this, HomeFeedActivity.class);
                                startActivity(intent);
                                //updateUI(user);
                            } else {
                                Log.e(TAG,"here2 ");
                                // If sign in fails, display a message to the user.
                                Log.e(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed - "+task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }
                        }
                    });

                }


            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

}
