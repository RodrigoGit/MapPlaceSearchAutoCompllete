package com.rviannaoliveira.maps;

import android.location.Location;

/**
 * Criado por rodrigo on 29/01/17.
 */

class VMapsPresenterImpl implements VMapsPresenter {

    private VMapsView mapsView;

    VMapsPresenterImpl(VMapsView mapView) {
        this.mapsView = mapView;
    }

    @Override
    public void setup() {
        mapsView.setupMap();
        mapsView.setupView();
    }

    @Override
    public void setupMarker(Location lastLocation) {
        if (lastLocation != null) {
            mapsView.markerLastLocation(lastLocation);
            mapsView.settingsGoogleMapDefault();
        } else {
            mapsView.createMapDefault();
        }
    }
}
