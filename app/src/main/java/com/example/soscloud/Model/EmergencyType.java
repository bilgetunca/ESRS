package com.example.soscloud.Model;

import java.util.HashMap;
import java.util.Map;

public enum EmergencyType {
    MEDICAL("Revir"),
    FIRE("Yangın"),
    SECURITY("Güvenlik"),
    DANGER("Polis");

    private final String friendlyName;

    EmergencyType(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getFriendlyName() {
        return friendlyName;
    }
    public static EmergencyType fromFriendlyName(String name) {
        for (EmergencyType type : values()) {
            if (type.friendlyName.equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Bilinmeyen acil durum türü: " + name);
    }
    public static Map<String, String> getFriendlyNames() {
        Map<String, String> names = new HashMap<>();
        for (EmergencyType type : values()) {
            names.put(type.name(), type.getFriendlyName());
        }
        return names;
    }
}
