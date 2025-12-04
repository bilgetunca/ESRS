package com.example.soscloud.ui.emergencymap;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.soscloud.Model.Emergency;
import com.example.soscloud.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class EmergencyDetailsMapFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "EmergencyDetailsMapFragment";
    private Emergency emergency;
    private OnBackPressedCallback onBackPressedCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkGooglePlayServicesAvailability();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emergency_map, container, false);

        // Toolbar'ı gizle
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().show();
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            setHasOptionsMenu(true);
        }

        Bundle args = getArguments();
        if (args != null) {
            emergency = new Emergency();
            emergency.setLatitude(args.getDouble("lat"));
            emergency.setLongitude(args.getDouble("long"));
        }

        // Geri butonu işlevselliği

        try {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.mapFragment);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing map: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Harita yüklenirken hata oluştu", Toast.LENGTH_SHORT).show();
        }

        setupBackNavigation();
        return view;
    }

    private void setupBackNavigation() {
        onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Toast.makeText(requireContext(),
                    "Haritadan çıkmak için sol üstteki geri tuşunu kullanın",
                    Toast.LENGTH_SHORT).show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedCallback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Toolbar'ı tekrar göster
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }
        if (onBackPressedCallback != null) {
            onBackPressedCallback.remove();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        try {
            if (emergency != null) {
                LatLng emergencyLocation = new LatLng(emergency.getLatitude(), emergency.getLongitude());

                googleMap.addMarker(new MarkerOptions()
                        .position(emergencyLocation)
                        .title("Acil Durum Konumu")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(emergencyLocation, 17f));
            } else {
                Log.e(TAG, "Emergency verisi bulunamadı.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up map: " + e.getMessage(), e);
            if (isAdded()) {
                Toast.makeText(requireContext(), "Harita hazırlanırken hata oluştu", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkGooglePlayServicesAvailability() {
        try {
            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
            int resultCode = apiAvailability.isGooglePlayServicesAvailable(requireContext());
            if (resultCode != ConnectionResult.SUCCESS) {
                if (apiAvailability.isUserResolvableError(resultCode)) {
                    apiAvailability.getErrorDialog(this, resultCode, 9001).show();
                } else {
                    Log.e(TAG, "Google Play Services is not available: " + resultCode);
                    Toast.makeText(requireContext(), "Google Harita servisleri kullanılamıyor", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking Google Play Services: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavHostFragment.findNavController(this).popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}