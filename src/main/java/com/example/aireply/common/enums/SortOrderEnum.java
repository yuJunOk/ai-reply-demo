package com.example.aireply.common.enums;

import lombok.Getter;

/**
 * 排序顺序枚举
 * @author pengYuJun
 */
@Getter
public enum SortOrderEnum {
	/**
	 *
	 */
    ASC("ASC", "升序"),
    DESC("DESC", "降序");

    private final String value;
    private final String description;

    SortOrderEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

	/**
     * 根据字符串值解析为枚举（用于反序列化）
     */
    public static SortOrderEnum of(String value) {
        if (value == null) {
			// 默认升序
			return ASC;
		}
        for (SortOrderEnum order : values()) {
            if (order.value.equalsIgnoreCase(value)) {
                return order;
            }
        }
		// 默认值
        return ASC;
    }

    @Override
    public String toString() {
        return value;
    }
}
