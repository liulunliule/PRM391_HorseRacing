package com.example.horseracing;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {
    private TextView tvWinner, tvSelectedHorses, tvPointsBefore, tvPointsAfter,tvBetPoints;
    private Button btnPlayAgain, btnExit;
    private int currentPoints, betPoints, pointsBefore;
    private String winningHorse;
    private ArrayList<String> selectedHorses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tvWinner = findViewById(R.id.tvWinner);
        tvSelectedHorses = findViewById(R.id.tvSelectedHorses);
        tvPointsBefore = findViewById(R.id.tvPointsBefore);
        tvPointsAfter = findViewById(R.id.tvPointsAfter);
        tvBetPoints = findViewById(R.id.tvBetPoints);
        btnPlayAgain = findViewById(R.id.btnPlayAgain);
        btnExit = findViewById(R.id.btnExit);

        // Nhận dữ liệu từ RaceActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            winningHorse = extras.getString("winner");
            selectedHorses = extras.getStringArrayList("selectedHorses");
            pointsBefore = extras.getInt("currentPoints");
            betPoints = extras.getInt("betPoints");

            // Hiển thị ngựa thắng
            tvWinner.setText("Ngựa thắng: " + winningHorse);

            // Hiển thị danh sách ngựa đã cược
            tvSelectedHorses.setText("Bạn đã cược: " + selectedHorses.toString());

            // Tính điểm sau cược
            if (selectedHorses.contains(winningHorse)) {
                currentPoints = pointsBefore + (betPoints * 2);
            } else {
                currentPoints = pointsBefore - (betPoints / 2);
            }

            // Hiển thị số điểm trước & sau cược
            tvPointsBefore.setText("Số điểm trước cược: " + pointsBefore);
            tvPointsAfter.setText("Số điểm sau cược: " + currentPoints);
            tvBetPoints.setText("Số điểm đặt cược: " + betPoints);
        }

        // Nút chơi lại
        btnPlayAgain.setOnClickListener(view -> {
            Intent intent = new Intent(ResultActivity.this, RaceActivity.class);
            intent.putExtra("currentPoints", currentPoints);
            startActivity(intent);
            finish();
        });

        // Nút thoát
        btnExit.setOnClickListener(view -> finishAffinity());
    }
}
