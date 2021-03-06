/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.gui;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import stamboom.controller.StamboomController;
import stamboom.domain.Administratie;
import stamboom.domain.Geslacht;
import stamboom.domain.Gezin;
import stamboom.domain.Persoon;
import stamboom.storage.DatabaseMediator;
import stamboom.storage.SerializationMediator;
import stamboom.util.StringUtilities;

/**
 *
 * @author frankpeeters
 */
public class StamboomFXController extends StamboomController implements Initializable {

    //MENUs en TABs
    @FXML MenuBar menuBar;
    @FXML MenuItem miNew;
    @FXML MenuItem miOpen;
    @FXML MenuItem miSave;
    @FXML CheckMenuItem cmDatabase;
    @FXML MenuItem miClose;
    @FXML Tab tabPersoon;
    @FXML Tab tabGezin;
    @FXML Tab tabPersoonInvoer;
    @FXML Tab tabGezinInvoer;

    //PERSOON
    @FXML ComboBox cbPersonen;
    @FXML TextField tfPersoonNr;
    @FXML TextField tfVoornamen;
    @FXML TextField tfTussenvoegsel;
    @FXML TextField tfAchternaam;
    @FXML TextField tfGeslacht;
    @FXML TextField tfGebDatum;
    @FXML TextField tfGebPlaats;
    @FXML ComboBox cbOuderlijkGezin;
    @FXML ListView lvAlsOuderBetrokkenBij;
    @FXML Button btStamboom;

    //INVOER GEZIN
    @FXML ComboBox cbOuder1Invoer;
    @FXML ComboBox cbOuder2Invoer;
    @FXML TextField tfHuwelijkInvoer;
    @FXML TextField tfScheidingInvoer;
    @FXML Button btOKGezinInvoer;
    @FXML Button btCancelGezinInvoer;

    //INVOER PERSOON
    @FXML TextField tfVoornamenInvoer;
    @FXML TextField tfAchternaamInvoer;
    @FXML TextField tfTussenvoegselInvoer;
    @FXML TextField tfGebdatumInvoer;
    @FXML TextField tfGebplaatsInvoer;
    @FXML ComboBox cbGeslachtInvoer;
    @FXML ComboBox cbOuderlijkGezinInvoer;

    //GEZIN
    @FXML ComboBox cbGezinnen;
    @FXML Button btnGezinSetHuwelijksdatum;
    @FXML Button btnGezinSetScheidingsdatum;
    @FXML TextField tfGezinsNr;
    @FXML TextField tfGezinOuder1;
    @FXML TextField tfGezinOuder2;
    @FXML TextField tfGezinHuwelijksdatum;
    @FXML TextField tfGezinScheidingsdatum;
    @FXML TextField tfGezinSetHuwelijksdatum;
    @FXML TextField tfGezinSetScheidingsdatum;
    @FXML ListView lvGezinKinderen;

    //opgave 4
    private boolean withDatabase;
    private Administratie admin = getAdministratie();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initComboboxes();
        withDatabase = false;
    }

    private void initComboboxes() {
        //todo opgave 3 
        cbOuderlijkGezinInvoer.setItems(admin.getGezinnen());
        cbOuder1Invoer.setItems(admin.getPersonen());
        cbOuder2Invoer.setItems(admin.getPersonen());
        cbGezinnen.setItems(admin.getGezinnen());
        cbOuderlijkGezin.setItems(admin.getGezinnen());
        cbPersonen.setItems(admin.getPersonen());
        if (cbGeslachtInvoer.getItems().isEmpty()) {
            cbGeslachtInvoer.getItems().addAll(Geslacht.MAN, Geslacht.VROUW);
        }
    }

    public void selectPersoon(Event evt) {
        Persoon persoon = (Persoon) cbPersonen.getSelectionModel().getSelectedItem();
        showPersoon(persoon);
    }

    private void showPersoon(Persoon persoon) {
        if (persoon == null) {
            clearTabPersoon();
        } else {
            tfPersoonNr.setText(persoon.getNr() + "");
            tfVoornamen.setText(persoon.getVoornamen());
            tfTussenvoegsel.setText(persoon.getTussenvoegsel());
            tfAchternaam.setText(persoon.getAchternaam());
            tfGeslacht.setText(persoon.getGeslacht().toString());
            tfGebDatum.setText(StringUtilities.datumString(persoon.getGebDat()));
            tfGebPlaats.setText(persoon.getGebPlaats());
            if (persoon.getOuderlijkGezin() != null) {
                cbOuderlijkGezin.getSelectionModel().select(persoon.getOuderlijkGezin());
            } else {
                cbOuderlijkGezin.getSelectionModel().clearSelection();
            }

            //todo opgave 3
            lvAlsOuderBetrokkenBij.setItems(persoon.getAlsOuderBetrokkenIn());
        }
    }

    public void setOuders(Event evt) {
        if (tfPersoonNr.getText().isEmpty()) {
            return;
        }
        Gezin ouderlijkGezin = (Gezin) cbOuderlijkGezin.getSelectionModel().getSelectedItem();
        if (ouderlijkGezin == null) {
            return;
        }

        int nr = Integer.parseInt(tfPersoonNr.getText());
        Persoon p = getAdministratie().getPersoon(nr);
        if (getAdministratie().setOuders(p, ouderlijkGezin)) {
            showDialog("Success", ouderlijkGezin.toString()
                    + " is nu het ouderlijk gezin van " + p.getNaam());
        }

    }

    public void selectGezin(Event evt) {
        // todo opgave 3
        Gezin gezin = (Gezin) cbGezinnen.getSelectionModel().getSelectedItem();
        showGezin(gezin);
    }

    private void showGezin(Gezin gezin) {
        // todo opgave 3
        if (gezin == null) {
            clearTabGezin();
        } else {
            tfGezinsNr.setText(gezin.getNr() + "");
            tfGezinOuder1.setText(gezin.getOuder1().getNaam());
            tfGezinOuder2.setText(gezin.getOuder2().getNaam());
            if (gezin.getHuwelijksdatum() == null || gezin.getScheidingsdatum() == null) {
                tfGezinHuwelijksdatum.setText("");
                tfGezinScheidingsdatum.setText("");
            } else {
                SimpleDateFormat df = new SimpleDateFormat("d-M-yyyy");
                String huwelijksdatumString = df.format(gezin.getHuwelijksdatum().getTime());
                String scheidingsdatumString = df.format(gezin.getScheidingsdatum().getTime());
                tfGezinHuwelijksdatum.setText(huwelijksdatumString);
                tfGezinScheidingsdatum.setText(scheidingsdatumString);
            }
            lvGezinKinderen.setItems(gezin.getKinderen());
        }
    }

    public void setHuwdatum(Event evt) {
        // todo opgave 3
        if (cbGezinnen.getSelectionModel().getSelectedItem() != null && !tfGezinSetHuwelijksdatum.getText().isEmpty()) {
            Gezin gezin = (Gezin) cbGezinnen.getSelectionModel().getSelectedItem();
            Calendar huwelijksdatum = StringUtilities.datum(tfGezinSetHuwelijksdatum.getText());
            admin.setHuwelijk(gezin, huwelijksdatum);
            SimpleDateFormat df = new SimpleDateFormat("d-M-yyyy");
            String huwelijksdatumString = df.format(gezin.getHuwelijksdatum().getTime());
            showDialog("Succes", "De huwelijksdatum van dit gezin is veranderd naar: " + huwelijksdatumString);
            tfGezinSetHuwelijksdatum.clear();
        } else {
            showDialog("Warning", "Kies een gezin en vul een huwelijksdatum in.");
        }
    }

    public void setScheidingsdatum(Event evt) {
        // todo opgave 3
        if (cbGezinnen.getSelectionModel().getSelectedItem() != null && !tfGezinSetScheidingsdatum.getText().isEmpty()) {
            Gezin gezin = (Gezin) cbGezinnen.getSelectionModel().getSelectedItem();
            Calendar scheidingsdatum = StringUtilities.datum(tfGezinSetScheidingsdatum.getText());
            admin.setScheiding(gezin, scheidingsdatum);
            SimpleDateFormat df = new SimpleDateFormat("d-M-yyyy");
            String scheidingsdatumString = df.format(gezin.getScheidingsdatum().getTime());
            showDialog("Succes", "De scheidingsdatum van dit gezin is veranderd naar: " + scheidingsdatumString);
            tfGezinSetScheidingsdatum.clear();
        } else {
            showDialog("Warning", "Kies een gezin en vul een scheidingsdatum in.");
        }
    }

    public void cancelPersoonInvoer(Event evt) {
        // todo opgave 3
        clearTabPersoonInvoer();
    }

    public void okPersoonInvoer(Event evt) {
        // todo opgave 3
        if (tfVoornamenInvoer.getText().equals("") || tfAchternaamInvoer.getText().equals("") || tfGebdatumInvoer.getText().equals("") || tfGebplaatsInvoer.getText().equals("") || cbGeslachtInvoer.getSelectionModel().getSelectedItem() == null) {
            showDialog("Warning", "Vul voornamen, achternaam, gebdatum, gebplaats en geslacht in!");
        } else {
            Calendar gebDatum = null;
            try {
                gebDatum = StringUtilities.datum(tfGebdatumInvoer.getText());
            } catch (IllegalArgumentException exc) {
                showDialog("Warning", "Gebdatum :" + exc.getMessage());
            }

            if (gebDatum != null) {

                String[] voornamen = tfVoornamenInvoer.getText().split("\\s");

                Geslacht geslacht = (Geslacht) cbGeslachtInvoer.getSelectionModel().getSelectedItem();
                Gezin ouderlijkGezin = (Gezin) cbOuderlijkGezinInvoer.getSelectionModel().getSelectedItem();
                if (ouderlijkGezin != null) {
                    admin.addPersoon(geslacht, voornamen, tfAchternaamInvoer.getText(), tfTussenvoegselInvoer.getText(), gebDatum, tfGebplaatsInvoer.getText(), ouderlijkGezin);
                    showDialog("Succes", "De nieuwe persoon is toegevoegd aan de administratie!");
                } else {
                    admin.addPersoon(geslacht, voornamen, tfAchternaamInvoer.getText(), tfTussenvoegselInvoer.getText(), gebDatum, tfGebplaatsInvoer.getText(), null);
                    showDialog("Succes", "De nieuwe persoon is toegevoegd aan de administratie!");
                }
            } else {
                showDialog("Warning", "Vul een geldige geboortedatum in.");
            }
        }

        clearTabPersoonInvoer();
    }

    public void okGezinInvoer(Event evt) {
        Persoon ouder1 = (Persoon) cbOuder1Invoer.getSelectionModel().getSelectedItem();
        if (ouder1 == null) {
            showDialog("Warning", "eerste ouder is niet ingevoerd");
            return;
        }
        Persoon ouder2 = (Persoon) cbOuder2Invoer.getSelectionModel().getSelectedItem();
        if (ouder1 == ouder2 || ouder2 == ouder1) {
            showDialog("Warning", "De ouders mogen niet gelijk zijn.");
            return;
        }
        Calendar huwdatum;
        try {
            huwdatum = StringUtilities.datum(tfHuwelijkInvoer.getText());
        } catch (IllegalArgumentException exc) {
            showDialog("Warning", "huwelijksdatum :" + exc.getMessage());
            return;
        }
        Gezin g;
        if (huwdatum != null) {
            g = this.getAdministratie().addHuwelijk(ouder1, ouder2, huwdatum);
            if (g == null) {
                showDialog("Warning", "Invoer huwelijk is niet geaccepteerd");
            } else {
                Calendar scheidingsdatum;
                try {
                    scheidingsdatum = StringUtilities.datum(tfScheidingInvoer.getText());
                    if (scheidingsdatum != null) {
                        this.getAdministratie().setScheiding(g, scheidingsdatum);
                        showDialog("Succes", "Gezin is toegevoegd!");
                    }
                } catch (IllegalArgumentException exc) {
                    showDialog("Warning", "scheidingsdatum :" + exc.getMessage());
                }
            }
        } else {
            g = this.getAdministratie().addOngehuwdGezin(ouder1, ouder2);
            if (g == null) {
                showDialog("Warning", "Invoer ongehuwd gezin is niet geaccepteerd");
            }
            showDialog("Succes", "Het nieuwe gezin is toegevoegd aan de administratie!");
        }

        clearTabGezinInvoer();
    }

    public void cancelGezinInvoer(Event evt) {
        clearTabGezinInvoer();
    }

    public void showStamboom(Event evt) {
        // todo opgave 3
        if (cbPersonen.getSelectionModel().getSelectedItem() != null) {
            Persoon persoon = (Persoon) cbPersonen.getSelectionModel().getSelectedItem();
            showDialog("Stamboom", persoon.stamboomAlsString());
        } else {
            showDialog("Warning", "Selecteer een persoon.");
        }
    }

    public void createEmptyStamboom(Event evt) {
        this.clearAdministratie();
        clearTabs();
        initComboboxes();
    }

    public void openStamboom(Event evt) {
        // todo opgave 3
        if (!cmDatabase.isSelected()) {
            SerializationMediator sm = new SerializationMediator();
            try {
                admin = sm.load();
                initComboboxes();
            } catch (IOException ex) {
                Logger.getLogger(StamboomFXController.class.getName()).log(Level.SEVERE, null, ex);
            }
            showDialog("Sucess", "De administratie is geladen uit het bestand!");
        } else {
            DatabaseMediator dm = new DatabaseMediator();
            try {
                // admin = dm.load();
                this.loadFromDatabase();
                admin = this.getAdministratie();
                initComboboxes();
            } catch (IOException ex) {
                Logger.getLogger(StamboomFXController.class.getName()).log(Level.SEVERE, null, ex);
            }
            showDialog("Succes", "De administratie is geladen uit de database!");
        }
    }

    public void saveStamboom(Event evt) {
        // todo opgave 3
        if (!cmDatabase.isSelected()) {
            SerializationMediator sm = new SerializationMediator();
            try {
                sm.save(admin);
            } catch (IOException ex) {
                Logger.getLogger(StamboomFXController.class.getName()).log(Level.SEVERE, null, ex);
            }
            showDialog("Succes", "De administratie is opgeslagen in een lokaal bestand!");
        } else {
            DatabaseMediator dm = new DatabaseMediator();
            try {
                //dm.save(admin);
                this.saveToDatabase(admin);

            } catch (IOException exc) {

            }
            showDialog("Succes", "De administratie is opgeslagen in de database!");
        }
    }

    public void closeApplication(Event evt) {
        saveStamboom(evt);
        getStage().close();
    }

    public void configureStorage(Event evt) {
        withDatabase = cmDatabase.isSelected();
    }

    public void selectTab(Event evt) {
        Object source = evt.getSource();
        if (source == tabPersoon) {
            clearTabPersoon();
        } else if (source == tabGezin) {
            clearTabGezin();
        } else if (source == tabPersoonInvoer) {
            clearTabPersoonInvoer();
        } else if (source == tabGezinInvoer) {
            clearTabGezinInvoer();
        }
    }

    private void clearTabs() {
        clearTabPersoon();
        clearTabPersoonInvoer();
        clearTabGezin();
        clearTabGezinInvoer();
    }

    private void clearTabPersoonInvoer() {
        //todo opgave 3
        tfVoornamenInvoer.clear();
        tfAchternaamInvoer.clear();
        tfTussenvoegselInvoer.clear();
        tfGebdatumInvoer.clear();
        tfGebplaatsInvoer.clear();
        cbGeslachtInvoer.getSelectionModel().clearSelection();
        cbOuderlijkGezinInvoer.getSelectionModel().clearSelection();
    }

    private void clearTabGezinInvoer() {
        cbOuder1Invoer.getSelectionModel().clearSelection();
        cbOuder2Invoer.getSelectionModel().clearSelection();
        tfHuwelijkInvoer.clear();
        tfScheidingInvoer.clear();
    }

    private void clearTabPersoon() {
        cbPersonen.getSelectionModel().clearSelection();
        tfPersoonNr.clear();
        tfVoornamen.clear();
        tfTussenvoegsel.clear();
        tfAchternaam.clear();
        tfGeslacht.clear();
        tfGebDatum.clear();
        tfGebPlaats.clear();
        cbOuderlijkGezin.getSelectionModel().clearSelection();
        lvAlsOuderBetrokkenBij.setItems(FXCollections.emptyObservableList());
    }

    private void clearTabGezin() {
        // todo opgave 3
        cbGezinnen.getSelectionModel().clearSelection();
        tfGezinSetHuwelijksdatum.clear();
        tfGezinSetScheidingsdatum.clear();
        tfGezinsNr.clear();
        tfGezinOuder1.clear();
        tfGezinOuder2.clear();
        tfGezinHuwelijksdatum.clear();
        tfGezinScheidingsdatum.clear();
        lvGezinKinderen.setItems(FXCollections.emptyObservableList());
    }

    private void showDialog(String type, String message) {
        Stage myDialog = new Dialog(getStage(), type, message);
        myDialog.show();
    }

    private Stage getStage() {
        return (Stage) menuBar.getScene().getWindow();
    }

}
