package com.rviannaoliveira.maps;

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
