package com.example.aireply.common.model.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author pengYuJun
 */
@Data
@Builder
public class IdBatchDTO<T> implements Serializable {
    /**
     * id
     */
    private List<T> idList;

    @Serial
    private static final long serialVersionUID = 1L;
}
