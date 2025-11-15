package com.example.primaryschoolmanagement.common.utils;

import com.example.primaryschoolmanagement.common.enums.ResultCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一响应结果类
 * 使用Jackson进行JSON序列化
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class R implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer code;
	private String msg;
	private Object dataset;
	
	public R(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	public R(ResultCode rs) {
		this.code = rs.getCode();
		this.msg = rs.getMsg();
	}
	
	public R(ResultCode rs, Object data) {
		this.code = rs.getCode();
		this.msg = rs.getMsg();
		this.dataset = data;
	}
	
	public static R ok() {
		return new R(ResultCode.SUCCESS);
	}
	
	public static R er() {
		return new R(ResultCode.ERROR);
	}
	
	public static R ok(Object data) {
		return new R(ResultCode.SUCCESS, data);
	}
	
	public static R unauthorzation() {
		return new R(ResultCode.UNAUTHORIZATION);
	}
	
	public static R checkError() {
		return new R(ResultCode.USERCHECK);
	}
	
	public static R ok(int code, String msg) {
		return new R(code, msg);
	}
	
	public static R er(int code, String msg) {
		return new R(code, msg);
	}
}
