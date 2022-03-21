package com.hitec.directions.views.map

import android.app.Notification
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.hitec.directions.R
import com.mapbox.common.MapboxSDKCommon.getContext
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.extension.style.image.image
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager

class MarkerHelperClass {

    var mapView: MapView? = null
    var context: Context
    var lat: Double = 0.0;
    var long: Double = 0.0

    constructor(context: Context){
        this.context = context
    }

    constructor(context: Context,mapView: MapView?, lat:Double, long: Double) {
        this.context = context
        this.mapView = mapView
        this.lat = lat;
        this.long = long;
    }


    fun setDestinationMarker(){
        LATITUDE = lat
        LONGITUDE = long

        println("destination marker: long: $LONGITUDE lat: $LATITUDE")

        mapView?.getMapboxMap().also {
            it!!.setCamera(
                CameraOptions.Builder()
                    .center(Point.fromLngLat(LONGITUDE, LATITUDE))
                    .zoom(9.0)
                    .build()
            )
        }!!.loadStyle(
            styleExtension = style(Style.MAPBOX_STREETS) {
            // prepare blue marker from resources
                +image(RED_ICON_ID) {
                    bitmap(BitmapFactory.decodeResource(getContext().resources, R.drawable.red_marker))
                }
                +geoJsonSource(SOURCE_ID) {
                    geometry(Point.fromLngLat(LONGITUDE, LATITUDE))
                }
                +symbolLayer(LAYER_ID, SOURCE_ID) {
                    iconImage(RED_ICON_ID)
                    iconAnchor(IconAnchor.BOTTOM)
                }
            }
        )
    }


    companion object {
        private const val RED_ICON_ID = "red"
        private const val SOURCE_ID = "source_id"
        private const val LAYER_ID = "layer_id"
        private var LATITUDE = 0.0
        private var LONGITUDE = 0.0
    }


}