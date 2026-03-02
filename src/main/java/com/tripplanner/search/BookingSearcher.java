package com.tripplanner.search;

import com.tripplanner.config.HotelSearchConfig;
import com.tripplanner.config.TripPlannerConfig;
import com.tripplanner.fetch.BrowserFetcher;
import com.tripplanner.url.BookingUrlBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingSearcher {

    // Selector that signals search results have loaded
    private static final String CARD_SELECTOR = "[data-testid='property-card']";

    // CSS selectors for price text within a card or on the page
    private static final String[] PRICE_SELECTORS = {
            "[data-testid='price-and-discounted-price']",
            ".prco-valign-middle-helper",
            "[data-testid='recommended-units-price']",
            "span[class*='price']",
            "strong[class*='price']"
    };

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
                results.add(result);
                current = current.plusDays(1);
            }
        }

        if (fetchEnabled) {
            System.out.println("  Opening headless browser for Booking.com...");
            try (BrowserFetcher fetcher = new BrowserFetcher()) {
                for (HotelResult result : results) {
                    fetchPrice(fetcher, result);
                }
            } catch (Exception e) {
                for (HotelResult result : results) {
                    if (result.getError() == null) {
                        result.setError("Browser init failed: " + e.getMessage());
                    }
                }
            }
        }

        return results;
    }

    private void fetchPrice(BrowserFetcher fetcher, HotelResult result) {
        try {
            String price = fetcher.fetchBookingPrice(
                    result.getUrl(), result.getHotelName(), CARD_SELECTOR, PRICE_SELECTORS);
            if (price != null) {
                result.setPrice(price);
                result.setFetched(true);
            } else {
                result.setError("Price not found in search results. "
                        + "The hotel may not be available for these dates, "
                        + "or the page structure may have changed.");
            }
        } catch (Exception e) {
            result.setError("Fetch failed: " + e.getMessage());
        }
    }

}

