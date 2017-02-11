package com.rviannaoliveira.maps;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Criado por rodrigo on 04/08/16.
 */


@SuppressWarnings("WeakerAccess")
public class MapsUtil {
    private static final String COMMA = ",";

    private MapsUtil(){}
    public static String formatAddressAutoComplete(String primaryText, String secondaryText) {
        if (secondaryText == null) {
            return primaryText;
        }
        if(!secondaryText.contains(COMMA)){
            return primaryText.concat(COMMA).concat(secondaryText);
        }
        return primaryText.concat(COMMA).concat(secondaryText.substring(0, secondaryText.indexOf(",")));
    }
    public static String formatLocalityAutoComplete(String locality, String subLocality) {
        String local    = locality    != null ? locality:"";
        String subLocal = subLocality != null ? subLocality:"";
        return local.concat(" - ").concat(subLocal);
    }


    public static void permissionLocationNear(Activity content) {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(content, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(content, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(content, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MapsActivity.MARKER_COARSE);
        }
    }

    public static void permissionLocationNear(AppCompatActivity content) {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(content, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(content, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(content, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MapsActivity.MARKER_COARSE);
        }
    }
    public static List<Address> getAddresses(Context context, Bundle bundle) {
        try {
            double latitude  = Double.parseDouble(String.valueOf(bundle.get(MapsActivity.LATITUDE)));
            double longitude = Double.parseDouble(String.valueOf(bundle.get(MapsActivity.LONGITUDE)));
            Geocoder geo = new Geocoder(context.getApplicationContext(), Locale.getDefault());
            return  geo.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Address> getAddresses(Context context, LatLng latLng) {
        try {
            if(latLng == null || context == null){
                return  null;
            }
            double latitude  = Double.parseDouble(String.valueOf(latLng.latitude));
            double longitude = Double.parseDouble(String.valueOf(latLng.longitude));
            Geocoder geo = new Geocoder(context.getApplicationContext(), Locale.getDefault());
            return  geo.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static LatLng getLatLgn(Bundle bundle) {
        double latitude  = Double.parseDouble(String.valueOf(bundle.get(MapsActivity.LATITUDE)));
        double longitude = Double.parseDouble(String.valueOf(bundle.get(MapsActivity.LONGITUDE)));
        return  new LatLng(latitude,longitude);
    }

}
