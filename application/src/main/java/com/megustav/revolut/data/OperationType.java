package com.megustav.revolut.data;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Account operation type
 *
 * @author MeGustav
 * 15/05/2018 22:50
 */
public enum OperationType {

    DEPOSIT(1),
    WITHDRAWAL(-1);

    /** Operation code */
    private final int code;

    OperationType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    /**
     * Get {@link OperationType} by numeric code
     *
     * @param code code
     * @return {@link OperationType}
     */
    public static OperationType of(int code) {
        return Stream.of(values())
                .filter(type -> Objects.equals(code, type.getCode()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown operation type code: " + code)
                );
    }
}
