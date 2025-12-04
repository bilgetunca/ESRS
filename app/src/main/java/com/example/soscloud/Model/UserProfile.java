package com.example.soscloud.Model;

import java.util.HashMap;
import java.util.Map;

public class UserProfile extends User {
    private String idNumber;
    private String phoneNumber;
    private String height;
    private String weight;
    private String birthDate;
    private String gender;
    private String bloodType;
    private String emergencyContact;
    private String medication;
    private String allergies;

    public UserProfile(String email, String name, String surname, UserType userType, 
                      String studentNo, String roomNo, EmergencyType duty) {
        super(email, name, surname, userType, studentNo, roomNo, duty);
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getMedication() {
        return medication;
    }

    public void setMedication(String medication) {
        this.medication = medication;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> data = super.toMap();

        if (idNumber != null) data.put("idNumber", idNumber);
        if (phoneNumber != null) data.put("phoneNumber", phoneNumber);
        if (height != null) data.put("height", height);
        if (weight != null) data.put("weight", weight);
        if (birthDate != null) data.put("birthDate", birthDate);
        if (gender != null) data.put("gender", gender);
        if (bloodType != null) data.put("bloodType", bloodType);
        if (emergencyContact != null) data.put("emergencyContact", emergencyContact);
        if (medication != null) data.put("medication", medication);
        if (allergies != null) data.put("allergies", allergies);
        
        return data;
    }

    public static UserProfile fromMap(Map<String, Object> data) {
        String email = (String) data.get("email");
        String name = (String) data.get("name");
        String surname = (String) data.get("surname");
        UserType userType = UserType.valueOf((String) data.get("type"));
        String studentNo = (String) data.get("studentNo");
        String roomNo = (String) data.get("roomNo");
        EmergencyType duty = null;
        if (data.containsKey("duty")) {
            try {
                duty = EmergencyType.valueOf((String) data.get("duty"));
            } catch (IllegalArgumentException e) {
            }
        }
        
        UserProfile profile = new UserProfile(email, name, surname, userType, studentNo, roomNo, duty);

        if (data.containsKey("idNumber")) profile.setIdNumber((String) data.get("idNumber"));
        if (data.containsKey("phoneNumber")) profile.setPhoneNumber((String) data.get("phoneNumber"));
        if (data.containsKey("height")) profile.setHeight((String) data.get("height"));
        if (data.containsKey("weight")) profile.setWeight((String) data.get("weight"));
        if (data.containsKey("birthDate")) profile.setBirthDate((String) data.get("birthDate"));
        if (data.containsKey("gender")) profile.setGender((String) data.get("gender"));
        if (data.containsKey("bloodType")) profile.setBloodType((String) data.get("bloodType"));
        if (data.containsKey("emergencyContact")) profile.setEmergencyContact((String) data.get("emergencyContact"));
        if (data.containsKey("medication")) profile.setMedication((String) data.get("medication"));
        if (data.containsKey("allergies")) profile.setAllergies((String) data.get("allergies"));
        
        return profile;
    }
} 