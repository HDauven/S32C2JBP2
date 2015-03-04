/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import stamboom.domain.Administratie;
import stamboom.storage.IStorageMediator;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.Properties;
import stamboom.storage.SerializationMediator;

public class StamboomController {

    private Administratie admin;
    private IStorageMediator storageMediator;

    /**
     * creatie van stamboomcontroller met lege administratie en onbekend
     * opslagmedium
     */
    public StamboomController() {
        admin = new Administratie();
        storageMediator = null;
    }

    public Administratie getAdministratie() {
        return admin;
    }

    /**
     * administratie wordt leeggemaakt (geen personen en geen gezinnen)
     */
    public void clearAdministratie() {
        admin = new Administratie();
    }

    /**
     * administratie wordt in geserialiseerd bestand opgeslagen
     *
     * @param bestand
     * @throws IOException
     */
    public void serialize(File bestand) throws IOException {
        //todo opgave 2
        try {
            SerializationMediator sm = new SerializationMediator();
            Properties prop = new Properties();
            prop.setProperty("file", bestand.getAbsolutePath());
            sm.configure(prop);
            sm.save(admin);
        } catch (IOException e) {
            e.printStackTrace();
        }       
    }

    /**
     * administratie wordt vanuit geserialiseerd bestand gevuld
     *
     * @param bestand
     * @throws IOException
     * @throws java.lang.ClassNotFoundException
     */
    public void deserialize(File bestand) throws IOException, ClassNotFoundException {
        //todo opgave 2
        try {
            SerializationMediator sm = new SerializationMediator();
            Properties prop = new Properties();
            prop.setProperty("file", bestand.getAbsolutePath());
            sm.configure(prop);
            if (admin != null)
            {
                admin = sm.load(); 
            } 
            else {
                throw new ClassNotFoundException();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
         
    }
    
    // opgave 4
    private void initDatabaseMedium() throws IOException {
//        if (!(storageMediator instanceof DatabaseMediator)) {
//            Properties props = new Properties();
//            try (FileInputStream in = new FileInputStream("database.properties")) {
//                props.load(in);
//            }
//            storageMediator = new DatabaseMediator(props);
//        }
    }
    
    /**
     * administratie wordt vanuit standaarddatabase opgehaald
     *
     * @throws IOException
     */
    public void loadFromDatabase() throws IOException {
        //todo opgave 4
    }

    /**
     * administratie wordt in standaarddatabase bewaard
     *
     * @throws IOException
     */
    public void saveToDatabase() throws IOException {
        //todo opgave 4
    }

}
