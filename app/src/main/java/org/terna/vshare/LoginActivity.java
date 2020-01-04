package org.terna.vshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    TextView logemail,logpassword;
    String email,pass;

    private FirebaseAuth mAuth;

    SharedPreferences sharedPreferences ;
    SharedPreferences.Editor myEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.LoginButton);
        logemail = findViewById(R.id.LoginEmailEditText);
        logpassword = findViewById(R.id.LoginPasswordEditText);

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        myEdit = sharedPreferences.edit();

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                            Intent intent = new Intent(LoginActivity.this,Main2Activity.class);
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
