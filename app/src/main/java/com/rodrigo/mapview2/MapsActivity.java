package com.rodrigo.mapview2;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
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
    private LatLng latLng;
    private AutoCompleteTextView mAutocompleteView;
    private Geocoder geo;
    private List<Address> addresses;
    private static int NVL_ZOOM_SEARCH = 20;
    private static int NVL_ZOOM_START = 14;
    private static final LatLng SAO_PAULO = new LatLng(-23.3250, -46.3809);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, null)
                .addApi(Places.GEO_DATA_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(AppIndex.API).build();

        setContentView(R.layout.activity_maps);
        mAutocompleteView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, null, null);
        mAutocompleteView.setAdapter(mAdapter);

        Button clearButton = (Button) findViewById(R.id.button_clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutocompleteView.setText("");
            }
        });
        Button save = (Button) findViewById(R.id.confirm_local_place);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG+"position",marker.getPosition().toString());
            }
        });

        geo = new Geocoder(MapsActivity.this.getApplicationContext(), Locale.getDefault());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(event);
        mMap.setOnMarkerDragListener(eventDrag);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            marker = mMap.addMarker(new MarkerOptions().position(SAO_PAULO).draggable(true));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SAO_PAULO, NVL_ZOOM_START));
            return;
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
                String locationNeighboord = addresses.get(0).getLocality() + " - " + addresses.get(0).getSubLocality();
                String addressName = addresses.get(0).getThoroughfare() + " - " + addresses.get(0).getSubThoroughfare();

                marker.setTitle(locationNeighboord);
                marker.setSnippet(addressName);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                marker.showInfoWindow();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
    private GoogleMap.OnMarkerClickListener event = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            return false;
        }
    };

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);
            final CharSequence secudaryText = item.getSecondaryText(null);
            String textAutoComplete = MapsActivity.this.formatAddressAutoComplete(primaryText.toString(), secudaryText.toString());

            Log.i(TAG, "Autocomplete item selected: " + textAutoComplete);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            mAutocompleteView.setText(textAutoComplete);
        }
    };

    private String formatAddressAutoComplete(String primaryText, String secudaryText) {
        if (secudaryText == null) {
            return primaryText;
        }
        return primaryText.concat(",").concat(secudaryText.substring(0, secudaryText.indexOf(",")));
    }
    private String formatLocalityAutoComplete(String locality, String subLocality) {
        String local    = locality    != null ? locality:"";
        String subLocal = subLocality != null ? subLocality:"";
        return local.concat(" - ").concat(subLocal);
    }

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, NVL_ZOOM_SEARCH));
            final Place place = places.get(0);
            latLng = place.getLatLng();
            marker.setPosition(latLng);
            marker.setDraggable(true);
            marker.hideInfoWindow();
            marker.showInfoWindow();
            places.release();
        }
    };

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        AppIndex.AppIndexApi.start(mGoogleApiClient, getIndexApiAction());
        super.onStart();
    }

    @Override
    public void onStop() {
        AppIndex.AppIndexApi.end(mGoogleApiClient, getIndexApiAction());
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)  {
            return;
        }

        try {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LatLng mylocation = null;

            if (marker == null) {
                if (mLastLocation != null) {
                    mylocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    addresses = geo.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                } else {
                    addresses = geo.getFromLocation(SAO_PAULO.latitude, SAO_PAULO.longitude, 1);
                }
                String locationNeighboord = addresses.get(0).getLocality() + " - " + addresses.get(0).getSubLocality();
                String addressName = addresses.get(0).getThoroughfare() + " - " + addresses.get(0).getSubThoroughfare();
                marker = mMap.addMarker(new MarkerOptions().position(mylocation).draggable(true));
                marker.setTitle(locationNeighboord);
                marker.setSnippet(addressName);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, NVL_ZOOM_START));
                mMap.setMyLocationEnabled(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onConnectionSuspended(int i) {
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}
