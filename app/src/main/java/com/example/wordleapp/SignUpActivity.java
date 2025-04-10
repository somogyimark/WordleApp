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

public class SignUpActivity extends AppCompatActivity {

    TextView login_account;
    EditText signUp_firstname, signUp_lastname, signUp_username, signUp_email, signUp_password, signUp_passwordAgain;
    Button signUp_button;

    SharedPreferences sharedPreferences;

    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        login_account = findViewById(R.id.login_account);
        signUp_firstname = findViewById(R.id.signUp_firstname);
        signUp_lastname = findViewById(R.id.signUp_lastname);
        signUp_username = findViewById(R.id.signUp_username);
        signUp_email = findViewById(R.id.signUp_email);
        signUp_password = findViewById(R.id.signUp_password);
        signUp_passwordAgain = findViewById(R.id.signUp_passwordAgain);
        signUp_button = findViewById(R.id.signUp_button);


        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        editor = sharedPreferences.edit();

        signUp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mySignUp_firstname = signUp_firstname.getText().toString().trim();
                String mySignUp_lastname = signUp_lastname.getText().toString().trim();
                String mySignUp_username = signUp_username.getText().toString().trim();
                String mySignUp_email = signUp_email.getText().toString().trim();
                String mySignUp_password = signUp_password.getText().toString().trim();
                String mySignUp_passwordAgain = signUp_passwordAgain.getText().toString().trim();

                if (!mySignUp_password.equals(mySignUp_passwordAgain)) {
                    Toast.makeText(SignUpActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONObject newUser = new JSONObject();
                try {
                    newUser.put("first_name", mySignUp_firstname);
                    newUser.put("last_name", mySignUp_lastname);
                    newUser.put("user_name", mySignUp_username);
                    newUser.put("email", mySignUp_email);
                    newUser.put("password", mySignUp_password);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SignUpActivity.this, "Error creating user", Toast.LENGTH_SHORT).show();
                    return;
                }



                SharedPreferences sharedPreferences = getSharedPreferences("users_data", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                String usersString = sharedPreferences.getString("users", "[]");
                try {
                    JSONArray usersArray = new JSONArray(usersString);

                    for (int i = 0; i < usersArray.length(); i++) {
                        JSONObject existingUser = usersArray.getJSONObject(i);
                        if (existingUser.getString("user_name").equals(mySignUp_username)) {
                            Toast.makeText(SignUpActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    usersArray.put(newUser);
                    editor.putString("users", usersArray.toString());
                    editor.apply();

                    Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SignUpActivity.this, "Error saving user", Toast.LENGTH_SHORT).show();
                }
            }
        });

        login_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            }
        });

    }
}