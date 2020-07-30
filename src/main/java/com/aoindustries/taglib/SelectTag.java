/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2010, 2011, 2012, 2013, 2015, 2016, 2017, 2019, 2020  AO Industries, Inc.
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
import com.aoindustries.encoding.Serialization;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import com.aoindustries.encoding.servlet.SerializationEE;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.lang.Strings;
import com.aoindustries.util.i18n.MarkupType;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import static javax.servlet.jsp.tagext.Tag.SKIP_BODY;

/**
 * @author  AO Industries, Inc.
 */
public class SelectTag
	extends AutoEncodingBufferedBodyTag
	implements
		// Global attributes
		IdAttribute,
		ClassAttribute,
		StyleAttribute,
		// Attributes
		DisabledAttribute,
		NameAttribute,
		SizeAttribute,
		// Events
		OnblurAttribute,
		OnchangeAttribute,
		OnfocusAttribute,
		OnkeypressAttribute
{

	@Override
	public MediaType getContentType() {
		return MediaType.XHTML;
	}

	@Override
	public MediaType getOutputType() {
		return MediaType.XHTML;
	}

	private static final long serialVersionUID = 1L;

	private String clazz;
	@Override
	public String getClazz() {
		return clazz;
	}
	@Override
	public void setClazz(String clazz) throws JspTagException {
		this.clazz = clazz;
	}

	private Object style;
	@Override
	public void setStyle(Object style) throws JspTagException {
		this.style = AttributeUtils.trimNullIfEmpty(style);
	}

	private boolean disabled;
	@Override
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	private String name;
	@Override
	public void setName(String name) throws JspTagException {
		this.name = Strings.nullIfEmpty(name);
	}

	private Object size;
	@Override
	public void setSize(Object size) throws JspTagException {
		this.size = AttributeUtils.trimNullIfEmpty(size);
	}

	private Object onblur;
	@Override
	public void setOnblur(Object onblur) throws JspTagException {
		this.onblur = AttributeUtils.trimNullIfEmpty(onblur);
	}

	private Object onchange;
	@Override
	public void setOnchange(Object onchange) throws JspTagException {
		this.onchange = AttributeUtils.trimNullIfEmpty(onchange);
	}

	private Object onfocus;
	@Override
	public void setOnfocus(Object onfocus) throws JspTagException {
		this.onfocus = AttributeUtils.trimNullIfEmpty(onfocus);
	}

	private Object onkeypress;
	@Override
	public void setOnkeypress(Object onkeypress) throws JspTagException {
		this.onkeypress = AttributeUtils.trimNullIfEmpty(onkeypress);
	}

	private transient BufferResult capturedBody;

	private void init() {
		clazz = null;
		style = null;
		disabled = false;
		name = null;
		size = null;
		onblur = null;
		onchange = null;
		onfocus = null;
		onkeypress = null;
		capturedBody = null;
	}

	@Override
	protected int doAfterBody(BufferResult capturedBody, Writer out) {
		assert this.capturedBody == null;
		assert capturedBody != null;
		this.capturedBody = capturedBody;
		return SKIP_BODY;
	}

	@Override
	protected int doEndTag(Writer out) throws JspTagException, IOException {
		Serialization serialization = SerializationEE.get(
			pageContext.getServletContext(),
			(HttpServletRequest)pageContext.getRequest()
		);
		out.write("<select");
		if(id != null) {
			out.write(" id=\"");
			encodeTextInXhtmlAttribute(id, out);
			out.write('"');
		}
		if(clazz != null) {
			out.write(" class=\"");
			encodeTextInXhtmlAttribute(clazz, out);
			out.write('"');
		}
		if(style != null) {
			out.write(" style=\"");
			Coercion.write(style, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(disabled) {
			out.write(" disabled");
			if(serialization == Serialization.XML) out.write("=\"disabled\"");
		}
		if(name != null) {
			out.write(" name=\"");
			encodeTextInXhtmlAttribute(name, out);
			out.write('"');
		}
		if(size != null) {
			out.write(" size=\"");
			Coercion.write(size, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(onblur != null) {
			out.write(" onblur=\"");
			Coercion.write(onblur, MarkupType.JAVASCRIPT, javaScriptInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
		if(onchange != null) {
			out.write(" onchange=\"");
			Coercion.write(onchange, MarkupType.JAVASCRIPT, javaScriptInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
		if(onfocus != null) {
			out.write(" onfocus=\"");
			Coercion.write(onfocus, MarkupType.JAVASCRIPT, javaScriptInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
		if(onkeypress != null) {
			out.write(" onkeypress=\"");
			Coercion.write(onkeypress, MarkupType.JAVASCRIPT, javaScriptInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
		out.write('>');
		Coercion.write(capturedBody, MarkupType.XHTML, out);
		out.write("</select>");
		return EVAL_PAGE;
	}
}
