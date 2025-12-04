package com.example.soscloud;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.soscloud.databinding.ActivitySosPersonnelBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class SosPersonnelActivity extends AppCompatActivity {

    private static final String TAG = "SosPersonnelActivity";
    private AppBarConfiguration mAppBarConfiguration;
    private ActivitySosPersonnelBinding binding;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = auth.getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d(TAG, "Notification permission granted");
                } else {
                    Log.d(TAG, "Notification permission denied");
                    Toast.makeText(this, "Bildirimleri almak için izin vermeniz gerekiyor", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySosPersonnelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        askNotificationPermission();

        createNotificationChannel();

        // getFCMToken();

        setSupportActionBar(binding.appBarSosPersonnel.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        View headerView = navigationView.getHeaderView(0);
        if (currentUser != null) {
            String email = currentUser.getEmail();
            TextView usernameTitle = headerView.findViewById(R.id.nav_user_name);
            TextView emailInfo = headerView.findViewById(R.id.nav_email);
            emailInfo.setText(email);

            db.collection("users").document(currentUser.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String firstName = documentSnapshot.getString("name");
                            String lastName = documentSnapshot.getString("surname");
                            usernameTitle.setText(firstName + " " + lastName);
                        }
                    });
        }

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.departmentNotificationsFragment,
                R.id.allEmergencyFragment,
                R.id.nav_campus_map,
                R.id.nav_profile)
                .setOpenableLayout(drawer)
                .build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_sos_personnel);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_logout) {
                auth.signOut();
                Intent intent = new Intent(SosPersonnelActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            }
            drawer.closeDrawers();
            return NavigationUI.onNavDestinationSelected(item, navController);
        });
    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "default_channel",
                    "Varsayılan Kanal",
                    NotificationManager.IMPORTANCE_HIGH);

            channel.setDescription("Acil durum bildirimleri için");
            channel.enableVibration(true);
            channel.enableLights(true);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d(TAG, "Notification channel created");
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_sos_personnel);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}