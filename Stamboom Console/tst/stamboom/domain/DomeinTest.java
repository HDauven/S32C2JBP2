package stamboom.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import stamboom.util.StringUtilities;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import stamboom.controller.StamboomController;

/**
 *
 * @author Frank Peeters
 */
public class DomeinTest extends TestCase{

    private StamboomController controller;
    private Administratie adm;
    private Persoon piet, teuntje;
    private Gezin pietEnTeuntje;
    private Calendar nu;

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

    @Before
    public void setUp() {
        controller = new StamboomController();
        adm = controller.getAdministratie();
        piet = adm.addPersoon(Geslacht.MAN, new String[]{"Piet", "Franciscus"}, "Swinkels",
                "", new GregorianCalendar(1950, Calendar.APRIL, 23), "ede", null);
        teuntje = adm.addPersoon(Geslacht.VROUW, new String[]{"Teuntje"}, "Vries", "de",
                new GregorianCalendar(1949, Calendar.MAY, 5), "Amersfoort", null);
        pietEnTeuntje = adm.addHuwelijk(piet, teuntje, new GregorianCalendar(1970, Calendar.MAY, 23));
        nu = GregorianCalendar.getInstance();
    }

    /**
     * Test van addPersoon methode in class Administratie.
     */
    @Test
    public void testAddPersoon() {
        /* er wordt een persoon met een gegeven geslacht, met als voornamen vnamen,
         * achternaam anaam, tussenvoegsel tvoegsel, geboortedatum gebdat,
         * geboorteplaats gebplaats en een gegeven ouderlijk gezin;*/
        assertEquals("geslacht onjuist", Geslacht.VROUW, teuntje.getGeslacht());
        assertEquals("voornamen onjuist", "Teuntje", teuntje.getVoornamen());
        assertEquals("voornamen onjuist", "Piet Franciscus", piet.getVoornamen());
        assertEquals("tussenvoegsel onjuist", "", piet.getTussenvoegsel());
        assertEquals("tussenvoegsel onjuist", "de", teuntje.getTussenvoegsel());
        assertEquals("geboortedatum onjuist", "23-4-1950",
                StringUtilities.datumString(piet.getGebDat()));
        assertEquals("geboorteplaats onjuist", "Ede", piet.getGebPlaats());
        assertEquals("ouderlijk gezin onjuist", null, piet.getOuderlijkGezin());

        Persoon jan = adm.addPersoon(Geslacht.MAN, new String[]{"Jan", "Jacobus"}, "Peters",
                "", new GregorianCalendar(1980, Calendar.APRIL, 23), "Venray", pietEnTeuntje);
        assertEquals("ouderlijk gezin onjuist", pietEnTeuntje, jan.getOuderlijkGezin());

        /* de persoon
         * krijgt een uniek nummer toegewezen de persoon is voortaan ook bij het
         * ouderlijk gezin bekend.
         */
        assertEquals("persoonsnummering onjuist", 1, piet.getNr());
        assertEquals("persoonsnummering onjuist", 3, jan.getNr());
        assertFalse("kind niet bij ouderlijk gezin geregistreerd", pietEnTeuntje.getKinderen().isEmpty());
        assertEquals("kind niet bij ouderlijk gezin geregistreerd", jan, pietEnTeuntje.getKinderen().get(0));


        /* Voor de voornamen, achternaam en gebplaats geldt
         * dat de eerste letter naar een hoofdletter en de resterende letters naar
         * een kleine letter zijn geconverteerd; het tussenvoegsel is zo nodig in
         * zijn geheel geconverteerd naar kleine letters.*/
        Persoon tom = adm.addPersoon(Geslacht.MAN, new String[]{"tom", "JACOBUS"}, "sWinkelS",
                "VaN deR", new GregorianCalendar(1971, Calendar.APRIL, 13), "venLO",
                pietEnTeuntje);
        ArrayList<Persoon> swinkelsen = adm.getPersonenMetAchternaam("Swinkels");
        assertEquals("zoeken op achternaam onjuist", 2, swinkelsen.size());
        assertEquals("voornamen niet correct weergegeven", "Tom Jacobus", tom.getVoornamen());
        assertEquals("achternaam niet correct weergegeven", "Swinkels", tom.getAchternaam());
        assertEquals("geboorteplaats niet correct weergegeven", "Venlo", tom.getGebPlaats());
        assertEquals("tussenvoegsel niet correct weergegeven", "van der", tom.getTussenvoegsel());

        /* @return als de persoon al bekend was (op basis van combinatie van naam,
         * geboorteplaats en geboortedatum), wordt er null
         * geretourneerd, anders de nieuwe persoon*/
        Persoon tom2 = adm.addPersoon(Geslacht.VROUW, new String[]{"t", "J"}, "sWinkelS",
                "VaN deR", new GregorianCalendar(1971, Calendar.APRIL, 13), "venLO",
                null);
        assertNull("unieke identificatie persoon onjuist", tom2);
        List<Persoon> kinderen = pietEnTeuntje.getKinderen();
        assertEquals("aantal kinderen onjuist", 2, kinderen.size());
        assertEquals("eerste kind ontbreekt", jan, kinderen.get(0));
        assertEquals("tweede kind ontbreekt", tom, kinderen.get(1));
    }

    @Test
    public void testGetNaam() {
        /**
         *
         * @return de initialen gevolgd door een eventueel tussenvoegsel en
         * afgesloten door de achternaam
         */
        assertEquals("naam incorrect", "P.F. Swinkels", piet.getNaam());
        assertEquals("naam incorrect", "T. de Vries", teuntje.getNaam());
    }

    @Test
    public void testSetOuders() {
        /**
         * als het ouderlijk gezin van deze persoon nog onbekend is dan wordt
         * deze persoon een kind van ouderlijkGezin en tevens wordt deze persoon
         * als kind in dat gezin geregistreerd
         */
        Persoon mark = adm.addPersoon(Geslacht.MAN, new String[]{"Markus", "Anna"}, "sWinkelS",
                "", new GregorianCalendar(1986, Calendar.APRIL, 13), "venLO",
                null);
        int aantalKinderen = pietEnTeuntje.aantalKinderen();
        mark.setOuders(pietEnTeuntje);
        assertEquals("ouders onbekend", pietEnTeuntje, mark.getOuderlijkGezin());
        assertEquals("ouders geen kind erbij", aantalKinderen + 1,
                pietEnTeuntje.aantalKinderen());
        List<Persoon> kinderen = pietEnTeuntje.getKinderen();
        boolean gevonden = false;
        for (Persoon kind : kinderen) {
            if (kind == mark) {
                gevonden = true;
            }
        }
        assertTrue("ouders geen kind erbij", gevonden);

        /*
         * als de ouders bij aanroep al
         * bekend zijn, verandert er niets
         */
        aantalKinderen = pietEnTeuntje.aantalKinderen();
        mark.setOuders(pietEnTeuntje);
        assertEquals("ouders ten onrechte kind erbij", aantalKinderen,
                pietEnTeuntje.aantalKinderen());

    }

    @Test
    public void testBeschrijvingGezin() {
        assertEquals("beschrijving gezin onjuist",
                pietEnTeuntje.toString(), pietEnTeuntje.beschrijving());
        Persoon mark = adm.addPersoon(Geslacht.MAN, new String[]{"Markus", "  Anna   "}, " sWinkelS  ",
                "", new GregorianCalendar(1986, Calendar.APRIL, 13), "venLO",
                pietEnTeuntje);
        assertEquals("beschrijving gezin onjuist",
                pietEnTeuntje.toString() + "; kinderen: -Markus Anna",
                pietEnTeuntje.beschrijving());
        Persoon leentje = adm.addPersoon(Geslacht.VROUW, new String[]{"Leentje"}, "sWinkelS",
                "", new GregorianCalendar(1987, Calendar.APRIL, 13), "venLO",
                pietEnTeuntje);
        assertEquals("beschrijving gezin onjuist",
                pietEnTeuntje.toString() + "; kinderen: -Markus Anna -Leentje",
                pietEnTeuntje.beschrijving());
    }

    @Test
    public void testAddOngehuwdGezin() {
        /**
         * er wordt, zo mogelijk (zie return) een (kinderloos) ongehuwd gezin
         * met ouder1 en ouder2 als ouders gecreeerd;
         */
        Persoon annie = adm.addPersoon(Geslacht.VROUW, new String[]{"Annie", "Gedula"}, "Dael",
                "", new GregorianCalendar(1981, Calendar.APRIL, 3), "Bergen op Zoom", null);
        Persoon jan = adm.addPersoon(Geslacht.MAN, new String[]{"Jan", "Jacobus"}, "Peters",
                "", new GregorianCalendar(1980, Calendar.APRIL, 23), "Venray", pietEnTeuntje);
        Gezin annieEnJan = adm.addOngehuwdGezin(annie, jan);
        assertTrue("geen kinderen", annieEnJan.getKinderen().isEmpty());
        assertFalse("ongehuwd gezin", annieEnJan.isHuwelijkOp(nu));
        assertEquals("ouder1 is annie", annie, annieEnJan.getOuder1());
        assertEquals("ouder2 is jan", jan, annieEnJan.getOuder2());
        /*de huwelijks- en
         * scheidingsdatum zijn onbekend (null);
         */
        assertNull("huwelijksdatum onbekend", annieEnJan.getHuwelijksdatum());
        assertNull("scheidingsdatum onbekend", annieEnJan.getScheidingsdatum());
        /* het gezin krijgt een uniek nummer toegewezen */
        assertEquals("nummering van gezin lijkt onjuist",
                pietEnTeuntje.getNr() + 1, annieEnJan.getNr());
        /* dit gezin wordt ook bij de afzonderlijke ouders geregistreerd;
         */
        assertFalse("registratie bij ouders onjuist", jan.getAlsOuderBetrokkenIn().isEmpty());
        assertFalse("registratie bij ouders onjuist", annie.getAlsOuderBetrokkenIn().isEmpty());
        assertEquals("registratie bij ouders onjuist", annieEnJan, jan.getAlsOuderBetrokkenIn().get(0));
        assertEquals("registratie bij ouders onjuist", annieEnJan, annie.getAlsOuderBetrokkenIn().get(0));

        /*
         * @param ouder1
         * @param ouder2 mag null zijn
         */
        Persoon toos = adm.addPersoon(Geslacht.VROUW, new String[]{"t", "J"}, "sWinkelS",
                "VaN deR", new GregorianCalendar(1971, Calendar.APRIL, 13), "venLO",
                null);
        Gezin eenoudergezin = adm.addOngehuwdGezin(toos, null);
        assertTrue("geen kinderen", eenoudergezin.getKinderen().isEmpty());
        assertFalse("ongehuwd gezin", eenoudergezin.isHuwelijkOp(nu));
        assertEquals("ouder1 is toos", toos, eenoudergezin.getOuder1());
        assertNull("ouder2 is onbekend", eenoudergezin.getOuder2());
        /*de huwelijks- en
         * scheidingsdatum zijn onbekend (null);
         */
        assertNull("huwelijksdatum onbekend", eenoudergezin.getHuwelijksdatum());
        assertNull("scheidingsdatum onbekend", eenoudergezin.getScheidingsdatum());
        /* het gezin krijgt een uniek nummer toegewezen */
        assertEquals("nummering van gezin lijkt onjuist",
                annieEnJan.getNr() + 1, eenoudergezin.getNr());
        /* dit gezin wordt ook bij de afzonderlijke ouders geregistreerd;
         */
        assertFalse("registratie bij ouder onjuist", toos.getAlsOuderBetrokkenIn().isEmpty());
        assertEquals("registratie bij ouder onjuist", eenoudergezin, toos.getAlsOuderBetrokkenIn().get(0));


        /* @return null als ouder1 = ouder2
         */
        Persoon pim = adm.addPersoon(Geslacht.MAN, new String[]{"Pim"}, "sWinkelS",
                "VaN deR", new GregorianCalendar(1995, Calendar.APRIL, 13), "venLO",
                pietEnTeuntje);

        assertNull("ouders verschillend", adm.addOngehuwdGezin(pim, pim));
        assertEquals("ouders verschillend", eenoudergezin.getNr(), adm.aantalGeregistreerdeGezinnen());

        /* of als de volgende voorwaarden worden
         * overtreden: 1) een van de ouders is op dit moment getrouwd 2) het koppel
         * uit een ongehuwd gezin kan niet tegelijkertijd als koppel bij een ander ongehuwd
         * gezin betrokken zijn*/
        assertNull("een van de ouders is op dit moment getrouwd", adm.addOngehuwdGezin(piet, toos));
        assertNull("een van de ouders is op dit moment getrouwd", adm.addOngehuwdGezin(toos, piet));
        assertNull("het koppel uit een ongehuwd gezin kan niet tegelijkertijd bij"
                + " een ander ongehuwd gezin betrokken zijn",
                adm.addOngehuwdGezin(annie, jan));
        assertNull("het koppel uit een ongehuwd gezin kan niet tegelijkertijd bij"
                + " een ander ongehuwd gezin betrokken zijn",
                adm.addOngehuwdGezin(jan, annie));


        /* anders het gewenste gezin
         */
        Gezin annieEnToos = adm.addOngehuwdGezin(toos, annie);
        assertNotNull("gezin met een van de partners verschillend", annieEnToos);
        List<Gezin> gezinnenToos = toos.getAlsOuderBetrokkenIn();
        List<Gezin> gezinnenAnnie = annie.getAlsOuderBetrokkenIn();

        assertFalse("registratie bij ouders onjuist", gezinnenToos.isEmpty());
        assertFalse("registratie bij ouders onjuist", gezinnenAnnie.isEmpty());

        /*
         * test heeftOngehuwdGezin
         */
        assertEquals("", annieEnToos, annie.heeftOngehuwdGezinMet(toos));
        assertEquals("", annieEnToos, toos.heeftOngehuwdGezinMet(annie));

        // Controleert of ongeboren personen een gezin kunnen vormen
        Persoon henk = adm.addPersoon(Geslacht.MAN, new String[]{"Henk"},
                "Rotterdam", "van", new GregorianCalendar(2010, Calendar.APRIL, 5), "Dordrecht", null);
        Persoon marjolein = adm.addPersoon(Geslacht.VROUW, new String[]{"Marjolein"},
                "Leeuw", "de", new GregorianCalendar(2060, Calendar.DECEMBER, 20), "Rotterdam", null);
        assertNull("marjolein is nog niet geboren", adm.addOngehuwdGezin(henk, marjolein));
    }

    @Test
    public void testAddHuwelijk() {
        /**
         * als er al een ongehuwd gezin voor dit koppel bestaat, wordt het
         * huwelijk voltrokken
         */
        Persoon annie = adm.addPersoon(Geslacht.VROUW, new String[]{"Annie", "Gedula"}, "Dael",
                "", new GregorianCalendar(1981, Calendar.APRIL, 3), "Bergen op Zoom", null);
        Persoon jan = adm.addPersoon(Geslacht.MAN, new String[]{"Jan", "Jacobus"}, "Peters",
                "", new GregorianCalendar(1980, Calendar.APRIL, 23), "Venray", pietEnTeuntje);
        Gezin annieEnJan = adm.addOngehuwdGezin(annie, jan);
        Gezin huwelijk = adm.addHuwelijk(jan, annie, nu);
        assertEquals("ongehuwd gezin trouwt", huwelijk, annieEnJan);
        assertEquals("huwelijksdatum bekend", nu, huwelijk.getHuwelijksdatum());
        assertNull("scheidingsdatum onbekend", huwelijk.getScheidingsdatum());

        /* anders wordt er zo mogelijk (zie return) een
         * (kinderloos) gehuwd gezin met ouder1 en ouder2 als ouders gecreeerd;
         * de scheidingsdatum is onbekend (null); */
        Persoon trees = adm.addPersoon(Geslacht.VROUW, new String[]{"trees", "Gedula"}, "Dael",
                "van", new GregorianCalendar(1981, Calendar.APRIL, 3), "Sevenum", null);
        Persoon jaap = adm.addPersoon(Geslacht.MAN, new String[]{"Jaap"}, "Giesbers",
                "", new GregorianCalendar(1980, Calendar.APRIL, 23), "Venray", null);
        Gezin treesEnJaap = adm.addHuwelijk(trees, jaap, new GregorianCalendar(2013, Calendar.APRIL, 23));
        assertTrue("geen kinderen", treesEnJaap.getKinderen().isEmpty());
        assertTrue("gehuwd gezin", treesEnJaap.isHuwelijkOp(nu));
        assertEquals("ouder1 is trees", trees, treesEnJaap.getOuder1());
        assertEquals("ouder2 is jaap", jaap, treesEnJaap.getOuder2());
        /*
         * scheidingsdatum is onbekend (null);
         */
        assertNull("scheidingsdatum onbekend", treesEnJaap.getScheidingsdatum());
        /* het gezin krijgt een uniek nummer toegewezen */
        assertEquals("nummering van gezin lijkt onjuist",
                3, treesEnJaap.getNr());
        /* dit gezin wordt ook bij de afzonderlijke ouders geregistreerd;
         */
        assertFalse("registratie bij ouders onjuist", trees.getAlsOuderBetrokkenIn().isEmpty());
        assertFalse("registratie bij ouders onjuist", jaap.getAlsOuderBetrokkenIn().isEmpty());
        assertEquals("registratie bij ouders onjuist", treesEnJaap, jaap.getAlsOuderBetrokkenIn().get(0));
        assertEquals("registratie bij ouders onjuist", treesEnJaap, trees.getAlsOuderBetrokkenIn().get(0));

        Persoon petra = adm.addPersoon(Geslacht.VROUW, new String[]{"Petra", "Gedula"}, "Dael",
                "", new GregorianCalendar(1981, Calendar.APRIL, 3), "Bergen op Zoom", null);
        Persoon roel = adm.addPersoon(Geslacht.MAN, new String[]{"Roel", "Jacobus"}, "Peters",
                "", new GregorianCalendar(1983, Calendar.APRIL, 23), "Venray", pietEnTeuntje);
        Gezin petraEnRoel = adm.addOngehuwdGezin(petra, roel);

        Persoon john = adm.addPersoon(Geslacht.MAN, new String[]{"John", "Adriaan"}, "Krop",
                "", new GregorianCalendar(1973, Calendar.JANUARY, 3), "Eindhoven", null);
        Gezin johnEnRoel = adm.addHuwelijk(john, roel, nu);
        assertNotNull("huwelijk waarvoor een van de partners bij een ongehuwd gezin betrokken is",
                johnEnRoel);

        /*het gezin krijgt een uniek nummer toegewezen;*/
        assertEquals("nummering van gezin lijkt onjuist", petraEnRoel.getNr() + 1, johnEnRoel.getNr());

        /**
         * @return null als ouder1 = ouder2 of als een van de ouders getrouwd is
         * anders het gehuwde gezin
         */
        Persoon pim = adm.addPersoon(Geslacht.MAN, new String[]{"Pim"}, "sWinkelS",
                "VaN deR", new GregorianCalendar(1995, Calendar.APRIL, 13), "venLO",
                null);

        assertNull("ouders verschillend", adm.addHuwelijk(pim, pim, nu));
        assertNull("ouders ongehuwd", adm.addHuwelijk(roel, pim, Calendar.getInstance()));
        assertNull("ouders ongehuwd", adm.addHuwelijk(pim, roel, Calendar.getInstance()));

        Calendar datum1 = new GregorianCalendar(1995, Calendar.APRIL, 13);
        Calendar datum2 = new GregorianCalendar(1996, Calendar.APRIL, 13);
        Calendar datum3 = new GregorianCalendar(1996, Calendar.APRIL, 14);
        Calendar datum4 = new GregorianCalendar(1996, Calendar.APRIL, 15);

        // merk op: datum1 < datum2 < datum3 < datum4
        Persoon miep = adm.addPersoon(Geslacht.VROUW, new String[]{"miep"}, "Dael",
                "", new GregorianCalendar(1971, Calendar.APRIL, 3), "Bergen op Zoom", null);
        Persoon jacco = adm.addPersoon(Geslacht.MAN, new String[]{"Jacco", "Jacobus"}, "Hop",
                "", new GregorianCalendar(1973, Calendar.APRIL, 23), "Venray", null);
        Gezin miepEnJacco = adm.addHuwelijk(miep, jacco, datum1);
        assertNotNull("Miep en Jacco hebben geen gezin", miepEnJacco);
        miepEnJacco.setScheiding(datum3);

        Persoon aafke = adm.addPersoon(Geslacht.VROUW, new String[]{"aafke"}, "Dael",
                "", new GregorianCalendar(1971, Calendar.APRIL, 3), "Bergen op Zoom", null);
        assertNull("jacco is nog getrouwd", adm.addHuwelijk(aafke, jacco, datum2));
        assertNotNull("jacco is niet meer getrouwd", adm.addHuwelijk(aafke, jacco, datum4));

        Persoon frank = adm.addPersoon(Geslacht.MAN, new String[]{"Frank", "Johan"}, "Kroes",
                "", new GregorianCalendar(1973, Calendar.APRIL, 23), "Helmond", null);

        assertNull("aafke is naderhand getrouwd", adm.addHuwelijk(aafke, frank, datum1));

        // Controleert of minderjarige personen niet ook kunnen trouwen
        Persoon henk = adm.addPersoon(Geslacht.MAN, new String[]{"Henk"},
                "Rotterdam", "van", new GregorianCalendar(1990, Calendar.APRIL, 5), "Dordrecht", null);
        Persoon marjolein = adm.addPersoon(Geslacht.VROUW, new String[]{"Marjolein"},
                "Leeuw", "de", new GregorianCalendar(1970, Calendar.DECEMBER, 20), "Rotterdam", null);
        assertNull("henk is minderjarig", adm.addHuwelijk(henk, marjolein, datum1));

        // Controleert of er een gezin wordt toegevoegd als ouders niet in een gezin direct trouwen
        Persoon flip = adm.addPersoon(Geslacht.MAN, new String[]{"flip"},
                "Petteren", "van", new GregorianCalendar(1965, Calendar.APRIL, 5), "Dordrecht", null);
        Gezin flipEnMarjolein = adm.addHuwelijk(flip, marjolein, datum4);
        assertEquals("Gezin was niet toegevoegd tijdens huwelijk",
                adm.getGezin(flipEnMarjolein.getNr()), flipEnMarjolein);
    }

    @Test
    public void testSetEnIsHuwelijk() {
        /**
         * registreert het huwelijk, mits dit gezin nog geen huwelijk is en
         * beide ouders op deze datum mogen trouwen (pas op: ook de toekomst kan
         * hierbij een rol spelen omdat toekomstige gezinnen eerder zijn
         * geregisteerd)
         */
        Persoon annie = adm.addPersoon(Geslacht.VROUW, new String[]{"Annie", "Gedula"}, "Dael",
                "", new GregorianCalendar(1981, Calendar.APRIL, 3), "Bergen op Zoom", null);
        Persoon jan = adm.addPersoon(Geslacht.MAN, new String[]{"Jan", "Jacobus"}, "Peters",
                "", new GregorianCalendar(1980, Calendar.APRIL, 23), "Venray", pietEnTeuntje);
        Gezin annieEnJan = adm.addOngehuwdGezin(annie, jan);

        assertFalse("is geen huwelijk", annieEnJan.isHuwelijkOp(nu));
        /*
         *
         * @param datum de huwelijksdatum
         * @return false als huwelijk niet mocht worden voltrokken, anders true
         */
        Calendar huwdatum = new GregorianCalendar(2008, Calendar.FEBRUARY, 3);
        Calendar laterDanHuwdatum = new GregorianCalendar(2008, Calendar.FEBRUARY, 4);
        Calendar eerderDanHuwdatum = new GregorianCalendar(2008, Calendar.FEBRUARY, 2);
        assertTrue("huwelijk is niet voltrokken", annieEnJan.setHuwelijk(huwdatum));
        assertTrue("huwelijksdatum later invoeren onjuist", annieEnJan.isHuwelijkOp(nu));
        assertTrue("huwelijksdatum invoeren onjuist", annieEnJan.isHuwelijkOp(laterDanHuwdatum));
        assertFalse("huwelijksdatum invoeren onjuist", annieEnJan.isHuwelijkOp(eerderDanHuwdatum));

        assertFalse("huwelijk is ten onrechte voltrokken", annieEnJan.setHuwelijk(nu));
        assertEquals("2e huwelijksdatum invoeren onjuist", huwdatum, annieEnJan.getHuwelijksdatum());


        /* (pas op: ook de toekomst kan
         * hierbij een rol spelen omdat toekomstige gezinnen eerder zijn
         * geregisteerd)*/
        Persoon pim = adm.addPersoon(Geslacht.MAN, new String[]{"Pim"}, "Pieterse",
                "VaN deR", new GregorianCalendar(1985, Calendar.APRIL, 13), "venLO",
                null);
        Gezin annieEnPim = adm.addHuwelijk(annie, pim, eerderDanHuwdatum);
        assertNull("huwelijk niet toegestaan vanwege toekomstig huwelijk", annieEnPim);
    }

    @Test
    public void testScheiding() {
        assertNull(pietEnTeuntje.getScheidingsdatum());
        //scheidingsDatum voor huwelijksDatum wordt niet geregistreerd
        pietEnTeuntje.setScheiding(new GregorianCalendar(1970, Calendar.APRIL, 23));
        assertNull("scheidingsdatum na huwelijksdatum", pietEnTeuntje.getScheidingsdatum());
        //correcte scheidingsdatum
        Calendar scheiding = new GregorianCalendar(1974, Calendar.JULY, 2);
        pietEnTeuntje.setScheiding(scheiding);
        assertEquals("scheidingsdatum ontbreekt", scheiding, pietEnTeuntje.getScheidingsdatum());
        //andere scheidingsdatum wordt niet meer geaccepteerd
        pietEnTeuntje.setScheiding(new GregorianCalendar(1975, Calendar.JULY, 2));
        assertEquals("twee maal scheiden kan niet", scheiding, pietEnTeuntje.getScheidingsdatum());

        /**
         *
         * @return true als dit een gescheiden huwelijk is op datum, anders
         * false
         */
        assertTrue("wel gescheiden", pietEnTeuntje.heeftGescheidenOudersOp(nu));
        assertFalse("nog niet gescheiden", pietEnTeuntje.heeftGescheidenOudersOp(
                new GregorianCalendar(1973, Calendar.JANUARY, 1)));
        assertFalse("nog niet getrouwd", pietEnTeuntje.heeftGescheidenOudersOp(
                new GregorianCalendar(1969, Calendar.JANUARY, 1)));
    }

    @Test
    public void testHeeftGescheidenOuders() {
        assertNull(pietEnTeuntje.getScheidingsdatum());
        assertFalse("nog niet gescheiden", pietEnTeuntje.heeftGescheidenOudersOp(
                new GregorianCalendar(1973, Calendar.JANUARY, 2)));
        assertFalse("nog niet getrouwd", pietEnTeuntje.heeftGescheidenOudersOp(
                new GregorianCalendar(1970, Calendar.JANUARY, 1)));
        pietEnTeuntje.setScheiding(new GregorianCalendar(1973, Calendar.JANUARY, 1));
        assertTrue("wel gescheiden", pietEnTeuntje.heeftGescheidenOudersOp(nu));
        assertTrue("wel gescheiden", pietEnTeuntje.heeftGescheidenOudersOp(
                new GregorianCalendar(1973, Calendar.JANUARY, 2)));
        assertFalse("nog niet gescheiden", pietEnTeuntje.heeftGescheidenOudersOp(
                new GregorianCalendar(1972, Calendar.DECEMBER, 31)));
    }

    @Test
    public void testGetPersoonEnPersonen() {
        /* @return de persoon met dezelfde initialen, tussenvoegsel, achternaam,
         * geboortedatum en -plaats mits bekend (ongeacht hoofd- en kleine letters),
         * anders null*/
        Persoon persoon = adm.getPersoon(new String[]{"P", "f"}, "swinkels",
                "", new GregorianCalendar(1950, Calendar.APRIL, 23), "ede");
        assertEquals("identificatie persoon onjuist", piet, persoon);
        persoon = adm.getPersoon(new String[]{"Peter", "frans"}, "Swinkels",
                "", new GregorianCalendar(1950, Calendar.APRIL, 23), "ede");
        assertEquals("identificatie persoon onjuist", piet, persoon);

        //initialen onjuist
        persoon = adm.getPersoon(new String[]{"P"}, "Swinkels",
                "", new GregorianCalendar(1950, Calendar.APRIL, 23), "ede");
        assertNull("identificatie persoon onjuist", persoon);
        persoon = adm.getPersoon(new String[]{"P", "f", "k"}, "Swinkels",
                "", new GregorianCalendar(1950, Calendar.APRIL, 23), "ede");
        assertNull("identificatie persoon onjuist", persoon);
        //achternaam onjuist
        persoon = adm.getPersoon(new String[]{"Peter", "frans"}, "Swinkel",
                "", new GregorianCalendar(1950, Calendar.APRIL, 23), "ede");
        assertNull("identificatie persoon onjuist", persoon);
        //tussenvoegsel onjuist
        persoon = adm.getPersoon(new String[]{"Peter", "frans"}, "Swinkels",
                "van", new GregorianCalendar(1950, Calendar.APRIL, 23), "ede");
        assertNull("identificatie persoon onjuist", persoon);
        //geboortedatum onjuist
        persoon = adm.getPersoon(new String[]{"Peter", "frans"}, "Swinkels",
                "", new GregorianCalendar(1950, Calendar.APRIL, 22), "ede");
        assertNull("identificatie persoon onjuist", persoon);
        //geboorteplaats onjuist
        persoon = adm.getPersoon(new String[]{"P", "f"}, "Swinkels",
                "", new GregorianCalendar(1950, Calendar.APRIL, 23), "Wageningen");
        assertNull("identificatie persoon onjuist", persoon);

        try {
            List<Persoon> personen = adm.getPersonen();
            personen.clear();
        } catch (Exception exc) {
        }
        assertFalse("personen onjuist ingekapseld", adm.getPersonen().isEmpty());
    }

    @Test
    public void testGetPersonenMetAchternaam() {
        /**
         * @return alle personen met een achternaam gelijk aan de meegegeven
         * achternaam; er wordt niet op hoofd- en kleine letters gelet
         */
        assertEquals("aantal personen met achternaam onjuist", 1, adm.getPersonenMetAchternaam("Swinkels").size());
        assertEquals("aantal personen met achternaam onjuist", 1, adm.getPersonenMetAchternaam("swinKELs").size());
        adm.addPersoon(Geslacht.MAN, new String[]{"Franciscus", "Gerardus"}, "Swinkel",
                "", new GregorianCalendar(1952, Calendar.APRIL, 23), "ede", null);
        assertEquals("aantal personen met achternaam onjuist", 1, adm.getPersonenMetAchternaam("swinKELs").size());
        Persoon frans = adm.addPersoon(Geslacht.MAN, new String[]{"Franciscus", "Gerardus"}, "Swinkels",
                "van der", new GregorianCalendar(1952, Calendar.APRIL, 23), "ede", null);
        assertEquals("aantal personen met achternaam onjuist", 2, adm.getPersonenMetAchternaam("swinKELs").size());
        adm.addPersoon(Geslacht.MAN, new String[]{"Franciscus", "Gerardus"}, "Swinkelse",
                "", new GregorianCalendar(1952, Calendar.APRIL, 23), "ede", null);
        assertEquals("aantal personen met achternaam onjuist", 2, adm.getPersonenMetAchternaam("swinKELs").size());
        assertTrue("personen met achternaam onjuist", adm.getPersonenMetAchternaam("swinKELs").contains(piet));
        assertTrue("personen met achternaam onjuist", adm.getPersonenMetAchternaam("swinKELs").contains(frans));
    }
}
