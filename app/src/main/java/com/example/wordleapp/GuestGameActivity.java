package com.example.wordleapp;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

interface WordCheckCallback {
    void onResult(boolean isValid);
}


public class GuestGameActivity extends AppCompatActivity {

    private String targetWord;
    private int attempt = 0;

    private EditText guessInput;
    private Button submitButton;
    private TextView[][] textCells = new TextView[5][5];
    private Button playAgainButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        targetWord = getRandomWordFromInternalStorage();

        guessInput = findViewById(R.id.guessInput);
        submitButton = findViewById(R.id.submitButton);

        playAgainButton = findViewById(R.id.playAgainButton);


        playAgainButton.setOnClickListener(v -> recreate());


        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                String cellId = "cell_" + row + col;
                int resID = getResources().getIdentifier(cellId, "id", getPackageName());
                textCells[row][col] = findViewById(resID);
            }
        }

        submitButton.setOnClickListener(v -> {
            String guess = guessInput.getText().toString().toUpperCase();
            if (guess.length() != 5) {
                Toast.makeText(this, "Write a 5 letter word!", Toast.LENGTH_SHORT).show();
                return;
            }

            isWordValid(guess, isValid -> {
                if (!isValid) {
                    Toast.makeText(this, "It's not in the list!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (attempt >= 5) {
                    Toast.makeText(this, "No more attempts!", Toast.LENGTH_SHORT).show();
                    return;
                }

                checkGuess(guess);
                attempt++;
                guessInput.setText("");

                if (guess.equals(targetWord)) {
                    Toast.makeText(this, "Congratulations, you won!", Toast.LENGTH_LONG).show();
                    playAgainButton.setVisibility(View.VISIBLE);
                    submitButton.setEnabled(false);
                } else if (attempt == 5) {
                    Toast.makeText(this, "Game over! The word was: " + targetWord, Toast.LENGTH_LONG).show();
                    playAgainButton.setVisibility(View.VISIBLE);
                }
            });
        });

    }

    private void checkGuess(String guess) {
        boolean[] correctFlags = new boolean[5];
        int[] letterCount = new int[26]; // a-z


        for (int i = 0; i < targetWord.length(); i++) {
            char c = targetWord.charAt(i);
            letterCount[c - 'A']++;
        }


        for (int i = 0; i < 5; i++) {
            char guessChar = guess.charAt(i);
            textCells[attempt][i].setText(String.valueOf(guessChar));

            if (guessChar == targetWord.charAt(i)) {
                textCells[attempt][i].setBackgroundResource(R.drawable.green_background);
                correctFlags[i] = true;
                letterCount[guessChar - 'A']--;
            }
        }


        for (int i = 0; i < 5; i++) {
            char guessChar = guess.charAt(i);
            if (!correctFlags[i]) {
                if (letterCount[guessChar - 'A'] > 0) {
                    textCells[attempt][i].setBackgroundResource(R.drawable.yellow_background);
                    letterCount[guessChar - 'A']--;
                } else {
                    textCells[attempt][i].setBackgroundResource(R.drawable.edit_text_background);
                }
            }
        }
    }

    private String getRandomWordFromInternalStorage() {
        File file = new File(getFilesDir(), "words.txt");
        List<String> words = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() == 5) {
                    words.add(line.trim().toUpperCase());
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!words.isEmpty()) {
            Random random = new Random();
            return words.get(random.nextInt(words.size()));
        } else {
            return "APPLE";
        }
    }

    private void isWordValid(String guess, WordCheckCallback callback) {
        new Thread(() -> {
            boolean found = false;
            try {
                InputStream is = getAssets().open("words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().equalsIgnoreCase(guess)) {
                        found = true;
                        break;
                    }
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            boolean finalFound = found;
            runOnUiThread(() -> callback.onResult(finalFound));
        }).start();
    }




}