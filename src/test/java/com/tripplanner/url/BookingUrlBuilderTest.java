package com.tripplanner.url;

import com.tripplanner.config.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingUrlBuilderTest {

    private BookingUrlBuilder builder;
    private TripPlannerConfig config;

    @BeforeEach
    void setUp() {
        builder = new BookingUrlBuilder();
        config = new TripPlannerConfig();

        PassengersConfig passengers = new PassengersConfig();
        passengers.setAdults(2);
        passengers.setChildrenAges(Arrays.asList(4, 6));
        config.setPassengers(passengers);
    }

    @Test
    void testUrlContainsLocation() {
        HotelSearchConfig hotel = new HotelSearchConfig();
        hotel.setLocation("Cabo de Santo Agostinho");
        hotel.setRooms(1);
        hotel.setStayNights(7);

        String url = builder.buildUrl(hotel, LocalDate.of(2026, 7, 19), config);
        assertTrue(url.contains("ss=Cabo"), "URL should contain location");
    }

    @Test
    void testCheckinCheckoutDates() {
        HotelSearchConfig hotel = new HotelSearchConfig();
        hotel.setLocation("Test Location");
        hotel.setStayNights(7);

        String url = builder.buildUrl(hotel, LocalDate.of(2026, 7, 19), config);
        assertTrue(url.contains("checkin=2026-07-19"), "URL should contain checkin date");
        assertTrue(url.contains("checkout=2026-07-26"), "URL should contain checkout date (7 nights later)");
    }

    @Test
    void testAdultsAndChildrenParams() {
        HotelSearchConfig hotel = new HotelSearchConfig();
        hotel.setLocation("Test");
        hotel.setStayNights(7);

        String url = builder.buildUrl(hotel, LocalDate.of(2026, 7, 19), config);
        assertTrue(url.contains("group_adults=2"), "URL should contain group_adults=2");
        assertTrue(url.contains("group_children=2"), "URL should contain group_children=2");
        assertTrue(url.contains("age=4"), "URL should contain age=4");
        assertTrue(url.contains("age=6"), "URL should contain age=6");
    }

    @Test
    void testNoChildren() {
        PassengersConfig passengers = new PassengersConfig();
        passengers.setAdults(2);
        config.setPassengers(passengers);

        HotelSearchConfig hotel = new HotelSearchConfig();
        hotel.setLocation("Test");
        hotel.setStayNights(7);

        String url = builder.buildUrl(hotel, LocalDate.of(2026, 7, 19), config);
        assertTrue(url.contains("group_children=0"), "URL should contain group_children=0 when no children");
        assertFalse(url.contains("age="), "URL should not contain age params when no children");
    }

    @Test
    void testRoomsParam() {
        HotelSearchConfig hotel = new HotelSearchConfig();
        hotel.setLocation("Test");
        hotel.setRooms(2);
        hotel.setStayNights(7);

        String url = builder.buildUrl(hotel, LocalDate.of(2026, 7, 19), config);
        assertTrue(url.contains("no_rooms=2"), "URL should contain no_rooms=2");
    }

    @Test
    void testBuildAllUrls_dateRange() {
        HotelSearchConfig hotel = new HotelSearchConfig();
        hotel.setLocation("Test");
        hotel.setStayNights(7);
        hotel.setCheckinRangeStart(LocalDate.of(2026, 7, 15));
        hotel.setCheckinRangeEnd(LocalDate.of(2026, 7, 20));
        config.setHotels(List.of(hotel));

        List<String> urls = builder.buildAllUrls(config);
        assertEquals(6, urls.size(), "Should generate 6 URLs for a 6-day range");
    }
}
