package com.rviannaoliveira.maps;

import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Criado por rodrigo on 29/01/17.
 */

class VMapsEventHelper {
    private VMapsActivity context;
    GoogleMap.OnInfoWindowClickListener eventSaveMarker =  new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                context.save();
            }
    };
    ResultCallback<PlaceBuffer> updatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e("Place query: ", places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);
            LatLng latLng = place.getLatLng();
            context.getMarker().setPosition(latLng);
            context.getMarker().setDraggable(true);
            context.getMarker().hideInfoWindow();
            context.configureMarker(latLng, context.getResources().getInteger(R.integer.nvl_zoom_start));
            places.release();
        }
    };
    private GoogleMap map;
    GoogleMap.OnMapLongClickListener eventOnLongClick = new GoogleMap.OnMapLongClickListener() {
        @Override
        public void onMapLongClick(LatLng latLng) {
            context.getMarker().remove();
            context.setMarker(map.addMarker(new MarkerOptions().position(latLng)));
            context.configureMarker(latLng, context.getResources().getInteger(R.integer.nvl_zoom_search));
        }
    };
    private boolean myLocation;
    GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if(myLocation){
                    myLocation = false;
                    LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
                    context.getMarker().remove();
                    context.setMarker(map.addMarker(new MarkerOptions().position(loc)));
                }
            }
        };
    GoogleMap.OnMyLocationButtonClickListener eventMyLocationButton = new GoogleMap.OnMyLocationButtonClickListener() {
        @Override
        public boolean onMyLocationButtonClick() {
            myLocation = true;
            return false;
        }
    };

    VMapsEventHelper(VMapsActivity mapsActivity, GoogleMap googleMap) {
        this.context = mapsActivity;
        this.map = googleMap;
    }

}
