package org.vivoweb.dspacevivo.transformation.harvester.restv7;

import java.util.Iterator;
import java.util.List;
import kong.unirest.JsonNode;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.apache.jena.ext.com.google.common.collect.Lists;
import org.vivoweb.dspacevivo.model.Community;

/**
 * Harvest community metadata for Restv7 Harvester
 * @author jorgg
 */
public class CommunityItr implements Iterator<Community> {

    private int page = 0;
    private final int size = 20;
    private List<Community> restPage = Lists.newArrayList();
    private Community nextItem = null;
    private RESTv7Harvester endpoint;

    /**
     * Community iterator Constructor 
     * @param endpoint Restv7Harvester instance
     */
    public CommunityItr(RESTv7Harvester endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * Check if exist a next community in the response and collect metadata
     * @return 
     */
    @Override
    public boolean hasNext() {
        if (!restPage.isEmpty()) {
            return true;
        }
        JsonNode body = this.endpoint.getCommunityPage(this.page, this.size);
        this.page++;
        boolean hasNext = body.getObject().has("_embedded");
        if (hasNext) {
            JSONArray jsonArray = body.getObject().getJSONObject("_embedded").getJSONArray("communities");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Community community = this.endpoint.getCommunity(jsonObject);
                restPage.add(community);
            }

        }
        return !restPage.isEmpty();
    }
    
    /**
     * Recover the next community into the repository
     * @return 
     */
    @Override
    public Community next() {
        Community get = restPage.get(0);
        restPage.remove(get);
        return get;
    }

}
