package org.vivoweb.dspacevivo.transformation.harvester.oai;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vivoweb.dspacevivo.model.Collection;
import org.vivoweb.dspacevivo.model.Community;
import org.vivoweb.dspacevivo.model.Item;
import org.vivoweb.dspacevivo.model.Repository;
import org.vivoweb.dspacevivo.model.StatementLiteral;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * Handle and transform OAI_PMH request response 
 * @author jorgg
 */
public class OAIPMHResponse {

    private static final Logger LOG = LoggerFactory.getLogger(OAIPMHResponse.class);
    private String rawResponse;
    private Document xmlResponse;
    private List<String> setSpec;
    private static String XSLT_FILENAME = "src/main/resources/aoi_dc.xslt";
    private Properties prop;
    private Properties mapping;

    /**
     * Constructor with raw response result
     * @param rawResponse 
     */
    public OAIPMHResponse(String rawResponse) {
        this.rawResponse = rawResponse;
        this.setSpec = new ArrayList();
        parse();
    }
    
    /**
     * Constructor receiving raw response, config and mapping parameters
     * @param rawResponse 
     * @param p
     * @param mapping 
     */
    public OAIPMHResponse(String rawResponse, Properties p , Properties mapping) {
        this.rawResponse = rawResponse;
        parse();
        this.prop = p;
        this.setSpec = new ArrayList();
        this.mapping = mapping;
    }

    /**
     * Parse raw response to xml response
     */
    private void parse() {
        this.xmlResponse = Jsoup.parse(rawResponse, "", Parser.xmlParser());
    }

    /**
     * Parse raw response to Document xml
     * @param text
     * @return 
     */
    private static Document parse(String text) {
        return Jsoup.parse(text, "", Parser.xmlParser());
    }

    /**
     * Recover the resumption token for the next request
     * @return 
     */
    public Optional<String> getResumptionToken() {
        Optional<String> resumptionToken = Optional.empty();
        Elements elementsByTag = xmlResponse.getElementsByTag("resumptionToken");
        if (!elementsByTag.isEmpty()) {
            if (elementsByTag.size() > 1) {
                LOG.warn("Multiple 'resumptionToken' tags detected, taking the first one.");
            }
            Element xmlTag = elementsByTag.get(0);
            resumptionToken = Optional.of(xmlTag.text().trim());
        }
        return resumptionToken;
    }

    /**
     * Get raw response received
     * @return 
     */
    public String getRawResponse() {
        return rawResponse;
    }

    /**
     * Set raw response received
     * @param rawResponse 
     */
    public void setRawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
    }

    /**
     * Get xml response format
     * @return 
     */
    public Document getXmlResponse() {
        return xmlResponse;
    }

    /**
     * Set xml response format
     * @param xmlResponse 
     */
    public void setXmlResponse(Document xmlResponse) {
        this.xmlResponse = xmlResponse;
    }

    /**
     * Initialize an instance of the collection and assign values
     * @return Collection object
     */
    public Collection modelCollection() {
        Collection col = new Collection();
        col.setId("");
        col.setIsPartOfCommunityID(new ArrayList());
        col.setListOfStatementLiterals(new ArrayList());
        col.setUri("");
        col.setUrl("");
        col.setHasItem(new ArrayList());

        return col;
    }

    /**
     * Initialize an instance of item and assign values
     * @param doc
     * @param head
     * @return 
     */
    public Item modelItem(Document doc, Document head) {
        Document result = doc;
        Item resp = new Item();

        resp.setDspaceIsPartOfCollectionID(new ArrayList());
        Elements listh = head.getElementsByTag("header");
        for (Element e : listh.get(0).children()) {
            String htex = e.text();
            String htag = e.tagName();

            if ("identifier".equals(htag)) {
                resp.setId(htex.split(":")[2]);
            } else if ("setSpec".equals(htag)) {
                setSpec.add(htex);
                if (htex.contains("col")) {
                    resp.getDspaceIsPartOfCollectionID().add(htex);
                }
            }

        }

        Elements list = result.getElementsByTag("oai_dc");

        String id = resp.getId();
        String uri = this.prop.getProperty("uriPrefix") + id;
        resp.setListOfStatementLiterals(new ArrayList());
        for (Element e : list.get(0).children()) {
            String text = e.text();
            String tag = e.tagName();
            switch (tag) {
                case "dc:identifier":
                    resp.setUri(text);
                    break;
                case "dc:bundle":
                    resp.setDspaceBitstreamURL(text);
                    break;      
                    
                default:
                    StatementLiteral statementLiteral = new StatementLiteral();
                    statementLiteral.setSubjectUri(uri);
                    //statementLiteral.setPredicateUri(tag.replace("dc:", "http://purl.org/dc/terms/"));
                    statementLiteral.setPredicateUri(this.mapping.getProperty(tag.replace(":",".")).split(";")[0]);
                    statementLiteral.setObjectLiteral(text);
                    statementLiteral.setLiteralType(this.mapping.getProperty(tag.replace(":",".")).split(";")[1]);
                    resp.getListOfStatementLiterals().add(statementLiteral);

            }

        }

        return resp;
    }

    /**
     * Set the set or collection obtain in the response
     * @return 
     */
    public List<String> getSetSpec() {
        return setSpec;
    }

    /**
     * Iterate over the response and extract items metadata
     * @return 
     */
    public List<Item> modelItems() {
        List<Item> response = Lists.newArrayList();
        Document result = this.xmlResponse;
        Elements list = result.getElementsByTag("record");

        for (Element e : list) {

            Item resp = new Item();
            String id = e.getElementsByTag("identifier").text();
            resp.setId(id);
            Elements eset = e.getElementsByTag("setSpec");
            for (Element sp : eset) {
                String col = sp.getElementsByTag("setSpec").text();
                if (col.contains("col")) {
                    //resp.setDspaceIsPartOfCollectionID(col);
                }

            }
            Element meta = e.getElementsByTag("metadata").first().child(0);
            resp.setListOfStatementLiterals(new ArrayList());
            for (Element s : meta.children()) {

                if ("dc:bundle".equals(s.tagName())) {
                    resp.setDspaceBitstreamURL(s.text());
                } else {

                    StatementLiteral statementLiteral = new StatementLiteral();
                    statementLiteral.setSubjectUri(id);
                    statementLiteral.setPredicateUri(s.tagName().replace("dc:", "http://purl.org/dc/terms/"));
                    statementLiteral.setObjectLiteral(s.text());
                    statementLiteral.setLiteralType(null);
                    resp.getListOfStatementLiterals().add(statementLiteral);
                }

            }
            response.add(resp);
            //resp.setDspaceBitstreamURL(rawResponse);
        }

        return response;
    }

    /**
     * Convert string response to Document xml
     * @param st
     * @return
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException 
     */
    public org.w3c.dom.Document parsexml(String st) throws SAXException, ParserConfigurationException, IOException {
        DocumentBuilderFactory domFact = DocumentBuilderFactory.newInstance();
        domFact.setNamespaceAware(true);
        DocumentBuilder builder = domFact.newDocumentBuilder();

        org.w3c.dom.Document doc = builder.parse(new ByteArrayInputStream(st.getBytes()));
        return doc;

    }

    /**
     * Recover all items in the response
     * @return
     * @throws TransformerException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException 
     */
    public List<Item> modelItemsxoai() throws TransformerException, XPathExpressionException, SAXException, ParserConfigurationException, IOException {
        InputStream toInputStream1 = IOUtils.toInputStream(this.rawResponse);
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        org.w3c.dom.Document xmlDocument = builder.parse(toInputStream1);
        return extracttransform(xmlDocument);
    }

    /**
     * Recover all communities in the response
     * @return
     * @throws TransformerException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException 
     */
    public List<Community> modelCommunity() throws TransformerException, XPathExpressionException, SAXException, ParserConfigurationException, IOException {
        List<Community> lcom = new ArrayList();

        Document doc = this.xmlResponse;
        Elements list = doc.getElementsByTag("set");
        for (Element e : list) {
            Community com = new Community();
            String id = e.getElementsByTag("setSpec").text();
            String name = e.getElementsByTag("setName").text();
            if (id.contains("com_")) {
                com.setId(id);
                String uri = this.prop.getProperty("uriPrefix") + "handle" + id.replace("com_", "/").replace("_", "/");
                com.setUri(uri);
                lcom.add(com);

            }

        }

        return lcom;
    }

    /**
     * Recover repository metadata  
     * @return
     * @throws TransformerException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException 
     */   
    public List<Repository> modelRepository() throws TransformerException, XPathExpressionException, SAXException, ParserConfigurationException, IOException {
        List<Repository> lrepo = new ArrayList();

        Document doc = this.xmlResponse;
        Elements list = doc.getElementsByTag("Identify");
        for (Element e : list) {
            Repository repo = new Repository();
            String uri = e.getElementsByTag("baseURL").text();
            repo.setUri(uri);
            String id = e.getElementsByTag("repositoryIdentifier").text();
            repo.setId(id);
            repo.setListOfStatementLiterals(new ArrayList());
            Elements elements = e.children();
            for (Element echild : elements) {
                String tagname = echild.tagName();
                String textValue = echild.text();

                StatementLiteral statementLiteral = new StatementLiteral();
                statementLiteral.setSubjectUri(uri);
                statementLiteral.setPredicateUri("http://purl.org/dc/terms/" + tagname);
                statementLiteral.setObjectLiteral(textValue);
                statementLiteral.setLiteralType(null);
                repo.getListOfStatementLiterals().add(statementLiteral);

            }

            lrepo.add(repo);

        }

        return lrepo;
    }

    /**
     * Recover response collections
     * @return
     * @throws TransformerException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException 
     */
    public List<Collection> modelCollections() throws TransformerException, XPathExpressionException, SAXException, ParserConfigurationException, IOException {
        List<Collection> lcol = new ArrayList();

        Document doc = this.xmlResponse;
        Elements list = doc.getElementsByTag("set");
        for (Element e : list) {
            Collection col = new Collection();
            String id = e.getElementsByTag("setSpec").text();
            String name = e.getElementsByTag("setName").text();
            if (id.contains("col_")) {
                col.setId(id);
                String uri = this.prop.getProperty("uriPrefix") + "/handle" + id.replace("col_", "/").replace("_", "/");
                col.setUri(uri);
                lcol.add(col);
            }

        }

        return lcol;
    }

    /**
     * Recover items collections
     * @return
     * @throws TransformerException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException 
     */    
    public List<String> modelItemCollections() throws TransformerException, XPathExpressionException, SAXException, ParserConfigurationException, IOException {
        List<String> litems = new ArrayList();
        Document doc = this.xmlResponse;
        Elements list = doc.getElementsByTag("identifier");
        for (Element e : list) {
            String completeId = e.text();

            String[] parts = completeId.split(":");
            String id = parts[parts.length - 1];
            litems.add(id);

        }

        return litems;
    }

    /**
     * Recover response set spect or collections 
     * @return
     * @throws TransformerException
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException 
     */
    public List<String> modelSetSpec() throws TransformerException, XPathExpressionException, SAXException, ParserConfigurationException, IOException {
        List<String> lspec = new ArrayList();
        Document doc = this.xmlResponse;
        Elements list = doc.getElementsByTag("header");
        for (Element e : list) {
            Elements listspec = e.getElementsByTag("setSpec");
            for (Element spec : listspec) {
                String idspec = spec.text();
                lspec.add(idspec);
            }

        }

        return lspec;
    }

    /**
     * Transform xoai metadata format to oai_dc format and recover items
     * @param xmlDocument
     * @return
     * @throws XPathExpressionException
     * @throws TransformerException 
     */
    public List<Item> extracttransform(org.w3c.dom.Document xmlDocument) throws XPathExpressionException, TransformerException {
        List<Item> resp = Lists.newArrayList();
        XPath xPath = XPathFactory.newInstance().newXPath();
        xPath.setNamespaceContext(new NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                switch (prefix) {
                    case "oai20":
                        return "http://www.openarchives.org/OAI/2.0/";
                    case "marc":
                        return "http://www.loc.gov/MARC21/slim";
                    case "oai_cerif_openaire":
                        return "https://www.openaire.eu/cerif-profile/1.1/";
                    case "dc":
                        return "http://purl.org/dc/elements/1.1/";
                    case "aoi_dc":
                        return "http://www.openarchives.org/OAI/2.0/oai_dc/";
                }
                return null;
            }

            @Override
            public String getPrefix(String namespaceURI) {
                return null;
            }

            @Override
            public Iterator getPrefixes(String namespaceURI) {
                return null;
            }
        });

        String expression = "/OAI-PMH/ListRecords/record/metadata/metadata";
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODESET);

        String expressionhead = "/OAI-PMH/ListRecords/record/header";
        NodeList headers = (NodeList) xPath.compile(expressionhead).evaluate(xmlDocument, XPathConstants.NODESET);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node item = nodeList.item(i);
            String nodeToXML = nodeToXML(item);
            String ApplyXSLT = ApplyXSLT(nodeToXML, "a");
            Item it = modelItem(parse(ApplyXSLT), parse(nodeToXML(headers.item(i))));
            resp.add(it);
        }
        return resp;
    }
    
    /**
     * Transform node xml to string representation
     * @param node
     * @return
     * @throws TransformerException 
     */
    public static String nodeToXML(Node node) throws TransformerException {
        StringWriter sw = new StringWriter();
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.transform(new DOMSource(node), new StreamResult(sw));
        return sw.toString();
    }

    /**
     * Apply xslt transformation to response (Convert xoai to aoi_dc)
     * @param xmlIn
     * @param xsl
     * @return
     * @throws TransformerConfigurationException
     * @throws TransformerException 
     */
    public static String ApplyXSLT(String xmlIn, String xsl) throws TransformerConfigurationException, TransformerException {
        //StreamSource xslSource = new StreamSource(new StringReader(xsl));
        StreamSource xmlInSource = new StreamSource(new StringReader(xmlIn));
        Transformer tf = TransformerFactory.newInstance().newTransformer(new StreamSource(new File(XSLT_FILENAME)));
        StringWriter xmlOutWriter = new StringWriter();
        tf.transform(xmlInSource, new StreamResult(xmlOutWriter));
        return xmlOutWriter.toString();
    }

    /**
     * Rename namespace to facilitate conversion format
     * @param node
     * @param namespace 
     */
    public void renameNamespaceRecursive(Node node, String namespace) {
        org.w3c.dom.Document document = node.getOwnerDocument();
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            document.renameNode(node, namespace, node.getNodeName());
        }
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); ++i) {
            renameNamespaceRecursive(list.item(i), namespace);
        }
    }

}
