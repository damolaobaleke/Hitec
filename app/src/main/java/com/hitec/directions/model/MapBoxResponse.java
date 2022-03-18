package com.hitec.directions.model;

import java.util.HashMap;
import java.util.List;

public class MapBoxResponse {

    List<Route> routes;
    List<HashMap<String, Object>> waypoints;
    String code;
    String uuid;

    public MapBoxResponse(List<Route> routes, List<HashMap<String, Object>> waypoints, String code, String uuid) {
        this.routes = routes;
        this.waypoints = waypoints;
        this.code = code;
        this.uuid = uuid;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public List<HashMap<String, Object>> getWaypoints() {
        return waypoints;
    }

    public String getCode() {
        return code;
    }

    public String getUuid() {
        return uuid;
    }
}

