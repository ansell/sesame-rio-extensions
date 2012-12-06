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
     * The <a href="http://dvcs.w3.org/hg/rdf/raw-file/default/rdf-json/index.html">RDF/JSON</a>
     * file format is an RDF serialisation format that supports recording of named graphs.
     * <p>
     * The file extension <code>.rj</code> is recommended for RDF/JSON documents, <code>.trix</code>
     * is also accepted. The media type is <code>application/rdf+json</code> and the encoding is
     * UTF-8.
     * </p>
     * 
     * @see http://dvcs.w3.org/hg/rdf/raw-file/default/rdf-json/index.html
     */
    public static final RDFFormat RDFJSON = new RDFFormat("RDF/JSON", Arrays.asList("application/rdf+json",
            "application/json"), // RDF/JSON mime type from
                                 // http://dvcs.w3.org/hg/rdf/raw-file/default/rdf-json/index.html#sec-mediaReg
            Charset.forName("UTF-8"), // See section 3 of the JSON RFC:
                                      // http://www.ietf.org/rfc/rfc4627.txt
            Arrays.asList("rj", "json"), // See
                                         // http://dvcs.w3.org/hg/rdf/raw-file/default/rdf-json/index.html#sec-mediaReg
            false, // namespaces are not supported
            true);
    
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
