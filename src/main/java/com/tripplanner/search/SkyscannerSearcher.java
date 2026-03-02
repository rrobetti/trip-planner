package com.tripplanner.search;

import com.tripplanner.config.FlightSearchConfig;
import com.tripplanner.config.TripPlannerConfig;
import com.tripplanner.url.SkyscannerUrlBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SkyscannerSearcher {

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

                if (fetchEnabled) {
                    fetchPrice(result, url);
                }

                results.add(result);
                current = current.plusDays(1);
            }
        }

        return results;
    }

    private void fetchPrice(FlightResult result, String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .timeout(15000)
                    .get();

            // Skyscanner renders via JS; try several CSS selectors for price
            String price = tryExtractPrice(doc,
                    "[data-testid='price']",
                    ".BpkText_bpk-text--lg__NWVhO",
                    ".Price_mainPriceContainer__ZV9Pg",
                    "span[class*='price']",
                    "div[class*='price']");

            if (price != null) {
                result.setPrice(price);
                result.setFetched(true);
            } else {
                result.setError("Price not found in page (JS rendering may be required)");
            }
        } catch (IOException e) {
            result.setError("Fetch failed: " + e.getMessage());
        }
    }

    private String tryExtractPrice(Document doc, String... selectors) {
        for (String selector : selectors) {
            Elements elements = doc.select(selector);
            if (!elements.isEmpty()) {
                Element el = elements.first();
                String text = el.text().trim();
                if (!text.isEmpty() && (text.contains("€") || text.contains("$") || text.contains("£") || text.matches(".*\\d+.*"))) {
                    return text;
                }
            }
        }
        return null;
    }
}
