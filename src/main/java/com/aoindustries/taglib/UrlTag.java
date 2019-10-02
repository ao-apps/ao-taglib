/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013, 2016, 2017, 2019  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of ao-taglib.
 *
 * ao-taglib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ao-taglib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ao-taglib.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.taglib;

import com.aoindustries.encoding.MediaType;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.net.MutableURIParameters;
import com.aoindustries.net.URI;
import com.aoindustries.net.URIParametersMap;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspTagException;

/**
 * TODO: Have absolute option
 * TODO: Replace uses of ao:text with this as it is now more appropriate for sending dynamic parameters to JavaScript since it calls encodeURL.
 *
 * @author  AO Industries, Inc.
 */
public class UrlTag extends AutoEncodingBufferedTag implements ParamsAttribute {

	private MutableURIParameters params;

	@Override
	public MediaType getContentType() {
		return MediaType.URL;
	}

	@Override
	public MediaType getOutputType() {
		return MediaType.URL;
	}

	@Override
	public void addParam(String name, String value) {
		if(params==null) params = new URIParametersMap();
		params.addParameter(name, value);
	}

	@Override
	protected void doTag(BufferResult capturedBody, Writer out) throws JspTagException, IOException {
		// TODO: decodeURI for UTF-8, encodeURI for all others
		URI splitUrl = new URI(capturedBody.trim().toString()).addParameters(params);
		/* TODO: This?: if(url.startsWith("/")) {
			HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
			out.write(request.getContextPath());
		}*/
		out.write(splitUrl.toString());
	}
}
