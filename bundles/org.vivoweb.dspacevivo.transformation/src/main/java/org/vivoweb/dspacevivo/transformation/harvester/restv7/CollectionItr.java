package org.vivoweb.dspacevivo.transformation.harvester.restv7;

import java.util.Iterator;
import java.util.List;
import kong.unirest.JsonNode;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.apache.jena.ext.com.google.common.collect.Lists;
import org.vivoweb.dspacevivo.model.Collection;

/**
 * Harvest collection metadata for Restv7 Harvester
 * @author jorgg
 */
public class CollectionItr implements Iterator<Collection> {

    private int page = 0;
    private int size = 20;
    private List<Collection> restPage = Lists.newArrayList();
    private Collection nextItem = null;
    private RESTv7Harvester endpoint;

    /**
     * Collection iterator Constructor 
     * @param endpoint Restv7Harvester instance
     */
    public CollectionItr(RESTv7Harvester endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * Check if exist a next collection in the response and collect metadata
     * @return 
     */
    @Override
    public boolean hasNext() {
        if (!restPage.isEmpty()) {
            return true;
        }
        JsonNode body = this.endpoint.getCollectionPage(this.page, this.size);
        this.page++;
        boolean hasNext = body.getObject().has("_embedded");
        if (hasNext) {
            JSONArray jsonArray = body.getObject().getJSONObject("_embedded").getJSONArray("collections");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Collection collection = this.endpoint.getCollection(jsonObject);
                restPage.add(collection);
            }

        }
        return !restPage.isEmpty();
    }

    /**
     * Recover the next Collection into the repository
     * @return Collectio object
     */
    @Override
    public Collection next() {
        Collection get = restPage.get(0);
        restPage.remove(get);
        return get;
    }

}
