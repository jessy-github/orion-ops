package com.orion.ops.consts.app;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 发布序列类型
 *
 * @author Jiahang Li
 * @version 1.0.0
 * @since 2021/12/2 15:20
 */
@AllArgsConstructor
@Getter
public enum ReleaseSerialType {

    /**
     * 串行
     */
    SERIAL(10, "serial"),

    /**
     * 并行
     */
    PARALLEL(20, "parallel"),

    ;

    private final Integer type;

    private final String key;

    public static ReleaseSerialType of(Integer type) {
        if (type == null) {
            return PARALLEL;
        }
        for (ReleaseSerialType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return PARALLEL;
    }

    public static ReleaseSerialType of(String key) {
        if (key == null) {
            return PARALLEL;
        }
        for (ReleaseSerialType value : values()) {
            if (value.key.equalsIgnoreCase(key)) {
                return value;
            }
        }
        return PARALLEL;
    }

}