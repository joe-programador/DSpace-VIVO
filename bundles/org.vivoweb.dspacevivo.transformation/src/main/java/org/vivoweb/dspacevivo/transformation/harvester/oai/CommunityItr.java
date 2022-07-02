package org.vivoweb.dspacevivo.transformation.harvester.oai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.vivoweb.dspacevivo.model.Collection;
import org.vivoweb.dspacevivo.model.Community;
import org.vivoweb.dspacevivo.model.Item;

/**
 * Contains logic and model for iterative community harvester
 * @author jorgg
 */
public class CommunityItr implements Iterator<Community> {

    private DspaceOAI dspaceHarvester;
    private List<Community> oaiPage = null;
    private Item nextCommunity = null;
    
    /**
     * Iterative community harvester constructor
     * @param hr OAI harvester instance
     */
    public CommunityItr(DspaceOAI hr) {
        this.dspaceHarvester = hr;
    }

    /**
     * Check if it is possible to extract the following community from the repository and extract its metadata
     * @return True or False depend if next community can be harvested
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

                    oaiPage = oaipmhResponse.modelCommunity();

                    HashMap<String, List<Collection>> hmCollection = new HashMap<String, List<Collection>>();
                    Iterator<Collection> CollectionItr = this.dspaceHarvester.harvestCollection();
                    while (CollectionItr.hasNext()) {
                        Collection cl = CollectionItr.next();
                        for (String comId : cl.getIsPartOfCommunityID()) {
                            if (!hmCollection.containsKey(comId)) {
                                hmCollection.put(comId, new ArrayList());

                            }
                            hmCollection.get(comId).add(cl);
                        }

                    }

                    for (Community com : oaiPage) {
                        String id = com.getId();
                        com.setHasCollection(hmCollection.get(id));

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
     * Recover the next community into the repository
     * @return Collection object
     */
    @Override
    public Community next() {
        Community get = oaiPage.get(0);
        oaiPage.remove(get);

        return get;
    }

}
