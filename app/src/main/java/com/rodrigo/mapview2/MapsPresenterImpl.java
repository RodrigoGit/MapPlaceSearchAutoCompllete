package com.rodrigo.mapview2;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rodrigo.mapview2.util.MapsUtil;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by rodrigo on 01/08/16.
 */

class MapsPresenterImpl implements MapsPresenter {
    private static final String TAG = ">>>>>>";
    private Geocoder geo;
    private GoogleMap mMap;
    private List<Address> addresses;
    private AutoCompleteTextView mAutocompleteView;
    private PlaceAutocompleteAdapter mAdapter;
    private static final LatLng SAO_PAULO = new LatLng(-23.586950299999998, -46.682218999999996);
    private MapsActivity context;
    private Marker marker;

    MapsPresenterImpl(MapsActivity context){
        this.context = context;
    }

    @Override
    public void onCreate() {
        mAutocompleteView = (AutoCompleteTextView) context.findViewById(R.id.autoCompleteTextView);
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        mAdapter = new PlaceAutocompleteAdapter(context, context.getmGoogleApiClient(), null, null);
        mAutocompleteView.setAdapter(mAdapter);
        Button clearButton = (Button) context.findViewById(R.id.button_clear);
        Button save        = (Button) context.findViewById(R.id.confirm_local_place);
        save.setOnClickListener(this.eventSave);
        clearButton.setOnClickListener(this.eventClearSearch);
        geo = new Geocoder(context.getApplicationContext(), Locale.getDefault());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerDragListener(eventDrag);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.markerDefault();
        }
    }

    @Override
    public void onConnected() {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)  {
            return;
        }
        if (marker == null) {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(context.getmGoogleApiClient());
            if (mLastLocation != null) {
                LatLng myLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                marker = mMap.addMarker(new MarkerOptions().position(myLocation).draggable(true));
                this.configureMarker(myLocation,context.getResources().getInteger(R.integer.nvl_zoom_start));
                this.settingsGoogleMapDefault();
            } else {
                this.markerDefault();
            }
        }
    }

    @Override
    public void request() {
        Log.i(TAG+"position",marker.getPosition().toString());
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final AutocompletePrediction item = mAdapter.getItem(position);
            if(item != null){
                final String placeId = item.getPlaceId();
                final CharSequence primaryText = item.getPrimaryText(null);
                final CharSequence secondaryText = item.getSecondaryText(null);

                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(context.getmGoogleApiClient(), placeId);
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
            MapsPresenterImpl.this.configureMarker(latLng,context.getResources().getInteger(R.integer.nvl_zoom_start));
            places.release();
        }
    };


    private void settingsGoogleMapDefault(){
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

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

    void markerDefault(){
        marker = mMap.addMarker(new MarkerOptions().position(SAO_PAULO).draggable(true));
        this.settingsGoogleMapDefault();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SAO_PAULO, context.getResources().getInteger(R.integer.nvl_zoom_start)));
    }

    public View.OnClickListener eventClearSearch = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mAutocompleteView.setText("");
        }
    };
    public View.OnClickListener eventSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MapsPresenterImpl.this.request();
        }
    };

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
                MapsPresenterImpl.this.configureMarker(latLngCurrent, context.getResources().getInteger(R.integer.nvl_zoom_search));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}
