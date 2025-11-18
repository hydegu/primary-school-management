package com.example.primaryschoolmanagement.common.enums;

import lombok.Getter;

@Getter
public enum LeaveTypeEnum {
    SICK_LEAVE(1, "病假"),
    PERSONAL_LEAVE(2, "事假"),
    OTHER(3, "其他");

    private final Integer code;
    private final String text;

    LeaveTypeEnum(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    public static String getTextByCode(Integer code) {
        for (LeaveTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type.getText();
            }
        }
        return "未知类型";
    }
}