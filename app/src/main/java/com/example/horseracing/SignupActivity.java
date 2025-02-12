package com.example.horseracing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnSignup;
    private TextView tvLogin;
    private SharedPreferences sharedPreferences;
    private MediaPlayer mediaPlayer;
    private SoundPool soundPool;
    private int clickSoundId; // ID của âm thanh khi ấn nút

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignup = findViewById(R.id.btnSignup);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        tvLogin = findViewById(R.id.tvLogin);

        // 🎵 Phát nhạc nền lặp lại
        mediaPlayer = MediaPlayer.create(this, R.raw.signup);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        // 🔊 Khởi tạo SoundPool để phát âm thanh khi nhấn nút
        soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build())
                .build();

        clickSoundId = soundPool.load(this, R.raw.click_sound, 1); // Tải âm thanh click

        tvLogin.setOnClickListener(v -> {
            soundPool.play(clickSoundId, 1, 1, 0, 0, 1); // 🔊 Phát âm thanh
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        btnSignup.setOnClickListener(view -> {
            soundPool.play(clickSoundId, 1, 1, 0, 0, 1); // 🔊 Phát âm thanh
            registerUser();
        });
    }

    private void registerUser() {
        String username = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String usersJson = prefs.getString("users", "{}");

        try {
            JSONObject users = new JSONObject(usersJson);

            if (users.has(username)) {
                Toast.makeText(this, "Tài khoản đã tồn tại!", Toast.LENGTH_SHORT).show();
            } else {
                JSONObject userData = new JSONObject();
                userData.put("password", password);
                userData.put("points", 1000); // Điểm mặc định khi đăng ký

                users.put(username, userData);

                // Lưu tài khoản vào SharedPreferences
                prefs.edit().putString("users", users.toString()).apply();

                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                finish(); // Quay lại màn hình đăng nhập
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}
