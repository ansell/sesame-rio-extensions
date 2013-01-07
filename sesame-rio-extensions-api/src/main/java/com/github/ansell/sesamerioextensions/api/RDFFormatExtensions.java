/**
 * 
 */
package com.github.ansell.sesamerioextensions.api;

import java.nio.charset.Charset;
import java.util.Collection;

import org.openrdf.rio.RDFFormat;

/**
 * RDFFormat definitions relevant to Sesame Rio Extensions.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 * 
 */
public class RDFFormatExtensions extends RDFFormat
{
    
    /**
     * Creates a new RDFFormat object.
     * 
     * @param name
     *            The name of the RDF file format, e.g. "RDF/XML".
     * @param mimeType
     *            The MIME type of the RDF file format, e.g. <tt>application/rdf+xml</tt> for the
     *            RDF/XML file format.
     * @param charset
     *            The default character encoding of the RDF file format. Specify <tt>null</tt> if
     *            not applicable.
     * @param fileExtension
     *            The (default) file extension for the RDF file format, e.g. <tt>rdf</tt> for
     *            RDF/XML files.
     */
    public RDFFormatExtensions(String name, String mimeType, Charset charset, String fileExtension,
            boolean supportsNamespaces, boolean supportsContexts)
    {
        super(name, mimeType, charset, fileExtension, supportsNamespaces, supportsContexts);
    }
    
    /**
     * Creates a new RDFFormat object.
     * 
     * @param name
     *            The name of the RDF file format, e.g. "RDF/XML".
     * @param mimeType
     *            The MIME type of the RDF file format, e.g. <tt>application/rdf+xml</tt> for the
     *            RDF/XML file format.
     * @param charset
     *            The default character encoding of the RDF file format. Specify <tt>null</tt> if
     *            not applicable.
     * @param fileExtensions
     *            The RDF format's file extensions, e.g. <tt>rdf</tt> for RDF/XML files. The first
     *            item in the list is interpreted as the default file extension for the format.
     */
    public RDFFormatExtensions(String name, String mimeType, Charset charset, Collection<String> fileExtensions,
            boolean supportsNamespaces, boolean supportsContexts)
    {
        super(name, mimeType, charset, fileExtensions, supportsNamespaces, supportsContexts);
    }
    
    /**
     * Creates a new RDFFormat object.
     * 
     * @param name
     *            The name of the RDF file format, e.g. "RDF/XML".
     * @param mimeTypes
     *            The MIME types of the RDF file format, e.g. <tt>application/rdf+xml</tt> for the
     *            RDF/XML file format. The first item in the list is interpreted as the default MIME
     *            type for the format.
     * @param charset
     *            The default character encoding of the RDF file format. Specify <tt>null</tt> if
     *            not applicable.
     * @param fileExtensions
     *            The RDF format's file extensions, e.g. <tt>rdf</tt> for RDF/XML files. The first
     *            item in the list is interpreted as the default file extension for the format.
     */
    public RDFFormatExtensions(String name, Collection<String> mimeTypes, Charset charset,
            Collection<String> fileExtensions, boolean supportsNamespaces, boolean supportsContexts)
    {
        super(name, mimeTypes, charset, fileExtensions, supportsNamespaces, supportsContexts);
    }
    
}
