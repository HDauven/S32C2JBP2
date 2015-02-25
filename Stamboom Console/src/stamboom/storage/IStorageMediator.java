/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.storage;

import java.io.IOException;
import java.util.Properties;
import stamboom.domain.Administratie;

public interface IStorageMediator {
    
    /**
     * pre: medium isCorrectlyConfigured()
     * @return administratie is initialized with the data stored at the 
     * configured location
     * @throws IOException
     */
    Administratie load() throws IOException;
    
    /**
     * pre: medium isCorrectlyConfigured()
     * admin is stored at the configured location
     * @param admin
     * @throws IOException 
     */
    void save(Administratie admin) throws IOException;
    
    /**
     * the mediator is initialized with properties in props, so afterwards config()=props; 
     * the specific requirements with respect to the configuration depends on the 
     * selected medium; in case of serialization a File-object is needed, in
     * case of a database a driver, url, username and password is needed; 
     * de keys are always spelled in lower case.
     * @param props 
     * @return true if isCorrectlyConfigured(), otherwise false
     */
    boolean configure(Properties props);
    
    /**
     * 
     * @return the configuration of the mediator; if no configuration is present,
     * the return value will be null; 
     */
    Properties config();
    
    /**
     * 
     * @return true if the mediator is configured correctly, otherwise
     * false
     */
    boolean isCorrectlyConfigured();   
}
