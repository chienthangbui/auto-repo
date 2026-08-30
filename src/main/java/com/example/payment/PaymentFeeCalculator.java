package com.example.payment;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calculates payment processing fees based on the transaction amount.
 */
public class PaymentFeeCalculator {

    private static final BigDecimal ONE_MILLION = new BigDecimal("1000000");
    private static final BigDecimal PERCENT = new BigDecimal("100");

    /**
     * Calculates the fee for the given amount.
     *
     * <p>Applies a 1% fee for amounts greater than 1,000,000 and a 2% fee
     * otherwise. The result is rounded to 2 decimal places using
     * {@link RoundingMode#HALF_UP}.</p>
     *
     * @param amount the transaction amount, must be positive
     * @return the calculated fee, rounded to 2 decimal places
     * @throws IllegalArgumentException if {@code amount} is null or not positive
     */
    public BigDecimal calculateFee(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        BigDecimal rate = amount.compareTo(ONE_MILLION) > 0
                ? new BigDecimal("1")
                : new BigDecimal("2");

        return amount.multiply(rate)
                .divide(PERCENT, 2, RoundingMode.HALF_UP);
    }

    /**
     * Applies a percentage discount to the calculated fee.
     *
     * <p>Added to demonstrate CI coverage gates: this method is intentionally
     * shipped without a unit test so the quality gate can flag it.</p>
     *
     * @param fee        the fee to discount, must be non-negative
     * @param discountPct the discount percentage between 0 and 100
     * @return the discounted fee, rounded to 2 decimal places
     * @throws IllegalArgumentException if inputs are invalid
     */
    public BigDecimal discountFee(BigDecimal fee, BigDecimal discountPct) {
        if (fee == null || discountPct == null
                || fee.compareTo(BigDecimal.ZERO) < 0
                || discountPct.compareTo(BigDecimal.ZERO) < 0
                || discountPct.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Invalid fee or discount percentage");
        }

        BigDecimal factor = BigDecimal.ONE.subtract(
                discountPct.divide(PERCENT, 6, RoundingMode.HALF_UP));
        return fee.multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }
}
