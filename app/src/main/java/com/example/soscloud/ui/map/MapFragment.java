package com.example.soscloud.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.soscloud.databinding.FragmentEmergencyMapBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import android.util.Log;

public class MapFragment extends Fragment {

    private FragmentEmergencyMapBinding binding;
    private OnBackPressedCallback callback;
    private float latitude = 0f;
    private float longitude = 0f;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEmergencyMapBinding.inflate(inflater, container, false);
        if (getArguments() != null) {
            latitude = getArguments().getFloat("latitude", 0f);
            longitude = getArguments().getFloat("longitude", 0f);
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(com.example.soscloud.R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    final LatLng emergencyLocation;
                    if (latitude != 0f && longitude != 0f) {
                        emergencyLocation = new LatLng(latitude, longitude);
                        googleMap.addMarker(new MarkerOptions().position(emergencyLocation).title("Acil Durum Konumu"));
                    } else {
                        emergencyLocation = null;
                    }
                    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                            if (location != null) {
                                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

                                // DEBUG KODLARI - BAŞLANGIÇ
                                Log.d("MAP_DEBUG", "=== KONUM BİLGİLERİ ===");
                                Log.d("MAP_DEBUG", "Latitude: " + location.getLatitude());
                                Log.d("MAP_DEBUG", "Longitude: " + location.getLongitude());
                                Log.d("MAP_DEBUG", "Accuracy: " + location.getAccuracy());
                                Log.d("MAP_DEBUG", "GoogleMap null mu: " + (googleMap == null));
                                // DEBUG KODLARI - BİTİŞ

                                googleMap.addMarker(new MarkerOptions().position(userLocation).title("Benim Konumum"));
                                if (emergencyLocation != null) {
                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                    builder.include(emergencyLocation);
                                    builder.include(userLocation);
                                    LatLngBounds bounds = builder.build();
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120));
                                } else {
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16f));
                                }
                            } else if (emergencyLocation != null) {
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(emergencyLocation, 16f));
                            }
                        });
                    } else if (emergencyLocation != null) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(emergencyLocation, 16f));
                    }
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        if (callback != null) {
            callback.remove();
        }
        super.onDestroyView();
        binding = null;
    }
} 