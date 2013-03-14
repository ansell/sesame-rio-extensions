package net.fortytwo.sesametools.rdfjson;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.util.ModelUtil;
import org.openrdf.rio.helpers.StatementCollector;

/**
 * Tests the RDF/JSON writer by way of the RDF/JSON parser.
 * 
 * @author Joshua Shinavier (http://fortytwo.net).
 */
public class RDFJSONWriterTest
{
    @Ignore
    @Test
    public void testAll() throws Exception
    {
        Assert.fail("TODO: Implement me using Jackson");
        /**
         * JSONObject j; JSONArray values; JSONArray contexts;
         * 
         * j = RDFJSONTestUtils.parseAndWrite("example1.json"); //
         * System.out.println("j.toString="+j.toString(2)); final JSONObject a =
         * j.getJSONObject(RDFJSONTestConstants.ARTHUR.toString()); values =
         * a.getJSONArray(RDF.TYPE.toString()); // System.out.println(values.get(0)); //
         * System.out.println(values.get(1)); Assert.assertEquals(2, values.length());
         * Assert.assertEquals("uri", values.getJSONObject(0).getString("type"));
         * Assert.assertEquals("uri", values.getJSONObject(1).getString("type")); JSONObject t =
         * values.getJSONObject(0); if(FOAF.PERSON.toString().equals(t.getString("value"))) { t =
         * values.getJSONObject(1); } // assertEquals(FOAF.PERSON.toString(),
         * values.getJSONObject(0).getString("value")); Assert.assertEquals(OWL.NAMESPACE + "Thing",
         * t.getString("value")); contexts = t.getJSONArray("graphs"); Assert.assertEquals(2,
         * contexts.length()); // System.out.println(contexts.get(0)); //
         * System.out.println(contexts.get(1));
         * 
         * Assert.assertTrue("null".equals(contexts.getString(0)) ||
         * "null".equals(contexts.getString(1))); values = a.getJSONArray(FOAF.KNOWS.toString());
         * Assert.assertEquals(1, values.length()); final JSONObject f = values.getJSONObject(0);
         * Assert.assertEquals("bnode", f.getString("type"));
         * Assert.assertTrue(f.getString("value").startsWith("_:"));
         * 
         * // Blank node subject final JSONObject p1 = j.getJSONObject("_:p1"); final JSONArray n =
         * p1.getJSONArray(FOAF.NAME.stringValue()); Assert.assertEquals(1, n.length());
         * Assert.assertEquals("Ford Prefect", n.getJSONObject(0).get("value"));
         * 
         * // j = parseAndWrite("example0.json");
         * 
         */
    }
    
    @Ignore
    @Test
    public void testBlankNodes() throws Exception
    {
        Assert.fail("TODO: Implement me using Jackson");
        /**
         * JSONObject j;
         * 
         * j = RDFJSONTestUtils.parseRdfXmlAndWriteJson("example3.rdf");
         * 
         * int subjects = 0; int bnodes = 0; final Iterator keys = j.keys(); while(keys.hasNext()) {
         * final String s = (String)keys.next(); if(s.startsWith("_:")) { bnodes++; } subjects++; }
         * Assert.assertEquals(1, bnodes); Assert.assertEquals(2, subjects);
         * 
         * final JSONObject o =
         * j.getJSONObject("http://www.bbc.co.uk/things/76369f3b-65a0-4e69-8c52-859adfdefa49#id");
         * final JSONArray a = o.getJSONArray("http://www.bbc.co.uk/ontologies/sport/discipline");
         * Assert.assertEquals(1, a.length()); Assert.assertEquals("bnode",
         * a.getJSONObject(0).getString("type"));
         **/
        
    }
    
    @Test
    public void testJsonParseRdfJsonWrite() throws Exception
    {
        final Model model = new LinkedHashModel();
        
        RDFJSONTestUtils.parseJsonAndWriteJson("example2.json", model);
        
        Assert.assertEquals(6, model.size());
    }
    
    @Test
    public void testRdfJsonParseRdfJsonWrite() throws Exception
    {
        final RDFJSONParser p = new RDFJSONParser();
        final Model model = new LinkedHashModel();
        p.setRDFHandler(new StatementCollector(model));
        
        final InputStream in = RDFJSONParser.class.getResourceAsStream("example4.json");
        try
        {
            p.parse(in, RDFJSONTestConstants.BASE_URI);
        }
        finally
        {
            in.close();
        }
        
        final StringWriter sw = new StringWriter();
        final RDFJSONWriter w = new RDFJSONWriter(sw);
        w.startRDF();
        for(final Statement nextStatement : model)
        {
            w.handleStatement(nextStatement);
        }
        w.endRDF();
        
        final RDFJSONParser comparisonParser = new RDFJSONParser();
        final Model comparisonModel = new LinkedHashModel();
        comparisonParser.setRDFHandler(new StatementCollector(comparisonModel));
        
        // System.err.println("writer wrote: " + sw.toString());
        
        comparisonParser.parse(new StringReader(sw.toString()), RDFJSONTestConstants.BASE_URI);
        
        for(final Statement nextStatement : model)
        {
            if(!comparisonModel.contains(nextStatement))
            {
                System.err.println(nextStatement);
            }
        }
        
        for(final Statement nextStatement : comparisonModel)
        {
            if(!model.contains(nextStatement))
            {
                System.err.println(nextStatement);
            }
        }
        
        Assert.assertEquals(2, model.size());
        Assert.assertEquals(2, comparisonModel.size());
        
        Assert.assertTrue(ModelUtil.equals(model, comparisonModel));
    }
    
    @Test
    public void testRdfXmlParseRdfJsonWrite() throws Exception
    {
        final Model model = new LinkedHashModel();
        
        RDFJSONTestUtils.parseXMLAndWriteJson("example3.xml", model);
        
        Assert.assertEquals(5, model.size());
    }
    
}
