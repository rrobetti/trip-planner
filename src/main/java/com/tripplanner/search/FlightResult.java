package com.tripplanner.search;

import java.time.LocalDate;

public class FlightResult extends SearchResult {
    private String origin;
    private String destination;
    private LocalDate departureDate;
    private boolean returnFlight;

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public LocalDate getDepartureDate() { return departureDate; }
    public void setDepartureDate(LocalDate departureDate) { this.departureDate = departureDate; }
    public boolean isReturnFlight() { return returnFlight; }
    public void setReturnFlight(boolean returnFlight) { this.returnFlight = returnFlight; }

    @Override
    public String getSummary() {
        return String.format("FLIGHT %s -> %s on %s [%s]%s | Price: %s",
                origin, destination, departureDate,
                returnFlight ? "return" : "one-way",
                fetched ? "" : " [URL only]",
                price != null ? price : "N/A");
    }
}
