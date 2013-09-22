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

import com.aoindustries.encoding.JavaScriptInXhtmlEncoder;
import com.aoindustries.encoding.MediaEncoder;
import com.aoindustries.encoding.MediaException;
import com.aoindustries.encoding.MediaType;
import com.aoindustries.io.AutoTempFileWriter;
import com.aoindustries.net.EmptyParameters;
import com.aoindustries.net.HttpParameters;
import com.aoindustries.net.HttpParametersMap;
import com.aoindustries.util.EncodingUtils;
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

    private MediaType type = MediaType.JAVASCRIPT;
    private String src;
    private HttpParametersMap params;

    @Override
    public MediaType getContentType() {
        return type;
    }

    @Override
    public MediaType getOutputType() {
        return src!=null ? MediaType.XHTML : type;
    }

	@Override
    public String getType() {
        return type.getMediaType();
    }

	@Override
    public void setType(String type) throws JspException {
		try {
			MediaType newType = MediaType.getMediaType(type);
			if(newType!=MediaType.JAVASCRIPT) throw new MediaException(ApplicationResources.accessor.getMessage("ScriptTag.unsupportedMediaType", type));
			this.type = newType;
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

	@Override
	protected void setMediaEncoderOptions(MediaEncoder mediaEncoder) {
		if(mediaEncoder instanceof JavaScriptInXhtmlEncoder) {
			assert src==null;
			((JavaScriptInXhtmlEncoder)mediaEncoder).setType(type);
		}
	}

	@Override
    protected void doTag(AutoTempFileWriter capturedBody, Writer out) throws JspException, IOException {
		if(src==null) {
			// Use default auto encoding
			capturedBody.writeTo(out);
		} else {
			// Write script tag with src attribute, discarding any body
			PageContext pageContext = (PageContext)getJspContext();
			out.write("<script type=\"");
			EncodingUtils.encodeXmlAttribute(type.getMediaType(), out);
			out.write('"');
			IframeTag.writeSrc(out, pageContext, src, params);
			out.write("></script>");
		}
    }
}
