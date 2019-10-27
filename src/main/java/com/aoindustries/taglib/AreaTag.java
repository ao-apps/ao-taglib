/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2019  AO Industries, Inc.
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
import static com.aoindustries.encoding.JavaScriptInXhtmlAttributeEncoder.javaScriptInXhtmlAttributeEncoder;
import com.aoindustries.encoding.MediaType;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import com.aoindustries.net.MutableURIParameters;
import com.aoindustries.net.URIParametersMap;
import com.aoindustries.servlet.http.Html;
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
public class AreaTag
	extends AutoEncodingNullTag
	implements
		DynamicAttributes,
		IdAttribute,
		HrefAttribute,
		ParamsAttribute,
		HreflangAttribute,
		RelAttribute,
		TypeAttribute,
		TargetAttribute,
		AltAttribute,
		TitleAttribute,
		ClassAttribute,
		StyleAttribute,
		OnclickAttribute,
		OnmouseoverAttribute,
		OnmouseoutAttribute
{

	private Object id;
	private String shape;
	private String coords;
	private String href;
	private MutableURIParameters params;
	private boolean absolute;
	private boolean canonical;
	private LastModifiedServlet.AddLastModifiedWhen addLastModified = LastModifiedServlet.AddLastModifiedWhen.AUTO;
	private Object hreflang;
	private Object rel;
	private Object type;
	private Object target;
	private Object alt;
	private Object title;
	private Object clazz;
	private Object style;
	private Object onclick;
	private Object onmouseover;
	private Object onmouseout;

	@Override
	public MediaType getOutputType() {
		return MediaType.XHTML;
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
	public void setId(Object id) throws JspTagException {
		this.id = AttributeUtils.trimNullIfEmpty(id);
	}

	public static boolean isValidShape(String shape) {
		return
			"default".equals(shape)
			|| "rect".equals(shape)
			|| "circle".equals(shape)
			|| "poly".equals(shape);
	}

	public void setShape(String shape) throws JspTagException {
		shape = shape.trim();
		if(shape.isEmpty()) {
			throw new AttributeRequiredException("shape");
		} else if(!isValidShape(shape)) {
			throw new LocalizedJspTagException(
				accessor,
				"AreaTag.shape.invalid",
				shape
			);
		}
		this.shape = shape;
	}

	public void setCoords(String coords) {
		this.coords = AttributeUtils.trimNullIfEmpty(coords);
	}

	@Override
	public void setHref(String href) throws JspTagException {
		this.href = AttributeUtils.nullIfEmpty(href);
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
	public void setHreflang(Object hreflang) throws JspTagException {
		this.hreflang = AttributeUtils.trimNullIfEmpty(hreflang);
	}

	@Override
	public void setRel(Object rel) throws JspTagException {
		this.rel = AttributeUtils.trimNullIfEmpty(rel);
	}

	@Override
	public void setType(Object type) throws JspTagException {
		this.type = AttributeUtils.trimNullIfEmpty(type);
	}

	@Override
	public void setTarget(Object target) throws JspTagException {
		this.target = AttributeUtils.trimNullIfEmpty(target);
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

	@Override
	public void setOnclick(Object onclick) throws JspTagException {
		this.onclick = AttributeUtils.trimNullIfEmpty(onclick);
	}

	@Override
	public void setOnmouseover(Object onmouseover) throws JspTagException {
		this.onmouseover = AttributeUtils.trimNullIfEmpty(onmouseover);
	}

	@Override
	public void setOnmouseout(Object onmouseout) throws JspTagException {
		this.onmouseout = AttributeUtils.trimNullIfEmpty(onmouseout);
	}

	@Override
	protected void doTag(Writer out) throws JspTagException, IOException {
		if(shape == null) throw new AttributeRequiredException("shape");
		if(!"default".equals(shape)) {
			if(coords == null) throw new AttributeRequiredException("coords");
		}
		if(href != null) {
			if(alt == null) throw new AttributeRequiredException("alt");
		}
		PageContext pageContext = (PageContext)getJspContext();
		Html html = Html.get(
			pageContext.getServletContext(),
			(HttpServletRequest)pageContext.getRequest(),
			out
		);
		out.write("<area");
		if(id != null) {
			out.write(" id=\"");
			Coercion.write(id, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		out.write(" shape=\"");
		textInXhtmlAttributeEncoder.write(shape, out);
		out.write('"');
		if(coords != null) {
			out.write(" coords=\"");
			textInXhtmlAttributeEncoder.write(coords, out);
			out.write('"');
		}
		UrlUtils.writeHref(pageContext, out, href, params, absolute, canonical, addLastModified);
		if(hreflang != null) {
			out.write(" hreflang=\"");
			Coercion.write(hreflang, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(rel != null) {
			out.write(" rel=\"");
			Coercion.write(rel, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(type != null) {
			out.write(" type=\"");
			Coercion.write(type, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(target != null) {
			out.write(" target=\"");
			Coercion.write(target, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(
			alt != null
			&& (href != null || !Coercion.isEmpty(alt))
		) {
			out.write(" alt=\"");
			Coercion.write(alt, MarkupType.TEXT, textInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
		if(title != null) {
			out.write(" title=\"");
			Coercion.write(title, MarkupType.TEXT, textInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
		if(clazz != null) {
			out.write(" class=\"");
			Coercion.write(clazz, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(style != null) {
			out.write(" style=\"");
			Coercion.write(style, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(onclick != null) {
			out.write(" onclick=\"");
			Coercion.write(onclick, MarkupType.JAVASCRIPT, javaScriptInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
		if(onmouseover != null) {
			out.write(" onmouseover=\"");
			Coercion.write(onmouseover, MarkupType.JAVASCRIPT, javaScriptInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
		if(onmouseout != null) {
			out.write(" onmouseout=\"");
			Coercion.write(onmouseout, MarkupType.JAVASCRIPT, javaScriptInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
		html.selfClose();
	}
}
