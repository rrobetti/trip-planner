package com.tripplanner.search;

import java.time.LocalDate;

public class HotelResult extends SearchResult {
    private String location;
    private String hotelName;
    private LocalDate checkin;
    private LocalDate checkout;

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getHotelName() { return hotelName; }
    public void setHotelName(String hotelName) { this.hotelName = hotelName; }
    public LocalDate getCheckin() { return checkin; }
    public void setCheckin(LocalDate checkin) { this.checkin = checkin; }
    public LocalDate getCheckout() { return checkout; }
    public void setCheckout(LocalDate checkout) { this.checkout = checkout; }

    @Override
    public String getSummary() {
        String hotel = hotelName != null ? hotelName : location;
        return String.format("HOTEL %s: check-in %s, check-out %s%s | Price: %s",
                hotel, checkin, checkout,
                fetched ? "" : " [URL only]",
                price != null ? price : "N/A");
    }
}
