package org.terna.vshare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    public static String TAG = "RegisterActivity";
    Button registerButton;
    EditText username, regemail, regpassword, regpassword2;
    String uname, email, pass, pass2;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        regemail = findViewById(R.id.regemail);
        regpassword = findViewById(R.id.regPassword);
        regpassword2 = findViewById(R.id.regPassword2);

        registerButton = findViewById(R.id.registerButton);

        mAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //take user input into string

                uname = username.getText().toString();
                email = regemail.getText().toString();
                pass = regpassword.getText().toString();
                pass2 = regpassword2.getText().toString();


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
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        //updateUI(user);
                                        Toast.makeText(RegisterActivity.this, "Successfully registered.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        //updateUI(null);
                                    }

                                }

                            });
                }




                //Intent HomeIntent = new Intent(RegisterActivity.this,Main2Activity.class);
                //startActivity(HomeIntent);
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
