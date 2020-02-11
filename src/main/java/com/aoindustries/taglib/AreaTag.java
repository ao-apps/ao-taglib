/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2019, 2020  AO Industries, Inc.
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
import com.aoindustries.html.Area;
import com.aoindustries.html.Html;
import com.aoindustries.html.servlet.HtmlEE;
import com.aoindustries.net.MutableURIParameters;
import com.aoindustries.net.URIParametersMap;
import com.aoindustries.servlet.http.LastModifiedServlet;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import static com.aoindustries.taglib.ApplicationResources.accessor;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
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
		this.hreflang = hreflang;
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
		Html html = HtmlEE.get(
			pageContext.getServletContext(),
			(HttpServletRequest)pageContext.getRequest(),
			out
		);
		Area area = html.area()
			.id(id)
			.shape(shape)
			.coords(coords)
			.href(UrlUtils.getHref(pageContext, href, params, absolute, canonical, addLastModified));
		if(hreflang instanceof Locale) {
			area.hreflang((Locale)hreflang);
		} else {
			hreflang = AttributeUtils.trimNullIfEmpty(hreflang);
			area.hreflang(Coercion.toString(hreflang));
		}
		area
			// TODO: These are coerced in both uses, change attribute to String
			.rel(Coercion.toString(rel))
			// TODO: type to Area (or remove entirely since this part of the standard is uncertain and currently unimplemented by all browsers?)
			.attribute("type", type)
			// TODO: target to Area
			.attribute("target", target);
		// non-existing alt is OK when there is no href (with href: "" stays "")
		if(
			alt != null
			&& (href != null || !Coercion.isEmpty(alt))
		) {
			area.alt(alt);
		}
		area
			.title(title)
			.clazz(clazz)
			.style(style)
			.onclick(onclick)
			.onmouseover(onmouseover)
			.onmouseout(onmouseout)
			.__();
	}
}
