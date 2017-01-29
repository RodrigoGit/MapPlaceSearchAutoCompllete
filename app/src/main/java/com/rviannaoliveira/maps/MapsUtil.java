package com.rviannaoliveira.maps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Criado por rodrigo on 04/08/16.
 */

class MapsUtil {
    private static final String COMMA = ",";

    static String formatAddressAutoComplete(String primaryText, String secondaryText) {
        if (secondaryText == null) {
            return primaryText;
        }
        if(!secondaryText.contains(COMMA)){
            return primaryText.concat(COMMA).concat(secondaryText);
        }
        return primaryText.concat(COMMA).concat(secondaryText.substring(0, secondaryText.indexOf(",")));
    }
    static String formatLocalityAutoComplete(String locality, String subLocality) {
        String local    = locality    != null ? locality:"";
        String subLocal = subLocality != null ? subLocality:"";
        return local.concat(" - ").concat(subLocal);
    }

    static void permissionLocationNear(MapsActivity content) {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(content, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(content, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(content, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MapsActivity.MARKER_COARSE);
        }
    }
}
