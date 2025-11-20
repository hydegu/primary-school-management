package com.example.primaryschoolmanagement.common.enums;

import lombok.Getter;

@Getter
public enum BusinessTypeEnum {
    LEAVE(1, "请假"),
    COURSE_CHANGE(2, "调课"),
    COURSE_SWAP(3, "换课"),
    CLASS_TRANSFER(4, "调班");

    private final Integer code;
    private final String text;

    BusinessTypeEnum(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    public static String getTextByCode(Integer code) {
        for (BusinessTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type.getText();
            }
        }
        return "未知类型";
    }
}