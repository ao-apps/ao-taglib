/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2013, 2014, 2015, 2016, 2017, 2019  AO Industries, Inc.
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
import com.aoindustries.encoding.MediaType;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import com.aoindustries.html.Html;
import com.aoindustries.html.Serialization;
import com.aoindustries.html.servlet.HtmlEE;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.net.URIParametersMap;
import com.aoindustries.servlet.http.LastModifiedServlet;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import static com.aoindustries.taglib.ApplicationResources.accessor;
import com.aoindustries.util.i18n.MarkupType;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.DynamicAttributes;

/**
 * @author  AO Industries, Inc.
 */
public class ImgTag
	extends AutoEncodingBufferedTag
	implements
		DynamicAttributes,
		IdAttribute,
		SrcAttribute,
		ParamsAttribute,
		WidthAttribute,
		HeightAttribute,
		AltAttribute,
		TitleAttribute,
		ClassAttribute,
		StyleAttribute
{

	private Object id;
	private String src;
	private URIParametersMap params;
	private boolean absolute;
	private boolean canonical;
	private LastModifiedServlet.AddLastModifiedWhen addLastModified = LastModifiedServlet.AddLastModifiedWhen.AUTO;
	private Object width;
	private Object height;
	private Object alt;
	private Object title;
	private Object clazz;
	private Object style;
	private String usemap;
	private boolean ismap;

	@Override
	public MediaType getContentType() {
		return MediaType.TEXT;
	}

	@Override
	public MediaType getOutputType() {
		return MediaType.XHTML;
	}

	@Override
	public void setId(Object id) throws JspTagException {
		this.id = AttributeUtils.trimNullIfEmpty(id);
	}

	@Override
	public void setSrc(String src) throws JspTagException {
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
	public void setWidth(Object width) throws JspTagException {
		this.width = AttributeUtils.trimNullIfEmpty(width);
	}

	@Override
	public void setHeight(Object height) throws JspTagException {
		this.height = AttributeUtils.trimNullIfEmpty(height);
	}

	@Override
	public void setAlt(Object alt) throws JspTagException {
		this.alt = AttributeUtils.trim(alt);
	}

	@Override
	public void setTitle(Object title) throws JspTagException {
		this.title = AttributeUtils.trimNullIfEmpty(title);
	}

	@Override
	public Object getClazz() {
		return clazz;
	}

	@Override
	public void setClazz(Object clazz) throws JspTagException {
		this.clazz = AttributeUtils.trimNullIfEmpty(clazz);
	}

	@Override
	public void setStyle(Object style) throws JspTagException {
		this.style = AttributeUtils.trimNullIfEmpty(style);
	}

	public void setUsemap(String usemap) {
		this.usemap = AttributeUtils.trimNullIfEmpty(usemap);
	}

	public void setIsmap(boolean ismap) {
		this.ismap = ismap;
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
		if(usemap == null && alt == null) throw new AttributeRequiredException("alt");
		PageContext pageContext = (PageContext)getJspContext();
		Html html = HtmlEE.get(
			pageContext.getServletContext(),
			(HttpServletRequest)pageContext.getRequest(),
			out
		);
		out.write("<img");
		if(id!=null) {
			out.write(" id=\"");
			Coercion.write(id, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		UrlUtils.writeSrc(getJspContext(), out, src, params, absolute, canonical, addLastModified);
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
		if(alt != null) {
			out.write(" alt=\"");
			Coercion.write(alt, MarkupType.TEXT, textInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
		if(title!=null) {
			out.write(" title=\"");
			Coercion.write(title, MarkupType.TEXT, textInXhtmlAttributeEncoder, false, out);
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
		if(usemap != null) {
			out.write(" usemap=\"");
			if(!usemap.startsWith("#")) out.write('#');
			textInXhtmlAttributeEncoder.write(usemap, out);
			out.write('"');
		}
		if(ismap) {
			out.write(" ismap");
			if(html.serialization == Serialization.XML) out.write("=\"ismap\"");
		}
		html.selfClose();
	}
}
