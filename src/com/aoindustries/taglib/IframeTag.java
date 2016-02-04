/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2011, 2012, 2013, 2015, 2016  AO Industries, Inc.
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

import com.aoindustries.encoding.Coercion;
import com.aoindustries.encoding.MediaType;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.net.EmptyParameters;
import com.aoindustries.net.HttpParameters;
import com.aoindustries.net.HttpParametersMap;
import com.aoindustries.servlet.http.LastModifiedServlet;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import static com.aoindustries.taglib.ApplicationResources.accessor;
import com.aoindustries.util.i18n.MarkupType;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.DynamicAttributes;

/**
 * @author  AO Industries, Inc.
 */
public class IframeTag
	extends AutoEncodingBufferedTag
	implements
		DynamicAttributes,
		IdAttribute,
		SrcAttribute,
		ParamsAttribute,
		WidthAttribute,
		HeightAttribute,
		FrameborderAttribute
{

    private Object id;
    private String src;
    private HttpParametersMap params;
	private boolean srcAbsolute;
	private LastModifiedServlet.AddLastModifiedWhen addLastModified = LastModifiedServlet.AddLastModifiedWhen.AUTO;
    private Object width;
    private Object height;
    private boolean frameborder = true;

    @Override
    public MediaType getContentType() {
        return MediaType.XHTML;
    }

    @Override
    public MediaType getOutputType() {
        return MediaType.XHTML;
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public void setId(Object id) {
		this.id = id;
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

	public boolean getSrcAbsolute() {
		return srcAbsolute;
	}

	public void setSrcAbsolute(boolean srcAbsolute) {
		this.srcAbsolute = srcAbsolute;
	}

	public String getAddLastModified() {
		return addLastModified.getLowerName();
	}

	public void setAddLastModified(String addLastModified) {
		this.addLastModified = LastModifiedServlet.AddLastModifiedWhen.valueOfLowerName(addLastModified);
	}

	@Override
    public Object getWidth() {
        return width;
    }

    @Override
    public void setWidth(Object width) {
        this.width = width;
    }

    @Override
    public Object getHeight() {
        return height;
    }

    @Override
    public void setHeight(Object height) {
		this.height = height;
    }

    @Override
    public boolean isFrameborder() {
        return frameborder;
    }

    @Override
    public void setFrameborder(boolean frameborder) {
        this.frameborder = frameborder;
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

	@Override
    protected void doTag(BufferResult capturedBody, Writer out) throws JspTagException, IOException {
		if(src==null) throw new AttributeRequiredException("src");
		out.write("<iframe");
		if(id!=null) {
			out.write(" id=\"");
			Coercion.write(id, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		UrlUtils.writeSrc(out, getJspContext(), src, params, srcAbsolute, addLastModified);
		if(width!=null) {
			out.write(" width=\"");
			Coercion.write(width, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(height!=null) {
			out.write(" height=\"");
			Coercion.write(height, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		out.write(" frameborder=\"");
		out.write(frameborder ? '1' : '0');
		out.write("\">");
		MarkupUtils.writeWithMarkup(capturedBody, MarkupType.XHTML, out);
		out.write("</iframe>");
    }
}
