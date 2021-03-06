/**
 * 
 */
package com.github.ansell.sesamerioextensions.api;

import java.nio.charset.Charset;
import java.util.Arrays;
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
     * The Talis <a
     * href="http://dvcs.w3.org/hg/rdf/raw-file/default/rdf-json/index.html">RDF/JSON</a> file
     * format, an RDF serialization format that supports recording of named graphs.
     * <p>
     * The file extension <code>.rj</code> is recommended for RDF/JSON documents. The media type is
     * <code>application/rdf+json</code> and the encoding is UTF-8.
     * </p>
     * 
     * NOTE: This format supports both application/json as a content type, and .json as an
     * extension, in addition to the specification defined in RDFFormat.RDFJSON.
     * 
     * @see https://dvcs.w3.org/hg/rdf/raw-file/default/rdf-json/index.html
     */
    public static final RDFFormat RDFJSONPREFERRED = new RDFFormat("RDF/JSON-preferred", Arrays.asList(
            "application/rdf+json", "application/json"), Charset.forName("UTF-8"), Arrays.asList("rj", "json"), false,
            true);
    
    static
    {
        RDFFormat.register(RDFFormatExtensions.RDFJSONPREFERRED);
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
    public RDFFormatExtensions(final String name, final Collection<String> mimeTypes, final Charset charset,
            final Collection<String> fileExtensions, final boolean supportsNamespaces, final boolean supportsContexts)
    {
        super(name, mimeTypes, charset, fileExtensions, supportsNamespaces, supportsContexts);
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
    public RDFFormatExtensions(final String name, final String mimeType, final Charset charset,
            final Collection<String> fileExtensions, final boolean supportsNamespaces, final boolean supportsContexts)
    {
        super(name, mimeType, charset, fileExtensions, supportsNamespaces, supportsContexts);
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
     * @param fileExtension
     *            The (default) file extension for the RDF file format, e.g. <tt>rdf</tt> for
     *            RDF/XML files.
     */
    public RDFFormatExtensions(final String name, final String mimeType, final Charset charset,
            final String fileExtension, final boolean supportsNamespaces, final boolean supportsContexts)
    {
        super(name, mimeType, charset, fileExtension, supportsNamespaces, supportsContexts);
    }
    
}
