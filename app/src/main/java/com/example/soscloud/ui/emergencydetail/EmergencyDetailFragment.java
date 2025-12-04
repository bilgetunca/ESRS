package com.example.soscloud.ui.emergencydetail;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.soscloud.Model.Emergency;
import com.example.soscloud.Model.EmergencyStatus;
import com.example.soscloud.Model.EmergencyType;
import com.example.soscloud.Model.UserType;
import com.example.soscloud.R;
import com.example.soscloud.databinding.FragmentEmergencyDetailBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EmergencyDetailFragment extends Fragment implements OnMapReadyCallback {

    private FragmentEmergencyDetailBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String emergencyId;
    private Emergency emergency;
    private String currentUserType;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEmergencyDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (getArguments() != null) {
            emergencyId = getArguments().getString("emergencyId");
        }

        if (emergencyId == null) {
            showError("Acil durum bilgisi bulunamadı");
            navigateBack();
            return;
        }

        setupUI();
        setupMapFragment();

        if (currentUser != null) {
            loadUserType(currentUser.getUid());
        } else {
            showError("Oturum açmanız gerekiyor");
            navigateBack();
        }
    }

    private void setupUI() {
        binding.btnShowOnMap.setOnClickListener(v -> {
            if (emergency != null) {
                Bundle bundle = new Bundle();
                bundle.putDouble("lat", emergency.getLatitude());
                bundle.putDouble("long", emergency.getLongitude());
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_emergencyDetailFragment_to_emergencyDetailsMapFragment, bundle);
            } else {
                Toast.makeText(requireContext(), "Konum bilgisi bulunamadı", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void loadUserType(String userId) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentUserType = documentSnapshot.getString("type");
                        loadEmergencyDetails();
                    } else {
                        showError("Kullanıcı bilgileri bulunamadı");
                    }
                })
                .addOnFailureListener(e -> showError("Kullanıcı bilgileri alınamadı: " + e.getMessage()));
    }


    private void displayEmergencyDetails(Emergency emergency) {
        binding.emergencyTypeText.setText(emergency.getEmergencyType().getFriendlyName());
        binding.emergencyStatusText.setText(emergency.getStatus().getFriendlyName());
        binding.studentNoText.setText(emergency.getStudentNo());

        if (UserType.SOS_PERSONEL.toString().equals(currentUserType)) {
            loadStudentDetails(emergency.getStudentNo());
        }


        try {
            long timestamp = Long.parseLong(emergency.getTimestamp());
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            String dateString = sdf.format(new Date(timestamp));
            binding.emergencyTimeText.setText(dateString);
        } catch (NumberFormatException e) {
            binding.emergencyTimeText.setText(emergency.getTimestamp());
        }

        binding.locationText.setText(String.format(Locale.getDefault(),
                "Enlem: %.6f, Boylam: %.6f", emergency.getLatitude(), emergency.getLongitude()));
    }

    private void loadStudentDetails(String studentNo) {
        db.collection("users")
                .whereEqualTo("studentNo", studentNo)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot userDoc = queryDocumentSnapshots.getDocuments().get(0);
                        String name = userDoc.getString("name");
                        String surname = userDoc.getString("surname");
                        String fullName = (name != null ? name : "") + " " + (surname != null ? surname : "");

                        if (!fullName.trim().isEmpty()) {
                            binding.studentNameText.setText(fullName);
                            binding.studentNameLayout.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .addOnFailureListener(e -> e.printStackTrace());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        binding.mapProgressBar.setVisibility(View.GONE);

        if (emergency != null) {
            LatLng location = new LatLng(emergency.getLatitude(), emergency.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(location).title("Acil Durum Konumu"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
        }
    }

    private void startNavigation(double latitude, double longitude) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        try {
            startActivity(mapIntent);
        } catch (Exception e) {
            Uri webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude);
            Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
            startActivity(webIntent);
        }
    }

    private void showUpdateStatusDialog() {
        if (emergency == null || emergency.getStatus() == EmergencyStatus.COMPLETED) {
            showError("Bu acil durum zaten tamamlandı");
            return;
        }

        EmergencyStatus newStatus = emergency.getStatus() == EmergencyStatus.SENT ?
                EmergencyStatus.READ : EmergencyStatus.COMPLETED;

        new AlertDialog.Builder(requireContext())
                .setTitle("Durum Güncelleme")
                .setMessage("Bu acil durumun durumunu '" + newStatus.getFriendlyName() + "' olarak güncellemek istiyor musunuz?")
                .setPositiveButton("Evet", (dialog, which) -> updateEmergencyStatus(newStatus))
                .setNegativeButton("İptal", null)
                .show();
    }

    private void updateEmergencyStatus(EmergencyStatus newStatus) {
        db.collection("emergencies").document(emergencyId)
                .update("status", newStatus.name())
                .addOnSuccessListener(aVoid -> {
                    emergency.setStatus(newStatus);
                    binding.emergencyStatusText.setText(newStatus.getFriendlyName());
                    showMessage("Durum başarıyla güncellendi");
                })
                .addOnFailureListener(e -> showError("Durum güncellenemedi: " + e.getMessage()));
    }

    private void contactStudent(String studentNo) {
        db.collection("users")
                .whereEqualTo("studentNo", studentNo)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot studentDoc = queryDocumentSnapshots.getDocuments().get(0);
                        String phoneNumber = studentDoc.getString("phoneNumber");

                        if (phoneNumber != null && !phoneNumber.isEmpty()) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + phoneNumber));
                            startActivity(intent);
                        } else {
                            showError("Öğrencinin telefon numarası bulunamadı");
                        }
                    } else {
                        showError("Öğrenci bulunamadı");
                    }
                })
                .addOnFailureListener(e -> showError("Öğrenci bilgileri alınamadı: " + e.getMessage()));
    }

    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void showMessage(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateBack() {
        if (getView() != null) {
            Navigation.findNavController(getView()).navigateUp();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadEmergencyDetails() {
        db.collection("emergencies").document(emergencyId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        emergency = convertDocumentToEmergency(documentSnapshot);
                        if (emergency != null) {
                            displayEmergencyDetails(emergency);
                            if (googleMap != null) {
                                LatLng location = new LatLng(emergency.getLatitude(), emergency.getLongitude());
                                googleMap.addMarker(new MarkerOptions().position(location).title("Acil Durum Konumu"));
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
                            }
                        } else {
                            showError("Acil durum verisi okunamadı");
                            navigateBack();
                        }
                    } else {
                        showError("Acil durum bulunamadı");
                        navigateBack();
                    }
                })
                .addOnFailureListener(e -> {
                    showError("Veri yüklenirken hata: " + e.getMessage());
                    navigateBack();
                });
    }

    private Emergency convertDocumentToEmergency(DocumentSnapshot doc) {
        try {
            String studentNo = doc.getString("studentNo");
            String emergencyTypeStr = doc.getString("emergencyType");
            Double longitude = doc.getDouble("longitude");
            Double latitude = doc.getDouble("latitude");
            String statusStr = doc.getString("status");
            String timestamp = doc.getString("timestamp");

            if (studentNo == null || emergencyTypeStr == null ||
                    longitude == null || latitude == null || statusStr == null || timestamp == null) {
                return null;
            }

            EmergencyType emergencyType = EmergencyType.valueOf(emergencyTypeStr);
            EmergencyStatus status = EmergencyStatus.valueOf(statusStr);

            Emergency emergency = new Emergency(studentNo, emergencyType,
                    longitude, latitude,  status, timestamp);
            emergency.setId(doc.getId());
            return emergency;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}