package com.example.soscloud.Model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class User {
    private String email;
    private String name;
    private String surname;
    private UserType userType;
    private String studentNo;
    private String roomNo;
    private EmergencyType duty;

    public User(String email, String name, String surname, UserType userType, String studentNo, String roomNo, EmergencyType duty) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.userType = userType;
        this.studentNo = studentNo;
        this.roomNo = roomNo;
        this.duty = duty;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public EmergencyType getDuty() {
        return duty;
    }

    public void setDuty(EmergencyType duty) {
        this.duty = duty;
    }

    public boolean canHandleEmergencyType(EmergencyType emergencyType) {
        if (userType != UserType.SOS_PERSONEL || duty == null) {
            return false;
        }
        return duty == emergencyType;
    }

    public String getDepartmentName() {
        if (duty == null) {
            return "";
        }
        return duty.getFriendlyName();
    }
    
    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("email", email);
        data.put("name", name);
        data.put("surname", surname);
        data.put("type", userType.toString());
        if (UserType.STUDENT.equals(userType)) {
            data.put("studentNo", studentNo);
            data.put("roomNo", roomNo);
        } else if (UserType.SOS_PERSONEL.equals(userType)) {
            data.put("duty", duty != null ? duty.name() : null);
            data.put("dutyFriendlyName", duty != null ? duty.getFriendlyName() : null);
        }
        return data;
    }

    public static User fromMap(Map<String, Object> data) {
        String email = (String) data.get("email");
        String name = (String) data.get("name");
        String surname = (String) data.get("surname");
        UserType userType = UserType.valueOf((String) data.get("type"));
        String studentNo = (String) data.get("studentNo");
        String roomNo = (String) data.get("roomNo");
        EmergencyType duty = data.get("duty") != null ? EmergencyType.valueOf((String) data.get("duty")) : null;
        
        return new User(email, name, surname, userType, studentNo, roomNo, duty);
    }
}
