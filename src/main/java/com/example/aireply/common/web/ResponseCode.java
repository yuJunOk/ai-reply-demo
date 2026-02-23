package com.example.aireply.common.web;

import lombok.Getter;

/**
 * 通用响应码枚举
 * @author pengYuJun
 */
@Getter
public enum ResponseCode {

	/**
	 * 操作成功
	 */
	SUCCESS(0, "操作成功"),

	/**
	 * 系统异常
	 */
	ERROR(500, "系统内部错误"),

	/**
	 * 参数错误
	 */
	PARAMS_ERROR(400, "请求参数错误"),

	/**
	 * 未登录
	 */
	UNAUTHORIZED(401, "未登录"),

	/**
	 * 无权限
	 */
	FORBIDDEN(403, "无操作权限"),

	/**
	 * 接口不存在
	 */
	NOT_FOUND(404, "接口不存在");

	private final int code;
	private final String message;

	ResponseCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

}
