package com.example.soscloud.ui.emergencymodal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.soscloud.Model.Emergency;
import com.example.soscloud.Model.EmergencyStatus;
import com.example.soscloud.Model.EmergencyType;
import com.example.soscloud.Model.UserType;
import com.example.soscloud.R;
import com.example.soscloud.ui.makenotification.EmergencyFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDateTime;

public class EmergencyModal extends Fragment {

    private EmergencyModalViewModel mViewModel;

    public static EmergencyModal newInstance() {
        return new EmergencyModal();
    }
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Emergency emergency;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emergency_modal, container, false);
        Button goBackButton = view.findViewById(R.id.btnCancelAlert);
        Button showMapButton = view.findViewById(R.id.btnShowMap);
        TextView emergencyTypeLabel = view.findViewById(R.id.emergencyTypeLabel);

        emergency = extractEmergencyData();

        if (emergency == null) {
            Log.e("EmergencyModal", "Emergency objesi boş.");
        }
        emergencyTypeLabel.setText(emergency.getEmergencyType().getFriendlyName()+" Acil Durum");
        goBackButton.setOnClickListener(v -> {
            NavHostFragment.findNavController(EmergencyModal.this)
                    .navigate(R.id.action_emergencyModalFragment_to_makeNotificationFragment);
        });

        showMapButton.setOnClickListener(v -> {
            if (emergency != null) {
                sendNotificationToSosStaff(emergency, () -> {
                    Bundle mapBundle = new Bundle();
                    mapBundle.putDouble("lat", emergency.getLatitude());
                    mapBundle.putDouble("long", emergency.getLongitude());

                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_emergencyModalFragment_to_emergencyDetailsMapFragment, mapBundle);
                });
            } else {
                Log.e("EmergencyModal", "Emergency objesi boş, bildirim gönderilemedi.");
            }
        });

        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        Log.w("EmergencyModal", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    String myToken = task.getResult();
                    Log.d("EmergencyModal", "My FCM Token: " + myToken);

                    String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    FirebaseFirestore.getInstance().collection("users")
                        .document(currentUserId)
                        .update("fcmToken", myToken)
                        .addOnSuccessListener(aVoid -> Log.d("FCM", "Token Firestore'a kaydedildi"))
                        .addOnFailureListener(e -> Log.e("FCM", "Token kaydedilemedi", e));
                }
            });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }
    }

    private Emergency extractEmergencyData() {
        Bundle args = getArguments();
        if (args == null) {
            Log.e("EmergencyModal", "Arguments boş");
            return null;
        }

        try {
            String emergencyTypeStr = args.getString("emergencyType");
            EmergencyType emergencyType = EmergencyType.fromFriendlyName(emergencyTypeStr);

            String studentNo = args.getString("studentNo");
            double latitude = args.getDouble("lat");
            double longitude = args.getDouble("long");

            return new Emergency(
                    studentNo,
                    emergencyType,
                    longitude,
                    latitude,
                    EmergencyStatus.SENT,
                    Timestamp.now().toString()
            );

        } catch (Exception e) {
            Log.e("EmergencyModal", "Hata: " + e.getMessage());
            return null;
        }
    }

    private void sendNotificationToSosStaff(Emergency emergency, Runnable onSuccess) {
        String emergencyId = db.collection("emergencies").document().getId();
        emergency.setStatus(EmergencyStatus.SENT);
        emergency.setTimestamp(String.valueOf(System.currentTimeMillis()));

        db.collection("emergencies").document(emergencyId)
                .set(emergency.toMap())
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Emergency başarıyla kaydedildi: " + emergencyId);
                    sendToSosPersonnel(emergency, onSuccess);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Emergency kaydedilemedi: " + e.getMessage());
                });
    }

    private void sendToSosPersonnel(Emergency emergency, Runnable onSuccess) {
        Log.d("EmergencyModal", "Sending notification to SOS personnel for emergency type: " + emergency.getEmergencyType().getFriendlyName());
        db.collection("users")
                .whereEqualTo("type", UserType.SOS_PERSONEL.toString())
                .whereEqualTo("duty", emergency.getEmergencyType().name())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String token = document.getString("fcmToken");
                        if (token != null && !token.isEmpty()) {
                            String title = "Acil Durum - " + emergency.getEmergencyType().getFriendlyName();
                            // TODO: Replace with Cloud Function notification trigger
                            Log.d("EmergencyModal", "Notification sent to token: " + token);
                        } else {
                            Log.e("EmergencyModal", "SOS personelinin FCM Token'ı yok veya boş: " + document.getId());
                        }
                    }
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("EmergencyModal", "SOS personeline bildirim gönderilemedi", e);
                });
    }

}