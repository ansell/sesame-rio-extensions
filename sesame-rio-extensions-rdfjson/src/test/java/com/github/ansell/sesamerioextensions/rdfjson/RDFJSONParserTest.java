package com.github.ansell.sesamerioextensions.rdfjson;

import java.io.InputStream;
import java.util.Collections;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.rio.helpers.StatementCollector;

import com.github.ansell.sesamerioextensions.rdfjson.RDFJSONTestConstants.FOAF;

/**
 * @author Joshua Shinavier (http://fortytwo.net).
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RDFJSONParserTest
{
    private final ValueFactory vf = ValueFactoryImpl.getInstance();
    private Model g;
    
    protected void assertExpected(final Model actual, final Statement... expectedStatements) throws Exception
    {
        final Model expected = new TreeModel();
        Collections.addAll(expected, expectedStatements);
        
        for(final Statement t : expected)
        {
            if(!actual.contains(t))
            {
                Assert.fail("expected statement not found: " + t);
            }
        }
        for(final Statement t : actual)
        {
            if(!expected.contains(t))
            {
                Assert.fail("unexpected statement found: " + t);
            }
        }
    }
    
    @After
    public void cleanup()
    {
        // make sure we nullify the graph after each test is complete
        this.g = null;
    }
    
    protected Model parseToGraph(final String fileName) throws Exception
    {
        final RDFJSONParser p = new RDFJSONParser();
        final Model model = new LinkedHashModel();
        p.setRDFHandler(new StatementCollector(model));
        
        final InputStream in = RDFJSONParser.class.getResourceAsStream(fileName);
        Assert.assertNotNull("Could not find test resource: " + fileName, in);
        try
        {
            p.parse(in, RDFJSONTestConstants.BASE_URI);
        }
        finally
        {
            in.close();
        }
        
        return model;
    }
    
    @Test
    public void testExpectedStatements() throws Exception
    {
        this.g = this.parseToGraph("example1.json");
        
        // System.out.println("example1.json.size()="+g.size());
        
        Assert.assertEquals(6, this.g.size());
        
        this.assertExpected(
                this.g,
                this.vf.createStatement(RDFJSONTestConstants.ARTHUR, RDF.TYPE, FOAF.PERSON),
                this.vf.createStatement(RDFJSONTestConstants.ARTHUR, RDF.TYPE,
                        this.vf.createURI(OWL.NAMESPACE + "Thing"), null),
                this.vf.createStatement(RDFJSONTestConstants.ARTHUR, RDF.TYPE,
                        this.vf.createURI(OWL.NAMESPACE + "Thing"), RDFJSONTestConstants.GRAPH1),
                this.vf.createStatement(RDFJSONTestConstants.ARTHUR, FOAF.NAME,
                        this.vf.createLiteral("Arthur Dent", "en")),
                this.vf.createStatement(RDFJSONTestConstants.ARTHUR, FOAF.KNOWS, RDFJSONTestConstants.P1),
                this.vf.createStatement(RDFJSONTestConstants.P1, FOAF.NAME,
                        this.vf.createLiteral("Ford Prefect", XMLSchema.STRING)));
    }
    
    @Test
    public void testSizeWithNullGraphs() throws Exception
    {
        this.g = this.parseToGraph("example2.json");
        
        for(final Statement st : this.g)
        {
            System.out.println(st);
        }
        
        Assert.assertEquals(6, this.g.size());
    }
    
    @Test
    public void testSizeWithoutNullGraphs() throws Exception
    {
        this.g = this.parseToGraph("example0.json");
        
        // for (Statement st : g) {
        // System.out.println(st);
        // }
        
        Assert.assertEquals(12, this.g.size());
        
    }
}
