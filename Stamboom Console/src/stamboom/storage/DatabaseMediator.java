/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.storage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Calendar;
import java.util.Properties;
import stamboom.domain.Administratie;
import stamboom.domain.Geslacht;
import stamboom.domain.Gezin;
import stamboom.domain.Persoon;

public class DatabaseMediator implements IStorageMediator {

    private Properties props;
    private Connection conn;

    @Override
    public Administratie load() throws IOException {
        //todo opgave 4
        Administratie admin = new Administratie();

        try {
            this.initConnection();

            Statement stat = this.conn.createStatement();
            String personenquery = "SELECT * FROM PERSONEN ORDER BY PERSOONSNUMMER",
                    gezinnenquery = "SELECT * FROM GEZINNEN ORDER BY GEZINSNUMMER",
                    kindereningezin = "SELECT PERSOONSNUMMER FROM PERSONEN WHERE OUDERS = ?";

            ResultSet persoonresults = stat.executeQuery(personenquery);

            while (persoonresults.next()) {
                int persoonsnummer = persoonresults.getInt("persoonsNummer");
                String achternaam = persoonresults.getString("achternaam"),
                        tussenvoegsel = persoonresults.getString("tussenvoegsel"),
                        geboorteplaats = persoonresults.getString("geboorteplaats");
                String[] voornamen = persoonresults.getString("voornamen").split(" ");
                Geslacht geslacht = Geslacht.valueOf(persoonresults.getString("geslacht").toUpperCase());

                Calendar gebdatum = Calendar.getInstance();
                gebdatum.setTime(persoonresults.getDate("geboortedatum"));

                admin.addPersoon(geslacht, voornamen, achternaam, tussenvoegsel, gebdatum, geboorteplaats, null);
            }

            ResultSet gezinnenresults = stat.executeQuery(gezinnenquery);

            while (gezinnenresults.next()) {
                int gezinnr = gezinnenresults.getInt("gezinsnummer"),
                        ouder1nr = gezinnenresults.getInt("ouder1"),
                        ouder2nr = gezinnenresults.getInt("ouder2");

                Calendar huwelijksDatum = null;
                if (gezinnenresults.getDate("huwelijksdatum") != null) {
                    Calendar huwdatum = Calendar.getInstance();
                    huwdatum.setTime(gezinnenresults.getDate("huwelijksdatum"));
                    huwelijksDatum = huwdatum;
                }

                Calendar scheidingsDatum = null;
                if (gezinnenresults.getDate("scheidingsdatum") != null) {
                    Calendar scheidatum = Calendar.getInstance();
                    scheidatum.setTime(gezinnenresults.getDate("scheidingsdatum"));
                    scheidingsDatum = scheidatum;
                }

                Gezin gezin;
                if (huwelijksDatum != null) {
                    gezin = admin.addHuwelijk(admin.getPersoon(ouder1nr), admin.getPersoon(ouder2nr), huwelijksDatum);

                    if (scheidingsDatum != null) {
                        admin.setScheiding(gezin, scheidingsDatum);
                    }
                } else {
                    gezin = admin.addOngehuwdGezin(admin.getPersoon(ouder1nr), admin.getPersoon(ouder2nr));
                    
                    if (scheidingsDatum != null) {
                        admin.setScheiding(gezin, scheidingsDatum);
                    }
                }

                PreparedStatement kinderenPS = this.conn.prepareStatement(kindereningezin);
                kinderenPS.setInt(1, gezinnr);
                ResultSet kinderenResult = kinderenPS.executeQuery();

                while (kinderenResult.next()) {
                    int persoonNr = kinderenResult.getInt("persoonsNummer");

                    Persoon persoon = admin.getPersoon(persoonNr);
                    admin.setOuders(persoon, gezin);
                }
            }
        } catch (SQLException | IllegalArgumentException ex) {
            throw new IOException(ex.getMessage());
        } finally {
            this.closeConnection();
        }
        return admin;
    }

    @Override
    public void save(Administratie admin) throws IOException {
        //todo opgave 4
        try {
            this.initConnection();

            PreparedStatement preState;

            for (Persoon p : admin.getPersonen()) {
                String query = "SELECT PERSOONSNUMMER FROM PERSONEN WHERE PERSOONSNUMMER = ?";

                preState = this.conn.prepareStatement(query);
                preState.setInt(1, p.getNr());

                ResultSet checkRes = preState.executeQuery();
                checkRes.last();

                if (checkRes.getRow() > 0) {
                    query = "UPDATE PERSONEN SET PERSOONSNUMMER = ?, ACHTERNAAM = ?, VOORNAMEN = ?, TUSSENVOEGSEL = ?, GEBOORTEDATUM = ?, GEBOORTEPLAATS = ?, GESLACHT = ?, OUDERS = ? WHERE PERSOONSNUMMER = ?";
                } else {
                    query = "INSERT INTO PERSONEN(PERSOONSNUMMER, ACHTERNAAM, VOORNAMEN, TUSSENVOEGSEL, GEBOORTEDATUM, GEBOORTEPLAATS, GESLACHT, OUDERS) VALUES(?, ?, ?, ?, ? ,? ,?, ?)";
                }

                preState = this.conn.prepareStatement(query);
                preState.setInt(1, p.getNr());
                preState.setString(2, p.getAchternaam());
                preState.setString(3, p.getVoornamen());
                preState.setString(4, p.getTussenvoegsel());

                Calendar gebcal = p.getGebDat();
                Date gebdate = new java.sql.Date(gebcal.getTimeInMillis());

                preState.setDate(5, gebdate);
                preState.setString(6, p.getGebPlaats());
                preState.setString(7, p.getGeslacht().toString());
                preState.setInt(8, p.getNr());
                preState.setInt(9, p.getOuderlijkGezin().getNr());
                preState.execute();
            }

            for (Gezin g : admin.getGezinnen()) {
                String query = "SELECT GEZINSNUMMER FROM GEZINNEN WHERE GEZINSNUMMER = ?";

                preState = this.conn.prepareStatement(query);
                preState.setInt(1, g.getNr());

                ResultSet checkRes = preState.executeQuery();
                checkRes.last();

                if (checkRes.getRow() > 0) {
                    query = "UPDATE GEZINNEN SET OUDER1 = ?, OUDER2 = ?, HUWELIJKSDATUM = ?, SCHEIDINGSDATUM = ? WHERE GEZINSNUMMER = ?";
                } else {
                    query = "INSERT INTO GEZINNEN(GEZINSNUMMER, OUDER1, OUDER2, HUWELIJKSDATUM, SCHEIDINGSDATUM) VALUES(?, ?, ?, ?, ?)";
                }

                preState = this.conn.prepareStatement(query);
                preState.setInt(1, g.getNr());
                preState.setInt(2, g.getOuder1().getNr());

                if (g.getOuder2() == null) {
                    preState.setNull(3, Types.INTEGER);
                } else {
                    preState.setInt(3, g.getOuder2().getNr());
                }

                if (g.getHuwelijksdatum() == null) {
                    preState.setNull(4, Types.DATE);
                } else {
                    Calendar huwcal = g.getHuwelijksdatum();
                    Date huwdate = new java.sql.Date(huwcal.getTimeInMillis());
                    preState.setDate(4, huwdate);
                }

                if (g.getScheidingsdatum()== null) {
                    preState.setNull(5, Types.DATE);
                } else {
                    Calendar scheidat = g.getScheidingsdatum();
                    Date scheidate = new java.sql.Date(scheidat.getTimeInMillis());
                    preState.setDate(5, scheidate);
                }
                
                preState.execute();

                for (Persoon p : g.getKinderen()) {
                    query = "UPDATE PERSONEN SET OUDER1 = ?, OUDER2 = ? WHERE PERSOONSNUMMER = ?";

                    preState = this.conn.prepareStatement(query);
                    preState.setInt(1, g.getOuder1().getNr());
                    preState.setInt(2, g.getOuder2().getNr());
                    preState.setInt(3, p.getNr());
                    preState.execute();
                }
            }

        } catch (SQLException | IllegalArgumentException ex) {
            throw new IOException(ex.getMessage());
        } finally {
            this.closeConnection();
        }

    }

    /**
     * Laadt de instellingen, in de vorm van een Properties bestand, en
     * controleert of deze in de correcte vorm is, en er verbinding gemaakt kan
     * worden met de database.
     *
     * @param props
     * @return
     */
    @Override
    public final boolean configure(Properties props) {
        this.props = props;
        if (!isCorrectlyConfigured()) {
            System.err.println("props mist een of meerdere keys");
            return false;
        }

        try {
            initConnection();
            return true;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            this.props = null;
            return false;
        } finally {
            closeConnection();
        }
    }

    @Override
    public Properties config() {
        return props;
    }

    @Override
    public boolean isCorrectlyConfigured() {
        if (props == null) {
            return false;
        }
        if (!props.containsKey("driver")) {
            return false;
        }
        if (!props.containsKey("url")) {
            return false;
        }
        if (!props.containsKey("username")) {
            return false;
        }
        if (!props.containsKey("password")) {
            return false;
        }
        return true;
    }

    private void initConnection() throws SQLException {
        //opgave 4
        String useddriver = this.props.getProperty("driver");
        if (useddriver != null & !useddriver.isEmpty()) {
            System.setProperty("jdcb.drivers", this.props.getProperty("driver"));
            this.conn = DriverManager.getConnection(this.props.getProperty("url"), this.props.getProperty("username"), this.props.getProperty("password"));
        } else {
            throw new IllegalArgumentException("Unable to set up database connection, driver is not set");
        }
    }

    private void closeConnection() {
        try {
            conn.close();
            conn = null;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
