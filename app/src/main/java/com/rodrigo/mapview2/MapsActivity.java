package com.rodrigo.mapview2;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by rodrigo on 30/07/16.
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = ">>>>>>";
    private GoogleMap mMap;
    private Marker marker;
    private GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;
    private AutoCompleteTextView mAutocompleteView;
    private Geocoder geo;
    private List<Address> addresses;
    private static int NVL_ZOOM_SEARCH = 20;
    private static int NVL_ZOOM_START = 14;
    private static final LatLng SAO_PAULO = new LatLng(-23.586950299999998, -46.682218999999996);
    private static final String COMMA = ",";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                                              .enableAutoManage(this, 0, null)
                                              .addApi(Places.GEO_DATA_API)
                                              .addApi(LocationServices.API)
                                              .addConnectionCallbacks(this)
                                              .addOnConnectionFailedListener(this)
                                              .build();

        setContentView(R.layout.activity_maps);
        mAutocompleteView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, null, null);
        mAutocompleteView.setAdapter(mAdapter);

        Button clearButton = (Button) findViewById(R.id.button_clear);
        clearButton.setOnClickListener(eventClearSearch);

        Button save = (Button) findViewById(R.id.confirm_local_place);
        save.setOnClickListener(eventSave);

        geo = new Geocoder(MapsActivity.this.getApplicationContext(), Locale.getDefault());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
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
        mMap = googleMap;
        mMap.setOnMarkerDragListener(eventDrag);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.markerDefault();
        }
    }


    private View.OnClickListener eventClearSearch = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mAutocompleteView.setText("");
        }
    };
    private View.OnClickListener eventSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG+"position",marker.getPosition().toString());
        }
    };


    private void markerDefault(){
        marker = mMap.addMarker(new MarkerOptions().position(SAO_PAULO).draggable(true));
        this.settingsGoogleMapDefault();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SAO_PAULO, NVL_ZOOM_START));
    }
    private GoogleMap.OnMarkerDragListener eventDrag = new GoogleMap.OnMarkerDragListener() {
        @Override
        public void onMarkerDragStart(Marker marker) {
            marker.hideInfoWindow();
        }

        @Override
        public void onMarkerDrag(Marker marker) {
        }

        @Override
        public void onMarkerDragEnd(Marker marker) {
            try {
                LatLng latLngCurrent = marker.getPosition();
                addresses = geo.getFromLocation(latLngCurrent.latitude, latLngCurrent.longitude, 1);
                MapsActivity.this.configureMarker(latLngCurrent,NVL_ZOOM_SEARCH);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final AutocompletePrediction item = mAdapter.getItem(position);
            if(item != null){
                final String placeId = item.getPlaceId();
                final CharSequence primaryText = item.getPrimaryText(null);
                final CharSequence secondaryText = item.getSecondaryText(null);

                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
                mAutocompleteView.setText( MapsActivity.this.formatAddressAutoComplete(primaryText.toString(), secondaryText.toString()));
            }
        }
    };

    private String formatAddressAutoComplete(String primaryText, String secondaryText) {
        if (secondaryText == null) {
            return primaryText;
        }
        if(!secondaryText.contains(COMMA)){
            return primaryText.concat(COMMA).concat(secondaryText);
        }
        return primaryText.concat(COMMA).concat(secondaryText.substring(0, secondaryText.indexOf(",")));
    }
    private String formatLocalityAutoComplete(String locality, String subLocality) {
        String local    = locality    != null ? locality:"";
        String subLocal = subLocality != null ? subLocality:"";
        return local.concat(" - ").concat(subLocal);
    }

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);
            LatLng latLng = place.getLatLng();
            marker.setPosition(latLng);
            marker.setDraggable(true);
            marker.hideInfoWindow();
            MapsActivity.this.configureMarker(latLng,NVL_ZOOM_SEARCH);
            places.release();
        }
    };

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)  {
            return;
        }
        if (marker == null) {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                LatLng myLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                marker = mMap.addMarker(new MarkerOptions().position(myLocation).draggable(true));
                this.configureMarker(myLocation,NVL_ZOOM_START);
                this.settingsGoogleMapDefault();
            } else {
                this.markerDefault();
            }
        }

    }
    @Override
    public void onConnectionSuspended(int i) {
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        this.markerDefault();
    }

    private void settingsGoogleMapDefault(){
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void configureMarker(LatLng latLng , int nvlZoom){
        try {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, nvlZoom));
            addresses = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);
            marker.setTitle(MapsActivity.this.formatLocalityAutoComplete(addresses.get(0).getLocality(),addresses.get(0).getSubLocality()));
            marker.setSnippet(MapsActivity.this.formatAddressAutoComplete(addresses.get(0).getThoroughfare(),addresses.get(0).getSubThoroughfare()));
            marker.showInfoWindow();
        } catch (IOException e) {
        }
    }

}
