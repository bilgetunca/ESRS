package com.example.soscloud;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.soscloud.Model.EmergencyType;
import com.example.soscloud.Model.UserType;
import com.example.soscloud.Repository.AuthRepository;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private AuthRepository authRepository;
    private FirebaseFirestore db;

    private void updateSosPersonnelData(String uid) {
        db.collection("users").document(uid).get()
                .addOnSuccessListener(snapshot -> {
                    String dutyStr = snapshot.getString("duty");
                    if (dutyStr != null) {
                        try {
                            EmergencyType duty = EmergencyType.valueOf(dutyStr);
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("duty", duty.name());
                            updates.put("dutyFriendlyName", duty.getFriendlyName());

                            db.collection("users").document(uid).update(updates)
                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "SOS personeli görevi güncellendi"))
                                    .addOnFailureListener(e -> Log.e(TAG, "SOS personeli görevi güncellenemedi: " + e.getMessage()));
                        } catch (IllegalArgumentException e) {
                            Log.e(TAG, "Geçersiz görev tipi: " + dutyStr);
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        authRepository = new AuthRepository();
        db = FirebaseFirestore.getInstance();

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterformActivity.class);
                startActivity(intent);
                finish();
            }
        });

        TextInputEditText emailInput = findViewById(R.id.emailInput);
        TextInputEditText passwordInput = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Boş alan bırakmayın", Toast.LENGTH_SHORT).show();
                return;
            }

            authRepository.loginUser(email, password,
                    task -> {
                        if (task.isSuccessful()) {
                            String uid = task.getResult().getUser().getUid();

                            db.collection("users").document(uid).get()
                                    .addOnSuccessListener(snapshot -> {
                                        String userType = snapshot.getString("type");

                                        if (UserType.SOS_PERSONEL.toString().equals(userType)) {
                                            updateSosPersonnelData(uid);
                                        }

                                        if (UserType.STUDENT.toString().equals(userType)) {
                                            Intent intent = new Intent(MainActivity.this, ReportEmergency.class);
                                            startActivity(intent);
                                        } else if (UserType.SOS_PERSONEL.toString().equals(userType)) {
                                            Intent intent = new Intent(MainActivity.this, SosPersonnelActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(MainActivity.this, "Kullanıcı tipi tanımsız!", Toast.LENGTH_SHORT).show();
                                        }
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(MainActivity.this, "Kullanıcı bilgileri alınamadı: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "Firestore'dan kullanıcı bilgileri alınamadı: " + e.getMessage());
                                    });
                        }
                    },
                    e -> Toast.makeText(MainActivity.this, "Giriş başarısız: " + e.getMessage(), Toast.LENGTH_LONG).show()
            );
        });
    }
}