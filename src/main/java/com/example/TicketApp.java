package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class TicketApp {
    public static void main(String[] args) {
        try {
            InputStream inputStream;

            if (args.length > 0) {
                File file = new File(args[0]);
                if (!file.exists()) {
                    System.err.println("Файл не найден: " + args[0]);
                    System.exit(1);
                }
                inputStream = new java.io.FileInputStream(file);
            } else {
                inputStream = TicketApp.class.getClassLoader().getResourceAsStream("tickets.json");
                if (inputStream == null) {
                    System.err.println("Файл tickets.json не найден в resources и не указан в аргументах");
                    System.err.println("Использование: java -jar program.jar [путь/к/tickets.json]");
                    System.exit(1);
                }
            }

            // парсинг json
            ObjectMapper objectMapper = new ObjectMapper();
            TicketData ticketData = objectMapper.readValue(inputStream, TicketData.class);
            List<Ticket> tickets = ticketData.getTickets();

            Map<String, Long> minFlightTimes = FlightStatsCalculator.calculateMinFlightTimes(tickets);
            double priceDifference = FlightStatsCalculator.calculatePriceDifference(tickets);

            System.out.println("Минимальное время полета между городами Владивосток и Тель-Авив:");
            minFlightTimes.forEach((carrier, minutes) -> {
                System.out.println(carrier + ": " + FlightStatsCalculator.formatTime(minutes));
            });

            System.out.println("\nРазница между средней ценой и медианой для полета между городами Владивосток и Тель-Авив: " + String.format("%.2f", priceDifference) + " руб.");

        } catch (Exception e) {
            System.err.println("Ошибка при обработке файла: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}