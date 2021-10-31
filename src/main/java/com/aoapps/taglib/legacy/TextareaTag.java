/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2010, 2011, 2013, 2015, 2016, 2017, 2019, 2020, 2021  AO Industries, Inc.
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
 * along with ao-taglib.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoapps.taglib.legacy;

import static com.aoapps.encoding.JavaScriptInXhtmlAttributeEncoder.javaScriptInXhtmlAttributeEncoder;
import com.aoapps.encoding.MediaType;
import com.aoapps.encoding.Serialization;
import static com.aoapps.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import static com.aoapps.encoding.TextInXhtmlEncoder.textInXhtmlEncoder;
import com.aoapps.encoding.servlet.SerializationEE;
import com.aoapps.hodgepodge.i18n.MarkupCoercion;
import com.aoapps.hodgepodge.i18n.MarkupType;
import com.aoapps.io.buffer.BufferResult;
import com.aoapps.lang.Coercion;
import com.aoapps.lang.Strings;
import com.aoapps.taglib.AttributeUtils;
import com.aoapps.taglib.ColsAttribute;
import com.aoapps.taglib.DisabledAttribute;
import com.aoapps.taglib.GlobalAttributesUtils;
import com.aoapps.taglib.NameAttribute;
import com.aoapps.taglib.OnchangeAttribute;
import com.aoapps.taglib.ReadonlyAttribute;
import com.aoapps.taglib.RowsAttribute;
import com.aoapps.taglib.ValueAttribute;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

/**
 * @author  AO Industries, Inc.
 */
public class TextareaTag extends ElementBufferedBodyTag
	implements
		// Attributes
		ColsAttribute,
		DisabledAttribute,
		NameAttribute,
		ReadonlyAttribute,
		RowsAttribute,
		ValueAttribute,
		// Events
		OnchangeAttribute
{

	public TextareaTag() {
		init();
	}

	@Override
	public MediaType getContentType() {
		return MediaType.TEXT;
	}

	@Override
	public MediaType getOutputType() {
		return MediaType.XHTML;
	}

/* BodyTag only: */
	private static final long serialVersionUID = 1L;
/**/

	private Integer cols;
	@Override
	public void setCols(int cols) {
		this.cols = cols;
	}

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

	private boolean readonly;
	@Override
	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	private Integer rows;
	@Override
	public void setRows(int rows) {
		this.rows = rows;
	}

	private Object value;
	@Override
	public void setValue(Object value) {
		this.value = AttributeUtils.nullIfEmpty(value);
	}

	private Object onchange;
	@Override
	public void setOnchange(Object onchange) {
		this.onchange = AttributeUtils.trimNullIfEmpty(onchange);
	}

	private void init() {
		cols = null;
		disabled = false;
		name = null;
		readonly = false;
		rows = null;
		value = null;
		onchange = null;
	}

	@Override
/* BodyTag only: */
	protected int doEndTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
/**/
/* SimpleTag only:
	protected void doTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
		PageContext pageContext = (PageContext)getJspContext();
/**/
		if(value == null) setValue(capturedBody.trim());
		Serialization serialization = SerializationEE.get(
			pageContext.getServletContext(),
			(HttpServletRequest)pageContext.getRequest()
		);
		out.write("<textarea");
		GlobalAttributesUtils.writeGlobalAttributes(global, out);
		if(cols != null) {
			out.write(" cols=\"");
			encodeTextInXhtmlAttribute(cols, out);
			out.append('"');
		}
		if(disabled) {
			out.write(" disabled");
			if(serialization == Serialization.XML) out.write("=\"disabled\"");
		}
		if(name != null) {
			out.write(" name=\"");
			encodeTextInXhtmlAttribute(name, out);
			out.append('"');
		}
		if(readonly) {
			out.write(" readonly");
			if(serialization == Serialization.XML) out.write("=\"readonly\"");
		}
		if(rows != null) {
			out.write(" rows=\"");
			encodeTextInXhtmlAttribute(rows, out);
			out.append('"');
		}
		if(onchange != null) {
			out.write(" onchange=\"");
			MarkupCoercion.write(onchange, MarkupType.JAVASCRIPT, true, javaScriptInXhtmlAttributeEncoder, false, out);
			out.append('"');
		}
		out.append('>');
		Coercion.write(value, textInXhtmlEncoder, out);
		out.write("</textarea>");
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
