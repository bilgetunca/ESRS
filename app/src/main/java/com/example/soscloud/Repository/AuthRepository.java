package com.example.soscloud.Repository;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class AuthRepository {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public void registerUser(
            String email,
            String password,
            @NonNull OnCompleteListener<AuthResult> onCompleteListener,
            @NonNull OnFailureListener onFailureListener
    ) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(onFailureListener);
    }

    public void saveUserData(
            @NonNull String uid,
            @NonNull Map<String, Object> userData,
            @NonNull OnSuccessListener<Void> onSuccessListener,
            @NonNull OnFailureListener onFailureListener
    ) {
        db.collection("users")
                .document(uid)
                .set(userData)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void loginUser(
            String email,
            String password,
            @NonNull OnCompleteListener<AuthResult> onCompleteListener,
            @NonNull OnFailureListener onFailureListener
    ) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(onCompleteListener)
                .addOnFailureListener(onFailureListener);
    }
    public FirebaseAuth getAuth() {
        return auth;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

}
