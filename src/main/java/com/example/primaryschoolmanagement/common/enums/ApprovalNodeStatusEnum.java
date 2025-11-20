package com.example.primaryschoolmanagement.common.enums;

import lombok.Getter;

@Getter
public enum ApprovalNodeStatusEnum {
    PENDING(1, "待审批"),
    APPROVED(2, "已通过"),
    REJECTED(3, "已拒绝");

    private final Integer code;
    private final String text;

    ApprovalNodeStatusEnum(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    public static String getTextByCode(Integer code) {
        for (ApprovalNodeStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status.getText();
            }
        }
        return "未知状态";
    }
}