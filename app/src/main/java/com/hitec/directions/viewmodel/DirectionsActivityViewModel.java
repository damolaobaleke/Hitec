package com.hitec.directions.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hitec.directions.BuildConfig;
import com.hitec.directions.model.MapBoxResponse;
import com.hitec.directions.model.Steps;
import com.hitec.directions.network.MapsApi;
import com.hitec.directions.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DirectionsActivityViewModel extends ViewModel {

    MutableLiveData<List<Steps>> instructions;
    MapsApi mapsApi;

    StringBuilder points;

    public DirectionsActivityViewModel(){

        instructions = new MutableLiveData<>();

    }

    public MutableLiveData<List<Steps>> fetchDirections(double currentLatitude, double currentLongitude, double destinationLatitude, double destinationLongitude){
        HashMap<String, Object> queryMap = new HashMap<>();
        queryMap.put("alternatives", false);
        queryMap.put("steps", true);
        queryMap.put(Constants.INSTANCE.getACCESS_TOKEN(), BuildConfig.MAPBOX_ACCESS_TOKEN);

        points = new StringBuilder();
        points.append(currentLongitude).append(",").append(currentLatitude).append(";").append(destinationLongitude).append(",").append(destinationLatitude);

        Call<MapBoxResponse> call = mapsApi.fetchDirections(points.toString(), queryMap);

        call.enqueue(new Callback<MapBoxResponse>() {
            @Override
            public void onResponse(@NonNull Call<MapBoxResponse> call, @NonNull Response<MapBoxResponse> response) {
                if(response.isSuccessful()){

                    assert response.body() != null;
                    Log.i("Directions", "Response: "+response.body().getRoutes().get(0).getLegs().get(0).getSteps());

                    List<Steps> steps = response.body().getRoutes().get(0).getLegs().get(0).getSteps();

                    instructions.setValue(steps);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MapBoxResponse> call, @NonNull Throwable t) {
                Log.i("", t.getLocalizedMessage());
            }
        });

        return instructions;
    }

    public void setUpNetworkRequest() {
        Gson gson = new GsonBuilder().serializeNulls().create();//to be able to see null value fields

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(5000, TimeUnit.MILLISECONDS).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.INSTANCE.getBASE_URL())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        mapsApi = retrofit.create(MapsApi.class);
    }
}
