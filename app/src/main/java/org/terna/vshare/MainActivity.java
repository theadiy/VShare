package org.terna.vshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button loginButton, RegisterButton, GoogleSigninButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.login);
        RegisterButton = findViewById(R.id.register);
        GoogleSigninButton = findViewById(R.id.GoogleSignInButton);

        loginButton.setOnClickListener(this);



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

                break;
        }

    }
}
