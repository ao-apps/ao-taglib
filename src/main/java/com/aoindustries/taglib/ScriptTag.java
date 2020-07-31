/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2013, 2015, 2016, 2017, 2019, 2020  AO Industries, Inc.
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
import com.aoindustries.html.Html;
import com.aoindustries.html.servlet.HtmlEE;
import com.aoindustries.io.LocalizedUnsupportedEncodingException;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.lang.Strings;
import com.aoindustries.net.MutableURIParameters;
import com.aoindustries.net.URIParametersMap;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import com.aoindustries.servlet.lastmodified.AddLastModified;
import static com.aoindustries.taglib.ApplicationResources.accessor;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.DynamicAttributes;

/**
 * @author  AO Industries, Inc.
 */
public class ScriptTag
	extends ElementBufferedTag
	implements
		DynamicAttributes,
		TypeAttribute,
		SrcAttribute,
		ParamsAttribute
{

	private MediaType mediaType = MediaType.JAVASCRIPT;
	private String src;
	private MutableURIParameters params;
	private boolean absolute;
	private boolean canonical;
	// TODO: async, defer, ...
	private AddLastModified addLastModified = AddLastModified.AUTO;

	@Override
	public MediaType getContentType() {
		return mediaType;
	}

	@Override
	public MediaType getOutputType() {
		return MediaType.XHTML;
	}

	@Override
	public void setType(String type) throws JspTagException {
		try {
			MediaType newMediaType = MediaType.getMediaTypeForContentType(Strings.trim(type));
			if(
				newMediaType != MediaType.JAVASCRIPT
				&& newMediaType != MediaType.JSON
				&& newMediaType != MediaType.LD_JSON
			) throw new LocalizedUnsupportedEncodingException(ApplicationResources.accessor, "ScriptTag.unsupportedMediaType", newMediaType);
			this.mediaType = newMediaType;
		} catch(UnsupportedEncodingException e) {
			throw new JspTagException(e);
		}
	}

	@Override
	public void setSrc(String src) {
		this.src = AttributeUtils.nullIfEmpty(src);
	}

	@Override
	public void addParam(String name, String value) {
		if(params==null) params = new URIParametersMap();
		params.addParameter(name, value);
	}

	public void setAbsolute(boolean absolute) {
		this.absolute = absolute;
	}

	public void setCanonical(boolean canonical) {
		this.canonical = canonical;
	}

	public void setAddLastModified(String addLastModified) {
		this.addLastModified = AddLastModified.valueOfLowerName(addLastModified.trim().toLowerCase(Locale.ROOT));
	}

	@Override
	public void setDynamicAttribute(String uri, String localName, Object value) throws JspTagException {
		if(
			uri==null
			&& localName.startsWith(ParamUtils.PARAM_ATTRIBUTE_PREFIX)
		) {
			ParamUtils.setDynamicAttribute(this, uri, localName, value);
		} else {
			throw new LocalizedJspTagException(
				accessor,
				"error.unexpectedDynamicAttribute",
				localName,
				ParamUtils.PARAM_ATTRIBUTE_PREFIX+"*"
			);
		}
	}

	//@Override
	//protected void setMediaEncoderOptions(MediaWriter mediaWriter) {
	//	if(mediaWriter instanceof JavaScriptInXhtmlWriter) {
	//		assert src==null;
	//		((JavaScriptInXhtmlWriter)mediaWriter).setType(type); // .trim()?
	//	}
	//}

	@Override
	protected void doTag(BufferResult capturedBody, Writer out) throws JspTagException, IOException {
		// Write script tag with src attribute, discarding any body
		PageContext pageContext = (PageContext)getJspContext();
		Html html = HtmlEE.get(
			pageContext.getServletContext(),
			(HttpServletRequest)pageContext.getRequest(),
			(HttpServletResponse)pageContext.getResponse(),
			out
		);
		doGlobalAttributes(html.script(mediaType.getContentType()))
			// Call getSrc always, since it validates src versus params
			.src(UrlUtils.getSrc(pageContext, src, params, addLastModified, absolute, canonical))
			// Only write body when there is no source (discard body when src provided)
			.out((src != null) ? null : capturedBody)
			.__();
	}
}
