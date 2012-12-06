/**
 *
 */
package net.fortytwo.sesametools.rdfjson;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

import org.json.JSONObject;
import org.junit.Assert;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.rdfxml.RDFXMLParser;
import org.openrdf.rio.turtle.TurtleParser;
import org.openrdf.rio.turtle.TurtleWriter;

/**
 * Test utils for comparative parsing using the RDF/JSON parser and the Sesame Turtle parser
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public final class RDFJSONTestUtils
{
    public static final JSONObject parseAndWrite(final String fileName) throws Exception
    {
        final RDFJSONParser p = new RDFJSONParser();
        final Writer stringWriter = new StringWriter();
        
        final RDFWriter w = new RDFJSONWriter(stringWriter);
        p.setRDFHandler(w);
        final InputStream in = RDFJSONTestUtils.class.getResourceAsStream(fileName);
        Assert.assertNotNull("Could not find test resource : " + fileName, in);
        try
        {
            p.parse(in, RDFJSONTestConstants.BASE_URI);
            return new JSONObject(stringWriter.toString());
        }
        finally
        {
            in.close();
        }
    }
    
    public static final String parseJsonAndWriteTurtle(final String fileName) throws Exception
    {
        final RDFParser p = new RDFJSONParser();
        final Writer stringWriter = new StringWriter();
        
        final RDFWriter w = new TurtleWriter(stringWriter);
        p.setRDFHandler(w);
        final InputStream in = RDFJSONTestUtils.class.getResourceAsStream(fileName);
        try
        {
            p.parse(in, RDFJSONTestConstants.BASE_URI);
            return stringWriter.toString();
        }
        finally
        {
            in.close();
        }
    }
    
    public static JSONObject parseRdfXmlAndWriteJson(final String fileName) throws Exception
    {
        final RDFParser p = new RDFXMLParser();
        final Writer stringWriter = new StringWriter();
        
        final RDFWriter w = new RDFJSONWriter(stringWriter);
        p.setRDFHandler(w);
        final InputStream in = RDFJSONTestUtils.class.getResourceAsStream(fileName);
        try
        {
            p.parse(in, RDFJSONTestConstants.BASE_URI);
            return new JSONObject(stringWriter.toString());
        }
        finally
        {
            in.close();
        }
    }
    
    public static final JSONObject parseTurtleAndWriteJson(final String fileName) throws Exception
    {
        final RDFParser p = new TurtleParser();
        final Writer stringWriter = new StringWriter();
        
        final RDFWriter w = new RDFJSONWriter(stringWriter);
        p.setRDFHandler(w);
        final InputStream in = RDFJSONTestUtils.class.getResourceAsStream(fileName);
        try
        {
            p.parse(in, RDFJSONTestConstants.BASE_URI);
            return new JSONObject(stringWriter.toString());
        }
        finally
        {
            in.close();
        }
    }
    
    public static final String parseTurtleAndWriteTurtle(final String fileName) throws Exception
    {
        final RDFParser p = new TurtleParser();
        final Writer stringWriter = new StringWriter();
        
        final RDFWriter w = new TurtleWriter(stringWriter);
        p.setRDFHandler(w);
        final InputStream in = RDFJSONTestUtils.class.getResourceAsStream(fileName);
        try
        {
            p.parse(in, RDFJSONTestConstants.BASE_URI);
            return stringWriter.toString();
        }
        finally
        {
            in.close();
        }
    }
    
    public static final JSONObject parseXMLAndWriteJson(final String fileName) throws Exception
    {
        final RDFParser p = new RDFXMLParser();
        final Writer stringWriter = new StringWriter();
        
        final RDFWriter w = new RDFJSONWriter(stringWriter);
        p.setRDFHandler(w);
        final InputStream in = RDFJSONTestConstants.class.getResourceAsStream(fileName);
        try
        {
            p.parse(in, RDFJSONTestConstants.BASE_URI);
            return new JSONObject(stringWriter.toString());
        }
        finally
        {
            in.close();
        }
    }
}
