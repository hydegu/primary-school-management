package com.example.primaryschoolmanagement.common.enums;

import lombok.Getter;

@Getter
public enum TargetConfirmEnum {
    PENDING(0, "未确认"),
    CONFIRMED(1, "已确认"),
    REJECTED(2, "已拒绝");

    private final Integer code;
    private final String text;

    TargetConfirmEnum(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    public static String getTextByCode(Integer code) {
        for (TargetConfirmEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status.getText();
            }
        }
        return "未知状态";
    }
}