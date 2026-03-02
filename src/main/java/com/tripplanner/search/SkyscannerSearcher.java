package com.tripplanner.search;

import com.tripplanner.config.FlightSearchConfig;
import com.tripplanner.config.TripPlannerConfig;
import com.tripplanner.fetch.BrowserFetcher;
import com.tripplanner.url.SkyscannerUrlBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SkyscannerSearcher {

    // CSS selectors to wait for (signals the results are rendering)
    private static final String[] WAIT_SELECTORS = {
            "[data-testid='Price']",
            "[class*='Price_mainPrice']",
            "[class*='price']",
            "body"
    };

    // CSS selectors from which to read the cheapest-price text
    private static final String[] PRICE_SELECTORS = {
            "[data-testid='Price']",
            "[class*='Price_mainPrice']",
            "[class*='BpkText'][class*='price']",
            "span[class*='price']",
            "div[class*='price']"
    };

    private final SkyscannerUrlBuilder urlBuilder = new SkyscannerUrlBuilder();
    private final boolean fetchEnabled;

    public SkyscannerSearcher(boolean fetchEnabled) {
        this.fetchEnabled = fetchEnabled;
    }

    public List<FlightResult> search(TripPlannerConfig config) {
        List<FlightResult> results = new ArrayList<>();

        for (FlightSearchConfig flightConfig : config.getFlights()) {
            LocalDate current = flightConfig.getDateRangeStart();
            LocalDate end = flightConfig.getDateRangeEnd();
            if (current == null || end == null) continue;

            while (!current.isAfter(end)) {
                String url = urlBuilder.buildUrl(flightConfig, current, config);
                FlightResult result = new FlightResult();
                result.setOrigin(flightConfig.getOrigin());
                result.setDestination(flightConfig.getDestination());
                result.setDepartureDate(current);
                result.setReturnFlight(flightConfig.isReturnFlight());
                result.setUrl(url);
                results.add(result);
                current = current.plusDays(1);
            }
        }

        if (fetchEnabled) {
            System.out.println("  Opening headless browser for Skyscanner...");
            try (BrowserFetcher fetcher = new BrowserFetcher()) {
                for (FlightResult result : results) {
                    fetchPrice(fetcher, result);
                }
            } catch (Exception e) {
                for (FlightResult result : results) {
                    if (result.getError() == null) {
                        result.setError("Browser init failed: " + e.getMessage());
                    }
                }
            }
        }

        return results;
    }

    private void fetchPrice(BrowserFetcher fetcher, FlightResult result) {
        try {
            String price = fetcher.fetchPrice(result.getUrl(), WAIT_SELECTORS, PRICE_SELECTORS);
            if (price != null) {
                result.setPrice(price);
                result.setFetched(true);
            } else {
                result.setError("Price not found - the page may require JavaScript rendering. "
                        + "Try accessing the URL manually or use a headless browser.");
            }
        } catch (Exception e) {
            result.setError("Fetch failed: " + e.getMessage());
        }
    }
}

