package com.hitec.directions.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hitec.directions.BuildConfig;
import com.hitec.directions.model.GeoCoderResponse;
import com.hitec.directions.network.MapsApi;
import com.hitec.directions.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapActivityViewModel extends ViewModel {
    MutableLiveData<List<String>> coordinates;
    MapsApi mapsApi;

    private final static String TAG = "MapActivityViewModel";

    public MapActivityViewModel(){
        coordinates = new MutableLiveData<>();
    }


    public MutableLiveData<List<String>> fetchDestinationCoordinates(String searchText){
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put(Constants.INSTANCE.getACCESS_TOKEN(), BuildConfig.MAPBOX_ACCESS_TOKEN);

        Call<GeoCoderResponse> call = mapsApi.fetchLatLng(searchText, queryMap);

        call.enqueue(new Callback<GeoCoderResponse>() {
            @Override
            public void onResponse(@NonNull Call<GeoCoderResponse> call, @NonNull Response<GeoCoderResponse> response) {
                if(response.isSuccessful()){
                    assert response.body() != null;

                    Log.i(TAG, "Center "+ response.body().getFeatures().get(0).get("center"));
                    //pos 0 -long

                    //convert Object to explicit List<String>
                    Object o = response.body().getFeatures().get(0).get("center");
                    assert o != null;
                    String[] latLng = o.toString().replace("[","").replace("]","").split(",");

                    List<String> points = new ArrayList<>();
                    points.add(latLng[0]);
                    points.add(latLng[1]);

                    Log.i(TAG, points.toString()+" "+points.get(0)+" "+points.get(1));

                    coordinates.setValue(points);
                }
            }

            @Override
            public void onFailure(@NonNull Call<GeoCoderResponse> call, @NonNull Throwable t) {
                Log.i(TAG, t.getLocalizedMessage());
            }
        });

        return coordinates;
    }


    public void setUpNetworkRequest() {
        Gson gson = new GsonBuilder().serializeNulls().create();//to be able to see null value fields

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(10000, TimeUnit.MILLISECONDS).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.INSTANCE.getBASE_URL())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        mapsApi = retrofit.create(MapsApi.class);
    }
}
