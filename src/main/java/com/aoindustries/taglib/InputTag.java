/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013, 2014, 2015, 2016, 2017, 2019, 2020  AO Industries, Inc.
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
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import com.aoindustries.html.Html;
import com.aoindustries.html.Input;
import com.aoindustries.html.servlet.HtmlEE;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.lang.Strings;
import com.aoindustries.net.URIParametersMap;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import com.aoindustries.servlet.lastmodified.AddLastModified;
import static com.aoindustries.taglib.ApplicationResources.accessor;
import com.aoindustries.util.i18n.MarkupType;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.DynamicAttributes;

/**
 * <p>
 * TODO: Have a default value of "true" for input type checkbox and radio?
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public class InputTag
	extends AutoEncodingBufferedTag
	implements
		DynamicAttributes,
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
		SrcAttribute,
		ParamsAttribute,
		WidthAttribute,
		HeightAttribute,
		AltAttribute,
		TitleAttribute,
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

	private String id;
	@Override
	public void setId(String id) throws JspTagException {
		this.id = id;
	}

	private String type;
	@Override
	public void setType(String type) throws JspTagException {
		String typeStr = Strings.trimNullIfEmpty(type);
		if(typeStr != null && !isValidType(typeStr)) throw new LocalizedJspTagException(ApplicationResources.accessor, "InputTag.type.invalid", typeStr);
		this.type = typeStr;
	}

	private String name;
	@Override
	public void setName(String name) throws JspTagException {
		this.name = name;
	}

	private Object value;
	@Override
	public void setValue(Object value) throws JspTagException {
		this.value = AttributeUtils.nullIfEmpty(value);
	}

	private Object onclick;
	@Override
	public void setOnclick(Object onclick) throws JspTagException {
		this.onclick = AttributeUtils.trimNullIfEmpty(onclick);
	}

	private Object onchange;
	@Override
	public void setOnchange(Object onchange) throws JspTagException {
		this.onchange = AttributeUtils.trimNullIfEmpty(onchange);
	}

	private Object onfocus;
	@Override
	public void setOnfocus(Object onfocus) throws JspTagException {
		this.onfocus = AttributeUtils.trimNullIfEmpty(onfocus);
	}

	private Object onblur;
	@Override
	public void setOnblur(Object onblur) throws JspTagException {
		this.onblur = AttributeUtils.trimNullIfEmpty(onblur);
	}

	private Object onkeypress;
	@Override
	public void setOnkeypress(Object onkeypress) throws JspTagException {
		this.onkeypress = AttributeUtils.trimNullIfEmpty(onkeypress);
	}

	private Object size;
	@Override
	public void setSize(Object size) throws JspTagException {
		this.size = AttributeUtils.trimNullIfEmpty(size);
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

	private String src;
	@Override
	public void setSrc(String src) throws JspTagException {
		this.src = AttributeUtils.nullIfEmpty(src);
	}

	private URIParametersMap params;
	@Override
	public void addParam(String name, String value) {
		if(params==null) params = new URIParametersMap();
		params.addParameter(name, value);
	}

	private boolean absolute;
	public void setAbsolute(boolean absolute) {
		this.absolute = absolute;
	}

	private boolean canonical;
	public void setCanonical(boolean canonical) {
		this.canonical = canonical;
	}

	private AddLastModified addLastModified = AddLastModified.AUTO;
	public void setAddLastModified(String addLastModified) {
		this.addLastModified = AddLastModified.valueOfLowerName(addLastModified.trim().toLowerCase(Locale.ROOT));
	}

	private Object width;
	@Override
	public void setWidth(Object width) throws JspTagException {
		this.width = AttributeUtils.trimNullIfEmpty(width);
	}

	private Object height;
	@Override
	public void setHeight(Object height) throws JspTagException {
		this.height = AttributeUtils.trimNullIfEmpty(height);
	}


	private Object alt;
	@Override
	public void setAlt(Object alt) throws JspTagException {
		this.alt = AttributeUtils.trim(alt);
	}

	private Object title;
	@Override
	public void setTitle(Object title) throws JspTagException {
		this.title = AttributeUtils.trimNullIfEmpty(title);
	}

	private String clazz;
	@Override
	public String getClazz() {
		return clazz;
	}

	@Override
	public void setClazz(String clazz) throws JspTagException {
		this.clazz = clazz;
	}

	private Object style;
	@Override
	public void setStyle(Object style) throws JspTagException {
		this.style = AttributeUtils.trimNullIfEmpty(style);
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
	public void setDynamicAttribute(String uri, String localName, Object value) throws JspTagException {
		if(
			uri==null
			&& localName.startsWith(ParamUtils.PARAM_ATTRIBUTE_PREFIX)
		) {
			ParamUtils.setDynamicAttribute(this, uri, localName, value);
		} else {
			throw new LocalizedJspTagException(
				accessor,
				"error.unexpectedDynamicAttribute",
				localName,
				ParamUtils.PARAM_ATTRIBUTE_PREFIX+"*"
			);
		}
	}

	@Override
	protected void doTag(BufferResult capturedBody, Writer out) throws JspTagException, IOException {
		if(type==null) throw new AttributeRequiredException("type");
		if(value==null) setValue(capturedBody.trim());
		if(Input.Dynamic.Type.IMAGE.toString().equalsIgnoreCase(type)) {
			if(alt == null) throw new AttributeRequiredException("alt");
		}
		PageContext pageContext = (PageContext)getJspContext();
		Html html = HtmlEE.get(
			pageContext.getServletContext(),
			(HttpServletRequest)pageContext.getRequest(),
			(HttpServletResponse)pageContext.getResponse(),
			out
		);
		Input.Dynamic input = html.input()
			.id(id)
			.type(type)
			.name(name)
			.value(value)
			.onclick(onclick);
		if(onchange!=null) {
			out.write(" onchange=\"");
			Coercion.write(onchange, MarkupType.JAVASCRIPT, javaScriptInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
		if(onfocus!=null) {
			out.write(" onfocus=\"");
			Coercion.write(onfocus, MarkupType.JAVASCRIPT, javaScriptInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
		if(onblur!=null) {
			out.write(" onblur=\"");
			Coercion.write(onblur, MarkupType.JAVASCRIPT, javaScriptInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
		if(onkeypress!=null) {
			out.write(" onkeypress=\"");
			Coercion.write(onkeypress, MarkupType.JAVASCRIPT, javaScriptInXhtmlAttributeEncoder, false, out);
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
		input
			.readonly(readonly)
			.disabled(disabled);
		UrlUtils.writeSrc(pageContext, out, src, params, addLastModified, absolute, canonical);
		if(width != null) {
			out.write(" width=\"");
			Coercion.write(width, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(height != null) {
			out.write(" height=\"");
			Coercion.write(height, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(alt != null) {
			out.write(" alt=\"");
			Coercion.write(alt, MarkupType.TEXT, textInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
		input
			.title(title)
			.clazz(clazz)
			.style(style)
			.checked(checked)
			.tabindex((tabindex >= 1) ? tabindex : null);
		if(!autocomplete) {
			out.write(" autocomplete=\"off\"");
		}
		input.__();
	}
}
