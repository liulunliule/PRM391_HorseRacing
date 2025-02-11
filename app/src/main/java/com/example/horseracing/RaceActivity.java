package com.example.horseracing;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Random;

public class RaceActivity extends AppCompatActivity {
    private SeekBar seekBarHorse1, seekBarHorse2, seekBarHorse3;
    private CheckBox cbHorse1, cbHorse2, cbHorse3;
    private EditText etBetPoints;
    private Button btnStartRace;
    private TextView tvCurrentPoints,tvBetPoints;

    private int currentPoints = 1000;
    private int betPoints = 0;
    private Handler handler = new Handler();
    private Random random = new Random();
    private boolean isRacing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);

        tvCurrentPoints = findViewById(R.id.tvCurrentPoints);
        seekBarHorse1 = findViewById(R.id.seekBarHorse1);
        seekBarHorse2 = findViewById(R.id.seekBarHorse2);
        seekBarHorse3 = findViewById(R.id.seekBarHorse3);
        cbHorse1 = findViewById(R.id.cbHorse1);
        cbHorse2 = findViewById(R.id.cbHorse2);
        cbHorse3 = findViewById(R.id.cbHorse3);
        etBetPoints = findViewById(R.id.etBetPoints);
        btnStartRace = findViewById(R.id.btnStartRace);

        currentPoints = getIntent().getIntExtra("currentPoints", 1000);
        tvCurrentPoints.setText("Điểm: " + currentPoints);

        btnStartRace.setOnClickListener(view -> startRace());
    }

    private void startRace() {
        if (isRacing) return;

        String betInput = etBetPoints.getText().toString();
        if (betInput.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số điểm cược!", Toast.LENGTH_SHORT).show();
            return;
        }
        betPoints = Integer.parseInt(betInput);

        if (betPoints <= 0 || betPoints > currentPoints) {
            Toast.makeText(this, "Số điểm cược không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> selectedHorses = new ArrayList<>();
        if (cbHorse1.isChecked()) selectedHorses.add("Ngựa 1");
        if (cbHorse2.isChecked()) selectedHorses.add("Ngựa 2");
        if (cbHorse3.isChecked()) selectedHorses.add("Ngựa 3");

        if (selectedHorses.isEmpty()) {
            Toast.makeText(this, "Hãy chọn ít nhất một con ngựa để cược!", Toast.LENGTH_SHORT).show();
            return;
        }

        isRacing = true;
        btnStartRace.setEnabled(false);
        seekBarHorse1.setProgress(0);
        seekBarHorse2.setProgress(0);
        seekBarHorse3.setProgress(0);

        final boolean[] raceOver = {false};

        new Thread(() -> {
            while (!raceOver[0]) {
                handler.post(() -> {
                    seekBarHorse1.incrementProgressBy(random.nextInt(5));
                    seekBarHorse2.incrementProgressBy(random.nextInt(5));
                    seekBarHorse3.incrementProgressBy(random.nextInt(5));

                    if (seekBarHorse1.getProgress() >= 100) {
                        raceOver[0] = true;
                        finishRace("Ngựa 1", selectedHorses);
                    } else if (seekBarHorse2.getProgress() >= 100) {
                        raceOver[0] = true;
                        finishRace("Ngựa 2", selectedHorses);
                    } else if (seekBarHorse3.getProgress() >= 100) {
                        raceOver[0] = true;
                        finishRace("Ngựa 3", selectedHorses);
                    }
                });

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void finishRace(String winner, ArrayList<String> selectedHorses) {
        isRacing = false;

        Intent intent = new Intent(RaceActivity.this, ResultActivity.class);

        intent.putExtra("winner", winner);
        intent.putExtra("selectedHorses", selectedHorses);
        intent.putExtra("currentPoints", currentPoints);
        intent.putExtra("betPoints", betPoints);
        startActivity(intent);
        finish();
    }
}
