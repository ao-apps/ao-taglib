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
import java.util.Locale;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * @author  AO Industries, Inc.
 */
public class OptionTag extends AutoEncodingBufferedTag implements ValueAttribute, SelectedAttribute, DisabledAttribute {

    private boolean valueSet;
    private String value;
    private boolean selected;
    private boolean disabled;

    public MediaType getContentType() {
        return MediaType.TEXT;
    }

    public MediaType getOutputType() {
        return MediaType.XHTML;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.valueSet = true;
        this.value = value;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
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
        Locale userLocale = response.getLocale();
        String body = capturedBody.toString().trim();
        if(!valueSet) value = body;
        out.write("<option value=\"");
        EncodingUtils.encodeXmlAttribute(value, out);
        out.write('"');
        if(selected) out.write(" selected=\"selected\"");
        if(disabled) out.write(" disabled=\"disabled\"");
        out.write('>');
        TextInXhtmlEncoder.encodeTextInXhtml(userLocale, body, out);
        out.write("</option>");
    }
}
