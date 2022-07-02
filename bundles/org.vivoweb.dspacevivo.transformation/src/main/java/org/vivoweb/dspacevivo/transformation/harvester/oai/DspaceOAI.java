package org.vivoweb.dspacevivo.transformation.harvester.oai;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.vivoweb.dspacevivo.model.Collection;
import org.vivoweb.dspacevivo.model.Community;
import org.vivoweb.dspacevivo.model.Item;
import org.vivoweb.dspacevivo.model.Repository;
import org.vivoweb.dspacevivo.transformation.harvester.DspaceHarvester;

/**
 * Implementation of the Dspace Harvester class. Using for harvester repositories via OAI-PMH protocol.
 * @author jorgg
 */
public class DspaceOAI extends DspaceHarvester {

    private static final String NO_TOKEN = null;
    private String resumptionToken = NO_TOKEN;
    private URI baseURI;
    private String verb;
    private String metadata;
    private String until;
    private String from;
    private String set;
    private String identifier;
    private OAIPMHHttpClient httpClient;
    private boolean empty = false;
    private List<String> recoverSets;

    /**
     * DspaceOAI constructor
     * @param prprts configuration parameters of the harvest process
     */
    public DspaceOAI(Properties prprts) {
        super(prprts);
    }

    /**
     * Establishes a connection with the repository through the OAI-PMH protocol using http
     */
    @Override
    public void connect() {
        try {
            this.baseURI = new URI(this.conf.getProperty("endpoint"));
        } catch (URISyntaxException ex) {
        }
        this.httpClient = new OAIPMHHttpClient();
    }

    /**
     * Model with parameters for items recover request
     * @return Iterator with items objects
     */
    @Override
    public Iterator<Item> harvestItems() {
        this.verb = "ListRecords";
        this.metadata = "xoai";
        this.until = null;
        this.from = null;
        this.set = null;
        this.identifier = null;
        this.recoverSets = null;

        if (OAIPMHVerb.valueOf(verb) == OAIPMHVerb.ListMetadataFormats || OAIPMHVerb.valueOf(verb) == OAIPMHVerb.ListSets
                || OAIPMHVerb.valueOf(verb) == OAIPMHVerb.Identify) {
            this.metadata = null;
            this.until = null;
            this.from = null;
            this.set = null;
            this.identifier = null;
        }
        return new ItemItr(this);
    }

    /**
     * Get list  with sets obtain in the harvest
     * @return 
     */
    public List<String> getRecoverSets() {
        return recoverSets;
    }

    /**
     * Set list  with sets obtain in the harvest
     * @param recoverSets 
     */
    public void setRecoverSets(List<String> recoverSets) {
        this.recoverSets = recoverSets;
    }

    /**
     * Model with parameters for collection recover request
     * @return Iterator with collections objects
     */
    @Override
    public Iterator<Collection> harvestCollection() {
        this.verb = "ListSets";
        this.metadata = null;
        this.until = null;
        this.from = null;
        this.set = null;
        this.identifier = null;

        if (OAIPMHVerb.valueOf(verb) == OAIPMHVerb.ListMetadataFormats || OAIPMHVerb.valueOf(verb) == OAIPMHVerb.ListSets
                || OAIPMHVerb.valueOf(verb) == OAIPMHVerb.Identify) {
            this.metadata = null;
            this.until = null;
            this.from = null;
            this.set = null;
            this.identifier = null;
        }
        return new CollectionItr(this);
    }

    /**
     * Model with parameters for community recover request
     * @return Iterator with community objects
     */
    @Override
    public Iterator<Community> harvestCommunity() {
        this.verb = "ListSets";
        this.metadata = null;
        this.until = null;
        this.from = null;
        this.set = null;
        this.identifier = null;

        if (OAIPMHVerb.valueOf(verb) == OAIPMHVerb.ListMetadataFormats || OAIPMHVerb.valueOf(verb) == OAIPMHVerb.ListSets
                || OAIPMHVerb.valueOf(verb) == OAIPMHVerb.Identify) {
            this.metadata = null;
            this.until = null;
            this.from = null;
            this.set = null;
            this.identifier = null;
        }
        return new CommunityItr(this);
    }

    /**
     * Model with parameters for repository recover request
     * @return Iterator with repository objects
     */
    @Override
    public Iterator<Repository> harvestRepository() {
        this.verb = "Identify";
        this.metadata = null;
        this.until = null;
        this.from = null;
        this.set = null;
        this.identifier = null;

        if (OAIPMHVerb.valueOf(verb) == OAIPMHVerb.ListMetadataFormats || OAIPMHVerb.valueOf(verb) == OAIPMHVerb.ListSets
                || OAIPMHVerb.valueOf(verb) == OAIPMHVerb.Identify) {
            this.metadata = null;
            this.until = null;
            this.from = null;
            this.set = null;
            this.identifier = null;
        }
        return new RepositoryItr(this);
    }

    /**
     * Get Resumption token using for iterative request
     * @return 
     */
    public String getResumptionToken() {
        return resumptionToken;
    }

    /**
     * Set Resumption token using for iterative request
     * @param resumptionToken
     */
    public void setResumptionToken(String resumptionToken) {
        this.resumptionToken = resumptionToken;
    }

    /**
     * Get repository base  uri use in the request
     * @return 
     */
    public URI getBaseURI() {
        return baseURI;
    }

    /**
     * Set repository base uri use in the request
     * @param baseURI
     */
    public void setBaseURI(URI baseURI) {
        this.baseURI = baseURI;
    }

    /**
     * Get OAI-PMH verb use in the request
     * @return 
     */
    public String getVerb() {
        return verb;
    }

    /**
     * Set OAI-PMH verb use in the request
     * @param verb
     */
    public void setVerb(String verb) {
        this.verb = verb;
    }

    /**
     * Get metadata format use in the request (oai_dc , xoai, etc)
     * @return 
     */
    public String getMetadata() {
        return metadata;
    }

    /**
     * Set metadata format use in the request (oai_dc , xoai, etc)
     * @param metadata 
     */
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    /**
     * Get until date for filter the response in the request
     * @return 
     */
    public String getUntil() {
        return until;
    }

    /**
     * Set until date for filter the response in the request
     * @param until 
     */
    public void setUntil(String until) {
        this.until = until;
    }

    /**
     * Get from date for filter the response in the request
     * @return 
     */
    public String getFrom() {
        return from;
    }

    /**
     * Get until date for filter the response in the request
     * @param from 
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Get the set or collection in the request
     * @return 
     */
    public String getSet() {
        return set;
    }

    /**
     * Set the set or collection in the request
     * @param set 
     */
    public void setSet(String set) {
        this.set = set;
    }

    /**
     * Get the resource identifier in the request
     * @return 
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Set the resource identifier in the request
     * @param identifier 
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Get http client connection
     * @return 
     */
    public OAIPMHHttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Set http client connection
     * @param httpClient 
     */
    public void setHttpClient(OAIPMHHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Get the auxiliar parameter that indicate if the response is empty
     * @return Boolean
     */
    public boolean isEmpty() {
        return empty;
    }

    /**
     * Set the auxiliar parameter that indicate if the response is empty
     * @param empty 
     */
    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

}
