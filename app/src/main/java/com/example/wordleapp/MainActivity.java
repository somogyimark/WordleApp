package com.example.wordleapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    EditText login_username;
    EditText login_password;
    Button button_login;
    TextView forgotPassword;
    TextView createAccount;
    TextView playAsAGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        login_username = findViewById(R.id.login_username);
        login_password = findViewById(R.id.login_password);
        button_login = findViewById(R.id.button_login);
        forgotPassword = findViewById(R.id.forgotPassword);
        createAccount = findViewById(R.id.createAccount);
        playAsAGuest = findViewById(R.id.playAsAGuest);


        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String enteredUsername = login_username.getText().toString().trim();
                String enteredPassword = login_password.getText().toString().trim();

                SharedPreferences sharedPreferences = getSharedPreferences("users_data", MODE_PRIVATE);
                String usersString = sharedPreferences.getString("users", "[]");

                try {
                    JSONArray usersArray = new JSONArray(usersString);

                    boolean loginSuccess = false;

                    for (int i = 0; i < usersArray.length(); i++) {
                        JSONObject user = usersArray.getJSONObject(i);

                        String savedUsername = user.getString("user_name").trim();
                        String savedPassword = user.getString("password").trim();

                        if (savedUsername.equals(enteredUsername) && savedPassword.equals(enteredPassword)) {
                            loginSuccess = true;
                            break;
                        }
                    }

                    if (loginSuccess) {
                        Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, GameActivity.class));
                    } else {
                        Toast.makeText(MainActivity.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error loading users", Toast.LENGTH_SHORT).show();
                }
            }
        });


        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this , SignUpActivity.class));
            }
        });

        playAsAGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this , GuestGameActivity.class));
            }
        });

    }

}