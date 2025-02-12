package com.example.horseracing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class RaceActivity extends AppCompatActivity {
    private SeekBar seekBarHorse1, seekBarHorse2, seekBarHorse3;
    private CheckBox cbHorse1, cbHorse2, cbHorse3;
    private EditText etBetPoints;
    private Button btnStartRace;
    private TextView tvCurrentPoints;
    private Button btnLogout;
    private String loggedInUser;
    private TextView tvWelcomeUser;
    private int currentPoints;
    private int betPoints = 0;
    private Handler handler = new Handler();
    private Random random = new Random();
    private boolean isRacing = false;
    private MediaPlayer mediaPlayer, backgroundMusicPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);

        tvWelcomeUser = findViewById(R.id.tvWelcomeUser);
        tvCurrentPoints = findViewById(R.id.tvCurrentPoints);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        loggedInUser = prefs.getString("loggedInUser", null);
        String usersJson = prefs.getString("users", "{}");

        try {
            JSONObject users = new JSONObject(usersJson);
            if (loggedInUser != null && users.has(loggedInUser)) {
                JSONObject userData = users.getJSONObject(loggedInUser);
                currentPoints = userData.getInt("points");

                tvWelcomeUser.setText("Chào, " + loggedInUser + "!");
                tvCurrentPoints.setText("Điểm: " + currentPoints);
            } else {
                logoutUser();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnLogout = findViewById(R.id.btnLogout);
        seekBarHorse1 = findViewById(R.id.seekBarHorse1);
        seekBarHorse2 = findViewById(R.id.seekBarHorse2);
        seekBarHorse3 = findViewById(R.id.seekBarHorse3);
        cbHorse1 = findViewById(R.id.cbHorse1);
        cbHorse2 = findViewById(R.id.cbHorse2);
        cbHorse3 = findViewById(R.id.cbHorse3);
        etBetPoints = findViewById(R.id.etBetPoints);
        btnStartRace = findViewById(R.id.btnStartRace);

        // Lấy điểm từ SharedPreferences
        tvCurrentPoints.setText("Điểm: " + currentPoints);

        btnStartRace.setOnClickListener(view -> {
            playSound(R.raw.click_sound);
            startRace();
        });

        btnLogout.setOnClickListener(view -> logoutUser());

        cbHorse1.setOnCheckedChangeListener((buttonView, isChecked) -> playSound(R.raw.pick));
        cbHorse2.setOnCheckedChangeListener((buttonView, isChecked) -> playSound(R.raw.pick));
        cbHorse3.setOnCheckedChangeListener((buttonView, isChecked) -> playSound(R.raw.pick));

        // Phát nhạc nền
        backgroundMusicPlayer = MediaPlayer.create(this, R.raw.background_theme);
        backgroundMusicPlayer.setLooping(true); // Đặt nhạc nền lặp lại
        backgroundMusicPlayer.start();
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

        // Phát tiếng ngựa khi bắt đầu đua
        playSound(R.raw.horse1);

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

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String usersJson = prefs.getString("users", "{}");

        try {
            JSONObject users = new JSONObject(usersJson);
            String loggedInUser = prefs.getString("loggedInUser", null);

            if (loggedInUser != null && users.has(loggedInUser)) {
                JSONObject userData = users.getJSONObject(loggedInUser);

                if (selectedHorses.contains(winner)) {
                    currentPoints += betPoints; // Thắng được cộng điểm
                } else {
                    currentPoints -= betPoints; // Thua bị trừ điểm
                }

                userData.put("points", currentPoints);
                users.put(loggedInUser, userData);
                prefs.edit().putString("users", users.toString()).apply();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Dừng nhạc nền khi kết thúc cuộc đua
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.stop();
            backgroundMusicPlayer.release();
        }

        Intent intent = new Intent(RaceActivity.this, ResultActivity.class);
        intent.putExtra("winner", winner);
        intent.putExtra("selectedHorses", selectedHorses);
        intent.putExtra("currentPoints", currentPoints);
        intent.putExtra("betPoints", betPoints);
        startActivity(intent);
        finish();
    }

    private void logoutUser() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        prefs.edit().remove("loggedInUser").apply();

        // Dừng nhạc nền khi người dùng đăng xuất
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.stop();
            backgroundMusicPlayer.release();
        }

        Intent intent = new Intent(RaceActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // Hàm phát âm thanh
    private void playSound(int soundResource) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = MediaPlayer.create(this, soundResource);
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.release();
        }
    }
}
