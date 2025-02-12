package com.example.horseracing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
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
    private Button btnStartRace, btnLogout;
    private TextView tvCurrentPoints, tvWelcomeUser;
    private String loggedInUser;
    private int currentPoints, betPoints = 0;
    private boolean isRacing = false;
    private Handler handler = new Handler();
    private Random random = new Random();

    private MediaPlayer mediaPlayerBackground, mediaPlayerHorseRace, mediaPlayerHorseWin;
    private SoundPool soundPool;
    private int clickSoundId, checkboxSoundId; // ID của âm thanh

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race);

        tvWelcomeUser = findViewById(R.id.tvWelcomeUser);
        tvCurrentPoints = findViewById(R.id.tvCurrentPoints);
        btnLogout = findViewById(R.id.btnLogout);
        seekBarHorse1 = findViewById(R.id.seekBarHorse1);
        seekBarHorse2 = findViewById(R.id.seekBarHorse2);
        seekBarHorse3 = findViewById(R.id.seekBarHorse3);
        cbHorse1 = findViewById(R.id.cbHorse1);
        cbHorse2 = findViewById(R.id.cbHorse2);
        cbHorse3 = findViewById(R.id.cbHorse3);
        etBetPoints = findViewById(R.id.etBetPoints);
        btnStartRace = findViewById(R.id.btnStartRace);

        // 🎵 Khởi động nhạc nền
        mediaPlayerBackground = MediaPlayer.create(this, R.raw.background_theme);
        mediaPlayerBackground.setLooping(true);
        mediaPlayerBackground.start();

        // 🔊 Khởi tạo SoundPool để phát âm thanh khi nhấn nút và chọn checkbox
        soundPool = new SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build())
                .build();

        clickSoundId = soundPool.load(this, R.raw.click_sound, 1); // Âm thanh khi nhấn nút
        checkboxSoundId = soundPool.load(this, R.raw.pick, 1); // Âm thanh khi chọn checkbox

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

        btnStartRace.setOnClickListener(view -> {
            soundPool.play(clickSoundId, 1, 1, 0, 0, 1); // 🔊 Phát âm thanh khi nhấn nút
            startRace();
        });

        btnLogout.setOnClickListener(view -> {
            soundPool.play(clickSoundId, 1, 1, 0, 0, 1); // 🔊 Phát âm thanh khi nhấn nút
            logoutUser();
        });

        // 🔊 Phát âm thanh khác khi chọn checkbox
        cbHorse1.setOnCheckedChangeListener((buttonView, isChecked) -> soundPool.play(checkboxSoundId, 1, 1, 0, 0, 1));
        cbHorse2.setOnCheckedChangeListener((buttonView, isChecked) -> soundPool.play(checkboxSoundId, 1, 1, 0, 0, 1));
        cbHorse3.setOnCheckedChangeListener((buttonView, isChecked) -> soundPool.play(checkboxSoundId, 1, 1, 0, 0, 1));
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

        isRacing = true;
        btnStartRace.setEnabled(false);

        // 🛑 Dừng nhạc nền trước khi đua
        if (mediaPlayerBackground != null) {
            mediaPlayerBackground.stop();
            mediaPlayerBackground.release();
            mediaPlayerBackground = null;
        }

        // 🎵 Phát tiếng ngựa khi bắt đầu đua
        mediaPlayerHorseRace = MediaPlayer.create(this, R.raw.horse1);
        mediaPlayerHorseRace.setLooping(true);
        mediaPlayerHorseRace.start();
    }

    private void logoutUser() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        prefs.edit().remove("loggedInUser").apply();

        Intent intent = new Intent(RaceActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayerBackground != null) {
            mediaPlayerBackground.release();
            mediaPlayerBackground = null;
        }

        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}
