/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2013  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aocode-public-taglib.
 *
 * aocode-public-taglib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aocode-public-taglib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aocode-public-taglib.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.taglib;

import com.aoindustries.encoding.MediaException;
import com.aoindustries.encoding.MediaType;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.io.Coercion;
import com.aoindustries.net.EmptyParameters;
import com.aoindustries.net.HttpParameters;
import com.aoindustries.net.HttpParametersMap;
import com.aoindustries.util.i18n.MarkupType;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * @author  AO Industries, Inc.
 */
public class ScriptTag
	extends AutoEncodingBufferedTag
	implements
		TypeAttribute,
		SrcAttribute,
		ParamsAttribute
{

	private Object type = MediaType.JAVASCRIPT;
    private MediaType mediaType = MediaType.JAVASCRIPT;
    private String src;
    private HttpParametersMap params;

    @Override
    public MediaType getContentType() {
        return mediaType;
    }

    @Override
    public MediaType getOutputType() {
        return src!=null ? MediaType.XHTML : mediaType;
    }

	@Override
    public Object getType() {
        return type;
    }

	@Override
    public void setType(Object type) throws JspException {
		try {
			MediaType newMediaType =
				(type instanceof MediaType)
				? (MediaType)type
				: MediaType.getMediaTypeForContentType(Coercion.toString(type))
			;
			if(newMediaType!=MediaType.JAVASCRIPT) throw new MediaException(ApplicationResources.accessor.getMessage("ScriptTag.unsupportedMediaType", newMediaType));
			this.type = type;
			this.mediaType = newMediaType;
		} catch(MediaException e) {
			throw new JspException(e);
		}
    }

    @Override
    public String getSrc() {
        return src;
    }

    @Override
    public void setSrc(String src) {
        this.src = src;
    }

    @Override
    public HttpParameters getParams() {
        return params==null ? EmptyParameters.getInstance() : params;
    }

    @Override
    public void addParam(String name, String value) {
        if(params==null) params = new HttpParametersMap();
        params.addParameter(name, value);
    }

	//@Override
	//protected void setMediaEncoderOptions(MediaWriter mediaWriter) {
	//	if(mediaWriter instanceof JavaScriptInXhtmlWriter) {
	//		assert src==null;
	//		((JavaScriptInXhtmlWriter)mediaWriter).setType(type);
	//	}
	//}

	@Override
    protected void doTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
		if(src==null) {
			// Use default auto encoding
			MarkupUtils.writeWithMarkup(capturedBody, mediaType.getMarkupType(), out);
		} else {
			// Write script tag with src attribute, discarding any body
			PageContext pageContext = (PageContext)getJspContext();
			out.write("<script type=\"");
			encodeTextInXhtmlAttribute(mediaType.getContentType(), out);
			out.write('"');
			IframeTag.writeSrc(out, pageContext, src, params);
			out.write("></script>");
		}
    }
}
