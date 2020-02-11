/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013, 2014, 2015, 2016, 2017, 2019, 2020  AO Industries, Inc.
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
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import static com.aoindustries.encoding.TextInXhtmlEncoder.textInXhtmlEncoder;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.net.MutableURIParameters;
import com.aoindustries.net.URIDecoder;
import com.aoindustries.net.URIParametersMap;
import com.aoindustries.net.URIParser;
import com.aoindustries.net.URIResolver;
import com.aoindustries.servlet.http.Dispatcher;
import com.aoindustries.servlet.http.LastModifiedServlet;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import static com.aoindustries.taglib.ApplicationResources.accessor;
import com.aoindustries.util.i18n.MarkupType;
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
public class ATag
	extends AutoEncodingBufferedTag
	implements
		DynamicAttributes,
		IdAttribute,
		HrefAttribute,
		ParamsAttribute,
		HreflangAttribute,
		RelAttribute,
		TypeAttribute,
		TargetAttribute,
		TitleAttribute,
		ClassAttribute,
		StyleAttribute,
		OnclickAttribute,
		OnmouseoverAttribute,
		OnmouseoutAttribute
{

	private Object id;
	private String href;
	private MutableURIParameters params;
	private boolean absolute;
	private boolean canonical;
	private LastModifiedServlet.AddLastModifiedWhen addLastModified = LastModifiedServlet.AddLastModifiedWhen.AUTO;
	private Object hreflang;
	private Object rel;
	private Object type;
	private Object target;
	private Object title;
	private Object clazz;
	private Object style;
	private Object onclick;
	private Object onmouseover;
	private Object onmouseout;

	@Override
	public MediaType getContentType() {
		return MediaType.XHTML;
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
		out.write("<a");
		if(id!=null) {
			out.write(" id=\"");
			Coercion.write(id, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		String transformed;
		if(URIParser.isScheme(href, "tel")) {
			transformed = href.replace(' ', '-');
		} else {
			transformed = href;
		}
		UrlUtils.writeHref(getJspContext(), out, transformed, params, absolute, canonical, addLastModified);
		if(hreflang instanceof Locale) {
			out.write(" hreflang=\"");
			encodeTextInXhtmlAttribute(((Locale)hreflang).toLanguageTag(), out);
			out.write('"');
		} else {
			hreflang = AttributeUtils.trimNullIfEmpty(hreflang);
			if(hreflang != null) {
				out.write(" hreflang=\"");
				Coercion.write(hreflang, textInXhtmlAttributeEncoder, out);
				out.write('"');
			}
		}
		if(rel!=null) {
			out.write(" rel=\"");
			Coercion.write(rel, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(type!=null) {
			out.write(" type=\"");
			Coercion.write(type, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(target!=null) {
			out.write(" target=\"");
			Coercion.write(target, textInXhtmlAttributeEncoder, out);
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
		if(onclick!=null) {
			out.write(" onclick=\"");
			Coercion.write(onclick, MarkupType.JAVASCRIPT, javaScriptInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
		if(onmouseover!=null) {
			out.write(" onmouseover=\"");
			Coercion.write(onmouseover, MarkupType.JAVASCRIPT, javaScriptInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
		if(onmouseout!=null) {
			out.write(" onmouseout=\"");
			Coercion.write(onmouseout, MarkupType.JAVASCRIPT, javaScriptInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
		out.write('>');
		BufferResult trimmedBody = capturedBody.trim();
		if(trimmedBody.getLength()==0) {
			// When the body is empty after trimming, display the href itself
			if(href!=null) {
				PageContext pageContext = (PageContext)getJspContext();
				HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
				String toDecode;
				if(URIParser.isScheme(href, "mailto")) {
					toDecode = href.substring("mailto:".length());
				} else if(URIParser.isScheme(href, "telnet")) {
					toDecode = href.substring("telnet:".length());
				} else if(URIParser.isScheme(href, "tel")) {
					toDecode = href.substring("tel:".length());
				} else {
					toDecode = URIResolver.getAbsolutePath(Dispatcher.getCurrentPagePath(request), href);
				}
				// Decode to get a human-readable (but still unambiguous) display
				URIDecoder.decodeURI(toDecode, textInXhtmlEncoder, out);
			}
		} else {
			Coercion.write(trimmedBody, MarkupType.XHTML, out);
		}
		out.write("</a>");
	}
}
