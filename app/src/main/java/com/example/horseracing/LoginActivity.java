package com.example.horseracing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.AudioAttributes;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.horseracing.databinding.ActivityLoginBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignup;
    private MediaPlayer mediaPlayer;
    private SoundPool soundPool;
    private int clickSoundId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        tvSignup = findViewById(R.id.tvSignup);

        // Loop music
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

        clickSoundId = soundPool.load(this, R.raw.click_sound, 1);


        tvSignup.setOnClickListener(v -> {
            soundPool.play(clickSoundId, 1, 1, 0, 0, 1); // 🔊 Phát âm thanh
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(view -> {
            soundPool.play(clickSoundId, 1, 1, 0, 0, 1); // 🔊 Phát âm thanh
            loginUser();
        });
    }

    private void loginUser() {
        String username = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String usersJson = prefs.getString("users", "{}"); // Lấy danh sách tài khoản

        try {
            JSONObject users = new JSONObject(usersJson);

            if (users.has(username)) {
                JSONObject userData = users.getJSONObject(username);
                String savedPassword = userData.getString("password");

                if (savedPassword.equals(password)) {
                    int currentPoints = userData.getInt("points");

                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                    // Lưu tài khoản đăng nhập vào SharedPreferences
                    prefs.edit().putString("loggedInUser", username).apply();

                    // Pause music
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }

                    // Chuyển qua màn hình đua ngựa
                    Intent intent = new Intent(LoginActivity.this, RaceActivity.class);
                    intent.putExtra("currentPoints", currentPoints);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Sai mật khẩu!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Tài khoản không tồn tại!", Toast.LENGTH_SHORT).show();
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