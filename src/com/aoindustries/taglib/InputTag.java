/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013  AO Industries, Inc.
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
import static com.aoindustries.encoding.JavaScriptInXhtmlAttributeEncoder.encodeJavaScriptInXhtmlAttribute;
import com.aoindustries.encoding.TextInXhtmlAttributeEncoder;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import com.aoindustries.io.AutoTempFileWriter;
import com.aoindustries.io.Coercion;
import com.aoindustries.servlet.jsp.LocalizedJspException;
import com.aoindustries.util.ref.ReferenceUtils;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspException;

/**
 * @author  AO Industries, Inc.
 */
public class InputTag extends AutoEncodingBufferedTag implements
    IdAttribute,
    TypeAttribute,
    NameAttribute,
    ValueAttribute,
    OnclickAttribute,
    OnchangeAttribute,
    OnfocusAttribute,
    OnblurAttribute,
    OnkeypressAttribute,
    SizeAttribute,
    MaxlengthAttribute,
    ReadonlyAttribute,
    DisabledAttribute,
    ClassAttribute,
    StyleAttribute,
    CheckedAttribute {

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

    private String id;
    private String type;
    private String name;
    private Object value;
    private String onclick;
    private String onchange;
    private String onfocus;
    private String onblur;
    private String onkeypress;
    private String size;
    private Integer maxlength;
    private boolean readonly;
    private boolean disabled;
    private String clazz;
    private String style;
    private boolean checked;

    @Override
    public MediaType getContentType() {
        return MediaType.TEXT;
    }

    @Override
    public MediaType getOutputType() {
        return MediaType.XHTML;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) throws JspException {
        if(!isValidType(type)) throw new LocalizedJspException(ApplicationResources.accessor, "InputTag.type.invalid", type);
        this.type = type;
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
    public String getOnclick() {
        return onclick;
    }

    @Override
    public void setOnclick(String onclick) {
        this.onclick = onclick;
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
    public String getOnblur() {
        return onblur;
    }

    @Override
    public void setOnblur(String onblur) {
        this.onblur = onblur;
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
    public String getSize() {
        return size;
    }

    @Override
    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public Integer getMaxlength() {
        return maxlength;
    }

    @Override
    public void setMaxlength(Integer maxlength) {
        this.maxlength = maxlength;
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
    public String getClazz() {
        return clazz;
    }

    @Override
    public void setClazz(String clazz) {
        this.clazz = clazz;
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
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
    }


    @Override
    protected void doTag(AutoTempFileWriter capturedBody, Writer out) throws JspException, IOException {
		try {
			if(type==null) throw new AttributeRequiredException("type");
			if(value==null) setValue(capturedBody.trim());
			out.write("<input");
			if(id!=null) {
				out.write(" id=\"");
				encodeTextInXhtmlAttribute(id, out);
				out.write('"');
			}
			out.write(" type=\"");
			out.write(type);
			out.write('"');
			if(name!=null) {
				out.write(" name=\"");
				encodeTextInXhtmlAttribute(name, out);
				out.write('"');
			}
			out.write(" value=\"");
			Coercion.toString(
				value,
				TextInXhtmlAttributeEncoder.getInstance(),
				out
			);
			out.write('"');
			if(onclick!=null) {
				out.write(" onclick=\"");
				encodeJavaScriptInXhtmlAttribute(onclick, out);
				out.write('"');
			}
			if(onchange!=null) {
				out.write(" onchange=\"");
				encodeJavaScriptInXhtmlAttribute(onchange, out);
				out.write('"');
			}
			if(onfocus!=null) {
				out.write(" onfocus=\"");
				encodeJavaScriptInXhtmlAttribute(onfocus, out);
				out.write('"');
			}
			if(onblur!=null) {
				out.write(" onblur=\"");
				encodeJavaScriptInXhtmlAttribute(onblur, out);
				out.write('"');
			}
			if(onkeypress!=null) {
				out.write(" onkeypress=\"");
				encodeJavaScriptInXhtmlAttribute(onkeypress, out);
				out.write('"');
			}
			if(size!=null) {
				out.write(" size=\"");
				encodeJavaScriptInXhtmlAttribute(size, out);
				out.write('"');
			}
			if(maxlength!=null) {
				out.write(" maxlength=\"");
				out.write(maxlength.toString());
				out.write('"');
			}
			if(readonly) out.write(" readonly=\"readonly\"");
			if(disabled) out.write(" disabled=\"disabled\"");
			if(clazz!=null) {
				out.write(" class=\"");
				encodeTextInXhtmlAttribute(clazz, out);
				out.write('"');
			}
			if(style!=null) {
				out.write(" style=\"");
				encodeTextInXhtmlAttribute(style, out);
				out.write('"');
			}
			if(checked) out.write(" checked=\"checked\"");
			out.write(" />");
		} finally {
			ReferenceUtils.release(value);
		}
    }
}
