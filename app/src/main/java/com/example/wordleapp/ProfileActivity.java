package com.example.wordleapp;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private String currentUsername;
    private SharedPreferences userPrefs;
    private TextView winCounter;
   // LinearLayout gameHistoryLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        winCounter = findViewById(R.id.winCounter);
        currentUsername = getIntent().getStringExtra("username");
        userPrefs = getSharedPreferences("users_data", MODE_PRIVATE);

        showWinCount();
    }

    private void showWinCount() {
        String usersString = userPrefs.getString("users", "[]");
        SharedPreferences sp = getSharedPreferences("game_data", MODE_PRIVATE);
        try {
            JSONArray usersArray = new JSONArray(usersString);
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject user = usersArray.getJSONObject(i);
                if (user.getString("user_name").equals(currentUsername)) {

                    int wins = user.optInt("wins", 0);
                   winCounter.setText("Wins: " + wins);
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
