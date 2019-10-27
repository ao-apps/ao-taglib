/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2013, 2015, 2016, 2017, 2019  AO Industries, Inc.
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

import com.aoindustries.encoding.Coercion;
import com.aoindustries.encoding.MediaEncoder;
import com.aoindustries.encoding.MediaException;
import com.aoindustries.encoding.MediaType;
import com.aoindustries.encoding.servlet.HttpServletResponseEncodingContext;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.net.MutableURIParameters;
import com.aoindustries.net.URIParametersMap;
import com.aoindustries.servlet.http.Html;
import com.aoindustries.servlet.http.LastModifiedServlet;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import static com.aoindustries.taglib.ApplicationResources.accessor;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.DynamicAttributes;

/**
 * @author  AO Industries, Inc.
 */
public class ScriptTag
	extends AutoEncodingBufferedTag
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
	private LastModifiedServlet.AddLastModifiedWhen addLastModified = LastModifiedServlet.AddLastModifiedWhen.AUTO;

	@Override
	public MediaType getContentType() {
		return mediaType;
	}

	@Override
	public MediaType getOutputType() {
		return MediaType.XHTML;
	}

	@Override
	public void setType(Object type) throws JspTagException {
		try {
			MediaType newMediaType =
				(type instanceof MediaType)
				? (MediaType)type
				: MediaType.getMediaTypeForContentType(Coercion.toString(type).trim())
			;
			if(
				newMediaType != MediaType.JAVASCRIPT
				&& newMediaType != MediaType.JSON
				&& newMediaType != MediaType.LD_JSON
			) throw new MediaException(ApplicationResources.accessor.getMessage("ScriptTag.unsupportedMediaType", newMediaType));
			this.mediaType = newMediaType;
		} catch(MediaException e) {
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
		this.addLastModified = LastModifiedServlet.AddLastModifiedWhen.valueOfLowerName(addLastModified.trim());
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
		try {
			// Write script tag with src attribute, discarding any body
			PageContext pageContext = (PageContext)getJspContext();
			ServletContext servletContext = pageContext.getServletContext();
			HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
			Html html = Html.get(servletContext, request, out);
			Html.Script script = html.script(mediaType.getContentType());
			UrlUtils.writeSrc(pageContext, out, src, params, absolute, canonical, addLastModified);
			out.write('>');
			// Only write body when there is no source (discard body when src provided)
			if(src == null && capturedBody.getLength() != 0) {
				html.nl();
				HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
				script.cdata.start();
				// TODO: writeWithMarkup appropriate for capturedBody?
				// TODO: I think this would only work with SegmentedBuffer with a single segment
				// TODO: We might need a special case in CharArrayWriter if we want this identity match for a single string
				// TODO: Set back to SegmentedBuffer, if this is the case
				Coercion.write(
					capturedBody,
					mediaType.getMarkupType(),
					MediaEncoder.getInstance(new HttpServletResponseEncodingContext(response), mediaType, getOutputType()),
					false,
					out
				);
				html.nl();
				script.cdata.end();
			}
			out.write("</script>");
		} catch(MediaException err) {
			throw new JspTagException(err);
		}
	}
}
