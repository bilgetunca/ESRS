package com.example.soscloud.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.soscloud.Model.EmergencyType;
import com.example.soscloud.Model.UserProfile;
import com.example.soscloud.Model.UserType;
import com.example.soscloud.databinding.FragmentProfileBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private static final String TAG = "ProfileFragment";
    private boolean isSosPersonnel = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        setupObservers();
        binding.saveProfileButton.setOnClickListener(v -> saveUserProfile());
        
        return binding.getRoot();
    }
    
    private void setupObservers() {
        viewModel.getUserProfile().observe(getViewLifecycleOwner(), userProfile -> {
            populateFields(userProfile);
            
            // Check if user is SOS personnel
            isSosPersonnel = userProfile != null && 
                    UserType.SOS_PERSONEL.equals(userProfile.getUserType());
            
            // Configure UI based on user type
            configureUIForUserType();
        });
        
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (!TextUtils.isEmpty(errorMessage)) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.saveProfileButton.setEnabled(!isLoading);
        });
        
        viewModel.getSaveSuccess().observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                Toast.makeText(getContext(), "Profil başarıyla güncellendi", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void configureUIForUserType() {
        if (isSosPersonnel) {
            // For SOS personnel show only department dropdown, name, surname, and ID number
            binding.nameInputLayout.setVisibility(View.VISIBLE);
            binding.surnameInputLayout.setVisibility(View.VISIBLE);
            binding.idNumberInputLayout.setVisibility(View.VISIBLE);
            binding.departmentInputLayout.setVisibility(View.VISIBLE);
            
            // Hide all other fields
            binding.emailInputLayout.setVisibility(View.GONE);
            binding.formTitle2.setVisibility(View.GONE);
            binding.phoneNumberInputLayout.setVisibility(View.GONE);
            binding.heightInputLayout.setVisibility(View.GONE);
            binding.weightInputLayout.setVisibility(View.GONE);
            binding.birthDateInputLayout.setVisibility(View.GONE);
            binding.genderInputLayout.setVisibility(View.GONE);
            binding.bloodTypeInputLayout.setVisibility(View.GONE);
            binding.roomNumberInputLayout.setVisibility(View.GONE);
            binding.emergencyContactInputLayout.setVisibility(View.GONE);
            binding.medicationInputLayout.setVisibility(View.GONE);
            binding.allergyInputLayout.setVisibility(View.GONE);
            
            // Setup department dropdown
            setupDepartmentDropdown();
        } else {
            // For other users, show all fields
            binding.nameInputLayout.setVisibility(View.VISIBLE);
            binding.surnameInputLayout.setVisibility(View.VISIBLE);
            binding.emailInputLayout.setVisibility(View.VISIBLE);
            binding.idNumberInputLayout.setVisibility(View.VISIBLE);
            binding.formTitle2.setVisibility(View.VISIBLE);
            binding.phoneNumberInputLayout.setVisibility(View.VISIBLE);
            binding.heightInputLayout.setVisibility(View.VISIBLE);
            binding.weightInputLayout.setVisibility(View.VISIBLE);
            binding.birthDateInputLayout.setVisibility(View.VISIBLE);
            binding.genderInputLayout.setVisibility(View.VISIBLE);
            binding.bloodTypeInputLayout.setVisibility(View.VISIBLE);
            binding.roomNumberInputLayout.setVisibility(View.VISIBLE);
            binding.emergencyContactInputLayout.setVisibility(View.VISIBLE);
            binding.medicationInputLayout.setVisibility(View.VISIBLE);
            binding.allergyInputLayout.setVisibility(View.VISIBLE);
            
            // Hide department dropdown for non-SOS personnel
            binding.departmentInputLayout.setVisibility(View.GONE);
        }
    }
    
    private void setupDepartmentDropdown() {
        // Get all emergency types for departments
        Map<String, String> departments = EmergencyType.getFriendlyNames();
        List<String> departmentNames = new ArrayList<>(departments.values());
        
        // Create adapter for spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                departmentNames
        );
        
        binding.departmentDropdown.setAdapter(adapter);
        
        // Set selected department based on user's duty
        UserProfile profile = viewModel.getUserProfile().getValue();
        if (profile != null && profile.getDuty() != null) {
            String departmentName = profile.getDuty().getFriendlyName();
            int position = departmentNames.indexOf(departmentName);
            if (position >= 0) {
                binding.departmentDropdown.setText(departmentName, false);
            }
        }
    }
    
    private void populateFields(UserProfile profile) {
        if (profile != null) {
            setTextIfNotNull(binding.nameInput, profile.getName());
            setTextIfNotNull(binding.surnameInput, profile.getSurname());
            setTextIfNotNull(binding.emailInput, profile.getEmail());
            setTextIfNotNull(binding.idNumberInput, profile.getIdNumber());
            setTextIfNotNull(binding.phoneNumberInput, profile.getPhoneNumber());
            setTextIfNotNull(binding.heightNumberInput, profile.getHeight());
            setTextIfNotNull(binding.weightNumberInput, profile.getWeight());
            setTextIfNotNull(binding.birthDateInput, profile.getBirthDate());
            setTextIfNotNull(binding.genderInput, profile.getGender());
            setTextIfNotNull(binding.bloodTypeInput, profile.getBloodType());
            setTextIfNotNull(binding.roomNumberInput, profile.getRoomNo());
            setTextIfNotNull(binding.emergencyContactInput, profile.getEmergencyContact());
            setTextIfNotNull(binding.medicationInput, profile.getMedication());
            setTextIfNotNull(binding.allergyInput, profile.getAllergies());
        }
    }
    
    private void setTextIfNotNull(TextInputEditText input, String text) {
        if (text != null) {
            input.setText(text);
        }
    }
    
    private void saveUserProfile() {
        String name = getTextFromInput(binding.nameInput);
        String surname = getTextFromInput(binding.surnameInput);
        String email = getTextFromInput(binding.emailInput);
        String idNumber = getTextFromInput(binding.idNumberInput);
        
        UserProfile currentProfile = viewModel.getUserProfile().getValue();
        if (currentProfile == null) {
            Toast.makeText(getContext(), "Profil verisi bulunamadı", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(surname)) {
            Toast.makeText(getContext(), "Ad ve soyad zorunludur", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Prepare updated profile
        UserProfile updatedProfile;
        
        if (isSosPersonnel) {
            // For SOS personnel, only update name, surname, ID number, and department
            updatedProfile = new UserProfile(
                    currentProfile.getEmail(),
                    name,
                    surname,
                    currentProfile.getUserType(),
                    currentProfile.getStudentNo(),
                    currentProfile.getRoomNo(),
                    currentProfile.getDuty()
            );
            
            // Update department if changed
            String selectedDepartment = binding.departmentDropdown.getText().toString();
            if (!TextUtils.isEmpty(selectedDepartment)) {
                try {
                    EmergencyType duty = EmergencyType.fromFriendlyName(selectedDepartment);
                    updatedProfile.setDuty(duty);
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Invalid department: " + selectedDepartment);
                }
            }
            
            // Set only the ID number
            updatedProfile.setIdNumber(idNumber);
            
            // Copy existing values for other fields
            updatedProfile.setPhoneNumber(currentProfile.getPhoneNumber());
            updatedProfile.setHeight(currentProfile.getHeight());
            updatedProfile.setWeight(currentProfile.getWeight());
            updatedProfile.setBirthDate(currentProfile.getBirthDate());
            updatedProfile.setGender(currentProfile.getGender());
            updatedProfile.setBloodType(currentProfile.getBloodType());
            updatedProfile.setEmergencyContact(currentProfile.getEmergencyContact());
            updatedProfile.setMedication(currentProfile.getMedication());
            updatedProfile.setAllergies(currentProfile.getAllergies());
        } else {
            // For other users, allow updating all fields
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getContext(), "Email adresi zorunludur", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Check if email is changing
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null && currentUser.getEmail() != null) {
                String currentEmail = currentUser.getEmail();
                if (!currentEmail.equals(email)) {
                    Toast.makeText(getContext(), "Email değişikliği algılandı. Hem giriş email'iniz hem de profil bilgileriniz güncellenecek.", Toast.LENGTH_LONG).show();
                }
            }
            
            String phoneNumber = getTextFromInput(binding.phoneNumberInput);
            String height = getTextFromInput(binding.heightNumberInput);
            String weight = getTextFromInput(binding.weightNumberInput);
            String birthDate = getTextFromInput(binding.birthDateInput);
            String gender = getTextFromInput(binding.genderInput);
            String bloodType = getTextFromInput(binding.bloodTypeInput);
            String roomNo = getTextFromInput(binding.roomNumberInput);
            String emergencyContact = getTextFromInput(binding.emergencyContactInput);
            String medication = getTextFromInput(binding.medicationInput);
            String allergies = getTextFromInput(binding.allergyInput);
            
            updatedProfile = new UserProfile(
                    email,
                    name, 
                    surname, 
                    currentProfile.getUserType(), 
                    currentProfile.getStudentNo(), 
                    roomNo, 
                    currentProfile.getDuty()
            );

            updatedProfile.setIdNumber(idNumber);
            updatedProfile.setPhoneNumber(phoneNumber);
            updatedProfile.setHeight(height);
            updatedProfile.setWeight(weight);
            updatedProfile.setBirthDate(birthDate);
            updatedProfile.setGender(gender);
            updatedProfile.setBloodType(bloodType);
            updatedProfile.setEmergencyContact(emergencyContact);
            updatedProfile.setMedication(medication);
            updatedProfile.setAllergies(allergies);
        }

        viewModel.saveUserProfile(updatedProfile);
    }
    
    private String getTextFromInput(TextInputEditText input) {
        if (input != null && input.getText() != null) {
            return input.getText().toString().trim();
        }
        return "";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
