/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2010, 2011, 2013, 2015, 2016, 2017, 2019  AO Industries, Inc.
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
import static com.aoindustries.encoding.TextInXhtmlEncoder.textInXhtmlEncoder;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.servlet.http.Html;
import com.aoindustries.util.i18n.MarkupType;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

/**
 * @author  AO Industries, Inc.
 */
public class TextareaTag
	extends AutoEncodingBufferedTag
	implements
		NameAttribute,
		ValueAttribute,
		ColsAttribute,
		RowsAttribute,
		ReadonlyAttribute,
		DisabledAttribute,
		OnchangeAttribute,
		StyleAttribute
{

	private Object name;
	private Object value;
	private int cols;
	private int rows;
	private boolean readonly;
	private boolean disabled;
	private Object onchange;
	private Object style;

	@Override
	public MediaType getContentType() {
		return MediaType.TEXT;
	}

	@Override
	public MediaType getOutputType() {
		return MediaType.XHTML;
	}

	@Override
	public void setName(Object name) throws JspTagException {
		this.name = AttributeUtils.nullIfEmpty(name);
	}

	@Override
	public void setValue(Object value) throws JspTagException {
		this.value = AttributeUtils.nullIfEmpty(value);
	}

	@Override
	public void setCols(int cols) {
		this.cols = cols;
	}

	@Override
	public void setRows(int rows) {
		this.rows = rows;
	}

	@Override
	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	@Override
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	@Override
	public void setOnchange(Object onchange) throws JspTagException {
		this.onchange = AttributeUtils.trimNullIfEmpty(onchange);
	}

	@Override
	public void setStyle(Object style) throws JspTagException {
		this.style = AttributeUtils.trimNullIfEmpty(style);
	}

	@Override
	protected void doTag(BufferResult capturedBody, Writer out) throws JspTagException, IOException {
		if(value==null) setValue(capturedBody.trim());
		PageContext pageContext = (PageContext)getJspContext();
		Html.Serialization serialization = Html.Serialization.get(
			pageContext.getServletContext(),
			(HttpServletRequest)pageContext.getRequest()
		);
		out.write("<textarea");
		if(name!=null) {
			out.write(" name=\"");
			Coercion.write(name, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		out.write(" cols=\"");
		out.write(Integer.toString(cols));
		out.write("\" rows=\"");
		out.write(Integer.toString(rows));
		out.write('"');
		if(readonly) {
			out.write(" readonly");
			if(serialization == Html.Serialization.XML) out.write("=\"readonly\"");
		}
		if(disabled) {
			out.write(" disabled");
			if(serialization == Html.Serialization.XML) out.write("=\"disabled\"");
		}
		if(onchange!=null) {
			out.write(" onchange=\"");
			Coercion.write(onchange, MarkupType.JAVASCRIPT, javaScriptInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
		if(style!=null) {
			out.write(" style=\"");
			Coercion.write(style, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		out.write('>');
		Coercion.write(value, textInXhtmlEncoder, out);
		out.write("</textarea>");
	}
}
