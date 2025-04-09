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
    private TextView[][] textCells = new TextView[5][5]; // [row][col]

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


        // Töltsük be az XML-ben lévő TextView cellákat
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
                Toast.makeText(this, "5 betűs szót írj be!", Toast.LENGTH_SHORT).show();
                return;
            }

            isWordValid(guess, isValid -> {
                if (!isValid) {
                    Toast.makeText(this, "Ez a szó nincs a listában!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // csak akkor mehet tovább a játék, ha valid a szó
                if (attempt >= 5) {
                    Toast.makeText(this, "Nincs több próbálkozás!", Toast.LENGTH_SHORT).show();
                    return;
                }

                checkGuess(guess);
                attempt++;
                guessInput.setText("");

                if (guess.equals(targetWord)) {
                    Toast.makeText(this, "Gratulálok, nyertél!", Toast.LENGTH_LONG).show();
                    submitButton.setEnabled(false);
                } else if (attempt == 5) {
                    Toast.makeText(this, "Vége! A szó: " + targetWord, Toast.LENGTH_LONG).show();
                }
            });
        });

    }

    private void checkGuess(String guess) {
        boolean[] correctFlags = new boolean[5];
        int[] letterCount = new int[26]; // a-z

        // 1. Lépés: megszámoljuk, mennyi van minden betűből a targetWord-ben
        for (int i = 0; i < targetWord.length(); i++) {
            char c = targetWord.charAt(i);
            letterCount[c - 'A']++;
        }

        // 2. Első kör: Zöldek ellenőrzése
        for (int i = 0; i < 5; i++) {
            char guessChar = guess.charAt(i);
            textCells[attempt][i].setText(String.valueOf(guessChar));

            if (guessChar == targetWord.charAt(i)) {
                textCells[attempt][i].setBackgroundResource(R.drawable.green_background);
                correctFlags[i] = true;
                letterCount[guessChar - 'A']--; // már felhasználtuk
            }
        }

        // 3. Második kör: Sárgák ellenőrzése
        for (int i = 0; i < 5; i++) {
            char guessChar = guess.charAt(i);
            if (!correctFlags[i]) {
                if (letterCount[guessChar - 'A'] > 0) {
                    textCells[attempt][i].setBackgroundResource(R.drawable.yellow_background);
                    letterCount[guessChar - 'A']--; // elhasználjuk egyszer
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