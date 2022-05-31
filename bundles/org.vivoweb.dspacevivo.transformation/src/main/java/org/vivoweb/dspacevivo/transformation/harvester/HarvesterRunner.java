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

public class HarvesterRunner {

    private static Logger logger = LoggerFactory.getLogger(HarvesterRunner.class);
    private DspaceHarvester dh = null;
    private static String pathConfigFile = null;

    public void setPathConfigFile(String pathConfigFile) {
        HarvesterRunner.pathConfigFile = pathConfigFile;
    }

    public String getPathConfigFile() {
        return pathConfigFile;
    }

    public void init( ) throws IOException {
        Properties conf = HarvesterConfiguration.getConf();
        if (pathConfigFile != null ){
           conf = HarvesterConfiguration.getConf( getPathConfigFile()); 
        } 
        switch (conf.getProperty("type")) {
            case "RESTv7":
                logger.info("Connecting to REST endpoint");
                dh = new RESTv7Harvester(conf);
                break;
            case "OAI":
                logger.info("Connecting to OAI-PMH endpoint");
                dh = new DspaceOAI(conf);
                break;
        }
        dh.connect();
    }

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
