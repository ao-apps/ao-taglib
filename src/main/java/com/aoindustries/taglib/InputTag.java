/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013, 2014, 2015, 2016, 2017, 2019, 2020, 2021  AO Industries, Inc.
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
import com.aoindustries.html.Html;
import com.aoindustries.html.Input;
import com.aoindustries.html.servlet.HtmlEE;
import com.aoindustries.i18n.Resources;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.lang.Coercion;
import com.aoindustries.lang.LocalizedIllegalArgumentException;
import com.aoindustries.lang.Strings;
import com.aoindustries.net.MutableURIParameters;
import com.aoindustries.net.URIParametersMap;
import com.aoindustries.servlet.lastmodified.AddLastModified;
import com.aoindustries.util.i18n.MarkupCoercion;
import com.aoindustries.util.i18n.MarkupType;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

/**
 * <p>
 * TODO: Have a default value of "true" for input type checkbox and radio?
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public class InputTag extends ElementBufferedTag
	implements
		// Attributes
		AltAttribute,
		CheckedAttribute,
		DisabledAttribute,
		HeightAttribute,
		MaxlengthAttribute,
		NameAttribute,
		ReadonlyAttribute,
		SizeAttribute,
		SrcAttribute,
		ParamsAttribute,
		TabindexAttribute,
		TitleAttribute,
		TypeAttribute,
		WidthAttribute,
		ValueAttribute,
		// Events
		OnblurAttribute,
		OnchangeAttribute,
		OnclickAttribute,
		OnfocusAttribute,
		OnkeypressAttribute
{

/* SimpleTag only: */
	public static final Resources RESOURCES = Resources.getResources(InputTag.class);
/**/

	public InputTag() {
		init();
	}

	@Override
	public MediaType getContentType() {
		return MediaType.TEXT;
	}

	@Override
	public MediaType getOutputType() {
		return MediaType.XHTML;
	}

/* BodyTag only:
	private static final long serialVersionUID = 1L;
/**/

	private Object alt;
	@Override
	public void setAlt(Object alt) {
		this.alt = AttributeUtils.trim(alt);
	}

	private boolean autocomplete;
	// TODO: Support full set of values from ao-fluent-html
	public void setAutocomplete(boolean autocomplete) {
		this.autocomplete = autocomplete;
	}

	private boolean checked;
	@Override
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	private boolean disabled;
	@Override
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	private Object height;
	@Override
	public void setHeight(Object height) {
		this.height = AttributeUtils.trimNullIfEmpty(height);
	}

	private Integer maxlength;
	@Override
	public void setMaxlength(Integer maxlength) {
		this.maxlength = maxlength;
	}

	private String name;
	@Override
	public void setName(String name) {
		this.name = name;
	}

	private boolean readonly;
	@Override
	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	private Object size;
	@Override
	public void setSize(Object size) {
		this.size = AttributeUtils.trimNullIfEmpty(size);
	}

	private String src;
	@Override
	public void setSrc(String src) {
		this.src = Strings.nullIfEmpty(src);
	}

	private MutableURIParameters params;
	@Override
	public void addParam(String name, String value) {
		if(params == null) params = new URIParametersMap();
		params.add(name, value);
	}

	private boolean absolute;
	public void setAbsolute(boolean absolute) {
		this.absolute = absolute;
	}

	private boolean canonical;
	public void setCanonical(boolean canonical) {
		this.canonical = canonical;
	}

	private AddLastModified addLastModified;
	public void setAddLastModified(String addLastModified) {
		this.addLastModified = AddLastModified.valueOfLowerName(addLastModified.trim().toLowerCase(Locale.ROOT));
	}

	private int tabindex;
	@Override
	public void setTabindex(int tabindex) {
		this.tabindex = tabindex;
	}

	private Object title;
	@Override
	public void setTitle(Object title) {
		this.title = AttributeUtils.trimNullIfEmpty(title);
	}

	private String type;
	@Override
	public void setType(String type) {
		String typeStr = Strings.trimNullIfEmpty(type);
		if(typeStr != null && !InputTagTEI.isValidType(typeStr)) throw new LocalizedIllegalArgumentException(RESOURCES, "type.invalid", typeStr);
		this.type = typeStr;
	}

	private Object width;
	@Override
	public void setWidth(Object width) {
		this.width = AttributeUtils.trimNullIfEmpty(width);
	}

	private Object value;
	@Override
	public void setValue(Object value) {
		this.value = AttributeUtils.nullIfEmpty(value);
	}

	private Object onblur;
	@Override
	public void setOnblur(Object onblur) {
		this.onblur = AttributeUtils.trimNullIfEmpty(onblur);
	}

	private Object onchange;
	@Override
	public void setOnchange(Object onchange) {
		this.onchange = AttributeUtils.trimNullIfEmpty(onchange);
	}

	private Object onclick;
	@Override
	public void setOnclick(Object onclick) {
		this.onclick = AttributeUtils.trimNullIfEmpty(onclick);
	}

	private Object onfocus;
	@Override
	public void setOnfocus(Object onfocus) {
		this.onfocus = AttributeUtils.trimNullIfEmpty(onfocus);
	}

	private Object onkeypress;
	@Override
	public void setOnkeypress(Object onkeypress) {
		this.onkeypress = AttributeUtils.trimNullIfEmpty(onkeypress);
	}

	/**
	 * @see  ParamUtils#addDynamicAttribute(java.lang.String, java.lang.String, java.lang.Object, java.util.List, com.aoindustries.taglib.ParamsAttribute)
	 */
	@Override
	protected boolean addDynamicAttribute(String uri, String localName, Object value, List<String> expectedPatterns) throws JspTagException {
		return
			super.addDynamicAttribute(uri, localName, value, expectedPatterns)
			|| ParamUtils.addDynamicAttribute(uri, localName, value, expectedPatterns, this);
	}

	private void init() {
		alt = null;
		autocomplete = true;
		checked = false;
		disabled = false;
		height = null;
		maxlength = null;
		name = null;
		readonly = false;
		size = null;
		src = null;
		params = null;
		absolute = false;
		canonical = false;
		addLastModified = AddLastModified.AUTO;
		tabindex = 0;
		title = null;
		type = null;
		width = null;
		value = null;
		onblur = null;
		onchange = null;
		onclick = null;
		onfocus = null;
		onkeypress = null;
	}

	@Override
/* BodyTag only:
	protected int doEndTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
/**/
/* SimpleTag only: */
	protected void doTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
		PageContext pageContext = (PageContext)getJspContext();
/**/
		if(type == null) throw new AttributeRequiredException("type");
		if(value == null) setValue(capturedBody.trim()); // TODO: Distinguish between empty and null, track valueSet boolean for when is set to null?
		if(Input.Dynamic.Type.IMAGE.toString().equalsIgnoreCase(type)) {
			if(alt == null) throw new AttributeRequiredException("alt");
		}
		Html html = HtmlEE.get(
			pageContext.getServletContext(),
			(HttpServletRequest)pageContext.getRequest(),
			(HttpServletResponse)pageContext.getResponse(),
			out
		);
		Input.Dynamic input = html.input();
		GlobalAttributesUtils.doGlobalAttributes(global, input);
		if(alt != null) {
			out.write(" alt=\"");
			MarkupCoercion.write(alt, MarkupType.TEXT, true, textInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
		// autocomplete is not valid in all doctypes
		if(!autocomplete) input.autocomplete(Input.Autocomplete.OFF);
		input
			.checked(checked)
			.disabled(disabled);
		if(height != null) {
			out.write(" height=\"");
			Coercion.write(height, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		if(maxlength != null) {
			out.write(" maxlength=\"");
			out.write(maxlength.toString());
			out.write('"');
		}
		input
			.name(name)
			.readonly(readonly);
		if(size != null) {
			out.write(" size=\"");
			Coercion.write(size, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		UrlUtils.writeSrc(pageContext, out, src, params, addLastModified, absolute, canonical);
		input
			.tabindex((tabindex >= 1) ? tabindex : null)
			.title(title)
			.type(type);
		if(width != null) {
			out.write(" width=\"");
			Coercion.write(width, textInXhtmlAttributeEncoder, out);
			out.write('"');
		}
		input.value(value);
		if(onblur != null) {
			out.write(" onblur=\"");
			MarkupCoercion.write(onblur, MarkupType.JAVASCRIPT, true, javaScriptInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
		if(onchange != null) {
			out.write(" onchange=\"");
			MarkupCoercion.write(onchange, MarkupType.JAVASCRIPT, true, javaScriptInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
		input.onclick(onclick);
		if(onfocus != null) {
			out.write(" onfocus=\"");
			MarkupCoercion.write(onfocus, MarkupType.JAVASCRIPT, true, javaScriptInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
		if(onkeypress != null) {
			out.write(" onkeypress=\"");
			MarkupCoercion.write(onkeypress, MarkupType.JAVASCRIPT, true, javaScriptInXhtmlAttributeEncoder, false, out);
			out.write('"');
		}
		input.__();
/* BodyTag only:
		return EVAL_PAGE;
/**/
	}

/* BodyTag only:
	@Override
	public void doFinally() {
		try {
			init();
		} finally {
			super.doFinally();
		}
	}
/**/
}
