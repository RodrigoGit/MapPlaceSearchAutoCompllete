package com.rviannaoliveira.maps;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;

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
 * Criado por rodrigo on 30/07/16.
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
    public static final LatLng SAO_PAULO = new LatLng(-23.586950299999998, -46.682218999999996);
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final int MARKER_COARSE = 123;
    private static final String TAG = ">>>>>>";
    private Geocoder geo;
    private GoogleMap mMap;
    private List<Address> addresses;
    private AutoCompleteTextView mAutocompleteView;
    private PlaceAutocompleteAdapter mAdapter;
    private Marker marker;

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

        mAutocompleteView = (AutoCompleteTextView) this.findViewById(R.id.autoCompleteTextView);
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        mAdapter = new PlaceAutocompleteAdapter(this, this.getmGoogleApiClient(), null, null);
        mAutocompleteView.setAdapter(mAdapter);
        Button clearButton = (Button) this.findViewById(R.id.button_clear);
        Button save        = (Button) this.findViewById(R.id.confirm_local_place);
        save.setOnClickListener(eventSave);
        clearButton.setOnClickListener(eventClearSearch);
        geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());

        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, MARKER_COARSE);
        }
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

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)  {
            return;
        }
        if (marker == null) {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(this.getmGoogleApiClient());
            if (mLastLocation != null) {
                LatLng myLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                marker = mMap.addMarker(new MarkerOptions().position(myLocation).draggable(true));
                this.configureMarker(myLocation, this.getResources().getInteger(R.integer.nvl_zoom_start));
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


    GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MARKER_COARSE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission
                    .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                    .checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            locationButton();
        }
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
                MapsActivity.this.configureMarker(latLngCurrent, getResources().getInteger(R.integer.nvl_zoom_search));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener eventClearSearch = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mAutocompleteView.setText("");
        }
    };
    private View.OnClickListener eventSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent mIntent = new Intent();
            Bundle mBundle = new Bundle();
            mBundle.putString(MapsActivity.LATITUDE, String.valueOf(marker.getPosition().latitude));
            mBundle.putString(MapsActivity.LONGITUDE, String.valueOf(marker.getPosition().longitude));
            mIntent.putExtras(mBundle);
            setResult(Activity.RESULT_OK, mIntent);
            finish();
        }
    };
    private void configureMarker(LatLng latLng , int nvlZoom){
        try {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, nvlZoom));
            addresses = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);
            marker.setTitle(MapsUtil.formatLocalityAutoComplete(addresses.get(0).getLocality(),addresses.get(0).getSubLocality()));
            marker.setSnippet(MapsUtil.formatAddressAutoComplete(addresses.get(0).getThoroughfare(),addresses.get(0).getSubThoroughfare()));
            marker.showInfoWindow();
        } catch (IOException e) {
            Log.i(TAG,e.getMessage());
        }
    }

    private void markerDefault(){
        if(mMap != null){
            marker = mMap.addMarker(new MarkerOptions().position(MapsActivity.SAO_PAULO).draggable(true));
            this.settingsGoogleMapDefault();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MapsActivity.SAO_PAULO, this.getResources().getInteger(R.integer.nvl_zoom_start)));
        }
    }

    private void settingsGoogleMapDefault(){
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMyLocationChangeListener(eventMyLocationChangeListener);
        locationButton();
    }

    private GoogleMap.OnMyLocationChangeListener eventMyLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            marker = mMap.addMarker(new MarkerOptions().position(loc));
            if(mMap != null){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
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

                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(getmGoogleApiClient(), placeId);
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
                mAutocompleteView.setText( MapsUtil.formatAddressAutoComplete(primaryText.toString(), secondaryText.toString()));
            }
        }
    };

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
            MapsActivity.this.configureMarker(latLng,getResources().getInteger(R.integer.nvl_zoom_start));
            places.release();
        }
    };


    public void locationButton() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mMap.setMyLocationEnabled(true);
        View locationButton = ((View) this.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(0, 250, 180, 0);
        locationButton.setLayoutParams(rlp);
    }
}
