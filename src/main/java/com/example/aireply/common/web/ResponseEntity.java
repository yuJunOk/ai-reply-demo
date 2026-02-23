package com.example.aireply.common.web;

import com.example.aireply.common.model.vo.PageVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 统一响应结果封装
 * @author pengYuJun
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ResponseEntity<T> implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	/** 状态码：0 表示成功，其他表示失败 */
	private int code;

	/** 响应数据 */
	private T data;

	/** 提示信息 */
	private String message;

	public ResponseEntity(ResponseCode code) {
		this(code.getCode(), null, code.getMessage());
	}

	public ResponseEntity(ResponseCode code, T data) {
		this(code.getCode(), data, code.getMessage());
	}

	public ResponseEntity(ResponseCode code, T data, String message) {
		this(code.getCode(), data, message);
	}

	// 静态工厂方法

	public static <T> ResponseEntity<T> success() {
		return new ResponseEntity<>(ResponseCode.SUCCESS);
	}

	public static <T> ResponseEntity<T> success(T data) {
		return new ResponseEntity<>(ResponseCode.SUCCESS, data);
	}

	public static <T> ResponseEntity<T> success(T data, String message) {
		return new ResponseEntity<>(ResponseCode.SUCCESS.getCode(), data, message);
	}

	public static <T> ResponseEntity<T> fail(ResponseCode code) {
		return new ResponseEntity<>(code);
	}

	public static <T> ResponseEntity<T> fail(ResponseCode code, String message) {
		return new ResponseEntity<>(code, null, message);
	}

	public static <T> ResponseEntity<PageVO<T>> page(List<T> records, long total) {
		PageVO<T> data = new PageVO<>(records, total);
		return new ResponseEntity<>(ResponseCode.SUCCESS, data);
	}

	/**
	 * 是否成功
	 */
	public boolean isSuccess() {
		return this.code == ResponseCode.SUCCESS.getCode();
	}
}
