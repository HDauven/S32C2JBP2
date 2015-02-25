package stamboom.domain;

import java.util.Calendar;
import java.util.GregorianCalendar;
import junit.framework.TestCase;
import org.junit.*;

/**
 *
 * @author Albert Lak, Frank Peeters
 */
public class StamboomTest extends TestCase {

    @BeforeClass
    public static void warning() {
        System.out.println(
                "Als een van de testen faalt,\n"
                + "weet je dat je een programmeer-\n"
                + "fout hebt begaan, maar zodra je \n"
                + "alle testen met goed gevolg hebt \n"
                + "doorstaan, heb je helaas geen \n"
                + "100% zekerheid dat de geteste \n"
                + "onderdelen uit je programma \n"
                + "foutloos zijn.\n\n");
    }

    @Test
    public void testStamboom() {
        Administratie adm = new Administratie();
        Persoon piet = adm.addPersoon(Geslacht.MAN, new String[]{"Piet"}, "Swinkels",
                "", new GregorianCalendar(1924, Calendar.APRIL, 23), "Den Haag", null);
        Persoon teuntje = adm.addPersoon(Geslacht.VROUW, new String[]{"Teuntje"}, "Vries", "de",
                new GregorianCalendar(1927, Calendar.MAY, 5), "Doesburg", null);
        Gezin teuntjeEnPiet = adm.addOngehuwdGezin(teuntje, piet);
        Persoon gijs = adm.addPersoon(Geslacht.MAN, new String[]{"Gijs", "Jozef"}, "Swinkels",
                "", new GregorianCalendar(1944, Calendar.APRIL, 21), "Geldrop", teuntjeEnPiet);
        Persoon ferdinand = adm.addPersoon(Geslacht.MAN, new String[]{"Ferdinand", "Karel", "Helene"}, "Vuiter", "de",
                new GregorianCalendar(1901, Calendar.JULY, 14), "Amsterdam", null);
        Persoon annalouise = adm.addPersoon(Geslacht.VROUW, new String[]{"Annalouise", "Isabel", "Teuntje"}, "Vuiter", "de",
                new GregorianCalendar(1902, Calendar.OCTOBER, 1), "Amsterdam", null);
        Gezin ferdinandEnAnnalouise = adm.addHuwelijk(ferdinand, annalouise,
                new GregorianCalendar(1921, Calendar.MAY, 5));
        Persoon louise = adm.addPersoon(Geslacht.VROUW, new String[]{"Louise", "Isabel", "Helene"}, "Vuiter", "de",
                new GregorianCalendar(1927, Calendar.JANUARY, 15), "Amsterdam", ferdinandEnAnnalouise);
        Gezin louiseAlleen = adm.addOngehuwdGezin(louise, null);
        Persoon mary = adm.addPersoon(Geslacht.VROUW, new String[]{"mary"}, "Vuiter",
                "de", new GregorianCalendar(1943, Calendar.MAY, 25), "Rotterdam", louiseAlleen);
        Gezin gijsEnMary = adm.addOngehuwdGezin(gijs, mary);
        Persoon jaron = adm.addPersoon(Geslacht.MAN, new String[]{"Jaron"}, "Swinkels",
                "", new GregorianCalendar(1962, Calendar.JULY, 22), "Velp", gijsEnMary);

        assertEquals("afmeting boom onjuist", 8, jaron.afmetingStamboom());
        String stamboomstring = jaron.stamboomAlsString();
        String[] regels = stamboomstring.split(System.getProperty("line.separator"));
        assertEquals("aantal regels", 8, regels.length);
        assertEquals("regel 3 onjuist", "    T. de Vries (VROUW) 5-5-1927", regels[2]);

        System.out.println(stamboomstring);
    }
}
