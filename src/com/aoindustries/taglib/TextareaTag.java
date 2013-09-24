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

import static com.aoindustries.encoding.JavaScriptInXhtmlAttributeEncoder.encodeJavaScriptInXhtmlAttribute;
import com.aoindustries.encoding.MediaType;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import com.aoindustries.encoding.TextInXhtmlEncoder;
import com.aoindustries.io.AutoTempFileWriter;
import com.aoindustries.io.Coercion;
import com.aoindustries.util.ref.ReferenceUtils;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspException;

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

    private String name;
    private Object value;
    private int cols;
    private int rows;
    private boolean readonly;
    private boolean disabled;
    private String onchange;
    private String style;

    @Override
    public MediaType getContentType() {
        return MediaType.TEXT;
    }

    @Override
    public MediaType getOutputType() {
        return MediaType.XHTML;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
		this.value = ReferenceUtils.replace(this.value, value);
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
    public String getOnchange() {
        return onchange;
    }

    @Override
    public void setOnchange(String onchange) {
        this.onchange = onchange;
    }

    @Override
    public String getStyle() {
        return style;
    }

    @Override
    public void setStyle(String style) {
        this.style = style;
    }

    @Override
    protected void doTag(AutoTempFileWriter capturedBody, Writer out) throws JspException, IOException {
		try {
			if(value==null) setValue(capturedBody.trim());
			out.write("<textarea");
			if(name!=null) {
				out.write(" name=\"");
				encodeTextInXhtmlAttribute(name, out);
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
				encodeJavaScriptInXhtmlAttribute(onchange, out);
				out.write('"');
			}
			if(style!=null) {
				out.write(" style=\"");
				encodeTextInXhtmlAttribute(style, out);
				out.write('"');
			}
			out.write('>');
			Coercion.toString(
				value,
				TextInXhtmlEncoder.getInstance(),
				out
			);
			out.write("</textarea>");
		} finally {
			value = ReferenceUtils.release(value);
		}
    }
}
