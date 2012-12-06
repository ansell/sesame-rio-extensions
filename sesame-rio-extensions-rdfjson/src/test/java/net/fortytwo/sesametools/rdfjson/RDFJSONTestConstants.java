package net.fortytwo.sesametools.rdfjson;

import org.openrdf.model.BNode;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 * @author Joshua Shinavier (http://fortytwo.net).
 */
public final class RDFJSONTestConstants
{
    protected interface FOAF
    {
        static final String NAMESPACE = "http://xmlns.com/foaf/0.1/";
        static final URI KNOWS = RDFJSONTestConstants.vf.createURI(FOAF.NAMESPACE + "knows"),
                NAME = RDFJSONTestConstants.vf.createURI(FOAF.NAMESPACE + "name"), PERSON = RDFJSONTestConstants.vf
                        .createURI(FOAF.NAMESPACE + "Person");
    }
    
    protected static final ValueFactory vf = new ValueFactoryImpl();
    protected static final String BASE_URI = "http://example.org/base#";
    protected static final URI ABOUT = RDFJSONTestConstants.vf.createURI("http://example.org/about"),
            ARTHUR = RDFJSONTestConstants.vf.createURI("http://example.org/Arthur"), GRAPH1 = RDFJSONTestConstants.vf
                    .createURI("http://example.org/graph1");
    
    protected static final BNode PERSON = RDFJSONTestConstants.vf.createBNode("person"), P1 = RDFJSONTestConstants.vf
            .createBNode("p1");
}
