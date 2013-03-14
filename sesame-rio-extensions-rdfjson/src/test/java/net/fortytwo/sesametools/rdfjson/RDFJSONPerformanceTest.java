/**
 * 
 */
package net.fortytwo.sesametools.rdfjson;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.impl.LinkedHashModel;

/**
 * Tests the performance of the RDFJSONParser and Writer against the relative performance of the
 * Sesame Turtle Parser and Writer
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RDFJSONPerformanceTest
{
    private long queryStartTime;
    private long queryEndTime;
    private long nextTotalTime;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        this.queryStartTime = System.currentTimeMillis();
        this.queryEndTime = 0L;
        this.nextTotalTime = 0L;
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        this.queryEndTime = System.currentTimeMillis();
        this.nextTotalTime = this.queryEndTime - this.queryStartTime;
        System.out.println("testTiming: nextTotalTime=" + this.nextTotalTime);
        
        this.queryStartTime = 0L;
        this.queryEndTime = 0L;
        this.nextTotalTime = 0L;
    }
    
    @Test
    public void testJsonAndJsonPerformance() throws Exception
    {
        RDFJSONTestUtils.parseJsonAndWriteJson("bio2rdf-configuration.json", new LinkedHashModel());
    }
    
    @Test
    public void testJsonAndTurtlePerformance() throws Exception
    {
        RDFJSONTestUtils.parseJsonAndWriteTurtle("bio2rdf-configuration.json");
    }
    
    @Test
    public void testTurtleAndJsonPerformance() throws Exception
    {
        RDFJSONTestUtils.parseTurtleAndWriteJson("bio2rdf-configuration.ttl");
    }
    
    @Test
    public void testTurtleAndTurtlePerformance() throws Exception
    {
        RDFJSONTestUtils.parseTurtleAndWriteTurtle("bio2rdf-configuration.ttl");
    }
}
