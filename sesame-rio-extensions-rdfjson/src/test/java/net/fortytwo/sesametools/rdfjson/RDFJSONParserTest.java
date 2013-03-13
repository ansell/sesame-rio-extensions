package net.fortytwo.sesametools.rdfjson;

import java.io.InputStream;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import net.fortytwo.sesametools.rdfjson.RDFJSONTestConstants.FOAF;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.rio.helpers.StatementCollector;

/**
 * @author Joshua Shinavier (http://fortytwo.net).
 */
public class RDFJSONParserTest
{
    
    private Model g;
    
    protected void assertExpected(final Model graph, final Statement... expectedStatements) throws Exception
    {
        final Set<Statement> expected = new TreeSet<Statement>(new StatementComparator());
        Collections.addAll(expected, expectedStatements);
        final Set<Statement> actual = new TreeSet<Statement>(new StatementComparator());
        for(final Statement st : graph)
        {
            actual.add(st);
        }
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
        
        this.assertExpected(this.g, RDFJSONTestConstants.vf.createStatement(RDFJSONTestConstants.ARTHUR, RDF.TYPE,
                FOAF.PERSON), RDFJSONTestConstants.vf.createStatement(RDFJSONTestConstants.ARTHUR, RDF.TYPE,
                RDFJSONTestConstants.vf.createURI(OWL.NAMESPACE + "Thing"), null), RDFJSONTestConstants.vf
                .createStatement(RDFJSONTestConstants.ARTHUR, RDF.TYPE,
                        RDFJSONTestConstants.vf.createURI(OWL.NAMESPACE + "Thing"), RDFJSONTestConstants.GRAPH1),
                RDFJSONTestConstants.vf.createStatement(RDFJSONTestConstants.ARTHUR, FOAF.NAME,
                        RDFJSONTestConstants.vf.createLiteral("Arthur Dent", "en")), RDFJSONTestConstants.vf
                        .createStatement(RDFJSONTestConstants.ARTHUR, FOAF.KNOWS, RDFJSONTestConstants.P1),
                RDFJSONTestConstants.vf.createStatement(RDFJSONTestConstants.P1, FOAF.NAME,
                        RDFJSONTestConstants.vf.createLiteral("Ford Prefect", XMLSchema.STRING)));
    }
    
    @Test
    public void testSizeWithNullGraphs() throws Exception
    {
        this.g = this.parseToGraph("example2.json");
        
        for(Statement st : g)
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
