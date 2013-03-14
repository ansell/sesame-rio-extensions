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

import com.github.ansell.sesamerioextensions.api.RDFFormatExtensions;
import com.github.ansell.sesamerioextensions.rdfjson.RDFJSON;

/**
 * RDFParser implementation for the proposed RDF/JSON format (see
 * http://n2.talis.com/wiki/RDF_JSON_Specification)
 * 
 * @author Joshua Shinavier (http://fortytwo.net). Builds on code by Hannes Ebner
 */
@SuppressWarnings("unused")
public class RDFJSONParser implements RDFParser
{
    
    private ValueFactory valueFactory;
    private RDFHandler rdfHandler;
    private ParseErrorListener parseErrorListener;
    private ParseLocationListener parseLocationListener;
    private ParserConfig config = new ParserConfig();
    
    @Override
    public ParserConfig getParserConfig()
    {
        return this.config;
    }
    
    @Override
    public RDFFormat getRDFFormat()
    {
        return RDFFormatExtensions.RDFJSONPREFERRED;
    }
    
    @Override
    public Collection<RioSetting<?>> getSupportedSettings()
    {
        final Collection<RioSetting<?>> result = new ArrayList<RioSetting<?>>(4);
        
        result.add(BasicParserSettings.DATATYPE_HANDLING);
        result.add(BasicParserSettings.PRESERVE_BNODE_IDS);
        result.add(BasicParserSettings.STOP_AT_FIRST_ERROR);
        result.add(BasicParserSettings.VERIFY_DATA);
        
        return result;
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
        RDFJSON.rdfJsonToHandler(reader, this.rdfHandler);
        this.rdfHandler.endRDF();
    }
    
    @Override
    public void setDatatypeHandling(final DatatypeHandling datatypeHandling)
    {
        this.config.set(BasicParserSettings.DATATYPE_HANDLING, datatypeHandling);
    }
    
    @Override
    public void setParseErrorListener(final ParseErrorListener listener)
    {
        this.parseErrorListener = listener;
    }
    
    @Override
    public void setParseLocationListener(final ParseLocationListener listener)
    {
        this.parseLocationListener = listener;
    }
    
    @Override
    public void setParserConfig(final ParserConfig config)
    {
        this.config = config;
    }
    
    @Override
    public void setPreserveBNodeIDs(final boolean preserveBNodeIDs)
    {
        this.config.set(BasicParserSettings.PRESERVE_BNODE_IDS, preserveBNodeIDs);
    }
    
    @Override
    public void setRDFHandler(final RDFHandler handler)
    {
        this.rdfHandler = handler;
    }
    
    @Override
    public void setStopAtFirstError(final boolean stopAtFirstError)
    {
        this.config.set(BasicParserSettings.STOP_AT_FIRST_ERROR, stopAtFirstError);
    }
    
    @Override
    public void setValueFactory(final ValueFactory valueFactory)
    {
        this.valueFactory = valueFactory;
    }
    
    @Override
    public void setVerifyData(final boolean verifyData)
    {
        this.config.set(BasicParserSettings.VERIFY_DATA, verifyData);
    }
}
