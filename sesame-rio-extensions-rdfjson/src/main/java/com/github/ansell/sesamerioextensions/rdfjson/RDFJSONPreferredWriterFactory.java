package com.github.ansell.sesamerioextensions.rdfjson;

import java.io.OutputStream;
import java.io.Writer;

import org.kohsuke.MetaInfServices;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFWriterFactory;
import org.openrdf.rio.rdfjson.RDFJSONWriter;

import com.github.ansell.sesamerioextensions.api.RDFFormatExtensions;

/**
 * Writer Factory for the RDFJSONWriter.
 * 
 * @author fkleedorfer
 */
@MetaInfServices(RDFWriterFactory.class)
public class RDFJSONPreferredWriterFactory implements RDFWriterFactory
{
    
    @Override
    public RDFFormat getRDFFormat()
    {
        return RDFFormatExtensions.RDFJSONPREFERRED;
    }
    
    @Override
    public RDFWriter getWriter(final OutputStream out)
    {
        return new RDFJSONWriter(out, this.getRDFFormat());
    }
    
    @Override
    public RDFWriter getWriter(final Writer writer)
    {
        return new RDFJSONWriter(writer, this.getRDFFormat());
    }
    
}
