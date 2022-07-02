package org.vivoweb.dspacevivo.transformation.harvester.oai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.vivoweb.dspacevivo.model.Collection;
import org.vivoweb.dspacevivo.model.Item;

/**
 * Contains logic and model for iterative collection harvester
 * @author jorgg
 */
public class CollectionItr implements Iterator<Collection> {

    private DspaceOAI dspaceHarvester;
    private List<Collection> oaiPage = null;
    private Item nextCollection = null;

    /**
     * Iterative collection  constructor
     * @param hr OAI harvester instance
     */
    public CollectionItr(DspaceOAI hr) {
        this.dspaceHarvester = hr;
    }

    /**
     * Check if it is possible to extract the following collection from the repository and extract its metadata
     * @return True or False depend if next collection can be harvested
     */
    @Override
    public boolean hasNext() {
        if (oaiPage != null && !oaiPage.isEmpty()) {
            return true;
        }
        OAIPMHResponse oaipmhResponse;
        boolean hasNext = false;
        boolean iterate = false;
        if (!this.dspaceHarvester.isEmpty()) {
            String responseXML = null;
            do {
                try {
                    responseXML = this.dspaceHarvester.getHttpClient().doRequest(this.dspaceHarvester.getBaseURI(), this.dspaceHarvester.getVerb(), this.dspaceHarvester.getSet(), this.dspaceHarvester.getFrom(), this.dspaceHarvester.getUntil(), this.dspaceHarvester.getMetadata(),
                            this.dspaceHarvester.getResumptionToken(), this.dspaceHarvester.getIdentifier());

                    oaipmhResponse = new OAIPMHResponse(responseXML, dspaceHarvester.getConf() , dspaceHarvester.getMapping());
                    oaiPage = oaipmhResponse.modelCollections();

                    for (Collection col : oaiPage) {
                        String id = col.getId();

                        col.setHasItem(new ArrayList());
                        Iterator<Item> harvestItemsItr = this.dspaceHarvester.harvestItems();
                        this.dspaceHarvester.setSet(id);
                        while (harvestItemsItr.hasNext()) {
                            col.getHasItem().add(harvestItemsItr.next());

                        }
                        this.dspaceHarvester.getRecoverSets().removeIf(element -> (element.startsWith("col")));
                        col.setIsPartOfCommunityID(this.dspaceHarvester.getRecoverSets());

                    }
                } catch (Exception ex) {
                    return hasNext;
                }

                Optional<String> resumptionToken = oaipmhResponse.getResumptionToken();
                if (resumptionToken.isPresent() && !resumptionToken.get().isEmpty()) {
                    this.dspaceHarvester.setResumptionToken(resumptionToken.get());
                } else {
                    this.dspaceHarvester.setResumptionToken(null);
                    this.dspaceHarvester.setEmpty(true);

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
     * Recover the next collection inte the repository
     * @return Collection object
     */
    @Override
    public Collection next() {
        Collection get = oaiPage.get(0);
        oaiPage.remove(get);
        return get;
    }

}
