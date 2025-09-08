package com.eams.Entity.course.Enum;

public enum RegistrationStatus {
    PENDING("pending"),
    APPROVED("approved"),
    REJECTED("rejected"),
    CANCELLED("cancelled");
    
    private final String value;
    
    RegistrationStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static RegistrationStatus fromValue(String value) {
        for (RegistrationStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }
}