package com.github.ansell.sesamerioextensions.rdfjson;

import java.io.OutputStream;
import java.io.Writer;

import org.kohsuke.MetaInfServices;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFWriterFactory;

/**
 * Writer Factory for the RDFJSONWriter.
 * 
 * @author fkleedorfer
 */
@MetaInfServices(RDFWriterFactory.class)
public class RDFJSONStandardWriterFactory implements RDFWriterFactory
{
    
    @Override
    public RDFFormat getRDFFormat()
    {
        return RDFFormat.RDFJSON;
    }
    
    @Override
    public RDFWriter getWriter(final OutputStream out)
    {
        return new RDFJSONWriter(out);
    }
    
    @Override
    public RDFWriter getWriter(final Writer writer)
    {
        return new RDFJSONWriter(writer);
    }
    
}
