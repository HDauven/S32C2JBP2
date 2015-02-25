/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.storage;

import stamboom.domain.Persoon;
import stamboom.domain.Geslacht;
import stamboom.domain.Gezin;
import stamboom.domain.Administratie;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.junit.*;
import static org.junit.Assert.*;
import stamboom.controller.StamboomController;
import stamboom.domain.DomeinTest;

/**
 *
 * @author Albert Lak, Frank Peeters
 */
public class SerialisatieTest extends TestCase {

    @BeforeClass
    public static void warning() {
        DomeinTest.warning();
    }

    @Test
    public void testOpslag() {
        StamboomController controller = new StamboomController();
        Administratie adm = controller.getAdministratie();
        Persoon piet = adm.addPersoon(Geslacht.MAN, new String[]{"Piet"}, "Swinkels",
                "", new GregorianCalendar(1950, Calendar.APRIL, 23), "Ede", null);

        Persoon teuntje = adm.addPersoon(Geslacht.VROUW, new String[]{"Teuntje"}, "Vries", "de",
                new GregorianCalendar(1949, Calendar.MAY, 5), "Wageningen", null);
        Gezin pietEnTeuntje = adm.addOngehuwdGezin(piet, teuntje);
        adm.setHuwelijk(pietEnTeuntje, new GregorianCalendar(1970, Calendar.MAY, 23));
        Persoon rosa = adm.addPersoon(Geslacht.VROUW, new String[]{"Rosa"}, "Swinkels",
                "", new GregorianCalendar(1975, Calendar.APRIL, 23), "Utrecht", pietEnTeuntje);

        File testOpslag = new File("testOpslag");
        if (testOpslag.exists()) {
            testOpslag.delete();
        }
        try {
            controller.serialize(testOpslag);
        } catch (IOException ex) {
            Logger.getLogger(SerialisatieTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
        assertTrue("Serialisatie onjuist (geen data in bestand)", testOpslag.length() > 0);

        Administratie adm2;
        try {
            controller.deserialize(testOpslag);
        } catch (IOException ex) {
            Logger.getLogger(SerialisatieTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
        adm2 = controller.getAdministratie();
        assertEquals("aantal personen onjuist", adm.aantalGeregistreerdePersonen(), adm2.aantalGeregistreerdePersonen());
        assertEquals("aantal gezinnen onjuist", adm.aantalGeregistreerdeGezinnen(), adm2.aantalGeregistreerdeGezinnen());

        Persoon jan = adm2.addPersoon(Geslacht.MAN, new String[]{"Jan"}, "Boven",
                "van", new GregorianCalendar(1975, Calendar.APRIL, 23), "Bergen Op Zoom", null);
        assertEquals("nummering van personen onjuist", adm.aantalGeregistreerdePersonen() + 1, jan.getNr());
        Gezin rosaEnJan = adm2.addOngehuwdGezin(rosa, jan);
        assertEquals("nummering van gezinnen onjuist", adm.aantalGeregistreerdeGezinnen() + 1, rosaEnJan.getNr());

        assertEquals("aantal personen in observable personen onjuist", adm.aantalGeregistreerdePersonen() + 1,
                adm2.getPersonen().size());
        assertTrue(testOpslag.delete());
    }
}
