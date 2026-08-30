package com.example.payment;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentFeeCalculatorTest {

    private final PaymentFeeCalculator calculator = new PaymentFeeCalculator();

    @Test
    void calculateFee_shouldRejectNonPositiveAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateFee(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateFee(new BigDecimal("-100")));
    }

    @Test
    void calculateFee_shouldApplyOnePercentForAmountAboveOneMillion() {
        BigDecimal fee = calculator.calculateFee(new BigDecimal("2000000"));
        assertEquals(0, fee.compareTo(new BigDecimal("20000.00")));
    }

    @Test
    void calculateFee_shouldApplyTwoPercentForAmountUpToOneMillion() {
        BigDecimal fee = calculator.calculateFee(new BigDecimal("1000000"));
        assertEquals(0, fee.compareTo(new BigDecimal("20000.00")));
    }
}
