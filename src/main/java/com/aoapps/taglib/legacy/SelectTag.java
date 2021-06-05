/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2010, 2011, 2012, 2013, 2015, 2016, 2017, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoapps.taglib.legacy;

import static com.aoapps.encoding.JavaScriptInXhtmlAttributeEncoder.javaScriptInXhtmlAttributeEncoder;
import com.aoapps.encoding.MediaType;
import com.aoapps.encoding.Serialization;
import static com.aoapps.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import static com.aoapps.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import com.aoapps.encoding.servlet.SerializationEE;
import com.aoapps.hodgepodge.i18n.MarkupCoercion;
import com.aoapps.hodgepodge.i18n.MarkupType;
import com.aoapps.io.buffer.BufferResult;
import com.aoapps.lang.Coercion;
import com.aoapps.lang.Strings;
import com.aoapps.taglib.AttributeUtils;
import com.aoapps.taglib.DisabledAttribute;
import com.aoapps.taglib.GlobalAttributesUtils;
import com.aoapps.taglib.NameAttribute;
import com.aoapps.taglib.OnblurAttribute;
import com.aoapps.taglib.OnchangeAttribute;
import com.aoapps.taglib.OnfocusAttribute;
import com.aoapps.taglib.OnkeypressAttribute;
import com.aoapps.taglib.SizeAttribute;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

/**
 * @author  AO Industries, Inc.
 */
public class SelectTag extends ElementBufferedBodyTag
	implements
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

	public SelectTag() {
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

/* BodyTag only: */
	private static final long serialVersionUID = 1L;
/**/

	private boolean disabled;
	@Override
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	private String name;
	@Override
	public void setName(String name) {
		this.name = Strings.nullIfEmpty(name);
	}

	private Object size;
	@Override
	public void setSize(Object size) {
		this.size = AttributeUtils.trimNullIfEmpty(size);
	}

	private Object onblur;
	@Override
	public void setOnblur(Object onblur) {
		this.onblur = AttributeUtils.trimNullIfEmpty(onblur);
	}

	private Object onchange;
	@Override
	public void setOnchange(Object onchange) {
		this.onchange = AttributeUtils.trimNullIfEmpty(onchange);
	}

	private Object onfocus;
	@Override
	public void setOnfocus(Object onfocus) {
		this.onfocus = AttributeUtils.trimNullIfEmpty(onfocus);
	}

	private Object onkeypress;
	@Override
	public void setOnkeypress(Object onkeypress) {
		this.onkeypress = AttributeUtils.trimNullIfEmpty(onkeypress);
	}

	private void init() {
		disabled = false;
		name = null;
		size = null;
		onblur = null;
		onchange = null;
		onfocus = null;
		onkeypress = null;
	}

	@Override
/* BodyTag only: */
	protected int doEndTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
/**/
/* SimpleTag only:
	protected void doTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
		PageContext pageContext = (PageContext)getJspContext();
/**/
		Serialization serialization = SerializationEE.get(
			pageContext.getServletContext(),
			(HttpServletRequest)pageContext.getRequest()
		);
		out.write("<select");
		GlobalAttributesUtils.writeGlobalAttributes(global, out);
		if(disabled) {
			out.write(" disabled");
			if(serialization == Serialization.XML) out.write("=\"disabled\"");
		}
		if(name != null) {
			out.write(" name=\"");
			encodeTextInXhtmlAttribute(name, out);
			out.append('"');
		}
		if(size != null) {
			out.write(" size=\"");
			Coercion.write(size, textInXhtmlAttributeEncoder, out);
			out.append('"');
		}
		if(onblur != null) {
			out.write(" onblur=\"");
			MarkupCoercion.write(onblur, MarkupType.JAVASCRIPT, true, javaScriptInXhtmlAttributeEncoder, false, out);
			out.append('"');
		}
		if(onchange != null) {
			out.write(" onchange=\"");
			MarkupCoercion.write(onchange, MarkupType.JAVASCRIPT, true, javaScriptInXhtmlAttributeEncoder, false, out);
			out.append('"');
		}
		if(onfocus != null) {
			out.write(" onfocus=\"");
			MarkupCoercion.write(onfocus, MarkupType.JAVASCRIPT, true, javaScriptInXhtmlAttributeEncoder, false, out);
			out.append('"');
		}
		if(onkeypress != null) {
			out.write(" onkeypress=\"");
			MarkupCoercion.write(onkeypress, MarkupType.JAVASCRIPT, true, javaScriptInXhtmlAttributeEncoder, false, out);
			out.append('"');
		}
		out.append('>');
		MarkupCoercion.write(capturedBody, MarkupType.XHTML, out);
		out.write("</select>");
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
