/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2010  AO Industries, Inc.
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

import com.aoindustries.encoding.MediaType;
import com.aoindustries.encoding.TextInXhtmlEncoder;
import com.aoindustries.io.StringBuilderWriter;
import com.aoindustries.util.EncodingUtils;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * @author  AO Industries, Inc.
 */
public class TextareaTag extends AutoEncodingBufferedTag implements NameAttribute, ValueAttribute, ColsAttribute, RowsAttribute, ReadonlyAttribute, DisabledAttribute {

    private String name;
    private String value;
    private int cols;
    private int rows;
    private boolean readonly;
    private boolean disabled;

    public MediaType getContentType() {
        return MediaType.TEXT;
    }

    public MediaType getOutputType() {
        return MediaType.XHTML;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    protected void doTag(StringBuilderWriter capturedBody, Writer out) throws JspException, IOException {
        PageContext pageContext = (PageContext)getJspContext();
        HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
        if(value==null) value = capturedBody.toString().trim();
        out.write("<textarea");
        if(name!=null) {
            out.write(" name=\"");
            EncodingUtils.encodeXmlAttribute(name, out);
            out.write('"');
        }
        out.write(" cols=\"");
        out.write(Integer.toString(cols));
        out.write("\" rows=\"");
        out.write(Integer.toString(rows));
        out.write('"');
        if(readonly) out.write(" readonly=\"readonly\"");
        if(disabled) out.write(" disabled=\"disabled\"");
        out.write('>');
        TextInXhtmlEncoder.encodeTextInXhtml(response.getLocale(), value, out);
        out.write("</textarea>");
    }
}
