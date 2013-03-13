package se.kmr.scam.rest.util;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class to help converting Sesame Graphs from and to RDF/JSON.
 * 
 * @author Hannes Ebner <hebner@kth.se>
 * @author Joshua Shinavier
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
     * @throws JSONException
     */
    private static void addObjectToArray(final Value object, final JSONArray valueArray, final JSONArray contexts)
        throws JSONException
    {
        final JSONObject valueObj = new JSONObject();
        
        if(object instanceof Literal)
        {
            valueObj.put(RDFJSON.STRING_VALUE, object.stringValue());
            
            valueObj.put(RDFJSON.STRING_TYPE, RDFJSON.STRING_LITERAL);
            final Literal l = (Literal)object;
            
            if(l.getLanguage() != null)
            {
                valueObj.put(RDFJSON.STRING_LANG, l.getLanguage());
            }
            else if(l.getDatatype() != null)
            {
                valueObj.put(RDFJSON.STRING_DATATYPE, l.getDatatype().stringValue());
            }
        }
        else if(object instanceof BNode)
        {
            valueObj.put(RDFJSON.STRING_VALUE, RDFJSON.resourceToString((BNode)object));
            
            valueObj.put(RDFJSON.STRING_TYPE, RDFJSON.STRING_BNODE);
        }
        else if(object instanceof URI)
        {
            valueObj.put(RDFJSON.STRING_VALUE, RDFJSON.resourceToString((URI)object));
            
            valueObj.put(RDFJSON.STRING_TYPE, RDFJSON.STRING_URI);
        }
        
        // net.sf.json line
        // if (contexts.size() > 0 && !(contexts.size() == 1 && contexts.contains(null)))
        // org.json line
        // if(contexts.length() > 0 && !(contexts.length() == 1 && contexts.isNull(0)))
        
        // if there is a context, and null is not the only context,
        // then, output the contexts for this object
        // Null context, or inherited from a document context, is the assumed value for the context
        // in this case
        if(contexts != null && !(contexts.length() == 1 && contexts.isNull(0)))
        {
            valueObj.put(RDFJSON.STRING_GRAPHS, contexts);
        }
        valueArray.put(valueObj);
    }
    
    /**
     * Outputs a {@link Model} directly to JSON.
     * 
     * @param graph
     *            A model containing all of the statements to be rendered to RDF/JSON.
     * 
     * @return An RDF/JSON string if successful, otherwise null.
     */
    public static String modelToRdfJson(final Model graph)
    {
        final Writer result = new StringWriter();
        
        if(RDFJSON.modelToRdfJson(graph, result) == null)
        {
            return null;
        }
        else
        {
            return result.toString();
        }
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
     */
    public static Writer modelToRdfJson(final Model graph, Writer writer)
    {
        if(writer == null)
        {
            writer = new StringWriter();
        }
        
        final JSONObject result = new JSONObject();
        try
        {
            for(final Resource nextSubject : graph.subjects())
            {
                final JSONObject predicateArray = new JSONObject();
                for(final URI nextPredicate : graph.filter(nextSubject, null, null).predicates())
                {
                    final JSONArray objectArray = new JSONArray();
                    for(final Value nextObject : graph.filter(nextSubject, nextPredicate, null).objects())
                    {
                        // contexts are optional, so this may return empty in some scenarios
                        // depending on the interpretation of the way contexts work
                        final Set<Resource> contexts = graph.filter(nextSubject, nextPredicate, nextObject).contexts();
                        
                        if(contexts.isEmpty() || (contexts.size() == 1 && contexts.iterator().next() == null))
                        {
                            RDFJSON.addObjectToArray(nextObject, objectArray, null);
                        }
                        else
                        {
                            final JSONArray contextArray = new JSONArray();
                            
                            for(final Resource nextContext : contexts)
                            {
                                contextArray.put(nextContext);
                            }
                            
                            RDFJSON.addObjectToArray(nextObject, objectArray, contextArray);
                        }
                    }
                    predicateArray.put(nextPredicate.stringValue(), objectArray);
                }
                result.put(RDFJSON.resourceToString(nextSubject), predicateArray);
            }
            
            result.write(writer);
            
            return writer;
        }
        catch(final JSONException e)
        {
            RDFJSON.log.error(e.getMessage(), e);
        }
        return null;
    }
    
    /**
     * Implementation using the json.org API.
     * 
     * @param json
     *            The RDF/JSON string to be parsed and converted into a Collection<Statement>.
     * @return A Collection<Statement> if successful, otherwise null.
     */
    public static Model rdfJsonToGraph(final String json)
    {
        final Model result = new LinkedHashModel();
        final ValueFactory vf = new ValueFactoryImpl();
        
        try
        {
            final JSONObject input = new JSONObject(json);
            @SuppressWarnings("unchecked")
            final Iterator<String> subjects = input.keys();
            while(subjects.hasNext())
            {
                final String subjStr = subjects.next();
                Resource subject = null;
                subject = subjStr.startsWith("_:") ? vf.createBNode(subjStr.substring(2)) : vf.createURI(subjStr);
                final JSONObject pObj = input.getJSONObject(subjStr);
                @SuppressWarnings("unchecked")
                final Iterator<String> predicates = pObj.keys();
                while(predicates.hasNext())
                {
                    final String predStr = predicates.next();
                    final URI predicate = vf.createURI(predStr);
                    final JSONArray predArr = pObj.getJSONArray(predStr);
                    for(int i = 0; i < predArr.length(); i++)
                    {
                        Value object = null;
                        final JSONObject obj = predArr.getJSONObject(i);
                        if(!obj.has(RDFJSON.STRING_VALUE))
                        {
                            continue;
                        }
                        final String value = obj.getString(RDFJSON.STRING_VALUE);
                        if(!obj.has(RDFJSON.STRING_TYPE))
                        {
                            continue;
                        }
                        final String type = obj.getString(RDFJSON.STRING_TYPE);
                        String lang = null;
                        if(obj.has(RDFJSON.STRING_LANG))
                        {
                            lang = obj.getString(RDFJSON.STRING_LANG);
                        }
                        String datatype = null;
                        if(obj.has(RDFJSON.STRING_DATATYPE))
                        {
                            datatype = obj.getString(RDFJSON.STRING_DATATYPE);
                        }
                        if(RDFJSON.STRING_LITERAL.equals(type))
                        {
                            if(lang != null)
                            {
                                object = vf.createLiteral(value, lang);
                            }
                            else if(datatype != null)
                            {
                                object = vf.createLiteral(value, vf.createURI(datatype));
                            }
                            else
                            {
                                object = vf.createLiteral(value);
                            }
                        }
                        else if(RDFJSON.STRING_BNODE.equals(type))
                        {
                            object = vf.createBNode(value.substring(2));
                        }
                        else if(RDFJSON.STRING_URI.equals(type))
                        {
                            object = vf.createURI(value);
                        }
                        
                        if(obj.has(RDFJSON.STRING_GRAPHS))
                        {
                            final JSONArray a = obj.getJSONArray(RDFJSON.STRING_GRAPHS);
                            // System.out.println("a.length() = " + a.length());
                            for(int j = 0; j < a.length(); j++)
                            {
                                // Note: any nulls here will result in statements in the default
                                // context.
                                final String s = a.getString(j);
                                // System.out.println("s = " + s);
                                final Resource context = s.equals(RDFJSON.STRING_NULL) ? null : vf.createURI(s);
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
        catch(final JSONException e)
        {
            // FIXME: Should be able to propagate the exception up from here and handle it somewhere
            // else without any difficulty
            RDFJSON.log.error(e.getMessage(), e);
            return null;
        }
        
        return result;
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