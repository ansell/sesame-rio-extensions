package net.fortytwo.sesametools.rdfjson;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RioSetting;
import org.openrdf.rio.WriterConfig;
import org.openrdf.rio.helpers.BasicWriterSettings;
import org.openrdf.rio.helpers.RDFWriterBase;

import se.kmr.scam.rest.util.RDFJSON;

import com.github.ansell.sesamerioextensions.api.RDFFormatExtensions;

/**
 * RDFWriter implementation for the proposed RDF/JSON format (see
 * http://n2.talis.com/wiki/RDF_JSON_Specification)
 * 
 * @author Joshua Shinavier (http://fortytwo.net). Builds on code by Hannes Ebner
 */
public class RDFJSONWriter extends RDFWriterBase implements RDFWriter
{
    
    private final Writer writer;
    private Model graph;
    
    public RDFJSONWriter(final OutputStream out)
    {
        this(new OutputStreamWriter(out, Charset.forName("UTF-8")));
    }
    
    public RDFJSONWriter(final Writer writer)
    {
        this.writer = writer;
    }
    
    @Override
    public void endRDF() throws RDFHandlerException
    {
        RDFJSON.modelToRdfJson(this.graph, this.writer);
        try
        {
            this.writer.flush();
        }
        catch(final IOException e)
        {
            throw new RDFHandlerException(e);
        }
    }
    
    @Override
    public RDFFormat getRDFFormat()
    {
        return RDFFormatExtensions.RDFJSONPREFERRED;
    }
    
    @Override
    public void handleComment(final String comment) throws RDFHandlerException
    {
        // Comments are ignored.
    }
    
    @Override
    public void handleNamespace(final String prefix, final String uri) throws RDFHandlerException
    {
        // Namespace prefixes are not used in RDF/JSON.
    }
    
    @Override
    public void handleStatement(final Statement statement) throws RDFHandlerException
    {
        // System.out.println("got it: " + statement);
        this.graph.add(statement);
    }
    
    @Override
    public void startRDF() throws RDFHandlerException
    {
        // this.graph = new TreeSet<Statement>(new StatementComparator());
        // TreeModel extends SortedSet<Statement>, which is what TreeSet<Statement> was being used
        // for previously
        this.graph = new TreeModel();
    }

    @Override
    public Collection<RioSetting<?>> getSupportedSettings()
    {
        Set<RioSetting<?>> results = new HashSet<RioSetting<?>>(super.getSupportedSettings());
        
        results.add(BasicWriterSettings.PRETTY_PRINT);
        
        return results;
    }

}
