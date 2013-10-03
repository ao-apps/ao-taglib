/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2011, 2012, 2013  AO Industries, Inc.
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

import com.aoindustries.encoding.MediaType;
import com.aoindustries.encoding.NewEncodingUtils;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.io.Coercion;
import com.aoindustries.net.EmptyParameters;
import com.aoindustries.net.HttpParameters;
import com.aoindustries.net.HttpParametersMap;
import com.aoindustries.net.HttpParametersUtils;
import com.aoindustries.net.MutableHttpParameters;
import com.aoindustries.servlet.jsp.LocalizedJspException;
import static com.aoindustries.taglib.ApplicationResources.accessor;
import com.aoindustries.util.i18n.MarkupType;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
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
	public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
		if(
			uri==null
			&& localName.startsWith(ParamUtils.PARAM_ATTRIBUTE_PREFIX)
		) {
			ParamUtils.setDynamicAttribute(this, uri, localName, value);
		} else {
			throw new LocalizedJspException(
				accessor,
				"error.unexpectedDynamicAttribute",
				localName,
				ParamUtils.PARAM_ATTRIBUTE_PREFIX+"*"
			);
		}
	}

	static void writeSrc(Writer out, PageContext pageContext, String src, MutableHttpParameters params) throws JspException, IOException {
		if(src!=null) {
            HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
			out.write(" src=\"");
			if(src.startsWith("/")) {
				String contextPath = ((HttpServletRequest)pageContext.getRequest()).getContextPath();
				if(contextPath.length()>0) src = contextPath+src;
			}
			src = HttpParametersUtils.addParams(src, params);
			encodeTextInXhtmlAttribute(
				response.encodeURL(
					NewEncodingUtils.encodeUrlPath(src)
				),
				out
			);
			out.write('"');
        } else {
            if(params!=null) throw new LocalizedJspException(ApplicationResources.accessor, "IframeTag.doTag.paramsWithoutSrc");
		}
	}

	@Override
    protected void doTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
		PageContext pageContext = (PageContext)getJspContext();
		if(src==null) throw new AttributeRequiredException("src");
		out.write("<iframe");
		if(id!=null) {
			out.write(" id=\"");
			Coercion.write(id, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		writeSrc(out, pageContext, src, params);
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
