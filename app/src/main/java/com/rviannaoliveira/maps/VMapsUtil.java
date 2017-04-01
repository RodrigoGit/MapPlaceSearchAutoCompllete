package com.rviannaoliveira.maps;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
public class VMapsUtil {
    private static final String COMMA = ",";

    private VMapsUtil() {
    }

    /**
     * Fomart with comma
     *
     * @param primaryText
     * @param secondaryText
     * @return
     */
    public static String formatAddressAutoComplete(String primaryText, String secondaryText) {
        if (secondaryText == null) {
            return primaryText;
        }
        if(!secondaryText.contains(COMMA)){
            return primaryText.concat(COMMA).concat(secondaryText);
        }
        return primaryText.concat(COMMA).concat(secondaryText.substring(0, secondaryText.indexOf(",")));
    }

    /**
     * Format with hifen
     *
     * @param locality
     * @param subLocality
     * @return
     */
    public static String formatLocalityAutoComplete(String locality, String subLocality) {
        String local    = locality    != null ? locality:"";
        String subLocal = subLocality != null ? subLocality:"";
        return local.concat(" - ").concat(subLocal);
    }

    /**
     * Permission Location Near with Activity
     * @param content
     */
    public static void permissionLocationNear(Activity content) {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(content, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(content, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(content, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, VMapsActivity.MARKER_COARSE);
        }
    }

    /**
     * Permission location near with AppCompatActivity
     * @param content
     */
    public static void permissionLocationNear(AppCompatActivity content) {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(content, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(content, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(content, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, VMapsActivity.MARKER_COARSE);
        }
    }

    /**
     * Get List of addresses using Geocoder using Bundle
     * @param context
     * @param bundle
     * @return
     */
    public static List<Address> getAddresses(Context context, Bundle bundle) {
        try {
            double latitude = Double.parseDouble(String.valueOf(bundle.get(VMapsActivity.LATITUDE)));
            double longitude = Double.parseDouble(String.valueOf(bundle.get(VMapsActivity.LONGITUDE)));
            Geocoder geo = new Geocoder(context.getApplicationContext(), Locale.getDefault());
            return  geo.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get List of addresses using Geocoder using LatLng
     * @param context
     * @param latLng
     * @return
     */
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

    /**
     * return Object Latitude e Longitude
     * @param bundle
     * @return
     */
    public static LatLng getLatLgn(Bundle bundle) {
        double latitude = Double.parseDouble(String.valueOf(bundle.get(VMapsActivity.LATITUDE)));
        double longitude = Double.parseDouble(String.valueOf(bundle.get(VMapsActivity.LONGITUDE)));
        return  new LatLng(latitude,longitude);
    }


    /**
     * Verify connection avaiable
     * @param context
     * @return
     */
    static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
