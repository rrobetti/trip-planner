package com.tripplanner.url;

import com.tripplanner.config.FlightOptionsConfig;
import com.tripplanner.config.FlightSearchConfig;
import com.tripplanner.config.PassengersConfig;
import com.tripplanner.config.TripPlannerConfig;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SkyscannerUrlBuilder {

    private static final String BASE_URL = "https://www.skyscanner.ie/transport/flights/";
    private static final DateTimeFormatter SKYSCANNER_DATE_FMT = DateTimeFormatter.ofPattern("yyMMdd");

    public String buildUrl(FlightSearchConfig flightConfig, LocalDate date, TripPlannerConfig config) {
        PassengersConfig passengers = config.getPassengers();
        FlightOptionsConfig options = config.getFlightOptions();

        String origin = flightConfig.getOrigin().toLowerCase();
        String destination = flightConfig.getDestination().toLowerCase();
        String dateStr = date.format(SKYSCANNER_DATE_FMT);

        StringBuilder url = new StringBuilder(BASE_URL);
        url.append(origin).append("/");
        url.append(destination).append("/");
        url.append(dateStr).append("/");
        url.append("?");

        List<String> params = new ArrayList<>();

        // Passengers
        params.add("adultsv2=" + passengers.getAdults());

        // Cabin class
        params.add("cabinclass=" + encode(options.getCabinClass()));

        // Children
        if (!passengers.getChildrenAges().isEmpty()) {
            String children = passengers.getChildrenAges().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining("%7C"));
            params.add("childrenv2=" + children);
        }

        params.add("ref=home");
        params.add("rtn=" + (flightConfig.isReturnFlight() ? "1" : "0"));
        params.add("preferdirects=" + options.isPreferDirects());
        params.add("outboundaltsenabled=" + options.isOutboundAltsEnabled());
        params.add("inboundaltsenabled=" + options.isInboundAltsEnabled());
        params.add("sortby=" + encode(options.getSortBy()));

        // Stops
        if (!options.getStops().isEmpty()) {
            params.add("stops=" + options.getStops().stream()
                    .map(this::encode)
                    .collect(Collectors.joining("%2C")));
        }

        // Fare attributes
        if (!options.getFareAttributes().isEmpty()) {
            params.add("fare-attributes=" + options.getFareAttributes().stream()
                    .map(this::encode)
                    .collect(Collectors.joining("%2C")));
        }

        // Extra params
        for (Map.Entry<String, String> entry : config.getExtraFlightParams().entrySet()) {
            params.add(encode(entry.getKey()) + "=" + encode(entry.getValue()));
        }

        url.append(String.join("&", params));
        return url.toString();
    }

    public List<String> buildAllUrls(TripPlannerConfig config) {
        List<String> urls = new ArrayList<>();
        for (FlightSearchConfig flightConfig : config.getFlights()) {
            LocalDate current = flightConfig.getDateRangeStart();
            LocalDate end = flightConfig.getDateRangeEnd();
            if (current == null || end == null) continue;
            while (!current.isAfter(end)) {
                urls.add(buildUrl(flightConfig, current, config));
                current = current.plusDays(1);
            }
        }
        return urls;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
