package bw.ac.kgetsibank.model;

import java.math.BigDecimal;

public interface InterestBearing {

    BigDecimal calculateInterest();

    void applyMonthlyInterest();
}