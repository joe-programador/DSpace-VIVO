package org.vivoweb.dspacevivo.transformation.harvester;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vivoweb.dspacevivo.model.Collection;
import org.vivoweb.dspacevivo.model.Community;
import org.vivoweb.dspacevivo.model.Item;
import org.vivoweb.dspacevivo.model.Repository;
import org.vivoweb.dspacevivo.transformation.harvester.config.HarvesterConfiguration;
import org.vivoweb.dspacevivo.transformation.harvester.oai.DspaceOAI;
import org.vivoweb.dspacevivo.transformation.harvester.restv7.RESTv7Harvester;

/**
 * Execution logic for harvester process
 * @author jorgg
 */
public class HarvesterRunner {

    private static Logger logger = LoggerFactory.getLogger(HarvesterRunner.class);
    private DspaceHarvester dh = null;
    private static String pathConfigFile = null;

    /**
     * Set path of config file 
     * @param pathConfigFile 
     */
    public void setPathConfigFile(String pathConfigFile) {
        HarvesterRunner.pathConfigFile = pathConfigFile;
    }

    /**
     * Get path of config file
     * @return 
     */
    public String getPathConfigFile() {
        return pathConfigFile;
    }

    /**
     * Init the harvest process. This process executes either of the two types of harvesters as indicated in the configuration file.
     * @throws IOException 
     */
    public void init( ) throws IOException {
        Properties conf = HarvesterConfiguration.getConf();
        Properties mapping = HarvesterConfiguration.getMapping();
        if (pathConfigFile != null ){
           conf = HarvesterConfiguration.getConf( getPathConfigFile()); 
        } 
        switch (conf.getProperty("type")) {
            case "RESTv7":
                logger.info("Connecting to REST endpoint");
                dh = new RESTv7Harvester(conf );

                break;
            case "OAI":
                logger.info("Connecting to OAI-PMH endpoint");
                dh = new DspaceOAI(conf);
                break;
        }
        dh.setMapping(mapping);
        dh.connect();
    }

    /**
     * Iterates and extracts the metadata of the items from the Dspace repository
     * @throws JsonProcessingException 
     */
    public void harvestItems() throws JsonProcessingException {
        ObjectMapper mp = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        Iterator<Item> harvestItemsItr = dh.harvestItems();
        int count = 0;
        if (harvestItemsItr != null) {
            while (harvestItemsItr.hasNext()) {
                count++;
                Item next = harvestItemsItr.next();
                logger.info("new Item harvested...");
                logger.info(" " + count);
                logger.info(mp.writeValueAsString(next));
            }
        }
    }

    /**
     * Iterates and extracts the metadata of the Collections  from the Dspace repository
     * @throws JsonProcessingException 
     */    
    public void harvestCollections() throws JsonProcessingException {
        ObjectMapper mp = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        Iterator<Collection> harvestCollection = dh.harvestCollection();
        int count = 0;
        if (harvestCollection != null) {
            while (harvestCollection.hasNext()) {
                count++;
                Collection next = harvestCollection.next();
                logger.info("new Collection harvested...");
                logger.info(" " + count);
                logger.info(mp.writeValueAsString(next));
            }
        }
    }

    /**
     * Iterates and extracts the metadata of the Communities  from the Dspace repository
     * @throws JsonProcessingException 
     */ 
    public void harvestCommunities() throws JsonProcessingException {
        ObjectMapper mp = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        Iterator<Community> harvestCommunity = dh.harvestCommunity();
        int count = 0;
        if (harvestCommunity != null) {
            while (harvestCommunity.hasNext()) {
                count++;
                Community next = harvestCommunity.next();
                logger.info("new Community harvested...");
                logger.info(" " + count);
                logger.info(mp.writeValueAsString(next));
            }
        }
    }

    /**
     * Iterates over all available resources in the repository (Communities, collections , etc) 
     * @throws JsonProcessingException 
     */   
    public void harvestRepositories() throws JsonProcessingException {
        ObjectMapper mp = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        Iterator<Repository> harvestRepository = dh.harvestRepository();
        int count = 0;
        if (harvestRepository != null) {
            while (harvestRepository.hasNext()) {
                count++;
                Repository next = harvestRepository.next();
                logger.info("new Repository harvested...");
                logger.info(" " + count);
                logger.info(mp.writeValueAsString(next));
            }
        }
    }

}
