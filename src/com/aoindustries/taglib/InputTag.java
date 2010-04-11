/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010  AO Industries, Inc.
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
public class InputTag extends AutoEncodingBufferedTag implements TypeAttribute, NameAttribute, ValueAttribute, OnclickAttribute, OnchangeAttribute, SizeAttribute, ReadonlyAttribute, DisabledAttribute, ClassAttribute, CheckedAttribute {

    public static boolean isValidType(String type) {
        return
            "button".equals(type)
            || "checkbox".equals(type)
            || "file".equals(type)
            || "hidden".equals(type)
            || "image".equals(type)
            || "password".equals(type)
            || "radio".equals(type)
            || "reset".equals(type)
            || "submit".equals(type)
            || "text".equals(type)
        ;
    }

    private String type;
    private String name;
    private String value;
    private String onclick;
    private String onchange;
    private String size;
    private boolean readonly;
    private boolean disabled;
    private String clazz;
    private boolean checked;

    public MediaType getContentType() {
        return MediaType.TEXT;
    }

    public MediaType getOutputType() {
        return MediaType.XHTML;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) throws JspException {
        if(!isValidType(type)) throw new JspException(ApplicationResourcesAccessor.getMessage(Locale.getDefault(), "InputTag.type.invalid", type));
        this.type = type;
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

    public String getOnclick() {
        return onclick;
    }

    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    public String getOnchange() {
        return onchange;
    }

    public void setOnchange(String onchange) {
        this.onchange = onchange;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
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

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }
    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }


    protected void doTag(StringBuilderWriter capturedBody, Writer out) throws JspException, IOException {
        PageContext pageContext = (PageContext)getJspContext();
        HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
        Locale userLocale = response.getLocale();
        if(type==null) throw new JspException(ApplicationResourcesAccessor.getMessage(userLocale, "InputTag.type.required"));
        if(value==null) value = capturedBody.toString().trim();
        out.write("<input type=\"");
        out.write(type);
        out.write('"');
        if(name!=null) {
            out.write(" name=\"");
            EncodingUtils.encodeXmlAttribute(name, out);
            out.write('"');
        }
        out.write(" value=\"");
        EncodingUtils.encodeXmlAttribute(value, out);
        out.write('"');
        if(onclick!=null) {
            out.write(" onclick=\"");
            JavaScriptInXhtmlAttributeEncoder.encodeJavaScriptInXhtmlAttribute(onclick, out);
            out.write('"');
        }
        if(onchange!=null) {
            out.write(" onchange=\"");
            JavaScriptInXhtmlAttributeEncoder.encodeJavaScriptInXhtmlAttribute(onchange, out);
            out.write('"');
        }
        if(size!=null) {
            out.write(" size=\"");
            JavaScriptInXhtmlAttributeEncoder.encodeJavaScriptInXhtmlAttribute(size, out);
            out.write('"');
        }
        if(readonly) out.write(" readonly=\"readonly\"");
        if(disabled) out.write(" disabled=\"disabled\"");
        if(clazz!=null) {
            out.write(" class=\"");
            EncodingUtils.encodeXmlAttribute(clazz, out);
            out.write('"');
        }
        if(checked) out.write(" checked=\"checked\"");
        out.write(" />");
    }
}
