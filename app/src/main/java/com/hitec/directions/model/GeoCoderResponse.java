package com.hitec.directions.model;

import java.util.HashMap;
import java.util.List;

public class GeoCoderResponse {
    String type;
    List<String> query;
    List<HashMap<String, Object>> features;
    String attribution;

    public GeoCoderResponse(String type, List<String> query, List<HashMap<String, Object>> features, String attribution) {
        this.type = type;
        this.query = query;
        this.features = features;
        this.attribution = attribution;
    }


    public String getType() {
        return type;
    }

    public List<String> getQuery() {
        return query;
    }

    public List<HashMap<String, Object>> getFeatures() {
        return features;
    }

    public String getAttribution() {
        return attribution;
    }
}
