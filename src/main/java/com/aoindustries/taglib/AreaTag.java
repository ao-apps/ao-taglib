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
import com.aoindustries.servlet.http.LastModifiedServlet;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import static com.aoindustries.taglib.ApplicationResources.accessor;
import com.aoindustries.util.StringUtility;
import com.aoindustries.util.i18n.MarkupType;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspTagException;
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
		try {
			this.id = Coercion.nullIfEmpty(id);
		} catch(IOException e) {
			throw new JspTagException(e);
		}
	}

	public static boolean isValidShape(String shape) {
		return
			"default".equals(shape)
			|| "rect".equals(shape)
			|| "circle".equals(shape)
			|| "poly".equals(shape);
	}

	public void setShape(String shape) throws JspTagException {
		shape = shape.trim(); // TODO: Review more places that should be trimmed
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
		if(coords != null) coords = coords.trim();
		this.coords = StringUtility.nullIfEmpty(coords);
	}

	@Override
	public void setHref(String href) {
		this.href = href;
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
		this.addLastModified = LastModifiedServlet.AddLastModifiedWhen.valueOfLowerName(addLastModified); // TODO: Trim all these?
	}

	@Override
	public void setHreflang(Object hreflang) throws JspTagException {
		try {
			this.hreflang = Coercion.nullIfEmpty(hreflang);
		} catch(IOException e) {
			throw new JspTagException(e);
		}
	}

	@Override
	public void setRel(Object rel) throws JspTagException {
		try {
			this.rel = Coercion.nullIfEmpty(rel);
		} catch(IOException e) {
			throw new JspTagException(e);
		}
	}

	@Override
	public void setType(Object type) throws JspTagException {
		try {
			this.type = Coercion.nullIfEmpty(type);
		} catch(IOException e) {
			throw new JspTagException(e);
		}
	}

	@Override
	public void setTarget(Object target) throws JspTagException {
		try {
			this.target = Coercion.nullIfEmpty(target);
		} catch(IOException e) {
			throw new JspTagException(e);
		}
	}

	@Override
	public void setAlt(Object alt) throws JspTagException {
		try {
			this.alt = Coercion.nullIfEmpty(alt);
		} catch(IOException e) {
			throw new JspTagException(e);
		}
	}

	@Override
	public void setTitle(Object title) throws JspTagException {
		try {
			this.title = Coercion.nullIfEmpty(title);
		} catch(IOException e) {
			throw new JspTagException(e);
		}
	}

	@Override
	public Object getClazz() {
		return clazz;
	}

	@Override
	public void setClazz(Object clazz) throws JspTagException {
		try {
			this.clazz = Coercion.nullIfEmpty(clazz);
		} catch(IOException e) {
			throw new JspTagException(e);
		}
	}

	@Override
	public void setStyle(Object style) throws JspTagException {
		try {
			this.style = Coercion.nullIfEmpty(style);
		} catch(IOException e) {
			throw new JspTagException(e);
		}
	}

	@Override
	public void setOnclick(Object onclick) throws JspTagException {
		try {
			this.onclick = Coercion.nullIfEmpty(onclick);
		} catch(IOException e) {
			throw new JspTagException(e);
		}
	}

	@Override
	public void setOnmouseover(Object onmouseover) throws JspTagException {
		try {
			this.onmouseover = Coercion.nullIfEmpty(onmouseover);
		} catch(IOException e) {
			throw new JspTagException(e);
		}
	}

	@Override
	public void setOnmouseout(Object onmouseout) throws JspTagException {
		try {
			this.onmouseout = Coercion.nullIfEmpty(onmouseout);
		} catch(IOException e) {
			throw new JspTagException(e);
		}
	}

	@Override
	protected void doTag(Writer out) throws JspTagException, IOException {
		if(shape == null) throw new AttributeRequiredException("shape");
		if(!"default".equals(shape)) {
			if(coords == null) throw new AttributeRequiredException("coords");
		}
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
		UrlUtils.writeHref(getJspContext(), out, href, params, absolute, canonical, addLastModified);
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
		if(alt != null) {
			out.write(" alt=\"");
			MarkupUtils.writeWithMarkup(alt, MarkupType.TEXT, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(title != null) {
			out.write(" title=\"");
			MarkupUtils.writeWithMarkup(title, MarkupType.TEXT, textInXhtmlAttributeEncoder, out);
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
			Coercion.write(onclick, javaScriptInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(onmouseover != null) {
			out.write(" onmouseover=\"");
			Coercion.write(onmouseover, javaScriptInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(onmouseout != null) {
			out.write(" onmouseout=\"");
			Coercion.write(onmouseout, javaScriptInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		out.write(" />");
	}
}
