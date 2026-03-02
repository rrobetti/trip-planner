package com.tripplanner.url;

import com.tripplanner.config.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SkyscannerUrlBuilderTest {

    private SkyscannerUrlBuilder builder;
    private TripPlannerConfig config;

    @BeforeEach
    void setUp() {
        builder = new SkyscannerUrlBuilder();
        config = new TripPlannerConfig();

        PassengersConfig passengers = new PassengersConfig();
        passengers.setAdults(2);
        passengers.setChildrenAges(Arrays.asList(4, 6));
        config.setPassengers(passengers);

        FlightOptionsConfig options = new FlightOptionsConfig();
        options.setCabinClass("economy");
        options.setStops(Arrays.asList("!direct", "!twoPlusStops"));
        options.setFareAttributes(Arrays.asList("checked-bag", "cabin-bag"));
        config.setFlightOptions(options);
    }

    @Test
    void testUrlContainsOriginAndDestination() {
        FlightSearchConfig flight = new FlightSearchConfig();
        flight.setOrigin("DUB");
        flight.setDestination("SAOA");
        flight.setReturnFlight(false);

        String url = builder.buildUrl(flight, LocalDate.of(2026, 7, 26), config);

        assertTrue(url.contains("/dub/saoa/"), "URL should contain origin and destination in lowercase");
    }

    @Test
    void testDateFormat() {
        FlightSearchConfig flight = new FlightSearchConfig();
        flight.setOrigin("DUB");
        flight.setDestination("SAOA");

        // July 26, 2026 => 260726
        String url = builder.buildUrl(flight, LocalDate.of(2026, 7, 26), config);
        assertTrue(url.contains("/260726/"), "Date should be formatted as DDMMYY: 260726");
    }

    @Test
    void testAdultsParam() {
        FlightSearchConfig flight = new FlightSearchConfig();
        flight.setOrigin("DUB");
        flight.setDestination("SAOA");

        String url = builder.buildUrl(flight, LocalDate.of(2026, 7, 26), config);
        assertTrue(url.contains("adultsv2=2"), "URL should contain adults count");
    }

    @Test
    void testChildrenParam() {
        FlightSearchConfig flight = new FlightSearchConfig();
        flight.setOrigin("DUB");
        flight.setDestination("SAOA");

        String url = builder.buildUrl(flight, LocalDate.of(2026, 7, 26), config);
        assertTrue(url.contains("childrenv2="), "URL should contain children param");
        assertTrue(url.contains("4"), "URL should contain child age 4");
        assertTrue(url.contains("6"), "URL should contain child age 6");
    }

    @Test
    void testReturnFlightParam() {
        FlightSearchConfig flightReturn = new FlightSearchConfig();
        flightReturn.setOrigin("DUB");
        flightReturn.setDestination("SAOA");
        flightReturn.setReturnFlight(true);

        FlightSearchConfig flightOneWay = new FlightSearchConfig();
        flightOneWay.setOrigin("DUB");
        flightOneWay.setDestination("SAOA");
        flightOneWay.setReturnFlight(false);

        String returnUrl = builder.buildUrl(flightReturn, LocalDate.of(2026, 7, 26), config);
        String oneWayUrl = builder.buildUrl(flightOneWay, LocalDate.of(2026, 7, 26), config);

        assertTrue(returnUrl.contains("rtn=1"), "Return flight URL should have rtn=1");
        assertTrue(oneWayUrl.contains("rtn=0"), "One-way flight URL should have rtn=0");
    }

    @Test
    void testCabinClass() {
        FlightSearchConfig flight = new FlightSearchConfig();
        flight.setOrigin("DUB");
        flight.setDestination("SAOA");

        String url = builder.buildUrl(flight, LocalDate.of(2026, 7, 26), config);
        assertTrue(url.contains("cabinclass=economy"), "URL should contain cabin class");
    }

    @Test
    void testFareAttributes() {
        FlightSearchConfig flight = new FlightSearchConfig();
        flight.setOrigin("DUB");
        flight.setDestination("SAOA");

        String url = builder.buildUrl(flight, LocalDate.of(2026, 7, 26), config);
        assertTrue(url.contains("fare-attributes="), "URL should contain fare-attributes");
        assertTrue(url.contains("checked-bag"), "URL should contain checked-bag");
        assertTrue(url.contains("cabin-bag"), "URL should contain cabin-bag");
    }

    @Test
    void testBuildAllUrls_dateRange() {
        FlightSearchConfig flight = new FlightSearchConfig();
        flight.setOrigin("DUB");
        flight.setDestination("SAOA");
        flight.setDateRangeStart(LocalDate.of(2026, 7, 15));
        flight.setDateRangeEnd(LocalDate.of(2026, 7, 20));
        config.setFlights(List.of(flight));

        List<String> urls = builder.buildAllUrls(config);
        assertEquals(6, urls.size(), "Should generate 6 URLs for a 6-day range");
    }
}
