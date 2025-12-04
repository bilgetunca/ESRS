package com.example.soscloud.Utils;

import android.util.Log;
import android.util.Patterns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InputValidator {
    public static boolean isEmailValid(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public static boolean isPasswordValid(String password, String confirmPassword){
        return password.equals(confirmPassword);
    }
    public static String convertTimestampToDateTime(String firebaseTimestamp) {
        try {
            long timestamp = Long.parseLong(firebaseTimestamp);
            Date date = new Date(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            return sdf.format(date);
        } catch (NumberFormatException e) {
            Log.e("TimestampConverter", "Timestamp sayıya dönüştürülemedi: " + e.getMessage());
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                return sdf.parse(firebaseTimestamp).toString();
            } catch (ParseException pe) {
                Log.e("TimestampConverter", "Tarih string'i parse edilemedi: " + pe.getMessage());
                return firebaseTimestamp;
            }
        }
    }

}
