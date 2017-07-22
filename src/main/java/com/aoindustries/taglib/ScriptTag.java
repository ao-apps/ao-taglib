/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2013, 2015, 2016, 2017  AO Industries, Inc.
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
import com.aoindustries.encoding.MediaException;
import com.aoindustries.encoding.MediaType;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.net.HttpParametersMap;
import com.aoindustries.servlet.http.LastModifiedServlet;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import static com.aoindustries.taglib.ApplicationResources.accessor;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspTagException;
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
	private HttpParametersMap params;
	private boolean srcAbsolute;
	private LastModifiedServlet.AddLastModifiedWhen addLastModified = LastModifiedServlet.AddLastModifiedWhen.AUTO;

	@Override
	public MediaType getContentType() {
		return mediaType;
	}

	@Override
	public MediaType getOutputType() {
		return src!=null ? MediaType.XHTML : mediaType;
	}

	@Override
	public void setType(Object type) throws JspTagException {
		try {
			MediaType newMediaType =
				(type instanceof MediaType)
				? (MediaType)type
				: MediaType.getMediaTypeForContentType(Coercion.toString(type))
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
		this.src = src;
	}

	@Override
	public void addParam(String name, String value) {
		if(params==null) params = new HttpParametersMap();
		params.addParameter(name, value);
	}

	public void setSrcAbsolute(boolean srcAbsolute) {
		this.srcAbsolute = srcAbsolute;
	}

	public void setAddLastModified(String addLastModified) {
		this.addLastModified = LastModifiedServlet.AddLastModifiedWhen.valueOfLowerName(addLastModified);
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
	//		((JavaScriptInXhtmlWriter)mediaWriter).setType(type);
	//	}
	//}

	@Override
	protected void doTag(BufferResult capturedBody, Writer out) throws JspTagException, IOException {
		if(src==null) {
			// Use default auto encoding
			MarkupUtils.writeWithMarkup(capturedBody, mediaType.getMarkupType(), out);
		} else {
			// Write script tag with src attribute, discarding any body
			out.write("<script type=\"");
			encodeTextInXhtmlAttribute(mediaType.getContentType(), out);
			out.write('"');
			UrlUtils.writeSrc(getJspContext(), out, src, params, srcAbsolute, addLastModified);
			out.write("></script>");
		}
	}
}
