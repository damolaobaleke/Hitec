package com.hitec.directions.views.directions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.hitec.directions.R;
import com.hitec.directions.model.Steps;
import com.hitec.directions.utils.Constants;
import com.hitec.directions.viewmodel.DirectionsActivityViewModel;
import com.hitec.directions.viewmodel.MapActivityViewModel;

import java.util.ArrayList;
import java.util.List;

public class DirectionsActivity extends AppCompatActivity {
    DirectionsActivityViewModel directionsActivityViewModel;

    double destinationLatitude;
    double destinationLongitude;
    double currentLatitude;
    double currentLongitude;
    String destination;

    TextView textViewDestination;
    RecyclerView recyclerViewDirections;
    RecyclerViewAdapterDirections recyclerViewAdapterDirections;
    List<String> directions;

    private final static String TAG = "DirectionsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        directionsActivityViewModel = new ViewModelProvider(this).get(DirectionsActivityViewModel.class);
        textViewDestination = findViewById(R.id.destination);

        setUpRecyclerView();

        Intent intent = getIntent();
        currentLatitude = intent.getDoubleExtra(Constants.INSTANCE.getCURRENT_LATITUDE(), 0.0);
        currentLongitude = intent.getDoubleExtra(Constants.INSTANCE.getCURRENT_LONGITUDE(), 0.0);
        destinationLatitude = intent.getDoubleExtra(Constants.INSTANCE.getDESTINATION_LATITUDE(), 0.0);
        destinationLongitude = intent.getDoubleExtra(Constants.INSTANCE.getDESTINATION_LONGITUDE(), 0.0);
        destination = intent.getStringExtra("destination");

        textViewDestination.setText(String.format("Destination: %s", destination));

        //Make Request here
        directionsActivityViewModel.setUpNetworkRequest();
        getDirections(currentLatitude, currentLongitude, destinationLatitude, destinationLongitude);

        directions = new ArrayList<>();
    }

    private void getDirections(double currentLatitude, double currentLongitude, double destinationLatitude, double destinationLongitude) {
        directionsActivityViewModel.fetchDirections(currentLatitude, currentLongitude, destinationLatitude, destinationLongitude).observe(this, new Observer<List<Steps>>() {
            @Override
            public void onChanged(List<Steps> steps) {

                recyclerViewAdapterDirections = new RecyclerViewAdapterDirections();

                for(int i=0; i<steps.size(); i++){
                    Log.i(TAG, i+1 +": "+steps.get(i).getManeuver().get("instruction").toString());

                    directions.add(steps.get(i).getManeuver().get("instruction").toString());
                }

                recyclerViewAdapterDirections.setInstructions(directions);
                recyclerViewDirections.setAdapter(recyclerViewAdapterDirections);
            }
        });
    }

    private void setUpRecyclerView(){
        recyclerViewDirections = findViewById(R.id.recyclerViewDirections);
        recyclerViewDirections.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDirections.setHasFixedSize(true);

    }
}