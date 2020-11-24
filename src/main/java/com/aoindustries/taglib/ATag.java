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
import com.aoindustries.lang.Strings;
import com.aoindustries.net.MutableURIParameters;
import com.aoindustries.net.URIDecoder;
import com.aoindustries.net.URIParametersMap;
import com.aoindustries.net.URIParser;
import com.aoindustries.net.URIResolver;
import com.aoindustries.servlet.http.Dispatcher;
import com.aoindustries.servlet.lastmodified.AddLastModified;
import com.aoindustries.util.i18n.MarkupType;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

/**
 * @author  AO Industries, Inc.
 */
public class ATag extends ElementBufferedTag
	implements
		// Attributes
		HrefAttribute,
		ParamsAttribute,
		HreflangAttribute,
		RelAttribute,
		TargetAttribute,
		TitleAttribute,
		TypeAttribute,
		// Events
		OnclickAttribute,
		OnmouseoutAttribute,
		OnmouseoverAttribute
{

	public ATag() {
		init();
	}

	@Override
	public MediaType getContentType() {
		return MediaType.XHTML;
	}

	@Override
	public MediaType getOutputType() {
		return MediaType.XHTML;
	}

/* BodyTag only:
	private static final long serialVersionUID = 1L;
/**/

	private String href;
	@Override
	public void setHref(String href) throws JspTagException {
		this.href = AttributeUtils.nullIfEmpty(href);
	}

	private MutableURIParameters params;
	@Override
	public void addParam(String name, String value) {
		if(params==null) params = new URIParametersMap();
		params.addParameter(name, value);
	}

	private boolean absolute;
	public void setAbsolute(boolean absolute) {
		this.absolute = absolute;
	}

	private boolean canonical;
	public void setCanonical(boolean canonical) {
		this.canonical = canonical;
	}

	private AddLastModified addLastModified;
	public void setAddLastModified(String addLastModified) {
		this.addLastModified = AddLastModified.valueOfLowerName(addLastModified.trim().toLowerCase(Locale.ROOT));
	}

	private Object hreflang;
	@Override
	public void setHreflang(Object hreflang) throws JspTagException {
		this.hreflang = hreflang;
	}

	private String rel;
	@Override
	public void setRel(String rel) throws JspTagException {
		this.rel = Strings.trimNullIfEmpty(rel);
	}

	private String target;
	@Override
	public void setTarget(String target) throws JspTagException {
		this.target = Strings.trimNullIfEmpty(target);
	}

	private Object title;
	@Override
	public void setTitle(Object title) throws JspTagException {
		this.title = AttributeUtils.trimNullIfEmpty(title);
	}

	private String type;
	@Override
	public void setType(String type) throws JspTagException {
		this.type = Strings.trimNullIfEmpty(type);
	}

	private Object onclick;
	@Override
	public void setOnclick(Object onclick) throws JspTagException {
		this.onclick = AttributeUtils.trimNullIfEmpty(onclick);
	}

	private Object onmouseout;
	@Override
	public void setOnmouseout(Object onmouseout) throws JspTagException {
		this.onmouseout = AttributeUtils.trimNullIfEmpty(onmouseout);
	}

	private Object onmouseover;
	@Override
	public void setOnmouseover(Object onmouseover) throws JspTagException {
		this.onmouseover = AttributeUtils.trimNullIfEmpty(onmouseover);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see  ParamUtils#addDynamicAttribute(java.lang.String, java.lang.String, java.lang.Object, java.util.List, com.aoindustries.taglib.ParamsAttribute)
	 */
	@Override
	protected boolean addDynamicAttribute(String uri, String localName, Object value, List<String> expectedPatterns) throws JspTagException {
		return
			super.addDynamicAttribute(uri, localName, value, expectedPatterns)
			|| ParamUtils.addDynamicAttribute(uri, localName, value, expectedPatterns, this);
	}

	private void init() {
		href = null;
		params = null;
		absolute = false;
		canonical = false;
		addLastModified = AddLastModified.AUTO;
		hreflang = null;
		rel = null;
		target = null;
		title = null;
		type = null;
		onclick = null;
		onmouseout = null;
		onmouseover = null;
	}

	@Override
/* BodyTag only:
	protected int doEndTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
/**/
/* SimpleTag only: */
	protected void doTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
		PageContext pageContext = (PageContext)getJspContext();
/**/
		out.write("<a");
		GlobalAttributesUtils.writeGlobalAttributes(global, out);
		String transformed;
		if(URIParser.isScheme(href, "tel")) {
			transformed = href.replace(' ', '-');
		} else {
			transformed = href;
		}
		UrlUtils.writeHref(pageContext, out, transformed, params, addLastModified, absolute, canonical);
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
		if(rel != null) {
			out.write(" rel=\"");
			encodeTextInXhtmlAttribute(rel, out);
			out.write('"');
		}
		if(target != null) {
			out.write(" target=\"");
			encodeTextInXhtmlAttribute(target, out);
			out.write('"');
		}
		if(title != null) {
			out.write(" title=\"");
			Coercion.write(title, MarkupType.TEXT, textInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
		if(type != null) {
			out.write(" type=\"");
			encodeTextInXhtmlAttribute(type, out);
			out.write('"');
		}
		if(onclick != null) {
			out.write(" onclick=\"");
			Coercion.write(onclick, MarkupType.JAVASCRIPT, javaScriptInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
		if(onmouseout != null) {
			out.write(" onmouseout=\"");
			Coercion.write(onmouseout, MarkupType.JAVASCRIPT, javaScriptInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
		if(onmouseover != null) {
			out.write(" onmouseover=\"");
			Coercion.write(onmouseover, MarkupType.JAVASCRIPT, javaScriptInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
		out.write('>');
		BufferResult trimmedBody = capturedBody.trim();
		if(trimmedBody.getLength() == 0) { // TODO: Make a BufferResult.isEmpty() that defaults to getLength() == 0, but provides a chance at optimizations
			// When the body is empty after trimming, display the href itself
			if(href != null) {
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
/* BodyTag only:
		return EVAL_PAGE;
/**/
	}

/* BodyTag only:
	@Override
	public void doFinally() {
		try {
			init();
		} finally {
			super.doFinally();
		}
	}
/**/
}
