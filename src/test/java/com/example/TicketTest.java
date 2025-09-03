package com.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TicketTest {

    @Test
    void testGetDepartureDateTime() {
        Ticket ticket = new Ticket();
        ticket.setDeparture_date("12.05.18");
        ticket.setDeparture_time("16:20");

        LocalDateTime expected = LocalDateTime.of(2018, 5, 12, 16, 20, 0);
        assertEquals(expected, ticket.getDepartureDateTime());
    }

    @Test
    void testGetArrivalDateTime() {
        Ticket ticket = new Ticket();
        ticket.setArrival_date("12.05.18");
        ticket.setArrival_time("22:10");

        LocalDateTime expected = LocalDateTime.of(2018, 5, 12, 22, 10, 0);
        assertEquals(expected, ticket.getArrivalDateTime());
    }

    @ParameterizedTest
    @CsvSource({
            "12.05.18, 16:20, 12.05.18, 22:10, 350",    // 5 часов 50 минут
            "12.05.18, 23:00, 13.05.18, 01:30, 150",    // переход через день
            "12.05.18, 9:40, 12.05.18, 19:25, 585"      // 9 часов 45 минут
    })
    void testGetFlightDurationMinutes(String depDate, String depTime,
                                      String arrDate, String arrTime, long expectedMinutes) {
        Ticket ticket = new Ticket();
        ticket.setDeparture_date(depDate);
        ticket.setDeparture_time(depTime);
        ticket.setArrival_date(arrDate);
        ticket.setArrival_time(arrTime);

        assertEquals(expectedMinutes, ticket.getFlightDurationMinutes());
    }

    @Test
    void testSingleDigitHourParsing() {
        Ticket ticket = new Ticket();
        ticket.setDeparture_date("12.05.18");
        ticket.setDeparture_time("6:10"); // одна цифра в часах
        ticket.setArrival_date("12.05.18");
        ticket.setArrival_time("15:25");

        LocalDateTime expectedDeparture = LocalDateTime.of(2018, 5, 12, 6, 10, 0);
        assertEquals(expectedDeparture, ticket.getDepartureDateTime());

        // проверка, что расчет времени все еще работает
        assertTrue(ticket.getFlightDurationMinutes() > 0);
    }
}