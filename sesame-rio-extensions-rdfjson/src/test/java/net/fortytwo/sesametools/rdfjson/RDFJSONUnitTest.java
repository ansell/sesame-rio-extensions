/**
 * 
 */
package net.fortytwo.sesametools.rdfjson;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.model.BNode;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kmr.scam.rest.util.RDFJSON;

/**
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class RDFJSONUnitTest
{
    private static final Logger log = LoggerFactory.getLogger(RDFJSONUnitTest.class);
    
    private String testInputFile;
    private String testInput;
    private Writer testWriter;
    private String testOutput;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        this.testWriter = new StringWriter();
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        this.testInputFile = null;
        this.testWriter = null;
        this.testOutput = null;
    }
    
    /**
     * Test method for {@link se.kmr.scam.rest.util.RDFJSON#graphToRdfJsonPreordered(java.util.Set)}
     * .
     */
    @Test
    @Ignore
    public void testGraphToRdfJsonPreorderedSetOfStatement()
    {
        Assert.fail("Not yet implemented"); // TODO
    }
    
    /**
     * Test method for
     * {@link se.kmr.scam.rest.util.RDFJSON#graphToRdfJsonPreordered(java.util.Set, java.io.Writer)}
     * .
     * 
     * @throws JSONException
     */
    @Test
    public void testGraphToRdfJsonPreorderedSetOfStatementWriter() throws JSONException
    {
        final Set<Statement> testStatements = new TreeSet<Statement>(new StatementComparator());
        final ValueFactoryImpl vf = ValueFactoryImpl.getInstance();
        
        final BNode testBNode1 = vf.createBNode();
        
        final BNode testBNode2 = vf.createBNode();
        
        final URI testURI1 = vf.createURI("http://example.org/test/rdf/json/1");
        
        final URI testURI2 = vf.createURI("http://my.test.org/rdf/type/2");
        
        final URI testURI3 = vf.createURI("http://example.org/test/rdf/json/3");
        
        final URI testURI4 = vf.createURI("http://example.org/test/rdf/json/4");
        
        final URI testURI5 = vf.createURI("http://my.test.org/rdf/type/5");
        
        final Statement testStatement1 = vf.createStatement(testURI1, testURI2, testURI3);
        testStatements.add(testStatement1);
        final Statement testStatement2 = vf.createStatement(testURI1, testURI2, testBNode1);
        testStatements.add(testStatement2);
        final Statement testStatement3 = vf.createStatement(testURI1, testURI2, testBNode2);
        testStatements.add(testStatement3);
        
        final Statement testStatement4 = vf.createStatement(testURI4, testURI2, testURI3);
        testStatements.add(testStatement4);
        final Statement testStatement5 = vf.createStatement(testURI4, testURI2, testBNode2);
        testStatements.add(testStatement5);
        final Statement testStatement6 = vf.createStatement(testURI4, testURI2, testBNode1);
        testStatements.add(testStatement6);
        
        final Statement testStatement7 = vf.createStatement(testBNode1, testURI5, testBNode2);
        testStatements.add(testStatement7);
        final Statement testStatement8 = vf.createStatement(testBNode1, testURI5, testURI1);
        testStatements.add(testStatement8);
        final Statement testStatement9 = vf.createStatement(testBNode1, testURI5, testURI4);
        testStatements.add(testStatement9);
        
        RDFJSONUnitTest.log.info("testStatements=" + testStatements);
        
        Assert.assertEquals(9, testStatements.size());
        
        // Verify that the statements are in an acceptable order (testStatement5 and testStatement6
        // can be legitimately swapped)
        
        final Iterator<Statement> testStatementIterator = testStatements.iterator();
        
        Assert.assertTrue(testStatementIterator.hasNext());
        
        // testStatement7 should always be first by virtue of the fact that it has two blank nodes
        // and no other statements have two blank nodes
        Assert.assertEquals(testStatement7, testStatementIterator.next());
        Assert.assertTrue(testStatementIterator.hasNext());
        
        // Then testStatement8
        Assert.assertEquals(testStatement8, testStatementIterator.next());
        Assert.assertTrue(testStatementIterator.hasNext());
        
        // Then testStatement9
        Assert.assertEquals(testStatement9, testStatementIterator.next());
        Assert.assertTrue(testStatementIterator.hasNext());
        
        final Writer testWriter2 = RDFJSON.graphToRdfJsonPreordered(testStatements, this.testWriter);
        
        // The returned writer should be the same as the one that was sent in
        Assert.assertEquals(this.testWriter, testWriter2);
        
        this.testOutput = this.testWriter.toString();
        
        Assert.assertTrue(this.testOutput.length() > 0);
        
        Assert.assertTrue(this.testOutput.startsWith("{"));
        
        Assert.assertTrue(this.testOutput.endsWith("}"));
        
        RDFJSONUnitTest.log.info("testOutput=" + this.testOutput);
        
        final int firstBlankNode = this.testOutput.indexOf("\"_:");
        
        // Test that a bnode exists after the opening brace
        Assert.assertTrue(firstBlankNode > 0);
        
        // The first value after the first blank node should be a blank node identifier
        final int firstValue = this.testOutput.indexOf("\"value\":\"_:", firstBlankNode);
        
        Assert.assertTrue("A suitable blank node value was not found", firstValue > 0);
        
        // This should be guaranteed by the indexOf contract, but doing a quick check anyway
        Assert.assertTrue(firstValue > firstBlankNode);
        
        // Do a quick check to see if the testOutput is valid JSON
        final JSONObject testJSONObject = new JSONObject(this.testOutput);
        
        Assert.assertNotNull(testJSONObject);
        Assert.assertTrue(testJSONObject.length() > 0);
        Assert.assertTrue(testJSONObject.names().length() > 0);
        Assert.assertTrue(testJSONObject.keys().hasNext());
    }
    
    /**
     * Test method for {@link se.kmr.scam.rest.util.RDFJSON#rdfJsonToGraph(java.lang.String)}.
     * 
     * @throws IOException
     */
    @Test
    public void testRdfJsonToGraph() throws IOException
    {
        this.testInputFile = "example0.json";
        
        this.testInput = IOUtils.toString(this.getClass().getResourceAsStream(this.testInputFile), "utf-8");
        
        Assert.assertNotNull(this.testInput);
        
        Assert.assertTrue(this.testInput.length() > 0);
        
        final Collection<Statement> rdfJsonToGraph = RDFJSON.rdfJsonToGraph(this.testInput);
        
        Assert.assertEquals(12, rdfJsonToGraph.size());
    }
    
}
