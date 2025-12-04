package com.example.soscloud.ui.makenotification;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MakeNotificationViewModel extends ViewModel {
    private final MutableLiveData<String> mText;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = auth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public MakeNotificationViewModel() {
        mText = new MutableLiveData<>();
        if(currentUser != null){
            db.collection("users").document(currentUser.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String firstName = documentSnapshot.getString("name");
                            String lastName = documentSnapshot.getString("surname");
                            mText.setValue("Merhaba "+firstName + " " + lastName);
                        }
                    });

        }

    }

    public LiveData<String> getText() {
        return mText;
    }
}
