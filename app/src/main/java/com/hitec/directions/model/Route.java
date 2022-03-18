package com.hitec.directions.model;

import java.util.HashMap;
import java.util.List;

public class Route {

    String weight_name;
    double weight;
    double duration;
    List<Leg> legs;
    Object geometry;

    public Route(String weightName, double weight, double duration, List<Leg> legs, Object geometry) {
        this.weight_name = weightName;
        this.weight = weight;
        this.duration = duration;
        this.legs = legs;
        this.geometry = geometry;
    }

    public String getWeightName() {
        return weight_name;
    }

    public double getWeight() {
        return weight;
    }

    public double getDuration() {
        return duration;
    }

    public List<Leg> getLegs() {
        return legs;
    }

    public Object getGeometry() {
        return geometry;
    }
}
