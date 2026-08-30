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

    @Test
    void discountFee_shouldApplyDiscountPercentage() {
        BigDecimal discounted = calculator.discountFee(
                new BigDecimal("100.00"), new BigDecimal("10"));
        assertEquals(0, discounted.compareTo(new BigDecimal("90.00")));
    }

    @Test
    void discountFee_shouldReturnFeeUnchangedForZeroDiscount() {
        BigDecimal discounted = calculator.discountFee(
                new BigDecimal("250.00"), BigDecimal.ZERO);
        assertEquals(0, discounted.compareTo(new BigDecimal("250.00")));
    }

    @Test
    void discountFee_shouldReturnZeroForFullDiscount() {
        BigDecimal discounted = calculator.discountFee(
                new BigDecimal("100.00"), new BigDecimal("100"));
        assertEquals(0, discounted.compareTo(BigDecimal.ZERO.setScale(2)));
    }

    @Test
    void discountFee_shouldRejectInvalidInputs() {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.discountFee(null, new BigDecimal("10")));
        assertThrows(IllegalArgumentException.class,
                () -> calculator.discountFee(new BigDecimal("100"), null));
        assertThrows(IllegalArgumentException.class,
                () -> calculator.discountFee(new BigDecimal("-1"), new BigDecimal("10")));
        assertThrows(IllegalArgumentException.class,
                () -> calculator.discountFee(new BigDecimal("100"), new BigDecimal("-1")));
        assertThrows(IllegalArgumentException.class,
                () -> calculator.discountFee(new BigDecimal("100"), new BigDecimal("101")));
    }
}
