package com.hitec.directions.utils

import android.content.Context
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.hitec.directions.R
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import com.mapbox.maps.CameraOptions
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.location

class MapListeners(var mapView: MapView, var context: Context) {
    var onIndicatorBearingChangedListener: OnIndicatorBearingChangedListener
    var onIndicatorPositionChangedListener: OnIndicatorPositionChangedListener
    var onMoveListener: OnMoveListener

    fun initLocationComponent() {
        val locationComponentPlugin = mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(

                bearingImage = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_person_pin_circle_60),

                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
    }

    fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun onCameraTrackingDismissed() {
        //Toast.makeText(context, "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show()
        mapView.location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.location.removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    init {
        onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener { v ->
            mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(v).build())
        }

        onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener { point ->
            mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(point).build())
            mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(point)
        }

        onMoveListener = object : OnMoveListener {
            override fun onMoveBegin(detector: MoveGestureDetector) {
                onCameraTrackingDismissed()
            }

            override fun onMove(detector: MoveGestureDetector): Boolean {
                return false
            }

            override fun onMoveEnd(detector: MoveGestureDetector) {}
        }
    }
}