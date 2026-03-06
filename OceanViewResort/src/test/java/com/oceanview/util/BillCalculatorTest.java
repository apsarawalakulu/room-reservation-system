package com.oceanview.util;

import com.oceanview.model.Reservation;
import com.oceanview.model.Room;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BillCalculatorTest {

    @Test
    void testCalculateValidBill() {
        // Setup
        Room mockRoom = new Room();
        mockRoom.setNightlyRate(new BigDecimal("10000.00"));

        Reservation reservation = new Reservation();
        reservation.setRoom(mockRoom);
        reservation.setCheckInDate(Date.valueOf(LocalDate.of(2026, 1, 1)));
        reservation.setCheckOutDate(Date.valueOf(LocalDate.of(2026, 1, 3))); // 2 nights

        // Execute
        BillCalculator.BillDetails details = BillCalculator.calculate(reservation);

        // Assert
        assertEquals(2, details.numberOfNights);
        assertEquals(0, new BigDecimal("10000.00").compareTo(details.nightlyRate));
        assertEquals(0, new BigDecimal("20000.00").compareTo(details.subTotal));
        assertEquals(0, new BigDecimal("2000.00").compareTo(details.tax)); // 10%
        assertEquals(0, new BigDecimal("22000.00").compareTo(details.grandTotal));
    }

    @Test
    void testCalculateSingleNightReversion() {
        // Setup
        Room mockRoom = new Room();
        mockRoom.setNightlyRate(new BigDecimal("5000.00"));

        Reservation reservation = new Reservation();
        reservation.setRoom(mockRoom);
        // Same day check out to test fallback to 1 night
        reservation.setCheckInDate(Date.valueOf(LocalDate.of(2026, 1, 1)));
        reservation.setCheckOutDate(Date.valueOf(LocalDate.of(2026, 1, 1))); 

        // Execute
        BillCalculator.BillDetails details = BillCalculator.calculate(reservation);

        // Assert
        assertEquals(1, details.numberOfNights);
        assertEquals(0, new BigDecimal("5500.00").compareTo(details.grandTotal));
    }

    @Test
    void testCalculateMissingRoomThrowsException() {
        Reservation reservation = new Reservation();
        reservation.setCheckInDate(Date.valueOf(LocalDate.of(2026, 1, 1)));
        reservation.setCheckOutDate(Date.valueOf(LocalDate.of(2026, 1, 3)));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            BillCalculator.calculate(reservation);
        });

        assertTrue(thrown.getMessage().contains("must have an associated room"));
    }
}
