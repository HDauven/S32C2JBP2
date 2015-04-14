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

            Statement stat = this.conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String personenQuery = "SELECT * FROM PERSONEN ORDER BY PERSOONSNUMMER",
                    gezinnenQuery = "SELECT * FROM GEZINNEN ORDER BY GEZINSNUMMER",
                    kinderenInGezin = "SELECT PERSOONSNUMMER FROM PERSONEN WHERE OUDERS = ?";

            ResultSet persoonResults = stat.executeQuery(personenQuery);

            while (persoonResults.next()) {
                String achternaam = persoonResults.getString("achternaam"),
                        tussenvoegsel = persoonResults.getString("tussenvoegsel"),
                        geboorteplaats = persoonResults.getString("geboorteplaats");
                String[] voornamen = persoonResults.getString("voornamen").split(" ");
                Geslacht geslacht = Geslacht.valueOf(persoonResults.getString("geslacht").toUpperCase());
                
                if (tussenvoegsel == null) {
                    tussenvoegsel = "";
                }

                Calendar gebdatum = Calendar.getInstance();
                gebdatum.setTime(persoonResults.getDate("geboortedatum"));
                Calendar geboorteDatum = gebdatum;
                
                

                admin.addPersoon(geslacht, voornamen, achternaam, tussenvoegsel, geboorteDatum, geboorteplaats, null);
            }

            ResultSet gezinnenResults = stat.executeQuery(gezinnenQuery);

            while (gezinnenResults.next()) {
                int gezinNr = gezinnenResults.getInt("gezinsnummer"),
                        ouder1Nr = gezinnenResults.getInt("ouder1"),
                        ouder2Nr = gezinnenResults.getInt("ouder2");

                Calendar huwelijksDatum = null;
                if (gezinnenResults.getDate("huwelijksdatum") != null) {
                    Calendar huwdatum = Calendar.getInstance();
                    huwdatum.setTime(gezinnenResults.getDate("huwelijksdatum"));
                    huwelijksDatum = huwdatum;
                }

                Calendar scheidingsDatum = null;
                if (gezinnenResults.getDate("scheidingsdatum") != null) {
                    Calendar scheidatum = Calendar.getInstance();
                    scheidatum.setTime(gezinnenResults.getDate("scheidingsdatum"));
                    scheidingsDatum = scheidatum;
                }

                Gezin gezin;
                if (huwelijksDatum != null) {
                    gezin = admin.addHuwelijk(admin.getPersoon(ouder1Nr), admin.getPersoon(ouder2Nr), huwelijksDatum);

                    if (scheidingsDatum != null) {
                        admin.setScheiding(gezin, scheidingsDatum);
                    }
                } else {
                    gezin = admin.addOngehuwdGezin(admin.getPersoon(ouder1Nr), admin.getPersoon(ouder2Nr));
                    
                    if (scheidingsDatum != null) {
                        admin.setScheiding(gezin, scheidingsDatum);
                    }
                }

                PreparedStatement kinderenPS = this.conn.prepareStatement(kinderenInGezin, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                kinderenPS.setInt(1, gezinNr);
                ResultSet kinderenResult = kinderenPS.executeQuery();

                while (kinderenResult.next()) {
                    int persoonNr = kinderenResult.getInt("persoonsnummer");

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

            PreparedStatement statement;

            for (Persoon p : admin.getPersonen()) {
                String query = "SELECT * FROM PERSONEN WHERE PERSOONSNUMMER = ?";

                statement = this.conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                statement.setInt(1, p.getNr());

                ResultSet result = statement.executeQuery();
                result.last();

                if (result.getRow() > 0) {
                    query = "UPDATE PERSONEN SET ACHTERNAAM = ?, VOORNAMEN = ?, TUSSENVOEGSEL = ?, GEBOORTEDATUM = ?, GEBOORTEPLAATS = ?, GESLACHT = ? WHERE PERSOONSNUMMER = ?";
                } else {
                    query = "INSERT INTO PERSONEN(ACHTERNAAM, VOORNAMEN, TUSSENVOEGSEL, GEBOORTEDATUM, GEBOORTEPLAATS, GESLACHT, PERSOONSNUMMER) VALUES(?, ?, ?, ?, ? ,? ,?)";
                }

                statement = this.conn.prepareStatement(query);
                statement.setString(1, p.getAchternaam());
                statement.setString(2, p.getVoornamen());
                if (p.getTussenvoegsel() != null) {
                    statement.setString(3, p.getTussenvoegsel());
                }
                else {
                    statement.setString(3, "");
                }

                Calendar gebcal = p.getGebDat();
                Date gebdate = new java.sql.Date(gebcal.getTimeInMillis());

                statement.setDate(4, gebdate);
                statement.setString(5, p.getGebPlaats());
                statement.setString(6, p.getGeslacht().toString());
                statement.setInt(7, p.getNr());
                statement.execute();
            }

            for (Gezin g : admin.getGezinnen()) {
                String query = "SELECT GEZINSNUMMER FROM GEZINNEN WHERE GEZINSNUMMER = ?";

                statement = this.conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                statement.setInt(1, g.getNr());

                ResultSet checkRes = statement.executeQuery();
                checkRes.last();

                if (checkRes.getRow() > 0) {
                    query = "UPDATE GEZINNEN SET OUDER1 = ?, OUDER2 = ?, HUWELIJKSDATUM = ?, SCHEIDINGSDATUM = ? WHERE GEZINSNUMMER = ?";
                } else {
                    query = "INSERT INTO GEZINNEN(OUDER1, OUDER2, HUWELIJKSDATUM, SCHEIDINGSDATUM, GEZINSNUMMER) VALUES(?, ?, ?, ?, ?)";
                }

                statement = this.conn.prepareStatement(query);
                statement.setInt(1, g.getOuder1().getNr());

                if (g.getOuder2() == null) {
                    statement.setNull(2, Types.INTEGER);
                } else {
                    statement.setInt(2, g.getOuder2().getNr());
                }

                if (g.getHuwelijksdatum() == null) {
                    statement.setNull(3, Types.DATE);
                } else {
                    Calendar huwcal = g.getHuwelijksdatum();
                    Date huwdate = new java.sql.Date(huwcal.getTimeInMillis());
                    statement.setDate(3, huwdate);
                }

                if (g.getScheidingsdatum()== null) {
                    statement.setNull(4, Types.DATE);
                } else {
                    Calendar scheical = g.getScheidingsdatum();
                    Date scheidate = new java.sql.Date(scheical.getTimeInMillis());
                    statement.setDate(4, scheidate);
                }
                statement.setInt(5, g.getNr());
                statement.execute();

                for (Persoon p : g.getKinderen()) {
                    query = "UPDATE PERSONEN SET OUDERS = ? WHERE PERSOONSNUMMER = ?";

                    statement = this.conn.prepareStatement(query);
                    statement.setInt(1, g.getNr());
                    statement.setInt(2, p.getNr());
                    statement.execute();
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
            System.err.println("props mist een of meer keys");
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
        String driverUsed = this.props.getProperty("driver");
        if (driverUsed != null & !driverUsed.isEmpty()) {
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
