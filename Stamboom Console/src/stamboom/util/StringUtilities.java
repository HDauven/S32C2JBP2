/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class StringUtilities {

    public static Calendar datum(String datumaanduiding) throws IllegalArgumentException {
        if (datumaanduiding == null || datumaanduiding.isEmpty()) {
            return null;
        }
        try {
            String[] datumSplit = datumaanduiding.split("-");
            if (datumSplit.length != 3) {
                throw new IllegalArgumentException("Datum bestaat niet uit drie delen");
            }

            int dag = Integer.parseInt(datumSplit[0]);
            if (dag <= 0 || dag > 31) {
                throw new IllegalArgumentException("Ongeldige dag");
            }

            int maand = Integer.parseInt(datumSplit[1]);
            if (maand <= 0 || maand > 12) {
                throw new IllegalArgumentException("Ongeldige maand");
            }

            int jaar = Integer.parseInt(datumSplit[2]);

            // maanden zijn binnen GregorianCalendar van 0 t/m 11 gecodeerd:
            return new GregorianCalendar(jaar, maand - 1, dag);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("dag, maand of jaar heeft niet het juiste getalsformat");
        }
    }

    public static String datumString(Calendar datum) {
        if(datum == null){
            return null;
        }
        return datum.get(Calendar.DAY_OF_MONTH) + "-"
                + (datum.get(Calendar.MONTH) + 1) + "-"
                + datum.get(Calendar.YEAR);
    }
    
    static final String SPATIES = "                                                                  ";

    public static String spaties(int nr) {
        if (nr > SPATIES.length()) {
            return SPATIES;
        }
        return SPATIES.substring(0, nr);
    }

    public static String withFirstCapital(String text) {
        if (text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}
