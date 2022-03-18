package com.hitec.directions.network;

import com.hitec.directions.model.GeoCoderResponse;
import com.hitec.directions.model.MapBoxResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface MapsApi {

//    @GET("/directions/v5/{profile}/{coordinates}")
//    Call<MapBoxResponse> fetchDirections(@Path("profile") String profile, @Path("coordinates") String coordinates,
//                                         @QueryMap Map<String, Object> queryMap);

    @GET("/directions/v5/mapbox/driving/{coordinates}")
    Call<MapBoxResponse> fetchDirections(@Path("coordinates") String coordinates,
                                         @QueryMap Map<String, Object> queryMap);

    @GET("/geocoding/v5/mapbox.places/{search_text}.json")
    Call<GeoCoderResponse> fetchLatLng(@Path("search_text") String searchText ,@QueryMap Map<String, String> queryMap);
}
