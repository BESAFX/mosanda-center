package com.besafx.app.entity.enums;

public enum PaymentType {
    Cash("نقدي"),
    Premium("أقساط");
    private String name;

    PaymentType(String name) {
        this.name = name;
    }

    public static PaymentType findByName(String name) {
        for (PaymentType v : values()) {
            if (v.getName().equals(name)) {
                return v;
            }
        }
        return null;
    }

    public static PaymentType findByValue(String value) {
        for (PaymentType v : values()) {
            if (v.name().equals(value)) {
                return v;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }
}
