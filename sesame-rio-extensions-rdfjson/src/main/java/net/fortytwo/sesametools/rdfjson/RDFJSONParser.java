package net.fortytwo.sesametools.rdfjson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.ParseErrorListener;
import org.openrdf.rio.ParseLocationListener;
import org.openrdf.rio.ParserConfig;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RioSetting;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.openrdf.rio.helpers.RDFParserBase;

import com.github.ansell.sesamerioextensions.api.RDFFormatExtensions;
import com.github.ansell.sesamerioextensions.rdfjson.RDFJSON;

/**
 * {@link RDFParser} implementation for the RDF/JSON format
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
@SuppressWarnings("unused")
public class RDFJSONParser extends RDFParserBase implements RDFParser
{
    @Override
    public RDFFormat getRDFFormat()
    {
        return RDFFormatExtensions.RDFJSONPREFERRED;
    }
    
    @Override
    public void parse(final InputStream in, final String baseURI) throws IOException, RDFParseException,
        RDFHandlerException
    {
        final Reader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
        this.parse(reader, baseURI);
    }
    
    @Override
    public void parse(final Reader reader, final String baseURI) throws IOException, RDFParseException,
        RDFHandlerException
    {
        if(null == this.rdfHandler)
        {
            throw new IllegalStateException("RDF handler has not been set");
        }
        
        this.rdfHandler.startRDF();
        RDFJSON.rdfJsonToHandler(reader, this.rdfHandler, this.valueFactory);
        this.rdfHandler.endRDF();
    }
    
}
