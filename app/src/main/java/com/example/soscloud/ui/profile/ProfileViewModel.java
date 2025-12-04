package com.example.soscloud.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.soscloud.Model.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import android.util.Log;

public class ProfileViewModel extends ViewModel {
    private static final String TAG = "ProfileViewModel";

    private final MutableLiveData<UserProfile> userProfileLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> saveSuccessLiveData = new MutableLiveData<>(false);

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public ProfileViewModel() {
        loadUserProfile();
    }

    public LiveData<UserProfile> getUserProfile() {
        return userProfileLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessageLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }

    public LiveData<Boolean> getSaveSuccess() {
        return saveSuccessLiveData;
    }

    public void loadUserProfile() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            errorMessageLiveData.setValue("Oturum açık değil, lütfen tekrar giriş yapın");
            return;
        }

        isLoadingLiveData.setValue(true);
        db.collection("users").document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    isLoadingLiveData.setValue(false);
                    if (documentSnapshot.exists()) {
                        try {
                            UserProfile profile = UserProfile.fromMap(documentSnapshot.getData());
                            userProfileLiveData.setValue(profile);
                        } catch (Exception e) {
                            Log.e(TAG, "Profil verisi dönüştürme hatası", e);
                            errorMessageLiveData.setValue("Profil verisi dönüştürülürken hata: " + e.getMessage());
                        }
                    } else {
                        errorMessageLiveData.setValue("Kullanıcı profili bulunamadı");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Profil yükleme hatası", e);
                    isLoadingLiveData.setValue(false);
                    errorMessageLiveData.setValue("Profil yüklenirken hata: " + e.getMessage());
                });
    }

    public void saveUserProfile(UserProfile profile) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            errorMessageLiveData.setValue("Oturum açık değil, lütfen tekrar giriş yapın");
            return;
        }

        isLoadingLiveData.setValue(true);
        saveSuccessLiveData.setValue(false);
        
        String currentEmail = currentUser.getEmail();
        String newEmail = profile.getEmail();
        
        if (currentEmail != null && newEmail != null && !currentEmail.equals(newEmail)) {
            updateAuthenticationEmail(currentUser, newEmail, profile);
        } else {
            updateFirestoreProfile(profile);
        }
    }
    
    private void updateAuthenticationEmail(FirebaseUser user, String newEmail, UserProfile profile) {
        user.verifyBeforeUpdateEmail(newEmail)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Doğrulama emaili gönderildi: " + newEmail);
                errorMessageLiveData.setValue("Email güncelleme için doğrulama bağlantısı gönderildi. Lütfen yeni email adresinize gelen bağlantıya tıklayın. Diğer profil bilgileriniz güncellendi.");
                updateFirestoreProfile(profile);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Auth email güncelleme hatası", e);
                String errorMessage = e.getMessage();
                if (errorMessage != null) {
                    if (errorMessage.contains("requires recent authentication")) {
                        errorMessageLiveData.setValue("Email değişikliği için yeniden giriş yapmanız gerekiyor, ancak diğer profil bilgileriniz güncellendi.");
                    } else if (errorMessage.contains("email address is already in use")) {
                        errorMessageLiveData.setValue("Bu email adresi başka bir hesap tarafından kullanılıyor, ancak diğer profil bilgileriniz güncellendi.");
                    } else if (errorMessage.contains("email address is badly formatted")) {
                        errorMessageLiveData.setValue("Geçersiz email formatı. Email değiştirilemedi, ancak diğer profil bilgileriniz güncellendi.");
                    } else {
                        errorMessageLiveData.setValue("Email güncellenirken hata oluştu: " + errorMessage + ". Diğer profil bilgileriniz güncellendi.");
                    }
                } else {
                    errorMessageLiveData.setValue("Email güncellenirken bilinmeyen bir hata oluştu. Diğer profil bilgileriniz güncellendi.");
                }
                updateFirestoreProfile(profile);
            });
    }
    
    private void updateFirestoreProfile(UserProfile profile) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            errorMessageLiveData.setValue("Oturum açık değil, işlem iptal edildi");
            isLoadingLiveData.setValue(false);
            return;
        }

        db.collection("users").document(currentUser.getUid())
                .update(profile.toMap())
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "Firestore profil güncelleme başarılı");
                    isLoadingLiveData.setValue(false);
                    saveSuccessLiveData.setValue(true);
                    userProfileLiveData.setValue(profile);
                    
                    if (errorMessageLiveData.getValue() == null || errorMessageLiveData.getValue().isEmpty()) {
                        errorMessageLiveData.setValue("Profil başarıyla güncellendi");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firestore profil güncelleme hatası", e);
                    isLoadingLiveData.setValue(false);
                    errorMessageLiveData.setValue("Profil güncellenirken hata: " + e.getMessage());
                });
    }
}