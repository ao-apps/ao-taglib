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

import com.aoindustries.encoding.JavaScriptInXhtmlAttributeEncoder;
import com.aoindustries.encoding.MediaType;
import com.aoindustries.io.AutoTempFileWriter;
import com.aoindustries.io.StringBuilderWriter;
import com.aoindustries.util.EncodingUtils;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspException;

/**
 * @author  AO Industries, Inc.
 */
public class SelectTag extends AutoEncodingBufferedTag implements NameAttribute, OnchangeAttribute, OnfocusAttribute, OnkeypressAttribute {

    private String name;
    private String onchange;
    private String onfocus;
    private String onkeypress;

    @Override
    public MediaType getContentType() {
        return MediaType.XHTML;
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
    public String getOnchange() {
        return onchange;
    }

    @Override
    public void setOnchange(String onchange) {
        this.onchange = onchange;
    }

    @Override
    public String getOnfocus() {
        return onfocus;
    }

    @Override
    public void setOnfocus(String onfocus) {
        this.onfocus = onfocus;
    }

    @Override
    public String getOnkeypress() {
        return onkeypress;
    }

    @Override
    public void setOnkeypress(String onkeypress) {
        this.onkeypress = onkeypress;
    }

    @Override
    protected void doTag(AutoTempFileWriter capturedBody, Writer out) throws JspException, IOException {
        out.write("<select");
        if(name!=null) {
            out.write(" name=\"");
            EncodingUtils.encodeXmlAttribute(name, out);
            out.write('"');
        }
        if(onchange!=null) {
            out.write(" onchange=\"");
            JavaScriptInXhtmlAttributeEncoder.encodeJavaScriptInXhtmlAttribute(onchange, out);
            out.write('"');
        }
        if(onfocus!=null) {
            out.write(" onfocus=\"");
            JavaScriptInXhtmlAttributeEncoder.encodeJavaScriptInXhtmlAttribute(onfocus, out);
            out.write('"');
        }
        if(onkeypress!=null) {
            out.write(" onkeypress=\"");
            JavaScriptInXhtmlAttributeEncoder.encodeJavaScriptInXhtmlAttribute(onkeypress, out);
            out.write('"');
        }
        out.write('>');
        capturedBody.writeTo(out);
        out.write("</select>");
    }
}
