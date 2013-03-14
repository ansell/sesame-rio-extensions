package com.github.ansell.sesamerioextensions.rdfjson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.WriterConfig;
import org.openrdf.rio.helpers.BasicWriterSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/**
 * A utility class to help converting Sesame Graphs from and to RDF/JSON using Jackson.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RDFJSONUtility
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
    
    private static final Logger log = LoggerFactory.getLogger(RDFJSONUtility.class);
    
    private static final JsonFactory JSON_FACTORY = new JsonFactory();
    
    static
    {
        // Disable features that may work for most JSON where the field names are in limited supply,
        // but does not work for RDF/JSON where a wide range of URIs are used for subjects and
        // predicates
        RDFJSONUtility.JSON_FACTORY.disable(JsonFactory.Feature.INTERN_FIELD_NAMES);
        RDFJSONUtility.JSON_FACTORY.disable(JsonFactory.Feature.CANONICALIZE_FIELD_NAMES);
        RDFJSONUtility.JSON_FACTORY.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
    }
    
    /**
     * Outputs a {@link Model} directly to JSON.
     * 
     * @param graph
     *            A model containing all of the statements to be rendered to RDF/JSON.
     * @param writer
     *            The output stream to use.
     * 
     * @throws IOException
     * @throws JsonGenerationException
     */
    public static void modelToRdfJson(final Model graph, final OutputStream writer) throws JsonGenerationException,
        IOException
    {
        RDFJSONUtility.modelToRdfJson(graph, writer, new WriterConfig());
    }
    
    /**
     * Outputs a {@link Model} directly to JSON.
     * 
     * @param graph
     *            A model containing all of the statements to be rendered to RDF/JSON.
     * @param writer
     *            The output stream to use.
     * @param writerConfig
     *            The {@link WriterConfig} to use for accessing specific settings.
     * @throws IOException
     * @throws JsonGenerationException
     */
    public static void modelToRdfJson(final Model graph, final OutputStream writer, final WriterConfig writerConfig)
        throws JsonGenerationException, IOException
    {
        final JsonGenerator jg = RDFJSONUtility.JSON_FACTORY.createJsonGenerator(writer);
        RDFJSONUtility.modelToRdfJsonInternal(graph, writerConfig, jg);
        
        jg.close();
    }
    
    /**
     * Outputs a {@link Model} directly to JSON.
     * 
     * @param graph
     *            A model containing all of the statements to be rendered to RDF/JSON.
     * @param writer
     *            The output writer to use.
     * 
     * @throws IOException
     * @throws JsonGenerationException
     */
    public static void modelToRdfJson(final Model graph, final Writer writer) throws JsonGenerationException,
        IOException
    {
        RDFJSONUtility.modelToRdfJson(graph, writer, new WriterConfig());
    }
    
    /**
     * Outputs a {@link Model} directly to JSON.
     * 
     * @param graph
     *            A model containing all of the statements to be rendered to RDF/JSON.
     * @param writer
     *            The output writer to use.
     * @param writerConfig
     *            The {@link WriterConfig} to use for accessing specific settings.
     * @throws IOException
     * @throws JsonGenerationException
     */
    public static void modelToRdfJson(final Model graph, final Writer writer, final WriterConfig writerConfig)
        throws JsonGenerationException, IOException
    {
        final JsonGenerator jg = RDFJSONUtility.JSON_FACTORY.createJsonGenerator(writer);
        RDFJSONUtility.modelToRdfJsonInternal(graph, writerConfig, jg);
        
        jg.close();
    }
    
    /**
     * @param graph
     * @param writerConfig
     * @param jg
     * @throws IOException
     * @throws JsonGenerationException
     */
    private static void modelToRdfJsonInternal(final Model graph, final WriterConfig writerConfig,
            final JsonGenerator jg) throws IOException, JsonGenerationException
    {
        if(writerConfig.get(BasicWriterSettings.PRETTY_PRINT))
        {
            // By default Jackson does not pretty print, so enable this unless PRETTY_PRINT setting
            // is disabled
            jg.useDefaultPrettyPrinter();
        }
        jg.writeStartObject();
        for(final Resource nextSubject : graph.subjects())
        {
            jg.writeObjectFieldStart(RDFJSONUtility.resourceToString(nextSubject));
            for(final URI nextPredicate : graph.filter(nextSubject, null, null).predicates())
            {
                jg.writeArrayFieldStart(nextPredicate.stringValue());
                for(final Value nextObject : graph.filter(nextSubject, nextPredicate, null).objects())
                {
                    // contexts are optional, so this may return empty in some scenarios
                    // depending on the interpretation of the way contexts work
                    final Set<Resource> contexts = graph.filter(nextSubject, nextPredicate, nextObject).contexts();
                    
                    RDFJSONUtility.writeObject(nextObject, contexts, jg);
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
     *            The RDF/JSON input stream to be parsed.
     * @param handler
     *            The {@link RDFHandler} to handle the resulting triples.
     * @throws RDFHandlerException
     */
    public static void rdfJsonToHandler(final InputStream json, final RDFHandler handler, final ValueFactory vf)
        throws RDFParseException, RDFHandlerException
    {
        JsonParser jp = null;
        
        try
        {
            jp = RDFJSONUtility.JSON_FACTORY.createJsonParser(json);
            RDFJSONUtility.rdfJsonToHandlerInternal(handler, vf, jp);
        }
        catch(final IOException e)
        {
            if(jp != null)
            {
                throw new RDFParseException(e, jp.getCurrentLocation().getLineNr(), jp.getCurrentLocation()
                        .getColumnNr());
            }
            else
            {
                throw new RDFParseException(e);
            }
        }
        finally
        {
            if(jp != null)
            {
                try
                {
                    jp.close();
                }
                catch(final IOException e)
                {
                    throw new RDFParseException("Found exception while closing JSON parser", e, jp.getCurrentLocation()
                            .getLineNr(), jp.getCurrentLocation().getColumnNr());
                }
            }
        }
    }
    
    /**
     * Implementation using the Jackson API.
     * 
     * @param json
     *            The RDF/JSON string to be parsed.
     * @param handler
     *            The {@link RDFHandler} to handle the resulting triples.
     * @throws RDFHandlerException
     */
    public static void rdfJsonToHandler(final Reader json, final RDFHandler handler, final ValueFactory vf)
        throws RDFParseException, RDFHandlerException
    {
        JsonParser jp = null;
        
        try
        {
            jp = RDFJSONUtility.JSON_FACTORY.createJsonParser(json);
            RDFJSONUtility.rdfJsonToHandlerInternal(handler, vf, jp);
        }
        catch(final IOException e)
        {
            if(jp != null)
            {
                throw new RDFParseException(e, jp.getCurrentLocation().getLineNr(), jp.getCurrentLocation()
                        .getColumnNr());
            }
            else
            {
                throw new RDFParseException(e);
            }
        }
        finally
        {
            if(jp != null)
            {
                try
                {
                    jp.close();
                }
                catch(final IOException e)
                {
                    throw new RDFParseException("Found exception while closing JSON parser", e, jp.getCurrentLocation()
                            .getLineNr(), jp.getCurrentLocation().getColumnNr());
                }
            }
        }
    }
    
    /**
     * @param handler
     * @param vf
     * @param jp
     * @throws IOException
     * @throws JsonParseException
     * @throws RDFParseException
     * @throws RDFHandlerException
     */
    private static void rdfJsonToHandlerInternal(final RDFHandler handler, final ValueFactory vf, final JsonParser jp)
        throws IOException, JsonParseException, RDFParseException, RDFHandlerException
    {
        if(jp.nextToken() != JsonToken.START_OBJECT)
        {
            throw new RDFParseException("Expected RDF/JSON document to start with an Object", jp.getCurrentLocation()
                    .getLineNr(), jp.getCurrentLocation().getColumnNr());
        }
        
        while(jp.nextToken() != JsonToken.END_OBJECT)
        {
            
            final String subjStr = jp.getCurrentName();
            Resource subject = null;
            
            // System.err.println("subject=" + subjStr);
            
            subject = subjStr.startsWith("_:") ? vf.createBNode(subjStr.substring(2)) : vf.createURI(subjStr);
            if(jp.nextToken() != JsonToken.START_OBJECT)
            {
                throw new RDFParseException("Expected subject value to start with an Object", jp.getCurrentLocation()
                        .getLineNr(), jp.getCurrentLocation().getColumnNr());
            }
            
            // iterate though the subject values until we get to the end of the object for this
            // subject
            while(jp.nextToken() != JsonToken.END_OBJECT)
            {
                final String predStr = jp.getCurrentName();
                
                // System.err.println("predicate=" + predStr);
                
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
                                + subjStr + "> predicate=<" + predStr + ">", jp.getCurrentLocation().getLineNr(), jp
                                .getCurrentLocation().getColumnNr());
                    }
                    
                    String nextValue = null;
                    String nextType = null;
                    String nextDatatype = null;
                    String nextLanguage = null;
                    final Set<String> nextContexts = new HashSet<String>(2);
                    
                    while(jp.nextToken() != JsonToken.END_OBJECT)
                    {
                        final String fieldName = jp.getCurrentName();
                        if(RDFJSONUtility.STRING_VALUE.equals(fieldName))
                        {
                            if(nextValue != null)
                            {
                                throw new RDFParseException("Multiple values found for a single object: subject="
                                        + subjStr + " predicate=" + predStr, jp.getCurrentLocation().getLineNr(), jp
                                        .getCurrentLocation().getColumnNr());
                            }
                            
                            jp.nextToken();
                            
                            nextValue = jp.getText();
                            // System.out.println("value='" + nextValue + "'");
                        }
                        else if(RDFJSONUtility.STRING_TYPE.equals(fieldName))
                        {
                            if(nextType != null)
                            {
                                throw new RDFParseException("Multiple types found for single object: subject="
                                        + subjStr + " predicate=" + predStr, jp.getCurrentLocation().getLineNr(), jp
                                        .getCurrentLocation().getColumnNr());
                            }
                            
                            jp.nextToken();
                            
                            nextType = jp.getText();
                            // System.out.println("fieldtype=" + nextType);
                        }
                        else if(RDFJSONUtility.STRING_LANG.equals(fieldName))
                        {
                            if(nextLanguage != null)
                            {
                                throw new RDFParseException("Multiple languages found for single object: subject="
                                        + subjStr + " predicate=" + predStr, jp.getCurrentLocation().getLineNr(), jp
                                        .getCurrentLocation().getColumnNr());
                            }
                            
                            jp.nextToken();
                            
                            nextLanguage = jp.getText();
                            // System.out.println("language=" + nextLanguage);
                        }
                        else if(RDFJSONUtility.STRING_DATATYPE.equals(fieldName))
                        {
                            if(nextDatatype != null)
                            {
                                throw new RDFParseException("Multiple datatypes found for single object: subject="
                                        + subjStr + " predicate=" + predStr, jp.getCurrentLocation().getLineNr(), jp
                                        .getCurrentLocation().getColumnNr());
                            }
                            
                            jp.nextToken();
                            
                            nextDatatype = jp.getText();
                            // System.out.println("datatype=<" + nextDatatype + ">");
                        }
                        else if(RDFJSONUtility.STRING_GRAPHS.equals(fieldName))
                        {
                            if(jp.nextToken() != JsonToken.START_ARRAY)
                            {
                                throw new RDFParseException("Expected graphs to start with an array", jp
                                        .getCurrentLocation().getLineNr(), jp.getCurrentLocation().getColumnNr());
                            }
                            
                            while(jp.nextToken() != JsonToken.END_ARRAY)
                            {
                                final String nextGraph = jp.getText();
                                // System.out.println("context=<" + nextGraph + ">");
                                
                                nextContexts.add(nextGraph);
                            }
                        }
                        else
                        {
                            throw new RDFParseException("Unrecognised JSON field name for object: subject=" + subjStr
                                    + " predicate=" + predStr + " fieldname=" + fieldName, jp.getCurrentLocation()
                                    .getLineNr(), jp.getCurrentLocation().getColumnNr());
                        }
                    }
                    
                    Value object = null;
                    
                    if(nextType == null)
                    {
                        throw new RDFParseException("No type for object: subject=" + subjStr + " predicate=" + predStr,
                                jp.getCurrentLocation().getLineNr(), jp.getCurrentLocation().getColumnNr());
                    }
                    
                    if(nextValue == null)
                    {
                        throw new RDFParseException(
                                "No value for object: subject=" + subjStr + " predicate=" + predStr, jp
                                        .getCurrentLocation().getLineNr(), jp.getCurrentLocation().getColumnNr());
                    }
                    
                    if(RDFJSONUtility.STRING_LITERAL.equals(nextType))
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
                    else if(RDFJSONUtility.STRING_BNODE.equals(nextType))
                    {
                        object = vf.createBNode(nextValue.substring(2));
                    }
                    else if(RDFJSONUtility.STRING_URI.equals(nextType))
                    {
                        object = vf.createURI(nextValue);
                    }
                    
                    if(!nextContexts.isEmpty())
                    {
                        for(final String nextContext : nextContexts)
                        {
                            // Note: any nulls here will result in statements in the default
                            // context.
                            // System.out.println("s = " + s);
                            final Resource context =
                                    nextContext.equals(RDFJSONUtility.STRING_NULL) ? null : vf.createURI(nextContext);
                            // System.out.println("context = " + context);
                            handler.handleStatement(vf.createStatement(subject, predicate, object, context));
                        }
                    }
                    else
                    {
                        handler.handleStatement(vf.createStatement(subject, predicate, object));
                    }
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
    private static void writeObject(final Value object, final Set<Resource> contexts, final JsonGenerator jg)
        throws JsonGenerationException, IOException
    {
        jg.writeStartObject();
        if(object instanceof Literal)
        {
            // System.err.println("Writing literal: " + object.stringValue());
            jg.writeObjectField(RDFJSONUtility.STRING_VALUE, object.stringValue());
            
            jg.writeObjectField(RDFJSONUtility.STRING_TYPE, RDFJSONUtility.STRING_LITERAL);
            final Literal l = (Literal)object;
            
            if(l.getLanguage() != null)
            {
                jg.writeObjectField(RDFJSONUtility.STRING_LANG, l.getLanguage());
            }
            
            if(l.getDatatype() != null)
            {
                jg.writeObjectField(RDFJSONUtility.STRING_DATATYPE, l.getDatatype().stringValue());
            }
        }
        else if(object instanceof BNode)
        {
            // System.err.println("Writing bnode: " + object.stringValue());
            jg.writeObjectField(RDFJSONUtility.STRING_VALUE, RDFJSONUtility.resourceToString((BNode)object));
            
            jg.writeObjectField(RDFJSONUtility.STRING_TYPE, RDFJSONUtility.STRING_BNODE);
        }
        else if(object instanceof URI)
        {
            // System.err.println("Writing uri: " + object.stringValue());
            jg.writeObjectField(RDFJSONUtility.STRING_VALUE, RDFJSONUtility.resourceToString((URI)object));
            
            jg.writeObjectField(RDFJSONUtility.STRING_TYPE, RDFJSONUtility.STRING_URI);
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
            jg.writeArrayFieldStart(RDFJSONUtility.STRING_GRAPHS);
            for(final Resource nextContext : contexts)
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
    
}