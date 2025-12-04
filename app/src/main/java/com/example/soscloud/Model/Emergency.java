package com.example.soscloud.Model;

import com.google.firebase.firestore.DocumentSnapshot;
import java.util.HashMap;
import java.util.Map;

public class Emergency {
    private String id;
    private String studentNo;
    private EmergencyType emergencyType;
    private double longitude, latitude;
    private EmergencyStatus status;
    private String timestamp;

    public Emergency(String studentNo, EmergencyType emergencyType, double longitude, double latitude, EmergencyStatus status, String timestamp) {
        this.studentNo = studentNo;
        this.emergencyType = emergencyType;
        this.longitude = longitude;
        this.latitude = latitude;
        this.status = status;
        this.timestamp = timestamp;
    }

    public Emergency() {
    }

    public static Emergency fromDocument(DocumentSnapshot doc) {
        try {
            String studentNo = doc.getString("studentNo");
            String emergencyTypeStr = doc.getString("emergencyType");
            Double longitude = doc.getDouble("longitude");
            Double latitude = doc.getDouble("latitude");
            String statusStr = doc.getString("status");
            String timestamp = doc.getString("timestamp");

            if (studentNo == null || emergencyTypeStr == null ||
                    longitude == null || latitude == null || statusStr == null) {
                return null;
            }

            EmergencyType emergencyType = EmergencyType.valueOf(emergencyTypeStr);
            EmergencyStatus status = EmergencyStatus.valueOf(statusStr);

            Emergency emergency = new Emergency(studentNo, emergencyType,
                    longitude, latitude, status, timestamp);
            emergency.setId(doc.getId());
            return emergency;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("studentNo", studentNo);
        data.put("emergencyType", emergencyType != null ? emergencyType.name() : null);
        data.put("longitude", longitude);
        data.put("latitude", latitude);
        data.put("status", status != null ? status.name() : null);
        data.put("timestamp", timestamp);
        return data;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public EmergencyType getEmergencyType() {
        return emergencyType;
    }

    public void setEmergencyType(EmergencyType emergencyType) {
        this.emergencyType = emergencyType;
    }



    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


    public EmergencyStatus getStatus() {
        return status;
    }

    public void setStatus(EmergencyStatus status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setDateTime(String dateTime) {

    }
}
