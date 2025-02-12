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

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            winningHorse = extras.getString("winner");
            selectedHorses = extras.getStringArrayList("selectedHorses");
            pointsBefore = extras.getInt("currentPoints");
            betPoints = extras.getInt("betPoints");

            tvWinner.setText("\uD83C\uDFC7 Ngựa thắng: " + winningHorse);

            tvSelectedHorses.setText("\uD83D\uDCCC Bạn đã cược: " + selectedHorses.toString());

            if (selectedHorses.contains(winningHorse)) {
                currentPoints = pointsBefore + (betPoints * 2);
            } else {
                currentPoints = pointsBefore - (betPoints / 2);
            }

            tvPointsBefore.setText("\uD83D\uDCB0 Số điểm trước cược: " + pointsBefore);
            tvPointsAfter.setText("\uD83D\uDCC8 Số điểm sau cược: " + currentPoints);
            tvBetPoints.setText("\uD83C\uDFAF Số điểm đặt cược: " + betPoints);
        }

        btnPlayAgain.setOnClickListener(view -> {
            Intent intent = new Intent(ResultActivity.this, RaceActivity.class);
            intent.putExtra("currentPoints", currentPoints);
            startActivity(intent);
            finish();
        });

        btnExit.setOnClickListener(view -> finishAffinity());
    }
}
