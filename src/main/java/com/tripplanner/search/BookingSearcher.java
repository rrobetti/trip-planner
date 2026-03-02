package com.tripplanner.search;

import com.tripplanner.config.HotelSearchConfig;
import com.tripplanner.config.TripPlannerConfig;
import com.tripplanner.url.BookingUrlBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingSearcher {

    private final BookingUrlBuilder urlBuilder = new BookingUrlBuilder();
    private final boolean fetchEnabled;

    public BookingSearcher(boolean fetchEnabled) {
        this.fetchEnabled = fetchEnabled;
    }

    public List<HotelResult> search(TripPlannerConfig config) {
        List<HotelResult> results = new ArrayList<>();

        for (HotelSearchConfig hotelConfig : config.getHotels()) {
            LocalDate current = hotelConfig.getCheckinRangeStart();
            LocalDate end = hotelConfig.getCheckinRangeEnd();
            if (current == null || end == null) continue;

            while (!current.isAfter(end)) {
                String url = urlBuilder.buildUrl(hotelConfig, current, config);
                HotelResult result = new HotelResult();
                result.setLocation(hotelConfig.getLocation());
                result.setHotelName(hotelConfig.getHotelName());
                result.setCheckin(current);
                result.setCheckout(current.plusDays(hotelConfig.getStayNights()));
                result.setUrl(url);

                if (fetchEnabled) {
                    fetchPrice(result, url, hotelConfig.getHotelName());
                }

                results.add(result);
                current = current.plusDays(1);
            }
        }

        return results;
    }

    private void fetchPrice(HotelResult result, String url, String hotelName) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header("Accept-Language", "en-GB,en;q=0.9")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .timeout(15000)
                    .get();

            String price = null;

            // If a specific hotel name was given, search for it in the results
            if (hotelName != null && !hotelName.isEmpty()) {
                price = findHotelPrice(doc, hotelName);
            }

            // Otherwise, try to get the cheapest price shown
            if (price == null) {
                price = tryExtractPrice(doc,
                        "[data-testid='price-and-discounted-price']",
                        ".prco-valign-middle-helper",
                        "span[class*='price']",
                        "[data-testid='recommended-units-price']",
                        "strong[class*='price']");
            }

            if (price != null) {
                result.setPrice(price);
                result.setFetched(true);
            } else {
                result.setError("Price not found in page");
            }
        } catch (IOException e) {
            result.setError("Fetch failed: " + e.getMessage());
        }
    }

    private String findHotelPrice(Document doc, String hotelName) {
        // Try to find a property card matching the hotel name
        String lowerName = hotelName.toLowerCase();
        Elements propertyCards = doc.select("[data-testid='property-card'], .sr_property_block, div[class*='property']");
        for (Element card : propertyCards) {
            String cardText = card.text().toLowerCase();
            if (cardText.contains(lowerName)) {
                Elements priceEls = card.select("[data-testid='price-and-discounted-price'], span[class*='price'], strong[class*='price']");
                if (!priceEls.isEmpty()) {
                    return priceEls.first().text().trim();
                }
            }
        }
        return null;
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
