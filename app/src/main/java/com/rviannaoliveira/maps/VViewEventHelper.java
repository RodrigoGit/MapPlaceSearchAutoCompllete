package com.rviannaoliveira.maps;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

/**
 * Criado por rodrigo on 29/01/17.
 */
class VViewEventHelper {

    private final VMapsActivity context;
    View.OnClickListener eventSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            context.save();
        }
    };

    VViewEventHelper(VMapsActivity context) {
        this.context = context;
    }

    AdapterView.OnItemClickListener autocompleteClickListener(final VPlaceAutocompleteAdapter adapter, final VMapsEventHelper mapsHelper,
                                                              final AutoCompleteTextView autoCompleteTextView, final GoogleApiClient googleApiClient){
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final AutocompletePrediction item = adapter.getItem(position);
                if(item != null){
                    final String placeId = item.getPlaceId();
                    final CharSequence primaryText = item.getPrimaryText(null);
                    final CharSequence secondaryText = item.getSecondaryText(null);

                    PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(googleApiClient, placeId);
                    placeResult.setResultCallback(mapsHelper.updatePlaceDetailsCallback);
                    autoCompleteTextView.setText(VMapsUtil.formatAddressAutoComplete(primaryText.toString(), secondaryText.toString()));
                }
            }
        };
    }

    View.OnClickListener eventClearSearch (final AutoCompleteTextView autoCompleteTextView) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteTextView.setText("");
            }
        };
    }

}
