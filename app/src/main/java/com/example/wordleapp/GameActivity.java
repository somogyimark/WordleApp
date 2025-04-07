package com.example.wordleapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private final String targetWord = "APPLE";
    private int attempt = 0;

    private EditText guessInput;
    private Button submitButton;
    private GridLayout wordleGrid;
    private TextView[][] textCells = new TextView[5][5]; // [sor][oszlop]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        guessInput = findViewById(R.id.guessInput);
        submitButton = findViewById(R.id.submitButton);
        wordleGrid = findViewById(R.id.wordleGrid);

        initGrid();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String guess = guessInput.getText().toString().toUpperCase();
                if (guess.length() != 5) {
                    Toast.makeText(GameActivity.this, "5 betűs szót írj be!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (attempt >= 5) {
                    Toast.makeText(GameActivity.this, "Nincs több próbálkozás!", Toast.LENGTH_SHORT).show();
                    return;
                }

                checkGuess(guess);
                attempt++;
                guessInput.setText("");

                if (guess.equals(targetWord)) {
                    Toast.makeText(GameActivity.this, "Gratulálok, nyertél!", Toast.LENGTH_LONG).show();
                    submitButton.setEnabled(false);
                } else if (attempt == 5) {
                    Toast.makeText(GameActivity.this, "Vége! A szó: " + targetWord, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initGrid() {
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                TextView tv = new TextView(this);
                tv.setText(" ");
                tv.setBackgroundColor(Color.LTGRAY);
                tv.setTextColor(Color.BLACK);
                tv.setTextSize(24f);
                tv.setGravity(View.TEXT_ALIGNMENT_CENTER);
                tv.setPadding(20, 20, 20, 20);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                params.columnSpec = GridLayout.spec(col, 1f);
                params.rowSpec = GridLayout.spec(row);
                params.setMargins(8, 8, 8, 8);

                tv.setLayoutParams(params);
                wordleGrid.addView(tv);
                textCells[row][col] = tv;
            }
        }
    }

    private void checkGuess(String guess) {
        boolean[] correctFlags = new boolean[5];

        for (int i = 0; i < 5; i++) {
            char guessChar = guess.charAt(i);
            textCells[attempt][i].setText(String.valueOf(guessChar));

            if (guessChar == targetWord.charAt(i)) {
                textCells[attempt][i].setBackgroundColor(Color.parseColor("#66BB6A")); // zöld
                correctFlags[i] = true;
            } else if (targetWord.contains(String.valueOf(guessChar))) {
                // csak sárga, ha nincs már zöldként beállítva
                if (!isAlreadyGreen(guessChar, correctFlags)) {
                    textCells[attempt][i].setBackgroundColor(Color.parseColor("#FFD54F")); // sárga
                } else {
                    textCells[attempt][i].setBackgroundColor(Color.LTGRAY); // alap
                }
            } else {
                textCells[attempt][i].setBackgroundColor(Color.LTGRAY); // alap
            }
        }
    }

    private boolean isAlreadyGreen(char c, boolean[] flags) {
        for (int i = 0; i < 5; i++) {
            if (targetWord.charAt(i) == c && flags[i]) {
                return true;
            }
        }
        return false;
    }
}
