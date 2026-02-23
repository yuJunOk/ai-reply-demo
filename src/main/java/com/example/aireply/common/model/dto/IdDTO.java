package com.example.aireply.common.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author pengYuJun
 */
@Data
@Builder
public class IdDTO<T> {
    /**
     * id
     */
    private T id;
}
