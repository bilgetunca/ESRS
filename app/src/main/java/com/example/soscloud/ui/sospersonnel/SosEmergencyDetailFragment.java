package com.example.soscloud.ui.sospersonnel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.soscloud.Model.Emergency;
import com.example.soscloud.Model.EmergencyStatus;
import com.example.soscloud.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SosEmergencyDetailFragment extends Fragment {
    private TextView tvEmergencyTypeHeader, tvDescription, tvReporter, tvTime, tvLocation, tvStatus, tvPhoneNumber;
    private Button btnShowOnMap, btnTakeAction, btnMarkComplete;
    private FirebaseFirestore db;
    private Emergency emergency;
    private String emergencyId;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sos_emergency_detail, container, false);
        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            emergencyId = getArguments().getString("emergencyId");
        }

        initializeViews(view);
        setupMap();
        loadEmergencyData();
        setupButtons();

        return view;
    }

    private void initializeViews(View view) {
        tvEmergencyTypeHeader = view.findViewById(R.id.tvEmergencyTypeHeader);
        tvReporter = view.findViewById(R.id.tvReporter);
        tvTime = view.findViewById(R.id.tvTime);
        tvLocation = view.findViewById(R.id.tvLocation);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber);
        btnShowOnMap = view.findViewById(R.id.btnShowOnMap);
        btnTakeAction = view.findViewById(R.id.btnTakeAction);
        btnMarkComplete = view.findViewById(R.id.btnMarkComplete);
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap gMap) {
                    googleMap = gMap;
                    if (emergency != null) {
                        showEmergencyOnMap();
                    }
                }
            });
        }
    }

    private void showEmergencyOnMap() {
        if (googleMap != null && emergency != null) {
            googleMap.clear();
            LatLng location = new LatLng(emergency.getLatitude(), emergency.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(location).title("Acil Durum Konumu"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f));
        }
    }

    private void loadEmergencyData() {
        if (emergencyId != null) {
            db.collection("emergencies").document(emergencyId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    emergency = Emergency.fromDocument(documentSnapshot);
                    if (emergency != null) {
                        updateUI(emergency);
                        showEmergencyOnMap();
                    }
                })
                .addOnFailureListener(e ->
                    Toast.makeText(getContext(), "Veri yüklenirken hata oluştu", Toast.LENGTH_SHORT).show());
        }
    }

    private void updateUI(Emergency emergency) {
        tvEmergencyTypeHeader.setText(emergency.getEmergencyType().getFriendlyName());
        tvReporter.setText(emergency.getStudentNo());
        tvTime.setText(emergency.getTimestamp());
        tvLocation.setText(String.format("%.6f, %.6f", emergency.getLatitude(), emergency.getLongitude()));
        tvStatus.setText(emergency.getStatus().getFriendlyName());

        FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo("studentNo", emergency.getStudentNo())
            .limit(1)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                if (!querySnapshot.isEmpty()) {
                    String phone = querySnapshot.getDocuments().get(0).getString("phoneNumber");
                    tvPhoneNumber.setText("" + (phone != null ? phone : "-"));
                }
            });

        EmergencyStatus status = emergency.getStatus();
        btnTakeAction.setEnabled(status == EmergencyStatus.SENT || status == EmergencyStatus.READ);
        btnMarkComplete.setVisibility(View.GONE);
    }

    private void setupButtons() {
        btnShowOnMap.setOnClickListener(v -> {
            if (emergency != null) {
                Bundle args = new Bundle();
                args.putFloat("latitude", (float) emergency.getLatitude());
                args.putFloat("longitude", (float) emergency.getLongitude());
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_sosEmergencyDetailFragment_to_mapFragment, args);
            }
        });

        btnTakeAction.setOnClickListener(v -> {
            if (emergency != null) {
                if (emergency.getStatus() == EmergencyStatus.SENT) {
                    updateEmergencyStatus(EmergencyStatus.READ);
                } else if (emergency.getStatus() == EmergencyStatus.READ) {
                    updateEmergencyStatus(EmergencyStatus.COMPLETED);
                }
            }
        });
    }

    private void updateEmergencyStatus(EmergencyStatus newStatus) {
        if (emergencyId != null) {
            DocumentReference docRef = db.collection("emergencies").document(emergencyId);
            docRef.update("status", newStatus.name())
                .addOnSuccessListener(aVoid -> {
                    emergency.setStatus(newStatus);
                    updateUI(emergency);
                    showEmergencyOnMap();
                    Toast.makeText(getContext(), "Durum güncellendi", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                    Toast.makeText(getContext(), "Durum güncellenirken hata oluştu", Toast.LENGTH_SHORT).show());
        }
    }
}