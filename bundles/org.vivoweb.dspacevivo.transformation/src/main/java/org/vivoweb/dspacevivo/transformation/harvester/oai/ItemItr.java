package org.vivoweb.dspacevivo.transformation.harvester.oai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.vivoweb.dspacevivo.model.Item;

/**
 * Contains logic and model for iterative item harvester
 * @author jorgg
 */
public class ItemItr implements Iterator<Item> {

    private DspaceOAI dspaceHarvester;
    private List<Item> oaiPage = null;
    private Item nextItem = null;
    private boolean finalValue;
    private List<String> setsList = null;

    /**
     * Iterative item harvester constructor
     * @param hr OAI harvester instance
     */
    public ItemItr(DspaceOAI hr) {
        this.dspaceHarvester = hr;
        this.finalValue = false;
        setsList = new ArrayList();
    }

    /**
     * Check if it is possible to extract the following item from the repository and extract its metadata
     * @return True or False depend if next item can be harvested
     */
    @Override
    public boolean hasNext() {
        if (oaiPage != null && !oaiPage.isEmpty()) {
            return true;
        }
        OAIPMHResponse oaipmhResponse;
        boolean hasNext = false;
        boolean iterate = false;
        if (!finalValue) {
            String responseXML = null;
            do {
                try {
                    responseXML = this.dspaceHarvester.getHttpClient().doRequest(this.dspaceHarvester.getBaseURI(), this.dspaceHarvester.getVerb(), this.dspaceHarvester.getSet(), this.dspaceHarvester.getFrom(), this.dspaceHarvester.getUntil(), this.dspaceHarvester.getMetadata(),
                            this.dspaceHarvester.getResumptionToken(), this.dspaceHarvester.getIdentifier());
                
                    oaipmhResponse = new OAIPMHResponse(responseXML, dspaceHarvester.getConf() , dspaceHarvester.getMapping() );
                    oaiPage = oaipmhResponse.modelItemsxoai();
                    this.dspaceHarvester.setRecoverSets(new ArrayList());
                    for (String spec : oaipmhResponse.getSetSpec()) {
                        if (!this.dspaceHarvester.getRecoverSets().contains(spec)) {
                            this.dspaceHarvester.getRecoverSets().add(spec);
                        }

                    }
                } catch (Exception ex) {
                    return hasNext;
                }

                Optional<String> resumptionToken = oaipmhResponse.getResumptionToken();
                if (resumptionToken.isPresent() && !resumptionToken.get().isEmpty()) {
                    this.dspaceHarvester.setResumptionToken(resumptionToken.get());
                } else {
                    this.dspaceHarvester.setResumptionToken(null);
                    finalValue = true;

                }

                if (!oaiPage.isEmpty()) {
                    hasNext = true;
                    iterate = false;
                } else if (this.dspaceHarvester.getResumptionToken() != null) {
                    iterate = true;
                } else {
                    hasNext = false;
                    iterate = false;
                }

            } while (iterate);

        }
        return hasNext;
    }
    
    /**
     * Recover the next item into the repository
     * @return Item object
     */
    @Override
    public Item next() {
        Item get = oaiPage.get(0);
        oaiPage.remove(get);

        return get;
    }

}
