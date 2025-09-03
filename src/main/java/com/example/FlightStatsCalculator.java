package com.example;

import java.util.*;
import java.util.stream.Collectors;

public class FlightStatsCalculator {

    public static Map<String, Long> calculateMinFlightTimes(List<Ticket> tickets) {
        return tickets.stream()
                .filter(ticket -> "VVO".equals(ticket.getOrigin()) && "TLV".equals(ticket.getDestination()))
                .filter(ticket -> {
                    long duration = ticket.getFlightDurationMinutes();
                    if (duration <= 0) {
                        System.err.println("Предупреждение: Обнаружен билет с некорректным временем полета: " + duration + " минут");
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.groupingBy(
                        Ticket::getCarrier,
                        Collectors.collectingAndThen(
                                Collectors.mapping(Ticket::getFlightDurationMinutes, Collectors.toList()),
                                durations -> durations.stream().min(Long::compare).orElse(0L)
                        )
                ));
    }

    public static double calculatePriceDifference(List<Ticket> tickets) {
        List<Integer> prices = tickets.stream()
                .filter(ticket -> "VVO".equals(ticket.getOrigin()) && "TLV".equals(ticket.getDestination()))
                .map(Ticket::getPrice)
                .filter(price -> price > 0)
                .sorted()
                .collect(Collectors.toList());

        if (prices.isEmpty()) {
            System.err.println("Предупреждение: Не найдено билетов между VVO и TLV");
            return 0;
        }

        double average = prices.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);

        double median;
        int size = prices.size();
        if (size % 2 == 0) {
            median = (prices.get(size/2 - 1) + prices.get(size/2)) / 2.0;
        } else {
            median = prices.get(size/2);
        }

        return average - median;
    }

    public static String formatTime(long minutes) {
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;
        return String.format("%d час %02d мин", hours, remainingMinutes);
    }
}