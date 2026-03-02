package com.tripplanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tripplanner.config.TripPlannerConfig;
import com.tripplanner.report.ResultReporter;
import com.tripplanner.search.BookingSearcher;
import com.tripplanner.search.FlightResult;
import com.tripplanner.search.HotelResult;
import com.tripplanner.search.SkyscannerSearcher;

import java.io.File;
import java.util.List;

public class TripPlanner {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: trip-planner <config.json> [--fetch]");
            System.err.println("  <config.json>  Path to the JSON configuration file");
            System.err.println("  --fetch        Attempt to fetch prices from Skyscanner/Booking.com");
            System.err.println("                 (Note: sites may block automated requests)");
            System.exit(1);
        }

        String configPath = args[0];
        boolean fetchEnabled = args.length > 1 && "--fetch".equals(args[1]);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        TripPlannerConfig config = mapper.readValue(new File(configPath), TripPlannerConfig.class);

        System.out.println("Trip Planner - Starting search...");
        System.out.println("Fetch mode: " + (fetchEnabled ? "ENABLED (will attempt HTTP requests)" : "DISABLED (URL generation only)"));
        System.out.println();

        SkyscannerSearcher flightSearcher = new SkyscannerSearcher(fetchEnabled);
        BookingSearcher hotelSearcher = new BookingSearcher(fetchEnabled);

        System.out.println("Searching flights...");
        List<FlightResult> flightResults = flightSearcher.search(config);
        System.out.println("Found " + flightResults.size() + " flight date combinations.");

        System.out.println("Searching hotels...");
        List<HotelResult> hotelResults = hotelSearcher.search(config);
        System.out.println("Found " + hotelResults.size() + " hotel date combinations.");

        ResultReporter reporter = new ResultReporter();
        reporter.report(flightResults, hotelResults);
    }
}
