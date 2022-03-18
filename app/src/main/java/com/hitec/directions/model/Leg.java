package com.hitec.directions.model;

import java.util.HashMap;
import java.util.List;

public class Leg {
    List<Object> via_waypoints;
    List<HashMap<String, String>> admins;
    double weight;
    double duration;
    List<Steps> steps;

    public List<Steps> getSteps() {
        return steps;
    }
}
