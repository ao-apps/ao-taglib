/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013, 2014, 2015, 2016, 2017, 2019  AO Industries, Inc.
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
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import com.aoindustries.util.i18n.MarkupType;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.servlet.jsp.JspTagException;

/**
 * @author  AO Industries, Inc.
 */
public class InputTag
	extends AutoEncodingBufferedTag
	implements
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
		CheckedAttribute,
		TabindexAttribute
{

	private static final Set<String> validTypes = Collections.unmodifiableSet(
		new LinkedHashSet<>(
			Arrays.asList(
				// From http://www.w3schools.com/tags/att_input_type.asp
				"button",
				"checkbox",
				"color",
				"date",
				"datetime-local",
				"email",
				"file",
				"hidden",
				"image",
				"month",
				"number",
				"password",
				"radio",
				"range",
				"reset",
				"search",
				"submit",
				"tel",
				"text",
				"time",
				"url",
				"week"
			)
		)
	);

	public static boolean isValidType(String type) {
		return validTypes.contains(type);
	}

	@Override
	public MediaType getContentType() {
		return MediaType.TEXT;
	}

	@Override
	public MediaType getOutputType() {
		return MediaType.XHTML;
	}

	private Object id;
	@Override
	public void setId(Object id) {
		this.id = id;
	}

	private Object type;
	private String typeString;
	@Override
	public void setType(Object type) throws JspTagException {
		String typeStr = Coercion.toString(type);
		if(!isValidType(typeStr)) throw new LocalizedJspTagException(ApplicationResources.accessor, "InputTag.type.invalid", typeStr);
		this.type = type;
		this.typeString = typeStr;
	}

	private Object name;
	@Override
	public void setName(Object name) {
		this.name = name;
	}

	private Object value;
	@Override
	public void setValue(Object value) {
		this.value = value;
	}

	private Object onclick;
	@Override
	public void setOnclick(Object onclick) {
		this.onclick = onclick;
	}

	private Object onchange;
	@Override
	public void setOnchange(Object onchange) {
		this.onchange = onchange;
	}

	private Object onfocus;
	@Override
	public void setOnfocus(Object onfocus) {
		this.onfocus = onfocus;
	}

	private Object onblur;
	@Override
	public void setOnblur(Object onblur) {
		this.onblur = onblur;
	}

	private Object onkeypress;
	@Override
	public void setOnkeypress(Object onkeypress) {
		this.onkeypress = onkeypress;
	}

	private Object size;
	@Override
	public void setSize(Object size) {
		this.size = size;
	}

	private Integer maxlength;
	@Override
	public void setMaxlength(Integer maxlength) {
		this.maxlength = maxlength;
	}

	private boolean readonly;
	@Override
	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	private boolean disabled;
	@Override
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	private Object clazz;
	@Override
	public Object getClazz() {
		return clazz;
	}

	@Override
	public void setClazz(Object clazz) {
		this.clazz = clazz;
	}

	private Object style;
	@Override
	public void setStyle(Object style) {
		this.style = style;
	}

	private boolean checked;
	@Override
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	private int tabindex;
	@Override
	public void setTabindex(int tabindex) {
		this.tabindex = tabindex;
	}

	private boolean autocomplete = true;
	public void setAutocomplete(boolean autocomplete) {
		this.autocomplete = autocomplete;
	}

	@Override
	protected void doTag(BufferResult capturedBody, Writer out) throws JspTagException, IOException {
		if(type==null) throw new AttributeRequiredException("type");
		if(value==null) setValue(capturedBody.trim());
		out.write("<input");
		if(id!=null) {
			out.write(" id=\"");
			Coercion.write(id, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		out.write(" type=\"");
		encodeTextInXhtmlAttribute(typeString, out);
		out.write('"');
		if(name!=null) {
			out.write(" name=\"");
			Coercion.write(name, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		out.write(" value=\"");
		if(
			"button".equals(type)
			|| "submit".equals(type)
		) {
			// Allow text markup from translations
			MarkupUtils.writeWithMarkup(value, MarkupType.TEXT, textInXhtmlAttributeEncoder, out);
		} else {
			Coercion.write(value, textInXhtmlAttributeEncoder, out);
		}
		out.write('"');
		if(onclick!=null) {
			out.write(" onclick=\"");
			Coercion.write(onclick, javaScriptInXhtmlAttributeEncoder, out);
			out.write('"');
		}
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
		if(size!=null) {
			out.write(" size=\"");
			Coercion.write(size, textInXhtmlAttributeEncoder, out);
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
			Coercion.write(clazz, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(style!=null) {
			out.write(" style=\"");
			Coercion.write(style, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(checked) out.write(" checked=\"checked\"");
		if(tabindex >= 1) {
			out.write(" tabindex=\"");
			out.write(Integer.toString(tabindex));
			out.write('"');
		}
		if(!autocomplete) {
			out.write(" autocomplete=\"off\"");
		}
		out.write(" />");
	}
}
