/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2010, 2011, 2013  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aocode-public-taglib.
 *
 * aocode-public-taglib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aocode-public-taglib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aocode-public-taglib.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.taglib;

import static com.aoindustries.encoding.JavaScriptInXhtmlAttributeEncoder.javaScriptInXhtmlAttributeEncoder;
import com.aoindustries.encoding.MediaType;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import static com.aoindustries.encoding.TextInXhtmlEncoder.textInXhtmlEncoder;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.io.Coercion;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspTagException;

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
    public Object getName() {
        return name;
    }

    @Override
    public void setName(Object name) {
		this.name = name;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
		this.value = value;
    }

    @Override
    public int getCols() {
        return cols;
    }

    @Override
    public void setCols(int cols) {
        this.cols = cols;
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public void setRows(int rows) {
        this.rows = rows;
    }

    @Override
    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public Object getOnchange() {
        return onchange;
    }

    @Override
    public void setOnchange(Object onchange) {
        this.onchange = onchange;
    }

    @Override
    public Object getStyle() {
        return style;
    }

    @Override
    public void setStyle(Object style) {
        this.style = style;
    }

    @Override
    protected void doTag(BufferResult capturedBody, Writer out) throws JspTagException, IOException {
		if(value==null) setValue(capturedBody.trim());
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
		if(readonly) out.write(" readonly=\"readonly\"");
		if(disabled) out.write(" disabled=\"disabled\"");
		if(onchange!=null) {
			out.write(" onchange=\"");
			Coercion.write(onchange, javaScriptInXhtmlAttributeEncoder, out);
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
