package com.example.horseracing;

import android.content.Intent;
import android.content.SharedPreferences;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        tvSignup = findViewById(R.id.tvSignup);

        tvSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(view -> loginUser());
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
}