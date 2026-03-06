package com.oceanview.util;

import com.oceanview.model.Reservation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class BillCalculator {

    private static final BigDecimal VAT_RATE = new BigDecimal("0.10");

    public static class BillDetails {
        public long numberOfNights;
        public BigDecimal nightlyRate;
        public BigDecimal subTotal;
        public BigDecimal tax;
        public BigDecimal grandTotal;
    }

    public static BillDetails calculate(Reservation reservation) {
        if (reservation.getRoom() == null || reservation.getRoom().getNightlyRate() == null) {
            throw new IllegalArgumentException("Reservation must have an associated room with a nightly rate.");
        }

        LocalDate checkIn = reservation.getCheckInDate().toLocalDate();
        LocalDate checkOut = reservation.getCheckOutDate().toLocalDate();
        
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights <= 0) {
            nights = 1; // Safeguard
        }

        BillDetails details = new BillDetails();
        details.numberOfNights = nights;
        details.nightlyRate = reservation.getRoom().getNightlyRate();
        
        details.subTotal = details.nightlyRate.multiply(BigDecimal.valueOf(nights));
        details.tax = details.subTotal.multiply(VAT_RATE).setScale(2, RoundingMode.HALF_UP);
        details.grandTotal = details.subTotal.add(details.tax).setScale(2, RoundingMode.HALF_UP);

        return details;
    }
}
