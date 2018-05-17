package com.megustav.revolut.data;

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
}
