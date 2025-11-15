package com.example.primaryschoolmanagement.common.utils;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 响应状态码枚举
 * 使用Jackson进行序列化
 */
@Getter
public enum ResultCode {
	SUCCESS(200, "操作成功"),
	ERROR(500, "操作失败"),
	USERCHECK(500, "用户名或密码错误"),
	UNAUTHORIZATION(403, "用户权限不足");
	private final int code;
	private final String msg;
	
	ResultCode(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
}
