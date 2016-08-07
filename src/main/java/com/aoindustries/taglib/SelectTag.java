/*
 * ao-taglib - Making JSP what it should have been all along.
 * Copyright (C) 2010, 2011, 2012, 2013, 2015, 2016  AO Industries, Inc.
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

import static com.aoindustries.encoding.JavaScriptInXhtmlAttributeEncoder.javaScriptInXhtmlAttributeEncoder;
import com.aoindustries.encoding.MediaType;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.encoding.Coercion;
import com.aoindustries.util.i18n.MarkupType;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspTagException;

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
    private Object name;
    private Object style;
    private boolean disabled;
    private Object onchange;
    private Object onfocus;
    private Object onblur;
    private Object onkeypress;

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
		this.id = id;
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
    public Object getStyle() {
        return style;
    }

    @Override
    public void setStyle(Object style) {
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
    public Object getOnchange() {
        return onchange;
    }

    @Override
    public void setOnchange(Object onchange) {
        this.onchange = onchange;
    }

    @Override
    public Object getOnfocus() {
        return onfocus;
    }

    @Override
    public void setOnfocus(Object onfocus) {
        this.onfocus = onfocus;
    }

    @Override
    public Object getOnblur() {
        return onblur;
    }

    @Override
    public void setOnblur(Object onblur) {
        this.onblur = onblur;
    }

    @Override
    public Object getOnkeypress() {
        return onkeypress;
    }

    @Override
    public void setOnkeypress(Object onkeypress) {
        this.onkeypress = onkeypress;
    }

    @Override
    protected void doTag(BufferResult capturedBody, Writer out) throws JspTagException, IOException {
		out.write("<select");
		if(id!=null) {
			out.write(" id=\"");
			Coercion.write(id, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(name!=null) {
			out.write(" name=\"");
			Coercion.write(name, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(style!=null) {
			out.write(" style=\"");
			Coercion.write(style, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(disabled) out.write(" disabled=\"disabled\"");
		if(onchange!=null) {
			out.write(" onchange=\"");
			Coercion.write(onchange, javaScriptInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(onfocus!=null) {
			out.write(" onfocus=\"");
			Coercion.write(onfocus, javaScriptInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(onblur!=null) {
			out.write(" onblur=\"");
			Coercion.write(onblur, javaScriptInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(onkeypress!=null) {
			out.write(" onkeypress=\"");
			Coercion.write(onkeypress, javaScriptInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		out.write('>');
		MarkupUtils.writeWithMarkup(capturedBody, MarkupType.XHTML, out);
		out.write("</select>");
    }
}
