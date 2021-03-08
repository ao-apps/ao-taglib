/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2013, 2014, 2015, 2016, 2017, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoindustries.taglib.legacy;

import com.aoindustries.encoding.MediaType;
import com.aoindustries.html.Document;
import com.aoindustries.html.servlet.DocumentEE;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.lang.Strings;
import com.aoindustries.net.MutableURIParameters;
import com.aoindustries.net.URIParametersMap;
import com.aoindustries.servlet.lastmodified.AddLastModified;
import com.aoindustries.taglib.AltAttribute;
import com.aoindustries.taglib.AttributeRequiredException;
import com.aoindustries.taglib.AttributeUtils;
import com.aoindustries.taglib.GlobalAttributesUtils;
import com.aoindustries.taglib.HeightAttribute;
import com.aoindustries.taglib.ParamUtils;
import com.aoindustries.taglib.ParamsAttribute;
import com.aoindustries.taglib.SrcAttribute;
import com.aoindustries.taglib.TitleAttribute;
import com.aoindustries.taglib.UrlUtils;
import com.aoindustries.taglib.WidthAttribute;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

/**
 * @author  AO Industries, Inc.
 */
public class ImgTag extends ElementBufferedBodyTag
	implements
		// Attributes
		SrcAttribute,
		ParamsAttribute,
		WidthAttribute,
		HeightAttribute,
		AltAttribute,
		TitleAttribute
{

	public ImgTag() {
		init();
	}

	@Override
	public MediaType getContentType() {
		return MediaType.TEXT; // TODO: Can't be MediaType.URL due to value used after trimming.  Compare to HrefTag, SrcTag, and UrlTag
	}

	@Override
	public MediaType getOutputType() {
		return MediaType.XHTML;
	}

/* BodyTag only: */
	private static final long serialVersionUID = 1L;
/**/

	private String src;
	@Override
	public void setSrc(String src) {
		this.src = Strings.nullIfEmpty(src);
	}

	private MutableURIParameters params;
	@Override
	public void addParam(String name, Object value) {
		if(params == null) params = new URIParametersMap();
		params.add(name, value);
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

	private Object width;
	@Override
	public void setWidth(Object width) {
		this.width = AttributeUtils.trimNullIfEmpty(width);
	}

	private Object height;
	@Override
	public void setHeight(Object height) {
		this.height = AttributeUtils.trimNullIfEmpty(height);
	}

	private Object alt;
	@Override
	public void setAlt(Object alt) {
		this.alt = AttributeUtils.trim(alt);
	}

	private Object title;
	@Override
	public void setTitle(Object title) {
		this.title = AttributeUtils.trimNullIfEmpty(title);
	}

	private String usemap;
	public void setUsemap(String usemap) {
		this.usemap = Strings.trimNullIfEmpty(usemap);
	}

	private boolean ismap;
	public void setIsmap(boolean ismap) {
		this.ismap = ismap;
	}

	/**
	 * @see  ParamUtils#addDynamicAttribute(java.lang.String, java.lang.String, java.lang.Object, java.util.List, com.aoindustries.taglib.ParamsAttribute)
	 */
	@Override
	protected boolean addDynamicAttribute(String uri, String localName, Object value, List<String> expectedPatterns) throws JspTagException {
		return
			super.addDynamicAttribute(uri, localName, value, expectedPatterns)
			|| ParamUtils.addDynamicAttribute(uri, localName, value, expectedPatterns, this);
	}

	private void init() {
		src = null;
		params = null;
		absolute = false;
		canonical = false;
		addLastModified = AddLastModified.AUTO;
		width = null;
		height = null;
		alt = null;
		title = null;
		usemap = null;
		ismap = false;
	}

	@Override
/* BodyTag only: */
	protected int doEndTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
/**/
/* SimpleTag only:
	protected void doTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
		PageContext pageContext = (PageContext)getJspContext();
/**/
		if(src == null) src = capturedBody.trim().toString(); // TODO: Validate here?
		if(usemap == null && alt == null) throw new AttributeRequiredException("alt");
		Document document = DocumentEE.get(
			pageContext.getServletContext(),
			(HttpServletRequest)pageContext.getRequest(),
			(HttpServletResponse)pageContext.getResponse(),
			out,
			false, // Do not add extra newlines to JSP
			false  // Do not add extra indentation to JSP
		);
		GlobalAttributesUtils.doGlobalAttributes(global, document.img())
			.src(UrlUtils.getSrc(pageContext, src, params, addLastModified, absolute, canonical))
			// TOOD: width to Integer via Img.width(Integer)
			.attribute("width", width)
			// TOOD: height to Integer via Img.height(Integer)
			.attribute("height", height)
			.alt(alt)
			.title(title)
			.usemap(usemap)
			.ismap(ismap)
			.__();
/* BodyTag only: */
		return EVAL_PAGE;
/**/
	}

/* BodyTag only: */
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
