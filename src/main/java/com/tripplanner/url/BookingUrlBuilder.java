package com.tripplanner.url;

import com.tripplanner.config.HotelSearchConfig;
import com.tripplanner.config.PassengersConfig;
import com.tripplanner.config.TripPlannerConfig;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookingUrlBuilder {

    private static final String BASE_URL = "https://www.booking.com/searchresults.en-gb.html";

    public String buildUrl(HotelSearchConfig hotelConfig, LocalDate checkin, TripPlannerConfig config) {
        PassengersConfig passengers = config.getPassengers();
        LocalDate checkout = checkin.plusDays(hotelConfig.getStayNights());

        List<String> params = new ArrayList<>();
        params.add("ss=" + encode(hotelConfig.getLocation()));
        params.add("ssne=" + encode(hotelConfig.getLocation()));
        params.add("ssne_untouched=" + encode(hotelConfig.getLocation()));
        params.add("lang=en-gb");
        params.add("sb=1");
        params.add("src_elem=sb");
        params.add("src=searchresults");
        params.add("dest_type=city");
        params.add("checkin=" + checkin.toString());
        params.add("checkout=" + checkout.toString());
        params.add("group_adults=" + passengers.getAdults());
        params.add("no_rooms=" + hotelConfig.getRooms());

        if (!passengers.getChildrenAges().isEmpty()) {
            params.add("group_children=" + passengers.getChildrenAges().size());
            for (int age : passengers.getChildrenAges()) {
                params.add("age=" + age);
            }
        } else {
            params.add("group_children=0");
        }

        // Extra hotel params
        for (Map.Entry<String, String> entry : config.getExtraHotelParams().entrySet()) {
            params.add(encode(entry.getKey()) + "=" + encode(entry.getValue()));
        }

        return BASE_URL + "?" + String.join("&", params);
    }

    public List<String> buildAllUrls(TripPlannerConfig config) {
        List<String> urls = new ArrayList<>();
        for (HotelSearchConfig hotelConfig : config.getHotels()) {
            LocalDate current = hotelConfig.getCheckinRangeStart();
            LocalDate end = hotelConfig.getCheckinRangeEnd();
            if (current == null || end == null) continue;
            while (!current.isAfter(end)) {
                urls.add(buildUrl(hotelConfig, current, config));
                current = current.plusDays(1);
            }
        }
        return urls;
    }

    private String encode(String value) {
        if (value == null) return "";
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
