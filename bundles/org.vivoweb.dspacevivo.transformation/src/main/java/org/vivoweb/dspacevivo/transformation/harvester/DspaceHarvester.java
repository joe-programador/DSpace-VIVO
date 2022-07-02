package org.vivoweb.dspacevivo.transformation.harvester;

import java.util.Iterator;
import java.util.Properties;
import org.vivoweb.dspacevivo.model.Collection;
import org.vivoweb.dspacevivo.model.Community;
import org.vivoweb.dspacevivo.model.Item;
import org.vivoweb.dspacevivo.model.Repository;

/**
 * Abstract method for Dspace Harvester logic
 * @author jorgg
 */
public abstract class DspaceHarvester {
  
    protected Properties conf = null;
    protected Properties mapping = null;
    
    /**
     * General constructor for Dspace Harvester
     * @param conf Set confing  params for harvester
     */
    public DspaceHarvester(Properties conf) {
        this.conf = conf;
    }
    
    /**
     * Get configuration properties
     * @return Harvest process setup params 
     */
    public Properties getConf() {
        return conf;
    }
    
    /**
     * Get mapping properties
     * @return mapping proccess vocabulary
     */
    public Properties getMapping() {
        return mapping;
    }
    
    /**
     * Set mapping properties
     * @param mapping mapping proccess vocabulary
     */
    public void setMapping(Properties mapping) {
        this.mapping = mapping;
    }
    
    /**
     * Connect logic for harvester proccess
     */
    public abstract void connect();
    
    /**
     * Contains iterator logic for harvest items
     */
    public abstract Iterator<Item> harvestItems();
    
    /**
     * Contains iterator logic for harvest community
     */
    public abstract Iterator<Community> harvestCommunity();
    
    /**
     * Contains iterator logic for harvest collection
     */
    public abstract Iterator<Collection> harvestCollection();

    /**
     * Contains iterator logic for harvest repository
     */
    public abstract Iterator<Repository> harvestRepository();
}
