/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2010, 2011, 2012, 2013  AO Industries, Inc.
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
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import com.aoindustries.io.AutoTempFileWriter;
import com.aoindustries.io.Coercion;
import com.aoindustries.util.ref.ReferenceUtils;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspException;

/**
 * @author  AO Industries, Inc.
 */
public class SelectTag
	extends AutoEncodingBufferedTag
	implements
		IdAttribute,
		NameAttribute,
		StyleAttribute,
		DisabledAttribute,
		OnchangeAttribute,
		OnfocusAttribute,
		OnblurAttribute,
		OnkeypressAttribute
{

    private Object id;
    private String name;
    private String style;
    private boolean disabled;
    private String onchange;
    private String onfocus;
    private String onblur;
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
    public Object getId() {
        return id;
    }

    @Override
    public void setId(Object id) {
		this.id = ReferenceUtils.replace(this.id, id);
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
    public String getStyle() {
        return style;
    }

    @Override
    public void setStyle(String style) {
        this.style = style;
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
    protected void doTag(AutoTempFileWriter capturedBody, Writer out) throws JspException, IOException {
		try {
			out.write("<select");
			if(id!=null) {
				out.write(" id=\"");
				Coercion.write(id, textInXhtmlAttributeEncoder, out);
				out.write('"');
			}
			if(name!=null) {
				out.write(" name=\"");
				encodeTextInXhtmlAttribute(name, out);
				out.write('"');
			}
			if(style!=null) {
				out.write(" style=\"");
				encodeTextInXhtmlAttribute(style, out);
				out.write('"');
			}
			if(disabled) out.write(" disabled=\"disabled\"");
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
			out.write('>');
			capturedBody.writeTo(out);
			out.write("</select>");
		} finally {
			id = ReferenceUtils.release(id);
		}
    }
}
