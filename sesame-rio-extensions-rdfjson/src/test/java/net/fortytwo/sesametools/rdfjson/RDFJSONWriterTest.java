package net.fortytwo.sesametools.rdfjson;

import java.util.Iterator;

import net.fortytwo.sesametools.rdfjson.RDFJSONTestConstants.FOAF;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;

/**
 * Tests the RDF/JSON writer by way of the RDF/JSON parser.
 * 
 * @author Joshua Shinavier (http://fortytwo.net).
 */
public class RDFJSONWriterTest
{
    @Test
    public void testAll() throws Exception
    {
        JSONObject j;
        JSONArray values;
        JSONArray contexts;
        
        j = RDFJSONTestUtils.parseAndWrite("example1.json");
        // System.out.println("j.toString="+j.toString(2));
        final JSONObject a = j.getJSONObject(RDFJSONTestConstants.ARTHUR.toString());
        values = a.getJSONArray(RDF.TYPE.toString());
        // System.out.println(values.get(0));
        // System.out.println(values.get(1));
        Assert.assertEquals(2, values.length());
        Assert.assertEquals("uri", values.getJSONObject(0).getString("type"));
        Assert.assertEquals("uri", values.getJSONObject(1).getString("type"));
        JSONObject t = values.getJSONObject(0);
        if(FOAF.PERSON.toString().equals(t.getString("value")))
        {
            t = values.getJSONObject(1);
        }
        // assertEquals(FOAF.PERSON.toString(), values.getJSONObject(0).getString("value"));
        Assert.assertEquals(OWL.NAMESPACE + "Thing", t.getString("value"));
        contexts = t.getJSONArray("graphs");
        Assert.assertEquals(2, contexts.length());
        // System.out.println(contexts.get(0));
        // System.out.println(contexts.get(1));
        
        Assert.assertTrue("null".equals(contexts.getString(0)) || "null".equals(contexts.getString(1)));
        values = a.getJSONArray(FOAF.KNOWS.toString());
        Assert.assertEquals(1, values.length());
        final JSONObject f = values.getJSONObject(0);
        Assert.assertEquals("bnode", f.getString("type"));
        Assert.assertTrue(f.getString("value").startsWith("_:"));
        
        // Blank node subject
        final JSONObject p1 = j.getJSONObject("_:p1");
        final JSONArray n = p1.getJSONArray(FOAF.NAME.stringValue());
        Assert.assertEquals(1, n.length());
        Assert.assertEquals("Ford Prefect", n.getJSONObject(0).get("value"));
        
        // j = parseAndWrite("example0.json");
    }
    
    @Test
    public void testBlankNodes() throws Exception
    {
        JSONObject j;
        
        j = RDFJSONTestUtils.parseRdfXmlAndWriteJson("example3.rdf");
        
        int subjects = 0;
        int bnodes = 0;
        final Iterator keys = j.keys();
        while(keys.hasNext())
        {
            final String s = (String)keys.next();
            if(s.startsWith("_:"))
            {
                bnodes++;
            }
            subjects++;
        }
        Assert.assertEquals(1, bnodes);
        Assert.assertEquals(2, subjects);
        
        final JSONObject o = j.getJSONObject("http://www.bbc.co.uk/things/76369f3b-65a0-4e69-8c52-859adfdefa49#id");
        final JSONArray a = o.getJSONArray("http://www.bbc.co.uk/ontologies/sport/discipline");
        Assert.assertEquals(1, a.length());
        Assert.assertEquals("bnode", a.getJSONObject(0).getString("type"));
        
    }
    
    @Test
    public void testRdfXmlParseRdfJsonWrite() throws Exception
    {
        final JSONObject j = RDFJSONTestUtils.parseXMLAndWriteJson("example3.xml");
        // TODO: add tests to check the results
    }
}
