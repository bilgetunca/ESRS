package com.example.soscloud.ui.emergencylist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.soscloud.Model.Emergency;
import com.example.soscloud.Model.EmergencyStatus;
import com.example.soscloud.Model.EmergencyType;
import com.example.soscloud.Model.UserType;
import com.example.soscloud.R;
import com.example.soscloud.adapters.EmergencyAdapter;
import com.example.soscloud.databinding.FragmentEmergencyListBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class EmergencyListFragment extends Fragment implements EmergencyAdapter.OnEmergencyClickListener {

    private static final String TAG = "EmergencyListFragment";
    private FragmentEmergencyListBinding binding;
    private EmergencyAdapter adapter;
    private final List<Emergency> emergencyList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String currentUserType;
    private String currentUserId;
    private String currentUserDuty;
    private String currentUserStudentNo;

    private String selectedStatusFilter = "Tümü";
    private String selectedDateFilter = "Tümü";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEmergencyListBinding.inflate(inflater, container, false);

        setupRecyclerView();
        setupSpinners();
        binding.buttonNewNotification.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(R.id.action_emergencyListFragment_to_makeNotificationFragment));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            handleUserNotLoggedIn();
            return;
        }

        currentUserId = currentUser.getUid();

        binding.swipeRefreshLayout.setOnRefreshListener(this::loadEmergencies);
        binding.progressBar.setVisibility(View.VISIBLE);

        fetchCurrentUserDetails();
    }

    private void setupRecyclerView() {
        adapter = new EmergencyAdapter(requireContext(), emergencyList, this);
        binding.emergencyRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.emergencyRecyclerView.setAdapter(adapter);
        binding.emergencyRecyclerView.setHasFixedSize(true);
    }

    private void setupSpinners() {
        List<String> statusOptions = Arrays.asList("Tümü", "Gönderildi", "Okundu", "Tamamlandı");
        List<String> dateOptions = Arrays.asList("Tümü", "Bugün", "Dün", "Son 7 Gün");

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, statusOptions);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.statusFilterSpinner.setAdapter(statusAdapter);

        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, dateOptions);
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.dateFilterSpinner.setAdapter(dateAdapter);

        binding.statusFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStatusFilter = statusOptions.get(position);
                filterAndDisplayEmergencies();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.dateFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        Calendar calendar = Calendar.getInstance();

        for (Emergency emergency : emergencyList) {
            if (emergency.getStatus() == null || emergency.getTimestamp() == null) continue;
            boolean matchesStatus = selectedStatusFilter.equals("Tümü") || emergency.getStatus().getFriendlyName().equals(selectedStatusFilter);

            boolean matchesDate = true;
            if (!selectedDateFilter.equals("Tümü")) {
                long timestampMillis = Long.parseLong(emergency.getTimestamp());
                Calendar emergencyCal = Calendar.getInstance();
                emergencyCal.setTimeInMillis(timestampMillis);

                switch (selectedDateFilter) {
                    case "Bugün":
                        matchesDate = isSameDay(calendar, emergencyCal);
                        break;
                    case "Dün":
                        calendar.add(Calendar.DATE, -1);
                        matchesDate = isSameDay(calendar, emergencyCal);
                        calendar.add(Calendar.DATE, 1);
                        break;
                    case "Son 7 Gün":
                        calendar.add(Calendar.DATE, -7);
                        matchesDate = emergencyCal.after(calendar);
                        calendar.add(Calendar.DATE, 7);
                        break;
                }
            }

            if (matchesStatus && matchesDate) {
                filtered.add(emergency);
            }
        }

        adapter.updateData(filtered);
        updateEmptyViewVisibility();
    }

    private boolean isSameDay(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    private void handleUserNotLoggedIn() {
        Log.w(TAG, "Kullanıcı oturum açmamış.");
        Toast.makeText(getContext(), R.string.login_required, Toast.LENGTH_SHORT).show();
        binding.progressBar.setVisibility(View.GONE);
        binding.emptyView.setVisibility(View.VISIBLE);
    }

    private void fetchCurrentUserDetails() {
        db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(this::handleUserDetailsFetchSuccess)
                .addOnFailureListener(this::handleUserDetailsFetchFailure);
    }

    private void handleUserDetailsFetchSuccess(DocumentSnapshot documentSnapshot) {
        if (documentSnapshot.exists()) {
            currentUserType = documentSnapshot.getString("type");
            currentUserDuty = documentSnapshot.getString("duty");
            currentUserStudentNo = documentSnapshot.getString("studentNo");
            loadEmergencies();
        } else {
            handleUserDetailsNotFound();
        }
    }

    private void handleUserDetailsNotFound() {
        Log.w(TAG, "Kullanıcı bilgileri bulunamadı.");
        binding.progressBar.setVisibility(View.GONE);
        binding.emptyView.setVisibility(View.VISIBLE);
        Toast.makeText(getContext(), R.string.user_info_not_found, Toast.LENGTH_SHORT).show();
    }

    private void handleUserDetailsFetchFailure(Exception e) {
        Log.e(TAG, "Kullanıcı bilgileri alınırken hata: ", e);
        binding.progressBar.setVisibility(View.GONE);
        binding.emptyView.setVisibility(View.VISIBLE);
        Toast.makeText(getContext(), getString(R.string.error) + e.getMessage(), Toast.LENGTH_SHORT).show();
    }


    private void loadEmergencies() {
        if (!isAdded() || getContext() == null) {
            Log.d(TAG, "Fragment eklenmemiş veya context null, acil durumlar yüklenmiyor.");
            binding.swipeRefreshLayout.setRefreshing(false);
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.emptyView.setVisibility(View.GONE);

        if ((UserType.SOS_PERSONEL.toString().equals(currentUserType) && currentUserDuty == null) ||
                (!UserType.SOS_PERSONEL.toString().equals(currentUserType) && currentUserStudentNo == null)) {
            handleMissingUserDetailsForEmergencyLoad();
            return;
        }

        Query query = buildEmergencyQuery();
        if (query != null) {
            fetchEmergenciesFromFirestore(query);
        }
    }

    private void handleMissingUserDetailsForEmergencyLoad() {
        Log.w(TAG, "Acil durumları yüklemek için gerekli kullanıcı bilgileri eksik.");
        binding.progressBar.setVisibility(View.GONE);
        binding.emptyView.setVisibility(View.VISIBLE);
        binding.swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getContext(), R.string.missing_user_details_for_emergency, Toast.LENGTH_SHORT).show();
    }

    private Query buildEmergencyQuery() {
        try {
            if (UserType.SOS_PERSONEL.toString().equals(currentUserType)) {
                return db.collection("emergencies")
                        .whereEqualTo("emergencyTypeName", currentUserDuty)
                        .orderBy("timestamp", Query.Direction.DESCENDING);
            } else {
                return db.collection("emergencies")
                        .whereEqualTo("studentNo", currentUserStudentNo);
            }
        } catch (Exception e) {
            Log.e(TAG, "Sorgu oluşturulurken hata: ", e);
            binding.progressBar.setVisibility(View.GONE);
            binding.swipeRefreshLayout.setRefreshing(false);
            binding.emptyView.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), getString(R.string.error_creating_query) + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void fetchEmergenciesFromFirestore(Query query) {
        query.get()
                .addOnSuccessListener(this::handleEmergencyFetchSuccess)
                .addOnFailureListener(this::handleEmergencyFetchFailure);
    }

    private void handleEmergencyFetchSuccess(com.google.firebase.firestore.QuerySnapshot queryDocumentSnapshots) {
        if (!isAdded() || getContext() == null) return;

        binding.progressBar.setVisibility(View.GONE);
        binding.swipeRefreshLayout.setRefreshing(false);

        emergencyList.clear();

        if (queryDocumentSnapshots.isEmpty()) {
            binding.emptyView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
            return;
        }
        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
            Emergency emergency = convertDocumentToEmergency(documentSnapshot);
            if (emergency != null) {
                emergencyList.add(emergency);
            }
        }
        
        if (!UserType.SOS_PERSONEL.toString().equals(currentUserType)) {
            emergencyList.sort((e1, e2) -> {
                try {
                    return Long.parseLong(e2.getTimestamp()) - Long.parseLong(e1.getTimestamp()) > 0 ? 1 : -1;
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Timestamp sıralama hatası", e);
                    return 0;
                }
            });
        }

        filterAndDisplayEmergencies();
    }

    private void handleEmergencyFetchFailure(Exception e) {
        if (!isAdded() || getContext() == null) return;

        Log.e(TAG, "Veriler yüklenirken hata: ", e);
        binding.progressBar.setVisibility(View.GONE);
        binding.swipeRefreshLayout.setRefreshing(false);
        binding.emptyView.setVisibility(View.VISIBLE);
        
        // Eğer hata indeks eksikliğinden kaynaklanıyorsa, kullanıcıya özel bir mesaj gösterelim
        String errorMessage = e.getMessage();
        if (errorMessage != null && errorMessage.contains("FAILED_PRECONDITION") && errorMessage.contains("index")) {
            // URL'yi hata mesajından çıkaralım
            int indexStart = errorMessage.indexOf("https://");
            if (indexStart != -1) {
                String indexUrl = errorMessage.substring(indexStart).trim();
                // Kullanıcıya gösterilecek mesaj
                Toast.makeText(getContext(), 
                        "Veritabanı indeksi gerekiyor. Lütfen yöneticinize bu hatayı bildirin.",
                        Toast.LENGTH_LONG).show();
                
                // Üstteki URL'yi konsola logla, böylece geliştirici görebilir
                Log.w(TAG, "Gereken indeks URL: " + indexUrl);
                
                // Opsiyonel: Kullanıcıya URL'yi açma seçeneği sunabilirsiniz (sadece geliştirme aşamasında)
                // showCreateIndexDialog(indexUrl);
            } else {
                Toast.makeText(getContext(), getString(R.string.error_loading_data) + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), getString(R.string.error_loading_data) + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    /* 
    // Sadece geliştirme aşamasında kullanılabilecek bir dialog
    private void showCreateIndexDialog(String indexUrl) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Indeks Gerekiyor")
                .setMessage("Bu sorgu için bir Firebase indeksi gerekiyor. Oluşturmak ister misiniz?")
                .setPositiveButton("Evet", (dialog, which) -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(indexUrl));
                    startActivity(browserIntent);
                })
                .setNegativeButton("Hayır", null)
                .show();
    }
    */

    private void updateEmptyViewVisibility() {
        binding.emptyView.setVisibility(emergencyList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private Emergency convertDocumentToEmergency(DocumentSnapshot documentSnapshot) {
        try {
            String studentNo = documentSnapshot.getString("studentNo");
            String emergencyTypeStr = documentSnapshot.getString("emergencyType");
            String emergencyTypeName = documentSnapshot.getString("emergencyTypeName");
            Double longitude = documentSnapshot.getDouble("longitude");
            Double latitude = documentSnapshot.getDouble("latitude");
            String statusStr = documentSnapshot.getString("status");
            String timestamp = documentSnapshot.getString("timestamp");

            EmergencyType emergencyType = parseEmergencyType(emergencyTypeStr);
            EmergencyStatus status = parseEmergencyStatus(statusStr);

            if (studentNo == null || emergencyType == null ||
                    longitude == null || latitude == null || status == null || timestamp == null) {
                Log.w(TAG, "Eksik veya hatalı acil durum verisi: " + documentSnapshot.getId());
                return null;
            }

            Emergency emergency = new Emergency(studentNo, emergencyType, longitude, latitude, status, timestamp);
            emergency.setId(documentSnapshot.getId());
            return emergency;
        } catch (Exception e) {
            Log.e(TAG, "Belge acil duruma dönüştürülürken hata: " + documentSnapshot.getId(), e);
            return null;
        }
    }

    private EmergencyType parseEmergencyType(String emergencyTypeStr) {
        if (emergencyTypeStr != null) {
            try {
                return EmergencyType.valueOf(emergencyTypeStr);
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "Bilinmeyen acil durum türü: " + emergencyTypeStr + ". Varsayılan MEDICAL atanıyor.");
                return EmergencyType.MEDICAL;
            }
        }
        return null;
    }

    private EmergencyStatus parseEmergencyStatus(String statusStr) {
        if (statusStr != null) {
            try {
                return EmergencyStatus.valueOf(statusStr);
            } catch (IllegalArgumentException e) {
                Log.w(TAG, "Bilinmeyen acil durum durumu: " + statusStr + ". Varsayılan SENT atanıyor.");
                return EmergencyStatus.SENT;
            }
        }
        return null;
    }

    @Override
    public void onEmergencyClick(Emergency emergency, int position) {
        if (emergency != null) {
            if (UserType.SOS_PERSONEL.toString().equals(currentUserType) &&
                    emergency.getStatus() == EmergencyStatus.SENT) {
                updateEmergencyStatus(emergency.getId(), EmergencyStatus.READ);
            }
            navigateToEmergencyDetail(emergency.getId());
        }
    }

    private void navigateToEmergencyDetail(String emergencyId) {
        Bundle bundle = new Bundle();
        bundle.putString("emergencyId", emergencyId);
        
        try {
            // Farklı bir yöntemle Navigation ID'yi tespit etmeye çalışalım
            int currentDestinationId = Navigation.findNavController(binding.getRoot()).getCurrentDestination().getId();
            
            // Hangi action'ı kullanacağımızı tespit edelim
            int actionId;
            if (currentDestinationId == R.id.nav_notification) {
                // Bildirim sayfasında isek bu action ID'yi kullan
                actionId = R.id.action_nav_notification_to_emergencyDetailFragment;
            } else {
                // Acil durum listesi sayfasında isek bu action ID'yi kullan
                actionId = R.id.action_emergencyListFragment_to_emergencyDetailFragment;
            }
            
            // Navigation işlemini gerçekleştir
            Navigation.findNavController(binding.getRoot())
                    .navigate(actionId, bundle);
                    
        } catch (Exception e) {
            Log.e(TAG, "Detay sayfasına yönlendirme hatası: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Detay sayfasına yönlendirilemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateEmergencyStatus(String emergencyId, EmergencyStatus newStatus) {
        db.collection("emergencies").document(emergencyId)
                .update("status", newStatus.name())
                .addOnSuccessListener(aVoid -> {
                    for (int i = 0; i < emergencyList.size(); i++) {
                        if (emergencyId.equals(emergencyList.get(i).getId())) {
                            emergencyList.get(i).setStatus(newStatus);
                            adapter.notifyItemChanged(i);
                            break;
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), getString(R.string.error_updating_status) + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}