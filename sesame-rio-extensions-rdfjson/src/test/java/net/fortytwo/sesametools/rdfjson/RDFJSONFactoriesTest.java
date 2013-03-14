package net.fortytwo.sesametools.rdfjson;

import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Test;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;

import com.github.ansell.sesamerioextensions.rdfjson.RDFJSONParser;
import com.github.ansell.sesamerioextensions.rdfjson.RDFJSONWriter;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RDFJSONFactoriesTest
{
    @Test
    public void testParserFactoryRegistered()
    {
        final RDFFormat fmt = Rio.getParserFormatForMIMEType("application/rdf+json");
        Assert.assertNotNull("Could not find the RDF/JSON RDFFormat instance", fmt);
        final RDFParser parser = Rio.createParser(fmt);
        Assert.assertTrue(parser instanceof RDFJSONParser);
    }
    
    @Test
    public void testParserFactoryRegisteredAlternate()
    {
        final RDFFormat fmt = Rio.getParserFormatForMIMEType("application/json");
        Assert.assertNotNull("Could not find the RDF/JSON RDFFormat instance", fmt);
        final RDFParser parser = Rio.createParser(fmt);
        Assert.assertTrue(parser instanceof RDFJSONParser);
    }
    
    @Test
    public void testWriterFactoryRegistered()
    {
        final RDFFormat fmt = Rio.getWriterFormatForMIMEType("application/rdf+json");
        Assert.assertNotNull("Could not find the RDF/JSON RDFFormat instance", fmt);
        final RDFWriter writer = Rio.createWriter(fmt, new StringWriter());
        Assert.assertTrue(writer instanceof RDFJSONWriter);
    }
    
    @Test
    public void testWriterFactoryRegisteredAlternate()
    {
        final RDFFormat fmt = Rio.getWriterFormatForMIMEType("application/json");
        Assert.assertNotNull("Could not find the RDF/JSON RDFFormat instance", fmt);
        final RDFWriter writer = Rio.createWriter(fmt, new StringWriter());
        Assert.assertTrue(writer instanceof RDFJSONWriter);
    }
}
