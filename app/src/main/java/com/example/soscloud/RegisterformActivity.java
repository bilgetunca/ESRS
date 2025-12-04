package com.example.soscloud;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.soscloud.Model.User;
import com.example.soscloud.Model.UserType;
import com.example.soscloud.Model.EmergencyType;
import com.example.soscloud.Repository.AuthRepository;
import com.example.soscloud.Utils.InputValidator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Console;
import java.util.Objects;

import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;

public class RegisterformActivity extends AppCompatActivity {
    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.registerform);
        MaterialButton registerBtn = findViewById(R.id.registerButton);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        authRepository = new AuthRepository();
        RadioGroup userTypeGroup = findViewById(R.id.userTypeGroup);
        LinearLayout studentFields = findViewById(R.id.studentFields);
        TextInputLayout staffField = findViewById(R.id.staffField);
        userTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioStudent) {
                studentFields.setVisibility(View.VISIBLE);
                staffField.setVisibility(View.GONE);
            } else if (checkedId == R.id.radioStaff) {
                studentFields.setVisibility(View.GONE);
                staffField.setVisibility(View.VISIBLE);
            }
        });

        TextInputEditText emailInput = findViewById(R.id.emailInput);
        TextInputEditText nameInput = findViewById(R.id.nameInput);
        TextInputEditText surnameInput = findViewById(R.id.surnameInput);
        AutoCompleteTextView dutyInput = findViewById(R.id.staffDutyInput);
        TextInputEditText passwordInput = findViewById(R.id.passwordInput);
        TextInputEditText confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        TextInputEditText studentNoInput = findViewById(R.id.studentNumberInput);
        TextInputEditText roomNoInput = findViewById(R.id.roomInput);

        String[] emergencyTypes = new String[]{
            EmergencyType.MEDICAL.getFriendlyName(),
            EmergencyType.FIRE.getFriendlyName(),
            EmergencyType.SECURITY.getFriendlyName(),
            EmergencyType.DANGER.getFriendlyName()
        };
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            emergencyTypes
        );
        dutyInput.setAdapter(adapter);

        registerBtn.setOnClickListener(v -> {
            int selectedId = userTypeGroup.getCheckedRadioButtonId();
            UserType userType = selectedId == R.id.radioStudent ? UserType.STUDENT : UserType.SOS_PERSONEL;
            String email = Objects.requireNonNull(emailInput.getText()).toString().trim();
            String name = nameInput.getText().toString().trim();
            String surname = surnameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();
            String studentNo = studentNoInput.getText().toString().trim();
            String roomNo = roomNoInput.getText().toString().trim();
            String dutyFriendlyName = dutyInput.getText().toString().trim();
            EmergencyType duty = null;
            
            if (!InputValidator.isEmailValid(email)) {
                emailInput.setError("Geçerli bir e-posta girin");
                return;
            }

            if (name.isEmpty() || surname.isEmpty()) {
                Toast.makeText(this, "İsim ve soyisim boş bırakılamaz", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedId == R.id.radioStudent) {
                if (studentNo.isEmpty()) {
                    studentNoInput.setError("Öğrenci numarası zorunludur");
                    return;
                }
            } else if (selectedId == R.id.radioStaff) {
                if (dutyFriendlyName.isEmpty()) {
                    dutyInput.setError("Birim bilgisi zorunludur");
                    return;
                }
                try {
                    duty = EmergencyType.fromFriendlyName(dutyFriendlyName);
                } catch (IllegalArgumentException e) {
                    dutyInput.setError("Geçersiz birim seçimi");
                    return;
                }
            }

            if (!InputValidator.isPasswordValid(password, confirmPassword)) {
                Toast.makeText(this, "Şifreler uyuşmuyor", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User(email, name, surname, userType,
                    selectedId == R.id.radioStudent ? studentNo : null,
                    selectedId == R.id.radioStudent ? roomNo : null,
                    selectedId == R.id.radioStaff ? duty : null);

            authRepository.registerUser(email, password,
                    task -> {
                        if (task.isSuccessful()) {
                            String uid = task.getResult().getUser().getUid();
                            authRepository.saveUserData(uid, user.toMap(),
                                    unused -> Toast.makeText(this, "Kayıt başarılı 🎉", Toast.LENGTH_SHORT).show(),
                                    e -> Toast.makeText(this, "Veri eklenemedi: " + e.getMessage(), Toast.LENGTH_LONG).show()
                            );
                        } else {
                            Toast.makeText(this, "Kayıt başarısız: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    },
                    e -> Toast.makeText(this, "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );

        });


    }
}