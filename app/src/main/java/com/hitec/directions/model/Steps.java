package com.hitec.directions.model;

import java.util.HashMap;
import java.util.List;

public class Steps {
    List<HashMap<String, Object>> intersections;
    HashMap<String, Object> maneuver;
    String name;
    double duration;
    double distance;
    String driving_side;
    double weight;
    String mode;
    String geometry;

    public HashMap<String, Object> getManeuver() {
        return maneuver;
    }
}
