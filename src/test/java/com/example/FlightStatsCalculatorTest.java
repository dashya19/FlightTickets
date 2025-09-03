package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FlightStatsCalculatorTest {

    private List<Ticket> testTickets;

    @BeforeEach
    void setUp() {
        // Создаем тестовые данные
        Ticket ticket1 = createTestTicket("VVO", "TLV", "TK", 12400, "12.05.18", "16:20", "12.05.18", "22:10");
        Ticket ticket2 = createTestTicket("VVO", "TLV", "S7", 13100, "12.05.18", "17:20", "12.05.18", "23:50");
        Ticket ticket3 = createTestTicket("VVO", "UFA", "TK", 33400, "12.05.18", "15:15", "12.05.18", "17:45");
        Ticket ticket4 = createTestTicket("VVO", "TLV", "TK", 11000, "12.05.18", "17:00", "12.05.18", "23:30");

        testTickets = Arrays.asList(ticket1, ticket2, ticket3, ticket4);
    }

    private Ticket createTestTicket(String origin, String dest, String carrier, int price,
                                    String depDate, String depTime, String arrDate, String arrTime) {
        Ticket ticket = new Ticket();
        ticket.setOrigin(origin);
        ticket.setDestination(dest);
        ticket.setCarrier(carrier);
        ticket.setPrice(price);
        ticket.setDeparture_date(depDate);
        ticket.setDeparture_time(depTime);
        ticket.setArrival_date(arrDate);
        ticket.setArrival_time(arrTime);
        return ticket;
    }

    @Test
    void testCalculateMinFlightTimes() {
        Map<String, Long> result = FlightStatsCalculator.calculateMinFlightTimes(testTickets);

        // VVO-TLV
        assertEquals(2, result.size());
        assertTrue(result.containsKey("TK"));
        assertTrue(result.containsKey("S7"));
        assertFalse(result.containsKey("UFA"));

        // TK имеет 2 билета: 350 и 390 минут, min - 350
        assertEquals(350L, result.get("TK"));
    }

    @Test
    void testCalculateMinFlightTimes_NoValidTickets() {
        List<Ticket> emptyList = Arrays.asList(
                createTestTicket("LED", "TLV", "SU", 10000, "12.05.18", "10:00", "12.05.18", "12:00")
        );

        Map<String, Long> result = FlightStatsCalculator.calculateMinFlightTimes(emptyList);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCalculatePriceDifference() {
        double difference = FlightStatsCalculator.calculatePriceDifference(testTickets);

        // цены для VVO-TLV: 12400, 13100, 11000
        // среднее: (12400 + 13100 + 11000) / 3 = 12166.67
        // медиана: 12400 (после сортировки: 11000, 12400, 13100)
        // разница: 12166.67 - 12400 = -233.33
        assertEquals(-233.33, difference, 0.01); // дельта для сравнения double
    }

    @Test
    void testCalculatePriceDifference_SinglePrice() {
        List<Ticket> singleTicket = Arrays.asList(
                createTestTicket("VVO", "TLV", "TK", 10000, "12.05.18", "10:00", "12.05.18", "12:00")
        );

        double difference = FlightStatsCalculator.calculatePriceDifference(singleTicket);
        // для одного элемента среднее = медиана = 10000, разница = 0
        assertEquals(0.0, difference, 0.01);
    }

    @Test
    void testCalculatePriceDifference_EvenNumberOfPrices() {
        List<Ticket> tickets = Arrays.asList(
                createTestTicket("VVO", "TLV", "TK", 10000, "12.05.18", "10:00", "12.05.18", "12:00"),
                createTestTicket("VVO", "TLV", "S7", 20000, "12.05.18", "11:00", "12.05.18", "13:00"),
                createTestTicket("VVO", "TLV", "SU", 30000, "12.05.18", "12:00", "12.05.18", "14:00"),
                createTestTicket("VVO", "TLV", "BA", 40000, "12.05.18", "13:00", "12.05.18", "15:00")
        );

        double difference = FlightStatsCalculator.calculatePriceDifference(tickets);
        // цены: 10000, 20000, 30000, 40000
        // среднее: 25000
        // медиана: (20000 + 30000) / 2 = 25000
        // разница: 0
        assertEquals(0.0, difference, 0.01);
    }

    @Test
    void testFormatTime() {
        assertEquals("5 час 50 мин", FlightStatsCalculator.formatTime(350));
        assertEquals("1 час 30 мин", FlightStatsCalculator.formatTime(90));
        assertEquals("0 час 05 мин", FlightStatsCalculator.formatTime(5));
        assertEquals("25 час 00 мин", FlightStatsCalculator.formatTime(1500));
    }
}