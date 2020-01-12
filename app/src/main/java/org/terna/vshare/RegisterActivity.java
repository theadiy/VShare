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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    public static String TAG = "RegisterActivity";
    Button registerButton;
    EditText username, regemail, regpassword, regpassword2;
    String uname, email, pass, pass2;
    LinearLayout registerLinearLayout;

    private FirebaseAuth mAuth;

    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor myEdit;

    Animation fromLeft, fromRight, fromBottom, fromBottom2, fromTop, rotate, zoomIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        regemail = findViewById(R.id.regemail);
        regpassword = findViewById(R.id.regPassword);
        regpassword2 = findViewById(R.id.regPassword2);
        registerButton = findViewById(R.id.registerButton);
        registerLinearLayout = findViewById(R.id.registerLinearLayout);


        username.setVisibility(View.INVISIBLE);
        regemail.setVisibility(View.INVISIBLE);
        regpassword.setVisibility(View.INVISIBLE);
        regpassword2.setVisibility(View.INVISIBLE);
        registerButton.setVisibility(View.INVISIBLE);
        registerLinearLayout.setVisibility(View.INVISIBLE);

        fromLeft = AnimationUtils.loadAnimation(this,R.anim.from_left);
        fromRight = AnimationUtils.loadAnimation(this,R.anim.from_right);
        fromBottom = AnimationUtils.loadAnimation(this,R.anim.from_bottom);
        fromBottom2 = AnimationUtils.loadAnimation(this,R.anim.from_bottom);
        fromTop = AnimationUtils.loadAnimation(this,R.anim.from_top);
        rotate = AnimationUtils.loadAnimation(this,R.anim.rotate);
        zoomIn = AnimationUtils.loadAnimation(this,R.anim.zoom_in);

        registerLinearLayout.setAnimation(fromBottom);
        registerLinearLayout.setVisibility(View.VISIBLE);
        registerLinearLayout.getAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                username.setAnimation(fromLeft);
                username.setVisibility(View.VISIBLE);
                username.getAnimation().setAnimationListener(new Animation.AnimationListener() {
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

                regemail.setAnimation(fromRight);
                regemail.setVisibility(View.VISIBLE);
                regemail.getAnimation().setAnimationListener(new Animation.AnimationListener() {
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

                regpassword.setAnimation(fromLeft);
                regpassword.setVisibility(View.VISIBLE);
                regpassword.getAnimation().setAnimationListener(new Animation.AnimationListener() {
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

                regpassword2.setAnimation(fromRight);
                regpassword2.setVisibility(View.VISIBLE);
                regpassword2.getAnimation().setAnimationListener(new Animation.AnimationListener() {
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

                registerButton.setAnimation(fromBottom2);
                registerButton.setVisibility(View.VISIBLE);
                registerButton.getAnimation().setAnimationListener(new Animation.AnimationListener() {
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

        mAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //take user input into string
                if(username.getText().toString().equals("")){

                    Toast.makeText(RegisterActivity.this," Please enter username ! ",Toast.LENGTH_SHORT).show();

                }else if(regemail.getText().toString().equals("")){

                    Toast.makeText(RegisterActivity.this," Please enter email address ! ",Toast.LENGTH_SHORT).show();

                }else if(regpassword.getText().toString().equals("")){

                    Toast.makeText(RegisterActivity.this," Please enter password ! ",Toast.LENGTH_SHORT).show();

                }else if(regpassword2.getText().toString().equals("")){

                    Toast.makeText(RegisterActivity.this," Please re-enter password ! ",Toast.LENGTH_SHORT).show();

                }else {

                    uname = username.getText().toString();
                    email = regemail.getText().toString();
                    pass = regpassword.getText().toString();
                    pass2 = regpassword2.getText().toString();

                    sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
                    myEdit = sharedPreferences.edit();


                    // check for username
                    // check email
                    //check password match
                    if(pass.equals(pass2)){



                        //push into firebase
                        //Toast.makeText(RegisterActivity.this,"Ready to create a account",Toast.LENGTH_SHORT).show();
                        mAuth.createUserWithEmailAndPassword(email,pass)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(TAG, "createUserWithEmail:success");
                                            Toast.makeText(RegisterActivity.this, "Successfully registered.",
                                                    Toast.LENGTH_SHORT).show();

                                            FirebaseUser user = mAuth.getCurrentUser();
                                            //updateUI(user);

                                            myEdit.putString("email",user.getEmail());
                                            myEdit.putString("uid",user.getUid());
                                            myEdit.apply();

                                            Intent intent = new Intent(RegisterActivity.this, HomeFeedActivity.class);
                                            startActivity(intent);

                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(RegisterActivity.this, "Authentication failed."+task.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                            //updateUI(null);
                                        }

                                    }

                                });
                    }else {

                        Toast.makeText(RegisterActivity.this," Password did not match ! ",Toast.LENGTH_SHORT).show();
                    }

                    //Intent HomeIntent = new Intent(RegisterActivity.this,HomeFeedActivity.class);
                    //startActivity(HomeIntent);

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
