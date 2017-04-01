package com.rviannaoliveira.maps;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Criado por rodrigo on 29/01/17.
 */

interface VMapsView {
    void setupMap();
    void setupView();
    void createMapDefault();
    void markerLastLocation(Location myLocation);
    void configureMarker(LatLng latLngCurrent, int integer);
    void settingsGoogleMapDefault();
}
