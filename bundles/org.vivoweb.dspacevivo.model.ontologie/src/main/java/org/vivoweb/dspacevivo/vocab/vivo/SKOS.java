/* CVS $Id: $ */
package org.vivoweb.dspacevivo.vocab.vivo; 
import org.apache.jena.rdf.model.*;
import org.apache.jena.ontology.*;
 
/**
 * Vocabulary definitions from /home/heon/01-SPRINT/00-PROJET-DSPACE-VIVO/00-GIT/DSpace-VIVO/bundles/org.vivoweb.dspacevivo.model.ontologie/src/main/resources/vivo.ttl 
 * @author Auto-generated by schemagen on 30 mars 2022 06:32 
 */
public class SKOS {
    /** <p>The ontology model that holds the vocabulary terms</p> */
    private static final OntModel M_MODEL = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://www.w3.org/2004/02/skos/core#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     * @return namespace as String
     * @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = M_MODEL.createResource( NS );
    
    public static final ObjectProperty broader = M_MODEL.createObjectProperty( "http://www.w3.org/2004/02/skos/core#broader" );
    
    public static final ObjectProperty narrower = M_MODEL.createObjectProperty( "http://www.w3.org/2004/02/skos/core#narrower" );
    
    public static final ObjectProperty related = M_MODEL.createObjectProperty( "http://www.w3.org/2004/02/skos/core#related" );
    
    public static final AnnotationProperty scopeNote = M_MODEL.createAnnotationProperty( "http://www.w3.org/2004/02/skos/core#scopeNote" );
    
    public static final OntClass Concept = M_MODEL.createClass( "http://www.w3.org/2004/02/skos/core#Concept" );
    
}