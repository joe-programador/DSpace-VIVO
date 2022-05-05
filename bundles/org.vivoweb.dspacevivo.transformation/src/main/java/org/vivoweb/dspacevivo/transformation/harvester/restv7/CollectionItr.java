package org.vivoweb.dspacevivo.transformation.harvester.restv7;

import java.util.Iterator;
import java.util.List;
import kong.unirest.JsonNode;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.apache.jena.ext.com.google.common.collect.Lists;
import org.vivoweb.dspacevivo.model.Collection;
import org.vivoweb.dspacevivo.model.Item;

public class CollectionItr implements Iterator<Collection> {

    private int page = 0;
    private int size = 20;
    private List<Collection> restPage = Lists.newArrayList();
    private Collection nextItem = null;
    private RESTv7Harvester endpoint;

    public CollectionItr(RESTv7Harvester endpoint) {
        this.endpoint = endpoint;
    }

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

    @Override
    public Collection next() {
        Collection get = restPage.get(0);
        restPage.remove(get);
        return get;
    }

}
