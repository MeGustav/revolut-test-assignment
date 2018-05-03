package com.megustav.revolut.data;

/**
 * Basic operation response
 *
 * @author MeGustav
 * 04/05/2018 00:17
 */
public class OperationResponse {
    
    /** Response code */
    private final int code;
    /** Response description */
    private final String description;

    public OperationResponse(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
