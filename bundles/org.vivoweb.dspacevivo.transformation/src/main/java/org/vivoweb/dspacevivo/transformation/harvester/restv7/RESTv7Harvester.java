package org.vivoweb.dspacevivo.transformation.harvester.restv7;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.apache.commons.compress.utils.Lists;
import org.vivoweb.dspacevivo.model.Collection;
import org.vivoweb.dspacevivo.model.Community;
import org.vivoweb.dspacevivo.model.Item;
import org.vivoweb.dspacevivo.model.Repository;
import org.vivoweb.dspacevivo.transformation.harvester.DspaceHarvester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vivoweb.dspacevivo.model.Statement;
import org.vivoweb.dspacevivo.model.StatementLiteral;

/**
 * Implementation of the Dspace Harvester class. Using for harvester repositories via Rest API.
 * @author jorgg
 */
public class RESTv7Harvester extends DspaceHarvester {

    private static Logger log = LoggerFactory.getLogger(RESTv7Harvester.class);
    private String csrfToken = null;
    private String authToken = null;

    /**
     * Rest Harvester constructor
     * @param prprts 
     */
    public RESTv7Harvester(Properties prprts) {
        super(prprts);
    }
    
    /**
     * Execute the request to API Rest
     * @param i
     * @return 
     */
    private HttpResponse<JsonNode> updateCSRF(HttpResponse<JsonNode> i) {
        List<String> get = i.getHeaders().get("DSPACE-XSRF-TOKEN");
        if (get.size() > 0) {
            this.csrfToken = get.get(0);
        }
        return i;
    }

    /**
     * Connect to API using  credentials and recover authorization token
     */
    @Override
    public void connect() {
        updateCSRF(Unirest.get(this.conf.getProperty("endpoint") + "/authn/status").asJson());
        this.authToken = updateCSRF(Unirest.post(this.conf.getProperty("endpoint") + "/authn/login")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("X-XSRF-TOKEN", this.csrfToken)
                .field("user", this.conf.getProperty("username"))
                .field("password", this.conf.getProperty("password"))
                .asJson()).getHeaders().get("Authorization").get(0);
    }

    /**
     * Request for items in the repository
     * @param page
     * @param size
     * @return 
     */
    public JsonNode getItemsPage(int page, int size) {
        return updateCSRF(Unirest.get(this.conf.getProperty("endpoint") + "/discover/browses/title/items?sort=dc.title,ASC&size=" + size + "&page=" + page)
                .header("X-XSRF-TOKEN", this.csrfToken)
                .header("Authorization", this.authToken)
                .asJson()).getBody();
    }
    
    /**
     * Request for collections in the repository
     * @param page
     * @param size
     * @return 
     */
    public JsonNode getCollectionPage(int page, int size) {
        return updateCSRF(Unirest.get(this.conf.getProperty("endpoint") + "/core/collections?page=" + page + "&size=" + size)
              .header("X-XSRF-TOKEN", this.csrfToken)
              .header("Authorization", this.authToken)
              .asJson()).getBody();
    }
    
    /**
     * Request for communities in the repository
     * @param page
     * @param size
     * @return 
     */
    public JsonNode getCommunityPage(int page, int size) {
        return updateCSRF(Unirest.get(this.conf.getProperty("endpoint") + "/core/communities?page=" + page + "&size=" + size)
              .header("X-XSRF-TOKEN", this.csrfToken)
              .header("Authorization", this.authToken)
              .asJson()).getBody();
    }
    
    /**
     * Harvest items iterator implementation 
     * @return 
     */
    @Override
    public Iterator<Item> harvestItems() {
        return new ItemItr((this));
    }
    
    /**
     * Harvest collection iterator implementation 
     * @return 
     */
    @Override
    public Iterator<Collection> harvestCollection() {
        return new CollectionItr((this));
    }

    /**
     * Execute a request to Api Rest
     * @param link 
     * @return 
     */
    private JsonNode calllinks(String link) {
        return updateCSRF(Unirest.get(link)
                .header("X-XSRF-TOKEN", this.csrfToken)
                .header("Authorization", this.authToken)
                .asJson()).getBody();

    }

    /**
     * Recover metadata item and return item object
     * @param jsonObject
     * @return 
     */
    public Item getItem(JSONObject jsonObject) {
        Item resp = new Item();
        resp.setId(jsonObject.getString("id"));
        resp.setUri(jsonObject.getString("handle"));
        resp.setUri(this.conf.getProperty("uriPrefix") + jsonObject.getString("handle"));
        resp.setUrl(jsonObject.getJSONObject("_links").getJSONObject("self").getString("href"));

        JsonNode collectionLink = calllinks(jsonObject.getJSONObject("_links").getJSONObject("owningCollection").getString("href"));

        resp.dspaceIsPartOfCollectionID(Lists.newArrayList());
        resp.getDspaceIsPartOfCollectionID().add(collectionLink.getObject().getString("handle"));

        resp.setListOfStatements(Lists.newArrayList());
        resp.setListOfStatementLiterals(Lists.newArrayList());
        List<Object> metadataMapping = metadataMapping(resp.getUri(), jsonObject.getJSONObject("metadata"));
        for (Object obj : metadataMapping) {
            if (obj instanceof StatementLiteral) {
                resp.getListOfStatementLiterals().add((StatementLiteral) obj);
            } else {
                resp.getListOfStatements().add((Statement) obj);
            }
        }

        return resp;

    }
    
    /**
     * Recover metadata collection and return collection object
     * @param jsonObject
     * @return 
     */
    public Collection getCollection (JSONObject jsonObject) {
        Collection resp = new Collection();
        System.out.println (jsonObject);

        resp.setId(jsonObject.getString("id"));
        resp.setUri(this.conf.getProperty("uriPrefix") + jsonObject.getString("handle"));
        resp.setUrl(jsonObject.getJSONObject("_links").getJSONObject("self").getString("href"));
        
        JsonNode body = calllinks (jsonObject.getJSONObject("_links").getJSONObject("parentCommunity").getString("href"));
        System.out.println ("Parent");
        resp.setIsPartOfCommunityID(Lists.newArrayList());
        
        if (body.getObject().has("metadata")){
            System.out.println (body.getObject().getJSONObject("metadata").getJSONArray("dc.identifier.uri").getJSONObject(0).getString("value"));
            resp.getIsPartOfCommunityID().add(body.getObject().getJSONObject("metadata").getJSONArray("dc.identifier.uri").getJSONObject(0).getString("value"));
        
        }
        System.out.println ("Items");
        JsonNode bodyItems = calllinks (jsonObject.getJSONObject("_links").getJSONObject("harvester").getString("href"));
        System.out.println (bodyItems);
        
        resp.setListOfStatements(Lists.newArrayList());
        resp.setListOfStatementLiterals(Lists.newArrayList());
        System.out.println (resp.getUrl());
        List<Object> metadataMapping = metadataMapping(resp.getUri(), jsonObject.getJSONObject("metadata"));
        for (Object obj : metadataMapping) {
            if (obj instanceof StatementLiteral) {
                resp.getListOfStatementLiterals().add((StatementLiteral) obj);
            } else {
                resp.getListOfStatements().add((Statement) obj);
            }
        }
        return resp;
    }
    
        /**
         * Recover metadata community and return community object
         * @param jsonObject
         * @return 
         */
        public Community getCommunity (JSONObject jsonObject) {
        Community resp = new Community();
        System.out.println (jsonObject);

        resp.setId(jsonObject.getString("id"));
        resp.setUri(this.conf.getProperty("uriPrefix") + jsonObject.getString("handle"));
        resp.setUrl(jsonObject.getJSONObject("_links").getJSONObject("self").getString("href"));
        
        JsonNode parent = calllinks (jsonObject.getJSONObject("_links").getJSONObject("parentCommunity").getString("href"));
        System.out.println ("Parent");
        resp.setIsSubcommunityOfID(Lists.newArrayList());
        
        if (parent.getObject().has("metadata")){
            System.out.println (parent.getObject().getJSONObject("metadata").getJSONArray("dc.identifier.uri").getJSONObject(0).getString("value"));
            //resp.setIsSubcommunityOfID().add(body.getObject().getJSONObject("metadata").getJSONArray("dc.identifier.uri").getJSONObject(0).getString("value"));
            resp.getIsSubcommunityOfID().add(parent.getObject().getJSONObject("metadata").getJSONArray("dc.identifier.uri").getJSONObject(0).getString("value"));
        }
              
        JsonNode subcom = calllinks (jsonObject.getJSONObject("_links").getJSONObject("subcommunities").getString("href"));
        resp.hasSubCommunity(Lists.newArrayList());
      
        if (subcom.getObject().has("_embedded")){
            JSONArray jsonArray = subcom.getObject().getJSONObject("_embedded").getJSONArray("subcommunities");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectcom = jsonArray.getJSONObject(i);
                Community community = this.getCommunity(jsonObjectcom);
                resp.getHasSubCommunity().add(community);
            }
        } 
        JsonNode collections = calllinks (jsonObject.getJSONObject("_links").getJSONObject("collections").getString("href"));
        resp.setHasCollection(Lists.newArrayList());
        
        if (collections.getObject().has("_embedded")){
            JSONArray jsonArray = collections.getObject().getJSONObject("_embedded").getJSONArray("collections");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectcol = jsonArray.getJSONObject(i);
                Collection collection = this.getCollection(jsonObjectcol);
                resp.getHasCollection().add(collection);
            }
        }
                
        resp.setListOfStatements(Lists.newArrayList());
        resp.setListOfStatementLiterals(Lists.newArrayList());
        System.out.println (resp.getUrl());
        List<Object> metadataMapping = metadataMapping(resp.getUri(), jsonObject.getJSONObject("metadata"));
        for (Object obj : metadataMapping) {
            if (obj instanceof StatementLiteral) {
                resp.getListOfStatementLiterals().add((StatementLiteral) obj);
            } else {
                resp.getListOfStatements().add((Statement) obj);
            }
        }
        
        
        
    
        return resp;
    }

    /**
     * Recover metadata community and return community object
     * @return 
     */
    @Override
    public Iterator<Community> harvestCommunity() {
        //TODO
        return new CommunityItr((this));
    }

    /**
     * Recover metadata repository and return repository object
     * @return 
     */    
    @Override
    public Iterator<Repository> harvestRepository() {
        //TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Mapping properties to valids urls
     * @param uri
     * @param metadata
     * @return 
     */
    public List<Object> metadataMapping(String uri, JSONObject metadata) {
        List<Object> responseList = Lists.newArrayList();
        Properties mapping = this.getMapping();
        for (String key : metadata.keySet()) {
            JSONArray jsonArray = metadata.getJSONArray(key);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //Skip nulls
                if (jsonObject.get("value") == null) {
                    continue;
                }
                String property = null;
                String datatype = "xsd:string";
                if (mapping.containsKey(key)) {
                property = mapping.getProperty(key).split(";")[0];
               
                datatype = mapping.getProperty(key).split(";")[1];
                }
                
                if (property == null) {
                    log.warn("Property without mapping:'" + key + "'");
                    continue;
                }
                StatementLiteral statementLiteral = new StatementLiteral();
                statementLiteral.setSubjectUri(uri);
                statementLiteral.setPredicateUri(property);
                statementLiteral.setObjectLiteral(jsonObject.getString("value"));
                statementLiteral.setLiteralType(datatype);
                responseList.add(statementLiteral);

            }
        }

        return responseList;
    }
}
