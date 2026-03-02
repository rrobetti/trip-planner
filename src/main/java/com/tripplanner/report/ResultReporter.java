package com.tripplanner.report;

import com.tripplanner.search.FlightResult;
import com.tripplanner.search.HotelResult;

import java.util.List;

public class ResultReporter {

    public void report(List<FlightResult> flightResults, List<HotelResult> hotelResults) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("TRIP PLANNER RESULTS");
        System.out.println("=".repeat(80));

        reportFlights(flightResults);
        reportHotels(hotelResults);

        System.out.println("\n" + "=".repeat(80));
        System.out.println("END OF RESULTS");
        System.out.println("=".repeat(80));
    }

    private void reportFlights(List<FlightResult> results) {
        if (results.isEmpty()) return;

        System.out.println("\n--- FLIGHT RESULTS (" + results.size() + " searches) ---\n");

        for (FlightResult r : results) {
            System.out.println(r.getSummary());
            System.out.println("  URL: " + r.getUrl());
            if (r.getError() != null) {
                System.out.println("  Note: " + r.getError());
            }
            System.out.println();
        }
    }

    private void reportHotels(List<HotelResult> results) {
        if (results.isEmpty()) return;

        System.out.println("\n--- HOTEL RESULTS (" + results.size() + " searches) ---\n");

        for (HotelResult r : results) {
            System.out.println(r.getSummary());
            System.out.println("  URL: " + r.getUrl());
            if (r.getError() != null) {
                System.out.println("  Note: " + r.getError());
            }
            System.out.println();
        }
    }
}
