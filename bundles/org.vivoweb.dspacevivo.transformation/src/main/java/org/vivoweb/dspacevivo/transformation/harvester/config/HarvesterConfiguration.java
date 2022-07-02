package org.vivoweb.dspacevivo.transformation.harvester.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Load config and mapping file using in the harvester process
 * @author jorgg
 */
public class HarvesterConfiguration {
    
    private static String routeConfig =  "harvester.conf";
    private static String mappingFile =  "mapping.conf";
    
    /**
     * Load the config parameters coming from config file.
     * @return config harvester parameters (url repository , uri base , etc)
     * @throws IOException 
     */
    public static Properties getConf() throws IOException {
        Properties props = new Properties();
        String resourceName = routeConfig;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
            props.load(resourceStream);
        }
        return props;
    }
    
    /**
     * Load mapping parameters for create valid urls 
     * @return mapping properties
     * @throws IOException 
     */
    public static Properties getMapping() throws IOException {
        Properties props = new Properties();
        String resourceName = mappingFile;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
            props.load(resourceStream);
        }
        return props;
    }
    
    /**
     * Load config parameters for user defined config file
     * @param pathConfigFile Path to config file
     * @return config properties
     * @throws IOException 
     */
    public static Properties getConf(String pathConfigFile) throws IOException {
            Properties props = new Properties();
            String resourceName = pathConfigFile; 
            
            try (InputStream resourceStream = new FileInputStream(resourceName);) {
                props.load(resourceStream);
            }
            return props;

    }


    
   
}
