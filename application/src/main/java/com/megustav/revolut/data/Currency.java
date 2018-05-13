package com.megustav.revolut.data;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Available currencies
 *
 * @author MeGustav
 * 13/05/2018 00:29
 */
public enum Currency {

    RUR("810"),
    RUB("643"),
    USD("840"),
    EUR("978");

    /** Currency numeric code */
    private final String numericCode;

    Currency(String numericCode) {
        this.numericCode = numericCode;
    }

    public String getNumericCode() {
        return numericCode;
    }

    /**
     * Get {@link Currency} by numeric code
     *
     * @param numericCode currency numeric code
     * @return {@link Currency}
     */
    public static Currency of(String numericCode) {
        return Stream.of(values())
                .filter(currency -> Objects.equals(numericCode, currency.getNumericCode()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown currency code: " + numericCode)
                );
    }
}
