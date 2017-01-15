package com.rviannaoliveira.maps;

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
}
