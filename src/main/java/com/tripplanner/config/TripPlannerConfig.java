package com.tripplanner.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TripPlannerConfig {
    private PassengersConfig passengers = new PassengersConfig();
    private List<FlightSearchConfig> flights = new ArrayList<>();
    private List<HotelSearchConfig> hotels = new ArrayList<>();
    private FlightOptionsConfig flightOptions = new FlightOptionsConfig();
    private Map<String, String> extraFlightParams = new HashMap<>();
    private Map<String, String> extraHotelParams = new HashMap<>();

    public PassengersConfig getPassengers() { return passengers; }
    public void setPassengers(PassengersConfig passengers) { this.passengers = passengers; }
    public List<FlightSearchConfig> getFlights() { return flights; }
    public void setFlights(List<FlightSearchConfig> flights) { this.flights = flights; }
    public List<HotelSearchConfig> getHotels() { return hotels; }
    public void setHotels(List<HotelSearchConfig> hotels) { this.hotels = hotels; }
    public FlightOptionsConfig getFlightOptions() { return flightOptions; }
    public void setFlightOptions(FlightOptionsConfig flightOptions) { this.flightOptions = flightOptions; }
    public Map<String, String> getExtraFlightParams() { return extraFlightParams; }
    public void setExtraFlightParams(Map<String, String> extraFlightParams) { this.extraFlightParams = extraFlightParams; }
    public Map<String, String> getExtraHotelParams() { return extraHotelParams; }
    public void setExtraHotelParams(Map<String, String> extraHotelParams) { this.extraHotelParams = extraHotelParams; }
}
