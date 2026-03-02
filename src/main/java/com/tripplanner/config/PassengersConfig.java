package com.tripplanner.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PassengersConfig {
    private int adults = 1;
    private List<Integer> childrenAges = new ArrayList<>();

    public int getAdults() { return adults; }
    public void setAdults(int adults) { this.adults = adults; }
    public List<Integer> getChildrenAges() { return childrenAges; }
    public void setChildrenAges(List<Integer> childrenAges) { this.childrenAges = childrenAges; }
}
