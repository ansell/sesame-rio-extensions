package com.github.ansell.sesamerioextensions.rdfjson;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/**
 * A utility class to help converting Sesame Graphs from and to RDF/JSON using Jackson.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RDFJSON
{
    private static final String STRING_NULL = "null";
    private static final String STRING_GRAPHS = "graphs";
    private static final String STRING_URI = "uri";
    private static final String STRING_BNODE = "bnode";
    private static final String STRING_DATATYPE = "datatype";
    private static final String STRING_LITERAL = "literal";
    private static final String STRING_LANG = "lang";
    private static final String STRING_TYPE = "type";
    private static final String STRING_VALUE = "value";
    
    private static final Logger log = LoggerFactory.getLogger(RDFJSON.class);
    
    private static final JsonFactory JSON_FACTORY = new JsonFactory();
    
    static
    {
        // Disable features that may work for most JSON where the field names are in limited supply,
        // but does not work for RDF/JSON where a wide range of URIs are used for subjects and
        // predicates
        JSON_FACTORY.disable(JsonFactory.Feature.INTERN_FIELD_NAMES);
        JSON_FACTORY.disable(JsonFactory.Feature.CANONICALIZE_FIELD_NAMES);
    }
    
    /**
     * Helper method to reduce complexity of the JSON serialisation algorithm
     * 
     * Any null contexts will only be serialised to JSON if there are also non-null contexts in the
     * contexts array
     * 
     * @param object
     *            The RDF value to serialise
     * @param valueArray
     *            The JSON Array to serialise the object to
     * @param contexts
     *            The set of contexts that are relevant to this object, including null contexts as
     *            they are found.
     * @throws IOException
     * @throws JsonGenerationException
     * @throws JSONException
     */
    private static void writeObject(final Value object, Set<Resource> contexts, final JsonGenerator jg)
        throws JsonGenerationException, IOException
    {
        jg.writeStartObject();
        if(object instanceof Literal)
        {
            jg.writeObjectField(STRING_VALUE, object.stringValue());
            
            jg.writeObjectField(STRING_TYPE, RDFJSON.STRING_LITERAL);
            final Literal l = (Literal)object;
            
            if(l.getLanguage() != null)
            {
                jg.writeObjectField(STRING_LANG, l.getLanguage());
            }
            
            if(l.getDatatype() != null)
            {
                jg.writeObjectField(STRING_DATATYPE, l.getDatatype().stringValue());
            }
        }
        else if(object instanceof BNode)
        {
            jg.writeObjectField(STRING_VALUE, RDFJSON.resourceToString((BNode)object));
            
            jg.writeObjectField(STRING_TYPE, RDFJSON.STRING_BNODE);
        }
        else if(object instanceof URI)
        {
            jg.writeObjectField(STRING_VALUE, RDFJSON.resourceToString((URI)object));
            
            jg.writeObjectField(STRING_TYPE, RDFJSON.STRING_URI);
        }
        
        // net.sf.json line
        // if (contexts.size() > 0 && !(contexts.size() == 1 && contexts.contains(null)))
        // org.json line
        // if(contexts.length() > 0 && !(contexts.length() == 1 && contexts.isNull(0)))
        
        // if there is a context, and null is not the only context,
        // then, output the contexts for this object
        // Null context, or inherited from a document context, is the assumed value for the context
        // in this case
        if(contexts != null && !contexts.isEmpty() && !(contexts.size() == 1 && contexts.iterator().next() == null))
        {
            jg.writeArrayFieldStart(STRING_GRAPHS);
            for(Resource nextContext : contexts)
            {
                if(nextContext == null)
                {
                    jg.writeNull();
                }
                else
                {
                    jg.writeString(nextContext.stringValue());
                }
            }
            jg.writeEndArray();
        }
        
        jg.writeEndObject();
    }
    
    /**
     * Outputs a {@link Model} directly to JSON.
     * 
     * @param graph
     *            A model containing all of the statements to be rendered to RDF/JSON.
     * @param writer
     *            The output writer to use, or null to use a new StringWriter.
     * 
     * @return An RDF/JSON string if successful, otherwise null.
     * @throws IOException
     * @throws JsonGenerationException
     */
    public static void modelToRdfJson(final Model graph, Writer writer) throws JsonGenerationException, IOException
    {
        JsonGenerator jg = JSON_FACTORY.createJsonGenerator(writer);
        
        jg.writeStartObject();
        for(final Resource nextSubject : graph.subjects())
        {
            jg.writeObjectFieldStart(nextSubject.stringValue());
            for(final URI nextPredicate : graph.filter(nextSubject, null, null).predicates())
            {
                jg.writeArrayFieldStart(nextPredicate.stringValue());
                for(final Value nextObject : graph.filter(nextSubject, nextPredicate, null).objects())
                {
                    // contexts are optional, so this may return empty in some scenarios
                    // depending on the interpretation of the way contexts work
                    final Set<Resource> contexts = graph.filter(nextSubject, nextPredicate, nextObject).contexts();
                    
                    RDFJSON.writeObject(nextObject, contexts, jg);
                }
                jg.writeEndArray();
            }
            jg.writeEndObject();
        }
        jg.writeEndObject();
    }
    
    /**
     * Implementation using the Jackson API.
     * 
     * @param json
     *            The RDF/JSON string to be parsed.
     * @param handler
     *            The {@link RDFHandler} to handle the resulting triples.
     */
    public static void rdfJsonToHandler(final Reader json, final RDFHandler handler) throws RDFParseException
    {
        final Model result = new LinkedHashModel();
        final ValueFactory vf = new ValueFactoryImpl();
        
        JsonParser jp = null;
        
        try
        {
            jp = JSON_FACTORY.createJsonParser(json);
            if(jp.nextToken() != JsonToken.START_OBJECT)
            {
                throw new RDFParseException("Expected RDF/JSON document to start with an Object", jp
                        .getCurrentLocation().getLineNr(), jp.getCurrentLocation().getColumnNr());
            }
            
            while(jp.nextToken() != JsonToken.END_OBJECT)
            {
                
                final String subjStr = jp.getCurrentName();
                Resource subject = null;
                subject = subjStr.startsWith("_:") ? vf.createBNode(subjStr.substring(2)) : vf.createURI(subjStr);
                if(jp.nextToken() != JsonToken.START_OBJECT)
                {
                    throw new RDFParseException("Expected subject value to start with an Object", jp
                            .getCurrentLocation().getLineNr(), jp.getCurrentLocation().getColumnNr());
                }
                
                // iterate though the subject values until we get to the end of the object for this
                // subject
                while(jp.nextToken() != JsonToken.END_OBJECT)
                {
                    final String predStr = jp.getCurrentName();
                    final URI predicate = vf.createURI(predStr);
                    if(jp.nextToken() != JsonToken.START_ARRAY)
                    {
                        throw new RDFParseException("Expected predicate value to start with an array", jp
                                .getCurrentLocation().getLineNr(), jp.getCurrentLocation().getColumnNr());
                    }
                    
                    while(jp.nextToken() != JsonToken.END_ARRAY)
                    {
                        if(jp.getCurrentToken() != JsonToken.START_OBJECT)
                        {
                            throw new RDFParseException("Expected object value to start with an Object: subject=<"
                                    + subjStr + "> predicate=<" + predStr + ">", jp.getCurrentLocation().getLineNr(),
                                    jp.getCurrentLocation().getColumnNr());
                        }
                        
                        String nextValue = null;
                        String nextType = null;
                        String nextDatatype = null;
                        String nextLanguage = null;
                        List<String> nextContexts = new ArrayList<String>(2);
                        
                        while(jp.nextToken() != JsonToken.END_OBJECT)
                        {
                            String fieldName = jp.getCurrentName();
                            if(STRING_VALUE.equals(fieldName))
                            {
                                if(nextValue != null)
                                {
                                    throw new RDFParseException("Multiple values found for a single object: subject="
                                            + subjStr + " predicate=" + predStr, jp.getCurrentLocation().getLineNr(),
                                            jp.getCurrentLocation().getColumnNr());
                                }
                                
                                jp.nextToken();
                                
                                nextValue = jp.getText();
                                System.out.println("value='" + nextValue + "'");
                            }
                            else if(STRING_TYPE.equals(fieldName))
                            {
                                if(nextType != null)
                                {
                                    throw new RDFParseException("Multiple types found for single object: subject="
                                            + subjStr + " predicate=" + predStr, jp.getCurrentLocation().getLineNr(),
                                            jp.getCurrentLocation().getColumnNr());
                                }
                                
                                jp.nextToken();
                                
                                nextType = jp.getText();
                                System.out.println("fieldtype=" + nextType);
                            }
                            else if(STRING_LANG.equals(fieldName))
                            {
                                if(nextLanguage != null)
                                {
                                    throw new RDFParseException("Multiple languages found for single object: subject="
                                            + subjStr + " predicate=" + predStr, jp.getCurrentLocation().getLineNr(),
                                            jp.getCurrentLocation().getColumnNr());
                                }
                                
                                jp.nextToken();
                                
                                nextLanguage = jp.getText();
                                System.out.println("language=" + nextLanguage);
                            }
                            else if(STRING_DATATYPE.equals(fieldName))
                            {
                                if(nextDatatype != null)
                                {
                                    throw new RDFParseException("Multiple datatypes found for single object: subject="
                                            + subjStr + " predicate=" + predStr, jp.getCurrentLocation().getLineNr(),
                                            jp.getCurrentLocation().getColumnNr());
                                }
                                
                                jp.nextToken();
                                
                                nextDatatype = jp.getText();
                                System.out.println("datatype=<" + nextDatatype + ">");
                            }
                            else if(STRING_GRAPHS.equals(fieldName))
                            {
                                if(jp.nextToken() != JsonToken.START_ARRAY)
                                {
                                    throw new RDFParseException("Expected graphs to start with an array", jp
                                            .getCurrentLocation().getLineNr(), jp.getCurrentLocation().getColumnNr());
                                }
                                
                                while(jp.nextToken() != JsonToken.END_ARRAY)
                                {
                                    jp.nextToken();
                                    
                                    String nextGraph = jp.getText();
                                    System.out.println("context=<" + nextGraph + ">");
                                    
                                    nextContexts.add(nextGraph);
                                }
                            }
                            else
                            {
                                throw new RDFParseException("Unrecognised JSON field name for object: subject="
                                        + subjStr + " predicate=" + predStr + " fieldname=" + fieldName, jp
                                        .getCurrentLocation().getLineNr(), jp.getCurrentLocation().getColumnNr());
                            }
                        }
                        
                        Value object = null;
                        
                        if(nextType == null)
                        {
                            throw new RDFParseException("No type for object: subject=" + subjStr + " predicate="
                                    + predStr, jp.getCurrentLocation().getLineNr(), jp.getCurrentLocation()
                                    .getColumnNr());
                        }
                        
                        if(nextValue == null)
                        {
                            throw new RDFParseException("No value for object: subject=" + subjStr + " predicate="
                                    + predStr, jp.getCurrentLocation().getLineNr(), jp.getCurrentLocation()
                                    .getColumnNr());
                        }
                        
                        if(RDFJSON.STRING_LITERAL.equals(nextType))
                        {
                            if(nextLanguage != null)
                            {
                                object = vf.createLiteral(nextValue, nextLanguage);
                            }
                            else if(nextDatatype != null)
                            {
                                object = vf.createLiteral(nextValue, vf.createURI(nextDatatype));
                            }
                            else
                            {
                                object = vf.createLiteral(nextValue);
                            }
                        }
                        else if(RDFJSON.STRING_BNODE.equals(nextType))
                        {
                            object = vf.createBNode(nextValue.substring(2));
                        }
                        else if(RDFJSON.STRING_URI.equals(nextType))
                        {
                            object = vf.createURI(nextValue);
                        }
                        
                        if(!nextContexts.isEmpty())
                        {
                            for(String nextContext : nextContexts)
                            {
                                // Note: any nulls here will result in statements in the default
                                // context.
                                // System.out.println("s = " + s);
                                final Resource context =
                                        nextContext.equals(RDFJSON.STRING_NULL) ? null : vf.createURI(nextContext);
                                // System.out.println("context = " + context);
                                result.add(vf.createStatement(subject, predicate, object, context));
                            }
                        }
                        else
                        {
                            result.add(vf.createStatement(subject, predicate, object));
                        }
                    }
                }
            }
        }
        catch(IOException e)
        {
            
        }
        finally
        {
            if(jp != null)
            {
                try
                {
                    jp.close();
                }
                catch(IOException e)
                {
                    throw new RDFParseException("Found exception while closing JSON parser", e, jp.getCurrentLocation()
                            .getLineNr(), jp.getCurrentLocation().getColumnNr());
                }
            }
        }
    }
    
    /**
     * Returns the correct syntax for a Resource, depending on whether it is a URI or a Blank Node
     * (ie, BNode)
     * 
     * @param uriOrBnode
     *            The resource to serialise to a string
     * @return The string value of the sesame resource
     */
    private static String resourceToString(final Resource uriOrBnode)
    {
        if(uriOrBnode instanceof URI)
        {
            return uriOrBnode.stringValue();
        }
        else
        {
            return "_:" + ((BNode)uriOrBnode).getID();
        }
    }
}