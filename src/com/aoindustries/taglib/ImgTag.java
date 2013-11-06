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

import com.aoindustries.encoding.MediaType;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.io.Coercion;
import com.aoindustries.net.EmptyParameters;
import com.aoindustries.net.HttpParameters;
import com.aoindustries.net.HttpParametersMap;
import com.aoindustries.servlet.http.ServletUtil;
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
public class ImgTag
	extends AutoEncodingBufferedTag
	implements
		DynamicAttributes,
		SrcAttribute,
		ParamsAttribute,
		WidthAttribute,
		HeightAttribute,
		AltAttribute,
		TitleAttribute,
		ClassAttribute,
		StyleAttribute
{

    private String src;
    private HttpParametersMap params;
	private ServletUtil.AddLastModifiedWhen addLastModified = ServletUtil.AddLastModifiedWhen.AUTO;
    private Object width;
    private Object height;
    private Object alt;
    private Object title;
    private Object clazz;
    private Object style;

    @Override
    public MediaType getContentType() {
        return MediaType.TEXT;
    }

    @Override
    public MediaType getOutputType() {
        return MediaType.XHTML;
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

	public String getAddLastModified() {
		return addLastModified.getLowerName();
	}

	public void setAddLastModified(String addLastModified) {
		this.addLastModified = ServletUtil.AddLastModifiedWhen.valueOfLowerName(addLastModified);
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
    public Object getAlt() {
        return alt;
    }

    @Override
    public void setAlt(Object alt) {
		this.alt = alt;
    }

    @Override
    public Object getTitle() {
        return title;
    }

    @Override
    public void setTitle(Object title) {
        this.title = title;
    }

    @Override
    public Object getClazz() {
        return clazz;
    }

    @Override
    public void setClazz(Object clazz) {
		this.clazz = clazz;
    }

    @Override
    public Object getStyle() {
        return style;
    }

    @Override
    public void setStyle(Object style) {
        this.style = style;
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
		if(src==null) src = capturedBody.trim().toString();
		if(width==null) throw new AttributeRequiredException("width");
		if(height==null) throw new AttributeRequiredException("height");
		if(alt==null) throw new AttributeRequiredException("alt");

		out.write("<img src=\"");
		encodeTextInXhtmlAttribute(
			UrlUtils.buildUrl(getJspContext(), src, params, addLastModified),
			out
		);
		out.write("\" width=\"");
		Coercion.write(width, textInXhtmlAttributeEncoder, out);
		out.write("\" height=\"");
		Coercion.write(height, textInXhtmlAttributeEncoder, out);
		out.write("\" alt=\"");
		MarkupUtils.writeWithMarkup(alt, MarkupType.TEXT, textInXhtmlAttributeEncoder, out);
		out.write('"');
		if(title!=null) {
			out.write(" title=\"");
			MarkupUtils.writeWithMarkup(title, MarkupType.TEXT, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(clazz!=null) {
			out.write(" class=\"");
			Coercion.write(clazz, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(style!=null) {
			out.write(" style=\"");
			Coercion.write(style, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		out.write(" />");
    }
}
