package org.terna.vshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    Button loginButton;
    TextView username,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.LoginButton);
        username = findViewById(R.id.LoginEmailEditText);
        password = findViewById(R.id.LoginPasswordEditText);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.getText().toString().compareTo("admin")==0 && password.getText().toString().compareTo("admin")==0 ){
                    Intent HomeIntent = new Intent(LoginActivity.this,Main2Activity.class);
                    startActivity(HomeIntent);
                }
                else{
                    Toast.makeText(getBaseContext(),"Invalid Credentials!",Toast.LENGTH_LONG).show();

                }

            }
        });
    }
}
