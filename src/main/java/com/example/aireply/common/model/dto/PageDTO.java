package com.example.aireply.common.model.dto;

import com.example.aireply.common.enums.SortOrderEnum;
import lombok.Data;

/**
 * 分页请求 DTO
 * @author pengYuJun
 */
@Data
public class PageDTO {

	/**
	 * 当前页码，从 1 开始
	 */
	private Integer current;

	/**
	 * 每页大小，默认 10
	 */
	private Integer pageSize;

	/**
	 * 排序字段（如：create_time）
	 */
	private String sortField;

	/**
	 * 排序顺序，默认升序
	 */
	private SortOrderEnum sortOrder = SortOrderEnum.ASC;

	public int getCurrent() {
		return current != null && current > 0 ? current : 1;
	}

	public int getPageSize() {
		if (pageSize == null || pageSize <= 0) {
			return 10;
		}
		// 可选：限制最大页大小，防攻击
		return Math.min(pageSize, 100);
	}

	public int offset() {
		return (getCurrent() - 1) * getPageSize();
	}
}
