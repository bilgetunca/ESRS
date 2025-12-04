package com.example.soscloud.ui.department;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.soscloud.Model.Emergency;
import com.example.soscloud.Model.EmergencyStatus;
import com.example.soscloud.Model.User;
import com.example.soscloud.R;
import com.example.soscloud.adapters.EmergencyAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class DepartmentNotificationsFragment extends Fragment implements EmergencyAdapter.OnEmergencyClickListener {
    private static final String TAG = "DepartmentNotifications";
    private RecyclerView recyclerView;
    private EmergencyAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyView;
    private FirebaseFirestore db;
    private User currentUser;
    private Spinner statusFilterSpinner, dateFilterSpinner;
    private String selectedStatusFilter = "Tümü";
    private String selectedDateFilter = "Tümü";
    private final List<Emergency> allEmergencies = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_department_notifications, container, false);
        recyclerView = root.findViewById(R.id.recyclerViewDepartmentNotifications);
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);
        emptyView = root.findViewById(R.id.emptyView);
        statusFilterSpinner = root.findViewById(R.id.statusFilterSpinner);
        dateFilterSpinner = root.findViewById(R.id.dateFilterSpinner);

        setupRecyclerView();
        setupSpinners();
        setupSwipeRefresh();
        loadCurrentUser();

        return root;
    }

    private void setupSpinners() {
        List<String> statusOptions = Arrays.asList("Tümü", "Gönderildi", "Okundu", "Tamamlandı");
        List<String> dateOptions = Arrays.asList("Tümü", "Bugün", "Dün", "Son 7 Gün");

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, statusOptions);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusFilterSpinner.setAdapter(statusAdapter);

        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, dateOptions);
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateFilterSpinner.setAdapter(dateAdapter);

        statusFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStatusFilter = statusOptions.get(position);
                filterAndDisplayEmergencies();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        dateFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDateFilter = dateOptions.get(position);
                filterAndDisplayEmergencies();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void filterAndDisplayEmergencies() {
        List<Emergency> filtered = new ArrayList<>();
        Calendar now = Calendar.getInstance();

        for (Emergency emergency : allEmergencies) {
            boolean matchesStatus = selectedStatusFilter.equals("Tümü") ||
                    emergency.getStatus().getFriendlyName().equals(selectedStatusFilter);

            boolean matchesDate = true;
            if (!selectedDateFilter.equals("Tümü")) {
                long timestampMillis = Long.parseLong(emergency.getTimestamp());
                Calendar emergencyTime = Calendar.getInstance();
                emergencyTime.setTimeInMillis(timestampMillis);

                switch (selectedDateFilter) {
                    case "Bugün":
                        matchesDate = isSameDay(now, emergencyTime);
                        break;
                    case "Dün":
                        Calendar yesterday = (Calendar) now.clone();
                        yesterday.add(Calendar.DATE, -1);
                        matchesDate = isSameDay(yesterday, emergencyTime);
                        break;
                    case "Son 7 Gün":
                        Calendar sevenDaysAgo = (Calendar) now.clone();
                        sevenDaysAgo.add(Calendar.DATE, -7);
                        matchesDate = emergencyTime.after(sevenDaysAgo);
                        break;
                }
            }

            if (matchesStatus && matchesDate) {
                filtered.add(emergency);
            }
        }

        if (filtered.isEmpty()) {
            showEmptyView("Filtreye uygun bildirim bulunmamaktadır.");
        } else {
            hideEmptyView();
            adapter.updateData(filtered);
        }
    }

    private boolean isSameDay(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    private void setupRecyclerView() {
        adapter = new EmergencyAdapter(requireContext(), new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (currentUser != null && currentUser.getDuty() != null) {
                loadDepartmentNotifications();
            } else {
                loadCurrentUser();
            }
        });
    }

    private void loadCurrentUser() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    currentUser = User.fromMap(documentSnapshot.getData());
                    loadDepartmentNotifications();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading user: " + e.getMessage());
                    showEmptyView("Kullanıcı bilgileri yüklenirken hata oluştu");
                    swipeRefreshLayout.setRefreshing(false);
                });
    }

    private void loadDepartmentNotifications() {
        if (currentUser == null || currentUser.getDuty() == null) {
            showEmptyView("Departman bilgisi bulunamadı");
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        String dutyType = currentUser.getDuty().name();
        db.collection("emergencies")
                .whereEqualTo("emergencyType", dutyType)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allEmergencies.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Emergency emergency = Emergency.fromDocument(doc);
                        if (emergency != null) {
                            allEmergencies.add(emergency);
                        }
                    }
                    filterAndDisplayEmergencies();
                    swipeRefreshLayout.setRefreshing(false);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading notifications: " + e.getMessage());
                    showEmptyView("Bildirimler yüklenirken hata oluştu");
                    swipeRefreshLayout.setRefreshing(false);
                });
    }

    private void showEmptyView(String message) {
        emptyView.setText(message);
        emptyView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void hideEmptyView() {
        emptyView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onEmergencyClick(Emergency emergency, int position) {
        if (emergency != null) {
            Bundle bundle = new Bundle();
            bundle.putString("emergencyId", emergency.getId());
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_departmentNotificationsFragment_to_sosEmergencyDetailFragment, bundle);
        }
    }
}
