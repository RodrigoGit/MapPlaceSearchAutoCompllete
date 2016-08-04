package com.rodrigo.mapview2;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by rodrigo on 01/08/16.
 */

interface MapsPresenter {
    void onCreate();
    void onMapReady(GoogleMap googleMap);
    void onConnected();
    void request();
}
