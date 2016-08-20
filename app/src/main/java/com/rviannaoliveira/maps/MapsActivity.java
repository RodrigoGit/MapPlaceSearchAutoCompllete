package com.rviannaoliveira.maps;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by rodrigo on 30/07/16.
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
    private MapsPresenterImpl mapsPresenterImpl;
    public static final LatLng SAO_PAULO = new LatLng(-23.586950299999998, -46.682218999999996);
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                                              .enableAutoManage(this, 0, null)
                                              .addApi(Places.GEO_DATA_API)
                                              .addApi(LocationServices.API)
                                              .addConnectionCallbacks(this)
                                              .addOnConnectionFailedListener(this)
                                              .build();

        if(mapsPresenterImpl == null){
            mapsPresenterImpl = new MapsPresenterImpl(this);
        }
        mapsPresenterImpl.onCreate();
        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapsPresenterImpl.onMapReady(googleMap);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mapsPresenterImpl.onConnected();

    }
    @Override
    public void onConnectionSuspended(int i) {
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mapsPresenterImpl.markerDefault();
    }


    GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }
}
