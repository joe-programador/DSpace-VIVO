/* CVS $Id: $ */
package org.vivoweb.dspacevivo.vocab.vivo; 
import org.apache.jena.rdf.model.*;
import org.apache.jena.ontology.*;
 
/**
 * Vocabulary definitions from /home/heon/01-SPRINT/00-PROJET-DSPACE-VIVO/00-GIT/DSpace-VIVO/bundles/org.vivoweb.dspacevivo.model.ontologie/src/main/resources/vivo.ttl 
 * @author Auto-generated by schemagen on 30 mars 2022 06:32 
 */
public class NS {
    /** <p>The ontology model that holds the vocabulary terms</p> */
    private static final OntModel M_MODEL = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://www.w3.org/2003/06/sw-vocab-status/ns#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     * @return namespace as String
     * @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = M_MODEL.createResource( NS );
    
    public static final AnnotationProperty term_status = M_MODEL.createAnnotationProperty( "http://www.w3.org/2003/06/sw-vocab-status/ns#term_status" );
    
}