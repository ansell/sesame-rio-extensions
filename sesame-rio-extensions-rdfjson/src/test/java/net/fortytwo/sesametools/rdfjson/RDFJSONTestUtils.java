/**
 *
 */
package net.fortytwo.sesametools.rdfjson;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.Assert;
import org.openrdf.model.Model;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.helpers.BasicWriterSettings;
import org.openrdf.rio.helpers.StatementCollector;
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
    public static final void parseJsonAndWriteJson(final String fileName, final Model results) throws Exception
    {
        final RDFJSONParser p = new RDFJSONParser();
        final Writer stringWriter = new StringWriter();
        
        final RDFWriter w = new RDFJSONWriter(stringWriter);
        w.getWriterConfig().set(BasicWriterSettings.PRETTY_PRINT, false);
        p.setRDFHandler(w);
        final InputStream in = RDFJSONTestUtils.class.getResourceAsStream(fileName);
        Assert.assertNotNull("Could not find test resource : " + fileName, in);
        try
        {
            p.parse(in, RDFJSONTestConstants.BASE_URI);
        }
        finally
        {
            in.close();
        }
        
        final RDFParser resultsParser = new RDFJSONParser();
        resultsParser.setRDFHandler(new StatementCollector(results));
        resultsParser.parse(new StringReader(stringWriter.toString()), RDFJSONTestConstants.BASE_URI);
    }
    
    public static final void parseJsonAndWriteTurtle(final String fileName) throws Exception
    {
        final RDFParser p = new RDFJSONParser();
        final Writer stringWriter = new StringWriter();
        
        final RDFWriter w = new TurtleWriter(stringWriter);
        w.getWriterConfig().set(BasicWriterSettings.PRETTY_PRINT, false);
        p.setRDFHandler(w);
        final InputStream in = RDFJSONTestUtils.class.getResourceAsStream(fileName);
        try
        {
            p.parse(in, RDFJSONTestConstants.BASE_URI);
        }
        finally
        {
            in.close();
        }
    }
    
    public static void parseRdfXmlAndWriteJson(final String fileName) throws Exception
    {
        final RDFParser p = new RDFXMLParser();
        final Writer stringWriter = new StringWriter();
        
        final RDFWriter w = new RDFJSONWriter(stringWriter);
        w.getWriterConfig().set(BasicWriterSettings.PRETTY_PRINT, false);
        p.setRDFHandler(w);
        final InputStream in = RDFJSONTestUtils.class.getResourceAsStream(fileName);
        try
        {
            p.parse(in, RDFJSONTestConstants.BASE_URI);
        }
        finally
        {
            in.close();
        }
    }
    
    public static final void parseTurtleAndWriteJson(final String fileName) throws Exception
    {
        final RDFParser p = new TurtleParser();
        final Writer stringWriter = new StringWriter();
        
        final RDFWriter w = new RDFJSONWriter(stringWriter);
        w.getWriterConfig().set(BasicWriterSettings.PRETTY_PRINT, false);
        p.setRDFHandler(w);
        final InputStream in = RDFJSONTestUtils.class.getResourceAsStream(fileName);
        try
        {
            p.parse(in, RDFJSONTestConstants.BASE_URI);
        }
        finally
        {
            in.close();
        }
    }
    
    public static final void parseTurtleAndWriteTurtle(final String fileName) throws Exception
    {
        final RDFParser p = new TurtleParser();
        final Writer stringWriter = new StringWriter();
        
        final RDFWriter w = new TurtleWriter(stringWriter);
        w.getWriterConfig().set(BasicWriterSettings.PRETTY_PRINT, false);
        p.setRDFHandler(w);
        final InputStream in = RDFJSONTestUtils.class.getResourceAsStream(fileName);
        try
        {
            p.parse(in, RDFJSONTestConstants.BASE_URI);
        }
        finally
        {
            in.close();
        }
    }
    
    public static final void parseXMLAndWriteJson(final String fileName, final Model results) throws Exception
    {
        final RDFParser p = new RDFXMLParser();
        final Writer stringWriter = new StringWriter();
        
        final RDFWriter w = new RDFJSONWriter(stringWriter);
        w.getWriterConfig().set(BasicWriterSettings.PRETTY_PRINT, false);
        p.setRDFHandler(w);
        final InputStream in = RDFJSONTestConstants.class.getResourceAsStream(fileName);
        try
        {
            p.parse(in, RDFJSONTestConstants.BASE_URI);
            // return stringWriter.toString();
        }
        finally
        {
            in.close();
        }
        
        final RDFParser resultsParser = new RDFJSONParser();
        resultsParser.setRDFHandler(new StatementCollector(results));
        resultsParser.parse(new StringReader(stringWriter.toString()), RDFJSONTestConstants.BASE_URI);
    }
}
