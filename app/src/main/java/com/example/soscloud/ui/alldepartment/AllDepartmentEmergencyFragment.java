package com.example.soscloud.ui.alldepartment;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soscloud.Model.Emergency;
import com.example.soscloud.R;
import com.example.soscloud.adapters.EmergencyAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AllDepartmentEmergencyFragment extends Fragment implements EmergencyAdapter.OnEmergencyClickListener {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private EmergencyAdapter adapter;
    private Spinner statusFilterSpinner, dateFilterSpinner;
    private String selectedStatusFilter = "Tümü";
    private String selectedDateFilter = "Tümü";
    private final List<Emergency> allEmergencies = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_all_department_emergency, container, false);

        recyclerView = root.findViewById(R.id.recyclerViewAllDepartmentEmergency);
        statusFilterSpinner = root.findViewById(R.id.statusFilterSpinner);
        dateFilterSpinner = root.findViewById(R.id.dateFilterSpinner);

        setupRecyclerView();
        setupSpinners();
        loadDepartmentNotifications();

        return root;
    }

    private void setupRecyclerView() {
        adapter = new EmergencyAdapter(requireContext(), new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
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

    private void loadDepartmentNotifications() {
        db = FirebaseFirestore.getInstance();
        db.collection("emergencies")
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
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading notifications: " + e.getMessage()));
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

        adapter.updateData(filtered);
    }

    private boolean isSameDay(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    @Override
    public void onEmergencyClick(Emergency emergency, int position) {
        // Tıklama işlemleri burada yapılabilir.
    }
}
