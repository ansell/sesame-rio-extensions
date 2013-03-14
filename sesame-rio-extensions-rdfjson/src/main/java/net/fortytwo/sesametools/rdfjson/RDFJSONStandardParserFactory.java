package net.fortytwo.sesametools.rdfjson;

import org.kohsuke.MetaInfServices;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFParserFactory;

/**
 * Parser Factory for the RDFJSONParser.
 * 
 * @author fkleedorfer
 */
@MetaInfServices(RDFParserFactory.class)
public class RDFJSONStandardParserFactory implements RDFParserFactory
{
    
    @Override
    public RDFParser getParser()
    {
        return new RDFJSONParser(RDFFormat.RDFJSON);
    }
    
    @Override
    public RDFFormat getRDFFormat()
    {
        return RDFFormat.RDFJSON;
    }
    
}
