package com.besafx.app.entity.enums;

public enum CourseCondition {
    Opened("سارية"),
    Closed("مغلقة");
    private String name;

    CourseCondition(String name) {
        this.name = name;
    }

    public static CourseCondition findByName(String name) {
        for (CourseCondition v : values()) {
            if (v.getName().equals(name)) {
                return v;
            }
        }
        return null;
    }

    public static CourseCondition findByValue(String value) {
        for (CourseCondition v : values()) {
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
