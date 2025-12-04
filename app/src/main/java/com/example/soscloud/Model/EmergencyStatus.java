package com.example.soscloud.Model;

public enum EmergencyStatus {
    SENT("Gönderildi"),
    READ("Okundu"),
    COMPLETED("Tamamlandı");

    private final String friendlyName;
    EmergencyStatus(String friendlyName) {
        this.friendlyName = friendlyName;
    }
    public String getFriendlyName() {
        return friendlyName;
    }
}
