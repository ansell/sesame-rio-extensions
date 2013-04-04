package com.github.ansell.sesamerioextensions.rdfjson;

import org.kohsuke.MetaInfServices;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFParserFactory;
import org.openrdf.rio.rdfjson.RDFJSONParser;

import com.github.ansell.sesamerioextensions.api.RDFFormatExtensions;

/**
 * Parser Factory for the RDFJSONParser.
 * 
 * @author fkleedorfer
 */
@MetaInfServices(RDFParserFactory.class)
public class RDFJSONPreferredParserFactory implements RDFParserFactory {

	@Override
	public RDFParser getParser() {
		return new RDFJSONParser(this.getRDFFormat());
	}

	@Override
	public RDFFormat getRDFFormat() {
		return RDFFormatExtensions.RDFJSONPREFERRED;
	}

}
