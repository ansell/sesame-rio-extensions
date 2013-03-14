/**
 * 
 */
package net.fortytwo.sesametools.rdfjson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.BNode;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.WriterConfig;
import org.openrdf.rio.helpers.BasicWriterSettings;
import org.openrdf.rio.helpers.StatementCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ansell.sesamerioextensions.rdfjson.RDFJSONUtility;

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
    private WriterConfig testWriterConfig;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        this.testWriter = new StringWriter();
        this.testWriterConfig = new WriterConfig();
        this.testWriterConfig.set(BasicWriterSettings.PRETTY_PRINT, false);
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        this.testInputFile = null;
        this.testWriter = null;
        this.testWriterConfig = null;
        this.testOutput = null;
    }
    
    /**
     * Test method for
     * {@link RDFJSONUtility.kmr.scam.rest.util.RDFJSON#graphToRdfJsonPreordered(java.util.Set, java.io.Writer)}
     * .
     * 
     * @throws JSONException
     */
    @Test
    public void testModelToRdfJsonPreorderedSetOfStatementWriter() throws Exception
    {
        // final Set<Statement> testStatements = new TreeSet<Statement>(new StatementComparator());
        final Model testStatements = new TreeModel();
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
        
        // RDFJSONUnitTest.log.info("testStatements=" + testStatements);
        
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
        
        RDFJSONUtility.modelToRdfJson(testStatements, this.testWriter, this.testWriterConfig);
        
        this.testOutput = this.testWriter.toString();
        
        Assert.assertTrue(this.testOutput.length() > 0);
        
        Assert.assertTrue(this.testOutput.startsWith("{"));
        
        Assert.assertTrue(this.testOutput.endsWith("}"));
        
        // RDFJSONUnitTest.log.info("testOutput=" + this.testOutput);
        
        final int firstBlankNode = this.testOutput.indexOf("\"_:");
        
        // Test that a bnode exists after the opening brace
        Assert.assertTrue(firstBlankNode > 0);
        
        // The first value after the first blank node should be a blank node identifier
        final int firstValue = this.testOutput.indexOf("\"value\":\"_:", firstBlankNode);
        
        Assert.assertTrue("A suitable blank node value was not found", firstValue > 0);
        
        // This should be guaranteed by the indexOf contract, but doing a quick check anyway
        Assert.assertTrue(firstValue > firstBlankNode);
        
        // Do a quick check to see if the testOutput is valid JSON
        
        // FIXME: TODO: Test using Jackson
        // Assert.fail("TODO: Implement me using Jackson");
        // final JSONObject testJSONObject = new JSONObject(this.testOutput);
        
        // Assert.assertNotNull(testJSONObject);
        // Assert.assertTrue(testJSONObject.length() > 0);
        // Assert.assertTrue(testJSONObject.names().length() > 0);
        // Assert.assertTrue(testJSONObject.keys().hasNext());
    }
    
    /**
     * Test method for
     * {@link RDFJSONUtility.kmr.scam.rest.util.RDFJSON#rdfJsonToGraph(java.lang.String)}.
     * 
     * @throws IOException
     * @throws RDFParseException
     * @throws RDFHandlerException
     */
    @Test
    public void testRdfJsonToGraph0() throws IOException, RDFParseException, RDFHandlerException
    {
        this.testInputFile = "example0.json";
        
        final Model rdfJsonToGraph = new LinkedHashModel();
        
        RDFJSONUtility.rdfJsonToHandler(new InputStreamReader(this.getClass().getResourceAsStream(this.testInputFile),
                StandardCharsets.UTF_8), new StatementCollector(rdfJsonToGraph), ValueFactoryImpl.getInstance());
        
        Assert.assertEquals(12, rdfJsonToGraph.size());
    }
    
    /**
     * Test method for
     * {@link RDFJSONUtility.kmr.scam.rest.util.RDFJSON#rdfJsonToGraph(java.lang.String)}.
     * 
     * @throws IOException
     * @throws RDFParseException
     * @throws RDFHandlerException
     */
    @Test
    public void testRdfJsonToGraph5() throws IOException, RDFParseException, RDFHandlerException
    {
        this.testInputFile = "example5.json";
        
        final Model rdfJsonToGraph = new LinkedHashModel();
        
        RDFJSONUtility.rdfJsonToHandler(new InputStreamReader(this.getClass().getResourceAsStream(this.testInputFile),
                StandardCharsets.UTF_8), new StatementCollector(rdfJsonToGraph), ValueFactoryImpl.getInstance());
        
        Assert.assertEquals(1, rdfJsonToGraph.size());
    }
    
}
