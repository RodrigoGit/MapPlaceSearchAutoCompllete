package com.rviannaoliveira.maps;

import android.location.Location;

/**
 * Criado por rodrigo on 29/01/17.
 */

interface MapsPresenter {
    void setup();
    void setupMarker(Location location);
}
