package com.example.horseracing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 🎵 Khởi tạo và phát nhạc nền
        mediaPlayer = MediaPlayer.create(this, R.raw.background_theme);
        mediaPlayer.setLooping(true); // Lặp vô hạn
        mediaPlayer.start(); // Bắt đầu phát nhạc

        SharedPreferences sharedPreferences = getSharedPreferences("HorseRacingPrefs", MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("SharedPreferences", entry.getKey() + ": " + entry.getValue().toString());
        }

        new Handler().postDelayed(() -> {
            // 🛑 Dừng nhạc trước khi chuyển màn hình
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }

            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
