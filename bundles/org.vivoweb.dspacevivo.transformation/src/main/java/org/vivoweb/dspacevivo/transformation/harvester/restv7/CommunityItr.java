package org.vivoweb.dspacevivo.transformation.harvester.restv7;

import java.util.Iterator;
import java.util.List;
import kong.unirest.JsonNode;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.apache.jena.ext.com.google.common.collect.Lists;
import org.vivoweb.dspacevivo.model.Collection;
import org.vivoweb.dspacevivo.model.Community;
import org.vivoweb.dspacevivo.model.Item;

public class CommunityItr implements Iterator<Community> {

    private int page = 0;
    private int size = 20;
    private List<Community> restPage = Lists.newArrayList();
    private Community nextItem = null;
    private RESTv7Harvester endpoint;

    public CommunityItr(RESTv7Harvester endpoint) {
        this.endpoint = endpoint;
    }

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

    @Override
    public Community next() {
        Community get = restPage.get(0);
        restPage.remove(get);
        return get;
    }

}
