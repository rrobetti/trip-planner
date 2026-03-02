package com.tripplanner.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FlightOptionsConfig {
    private String cabinClass = "economy";
    private List<String> stops = new ArrayList<>();
    private List<String> fareAttributes = new ArrayList<>();
    private boolean preferDirects = false;
    private boolean outboundAltsEnabled = false;
    private boolean inboundAltsEnabled = false;
    private String sortBy = "cheapest";

    public String getCabinClass() { return cabinClass; }
    public void setCabinClass(String cabinClass) { this.cabinClass = cabinClass; }
    public List<String> getStops() { return stops; }
    public void setStops(List<String> stops) { this.stops = stops; }
    public List<String> getFareAttributes() { return fareAttributes; }
    public void setFareAttributes(List<String> fareAttributes) { this.fareAttributes = fareAttributes; }
    public boolean isPreferDirects() { return preferDirects; }
    public void setPreferDirects(boolean preferDirects) { this.preferDirects = preferDirects; }
    public boolean isOutboundAltsEnabled() { return outboundAltsEnabled; }
    public void setOutboundAltsEnabled(boolean outboundAltsEnabled) { this.outboundAltsEnabled = outboundAltsEnabled; }
    public boolean isInboundAltsEnabled() { return inboundAltsEnabled; }
    public void setInboundAltsEnabled(boolean inboundAltsEnabled) { this.inboundAltsEnabled = inboundAltsEnabled; }
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
}
