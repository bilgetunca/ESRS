package com.example.soscloud.ui.makenotification;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.soscloud.Model.EmergencyType;
import com.example.soscloud.R;
import com.example.soscloud.ui.common.FirstAidInfoBottomSheet;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EmergencyFragment extends Fragment {
    private static final String TAG = "EmergencyFragment";
    private TextView txtLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<String> permissionLauncher;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = auth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check Google Play Services availability first
        checkGooglePlayServicesAvailability();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emergency_form, container, false);
        txtLocation = view.findViewById(R.id.txtLocation);
        Button btnGetLocation = view.findViewById(R.id.buttonCoordinates);

        try {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        } catch (Exception e) {
            Log.e(TAG, "Error initializing location client: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Konum servisi başlatılamadı", Toast.LENGTH_SHORT).show();
        }

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        getLocation();
                    } else {
                        Toast.makeText(requireContext(), "Konum izni reddedildi", Toast.LENGTH_SHORT).show();
                    }
                });

        btnGetLocation.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            } else {
                getLocation();
            }
        });

        Button confirmButton = view.findViewById(R.id.emergencyConfirmBtn);
        RadioButton radioRevir = view.findViewById(R.id.radioRevir);
        RadioButton radioYangin = view.findViewById(R.id.radioYangin);
        RadioButton radioGuvenlik = view.findViewById(R.id.radioGuvenlik);
        RadioButton radioPolis = view.findViewById(R.id.radioPolis);
        List<RadioButton> radioButtons = Arrays.asList(radioRevir, radioYangin, radioGuvenlik, radioPolis);
        for (RadioButton rb : radioButtons) {
            rb.setOnClickListener(v -> {
                for (RadioButton otherRb : radioButtons) {
                    otherRb.setChecked(otherRb == rb);
                }
            });
        }
        confirmButton.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Konum izni yok", Toast.LENGTH_SHORT).show();
                return;
            }

            if (fusedLocationClient == null) {
                Toast.makeText(requireContext(), "Konum servisi kullanılamıyor", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                        .addOnSuccessListener(requireActivity(), location -> {
                            if (location == null) {
                                Toast.makeText(requireContext(), "Konum alınamadı", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Bundle bundle = new Bundle();
                            bundle.putDouble("lat", location.getLatitude());
                            bundle.putDouble("long", location.getLongitude());
                            String selectedType = null;
                            if (radioRevir.isChecked()) {
                                selectedType = EmergencyType.MEDICAL.getFriendlyName();
                            } else if (radioYangin.isChecked()) {
                                selectedType = EmergencyType.FIRE.getFriendlyName();
                            } else if (radioGuvenlik.isChecked()) {
                                selectedType = EmergencyType.SECURITY.getFriendlyName();
                            } else if (radioPolis.isChecked()) {
                                selectedType = EmergencyType.DANGER.getFriendlyName();
                            }
                            bundle.putString("emergencyType",selectedType);
                            if (selectedType == null) {
                                Toast.makeText(requireContext(), "Lütfen bir acil durum türü seçin", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (currentUser != null) {
                                db.collection("users").document(currentUser.getUid()).get()
                                        .addOnSuccessListener(documentSnapshot -> {
                                            if (documentSnapshot.exists()) {
                                                String studentNo = documentSnapshot.getString("studentNo");
                                                bundle.putString("studentNo", studentNo);

                                                NavHostFragment.findNavController(EmergencyFragment.this).navigate(
                                                        R.id.action_emergencyFormFragment_to_emergencyModalFragment,
                                                        bundle
                                                );

                                            } else {
                                                Toast.makeText(requireContext(), "Öğrenci bilgisi alınamadı", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(requireContext(), "Firestore hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Location error: " + e.getMessage(), e);
                            Toast.makeText(requireContext(), "Konum alınırken hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } catch (Exception e) {
                Log.e(TAG, "Error getting location: " + e.getMessage(), e);
                Toast.makeText(requireContext(), "Konum servisi hatası: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void checkGooglePlayServicesAvailability() {
        try {
            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
            int resultCode = apiAvailability.isGooglePlayServicesAvailable(requireContext());
            if (resultCode != ConnectionResult.SUCCESS) {
                if (apiAvailability.isUserResolvableError(resultCode)) {
                    apiAvailability.getErrorDialog(this, resultCode, 9000).show();
                    // Servisin erişilebilir olmasını zorlamak için bu yöntemi kullanabiliriz
                    apiAvailability.makeGooglePlayServicesAvailable(requireActivity())
                            .addOnSuccessListener(task -> Log.d(TAG, "Google Play Services is now available"))
                            .addOnFailureListener(e -> Log.e(TAG, "Failed to make Google Play Services available", e));
                } else {
                    Log.e(TAG, "Google Play Services is not available: " + resultCode);
                    Toast.makeText(requireContext(), "Google Play Servisleri kullanılamıyor", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d(TAG, "Google Play Services is available");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking Google Play Services: " + e.getMessage(), e);
        }
    }

    private void getLocation() {
        if (fusedLocationClient == null) {
            Toast.makeText(requireContext(), "Konum servisi kullanılamıyor", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            getCityFromLocation(location);
                        } else {
                            txtLocation.setText("Konum alınamadı. Lütfen GPS'in açık olduğundan emin olun.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Location error: " + e.getMessage(), e);
                        txtLocation.setText("Konum alınırken hata oluştu: " + e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error in getLocation: " + e.getMessage(), e);
            txtLocation.setText("Konum servisi hatası: " + e.getMessage());
        }
    }

    private void getCityFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String city = address.getLocality();
                String district = address.getSubAdminArea();
                String country = address.getCountryName();

                txtLocation.setText(city + "-" + district + "-" + country);
            } else {
                txtLocation.setText("Adres bulunamadı");
            }
        } catch (IOException e) {
            txtLocation.setText("Adres alınırken hata oluştu");
            e.printStackTrace();
        }
    }
}