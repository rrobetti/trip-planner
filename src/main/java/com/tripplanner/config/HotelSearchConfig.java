package com.tripplanner.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HotelSearchConfig {
    private String location;
    private String hotelName;
    private int rooms = 1;
    private int stayNights = 7;
    private LocalDate checkinRangeStart;
    private LocalDate checkinRangeEnd;

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }
    public int getRooms() { return rooms; }
    public void setRooms(int rooms) { this.rooms = rooms; }
    public int getStayNights() { return stayNights; }
    public void setStayNights(int stayNights) { this.stayNights = stayNights; }
    public LocalDate getCheckinRangeStart() { return checkinRangeStart; }
    public void setCheckinRangeStart(LocalDate checkinRangeStart) { this.checkinRangeStart = checkinRangeStart; }
    public LocalDate getCheckinRangeEnd() { return checkinRangeEnd; }
    public void setCheckinRangeEnd(LocalDate checkinRangeEnd) { this.checkinRangeEnd = checkinRangeEnd; }
}
