package org.vivoweb.dspacevivo.transformation;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vivoweb.dspacevivo.transformation.harvester.HarvesterRunner;

/**
 * Run standalone version for test purposes
 * @author jorgg
 */
public class ConsoleApplication {

    private static Logger logger = LoggerFactory.getLogger(ConsoleApplication.class);
    
    /**
     * Run the repository data harvesting and transformation process
     * @param args Config file path (Optional)
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {

        logger.info("Testing Dspace Harvester...");
        logger.info("PARAMS...");
        
        HarvesterRunner runner = new HarvesterRunner();
        if (args.length == 1){
            logger.info(args[0].toString());
            runner.setPathConfigFile(args[0]);
        }
        logger.info("Init Dspace Harvester...");
        runner.init();
        logger.info("Harvesting Items...");
        runner.harvestItems();
        logger.info("Harvesting Communities...");
        //runner.harvestCommunities();
        logger.info("Harvesting Collections...");
        //runner.harvestCollections();
        logger.info("Harvesting Repositories...");
        //runner.harvestRepositories();
    }
    
}
