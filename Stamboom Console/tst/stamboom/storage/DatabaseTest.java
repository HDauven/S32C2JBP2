/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.storage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import stamboom.controller.StamboomController;
import stamboom.domain.Administratie;
import stamboom.domain.Geslacht;
import stamboom.domain.Gezin;
import stamboom.domain.Persoon;
import stamboom.util.StringUtilities;

/**
 *
 * @author Albert Lak en Frank Peeters
 */
public class DatabaseTest {

    private StamboomController controller;

    public DatabaseTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        controller = new StamboomController();
    }

    @Test
    public void testSaveToDatabase() throws IOException {

        Administratie adm = controller.getAdministratie();
        Persoon piet1, teuntje2;
        Gezin pietEnTeuntje;

        piet1 = adm.addPersoon(Geslacht.MAN, new String[]{"Piet"}, "Swinkels",
                "", new GregorianCalendar(1950, Calendar.APRIL, 23), "Uden", null);
        teuntje2 = adm.addPersoon(Geslacht.VROUW, new String[]{"Teuntje"}, "Vries", "de",
                new GregorianCalendar(1949, Calendar.MAY, 5), "Venlo", null);

        pietEnTeuntje = adm.addOngehuwdGezin(piet1, teuntje2);
        adm.setHuwelijk(pietEnTeuntje, new GregorianCalendar(1970, Calendar.MAY, 23));
        adm.setScheiding(pietEnTeuntje,
                new GregorianCalendar(1985, Calendar.NOVEMBER, 5));
        Persoon tom3 = adm.addPersoon(Geslacht.MAN, new String[]{"tom", "JACOBUS"}, "sWinkelS",
                "", new GregorianCalendar(1971, Calendar.APRIL, 13), " Breda",
                pietEnTeuntje);

        Persoon anja4 = adm.addPersoon(Geslacht.VROUW, new String[]{"Anja"}, "Bok",
                "de", new GregorianCalendar(1972, Calendar.JUNE, 5), "Bakel", null);

        Gezin tomEnAnja = adm.addOngehuwdGezin(tom3, anja4);

        Gezin pietAlleen = adm.addOngehuwdGezin(piet1, null);

        assertEquals("aantal personen onjuist", 4, adm.aantalGeregistreerdePersonen());
        assertEquals("aantal gezinnen onjuist", 3, adm.aantalGeregistreerdeGezinnen());

        controller.saveToDatabase();

    }

    @Test
    public void testLoadFromDataBase() throws IOException {

        controller.loadFromDatabase();
        Administratie adm = controller.getAdministratie();

        Persoon piet = adm.getPersoon(1);
        Persoon teuntje = adm.getPersoon(2);
        Persoon tom = adm.getPersoon(3);
        Persoon anja = adm.getPersoon(4);
        assertEquals("aantal personen in database onjuist", 4, adm.aantalGeregistreerdePersonen());
        ArrayList<Persoon> swinkelsen = adm.getPersonenMetAchternaam("Swinkels");
        assertEquals("aantal personen met Swinkels-achternaam onjuist", 2, swinkelsen.size());
        assertEquals("initialen onjuist", "P.", piet.getInitialen());
        assertEquals("gebdat onjuist", "5-5-1949", StringUtilities.datumString(teuntje.getGebDat()));
        assertEquals("geslacht onjuist", Geslacht.MAN, tom.getGeslacht());
        assertEquals("achternaam onjuist", "Bok", anja.getAchternaam());
        Gezin pietEnTeuntje = adm.getGezin(1);
        Gezin pietAlleen = adm.getGezin(3);
        assertNotNull("gezinnen piet onjuist", pietEnTeuntje);
        assertNotNull("gezinnen piet onjuist", pietAlleen);
        assertEquals("gezinnen piet onjuist", 2, piet.getAlsOuderBetrokkenIn().size());
        assertEquals("gezinnen teuntje onjuist", 1, teuntje.getAlsOuderBetrokkenIn().size());
        assertSame("gezinnen niet hetzelfde", pietEnTeuntje, teuntje.getAlsOuderBetrokkenIn().get(0));

        assertSame("kind onjuist", tom, pietEnTeuntje.getKinderen().get(0));
        assertEquals("aantal kinderen onjuist", 1, pietEnTeuntje.aantalKinderen());
        assertTrue("niet alleenstaand", pietAlleen.isOngehuwd());
        assertSame(piet, pietAlleen.getOuder1());
        assertSame(piet, pietEnTeuntje.getOuder1());
        assertSame(teuntje, pietEnTeuntje.getOuder2());
        assertEquals("huwelijk niet geregistreerd", new GregorianCalendar(1970, Calendar.MAY, 23),
                pietEnTeuntje.getHuwelijksdatum());
        assertEquals("scheiding niet geregistreerd", new GregorianCalendar(1985, Calendar.NOVEMBER, 5),
                pietEnTeuntje.getScheidingsdatum());

        Gezin tomEnAnja = adm.getGezin(2);
        assertSame(tomEnAnja, anja.getAlsOuderBetrokkenIn().get(0));
        assertNull(tomEnAnja.getHuwelijksdatum());
        assertTrue(tomEnAnja.aantalKinderen() == 0);

        Persoon jan = adm.addPersoon(Geslacht.MAN, new String[]{"Jan"}, "Boven",
                "van", new GregorianCalendar(1953, Calendar.APRIL, 23), "Amsterdam", null);

        assertEquals("numering personen niet meer juist", 5, jan.getNr());
        Gezin janEnTeuntje = adm.addOngehuwdGezin(jan, teuntje);
        assertEquals("nummering gezinnen niet meer juist", 4, janEnTeuntje.getNr());
    }

}
