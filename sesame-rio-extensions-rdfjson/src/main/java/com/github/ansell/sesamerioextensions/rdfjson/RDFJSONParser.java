package com.github.ansell.sesamerioextensions.rdfjson;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.helpers.RDFParserBase;

import com.github.ansell.sesamerioextensions.api.RDFFormatExtensions;

/**
 * {@link RDFParser} implementation for the RDF/JSON format
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class RDFJSONParser extends RDFParserBase implements RDFParser
{
    private final RDFFormat actualFormat;
    
    public RDFJSONParser()
    {
        this.actualFormat = RDFFormatExtensions.RDFJSONPREFERRED;
    }
    
    public RDFJSONParser(final RDFFormat actualFormat)
    {
        this.actualFormat = actualFormat;
    }
    
    @Override
    public RDFFormat getRDFFormat()
    {
        return this.actualFormat;
    }
    
    @Override
    public void parse(final InputStream inputStream, final String baseUri) throws IOException, RDFParseException,
        RDFHandlerException
    {
        if(null == this.rdfHandler)
        {
            throw new IllegalStateException("RDF handler has not been set");
        }
        
        this.rdfHandler.startRDF();
        RDFJSONUtility.rdfJsonToHandler(inputStream, this.rdfHandler, this.valueFactory);
        this.rdfHandler.endRDF();
    }
    
    @Override
    public void parse(final Reader reader, final String baseUri) throws IOException, RDFParseException,
        RDFHandlerException
    {
        if(null == this.rdfHandler)
        {
            throw new IllegalStateException("RDF handler has not been set");
        }
        
        this.rdfHandler.startRDF();
        RDFJSONUtility.rdfJsonToHandler(reader, this.rdfHandler, this.valueFactory);
        this.rdfHandler.endRDF();
    }
    
}