package com.tripplanner.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FlightSearchConfig {
    private String origin;
    private String destination;
    private boolean returnFlight = false;
    private LocalDate dateRangeStart;
    private LocalDate dateRangeEnd;
    private Integer maxDurationDays;

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public boolean isReturnFlight() { return returnFlight; }
    public void setReturnFlight(boolean returnFlight) { this.returnFlight = returnFlight; }
    public LocalDate getDateRangeStart() { return dateRangeStart; }
    public void setDateRangeStart(LocalDate dateRangeStart) { this.dateRangeStart = dateRangeStart; }
    public LocalDate getDateRangeEnd() { return dateRangeEnd; }
    public void setDateRangeEnd(LocalDate dateRangeEnd) { this.dateRangeEnd = dateRangeEnd; }
    public Integer getMaxDurationDays() { return maxDurationDays; }
    public void setMaxDurationDays(Integer maxDurationDays) { this.maxDurationDays = maxDurationDays; }
}
