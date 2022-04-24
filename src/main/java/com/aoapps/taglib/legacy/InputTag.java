/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013, 2014, 2015, 2016, 2017, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
 * along with ao-taglib.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.aoapps.taglib.legacy;

import com.aoapps.encoding.MediaType;
import static com.aoapps.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import com.aoapps.html.any.AnyINPUT;
import com.aoapps.html.servlet.DocumentEE;
import com.aoapps.html.servlet.INPUT;
import com.aoapps.io.buffer.BufferResult;
import com.aoapps.lang.Coercion;
import com.aoapps.lang.LocalizedIllegalArgumentException;
import com.aoapps.lang.Strings;
import com.aoapps.net.MutableURIParameters;
import com.aoapps.net.URIParametersMap;
import com.aoapps.servlet.lastmodified.AddLastModified;
import com.aoapps.taglib.AltAttribute;
import com.aoapps.taglib.AttributeRequiredException;
import com.aoapps.taglib.AttributeUtils;
import com.aoapps.taglib.CheckedAttribute;
import com.aoapps.taglib.DisabledAttribute;
import com.aoapps.taglib.GlobalAttributesUtils;
import com.aoapps.taglib.HeightAttribute;
import static com.aoapps.taglib.InputTag.RESOURCES;
import com.aoapps.taglib.InputTagTEI;
import com.aoapps.taglib.MaxlengthAttribute;
import com.aoapps.taglib.NameAttribute;
import com.aoapps.taglib.OnblurAttribute;
import com.aoapps.taglib.OnchangeAttribute;
import com.aoapps.taglib.OnclickAttribute;
import com.aoapps.taglib.OnfocusAttribute;
import com.aoapps.taglib.OnkeypressAttribute;
import com.aoapps.taglib.ParamUtils;
import com.aoapps.taglib.ParamsAttribute;
import com.aoapps.taglib.ReadonlyAttribute;
import com.aoapps.taglib.SizeAttribute;
import com.aoapps.taglib.SrcAttribute;
import com.aoapps.taglib.TabindexAttribute;
import com.aoapps.taglib.TitleAttribute;
import com.aoapps.taglib.TypeAttribute;
import com.aoapps.taglib.UrlUtils;
import com.aoapps.taglib.ValueAttribute;
import com.aoapps.taglib.WidthAttribute;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

/**
 * <p>
 * TODO: Have a default value of "true" for input type checkbox and radio?
 * </p>
 *
 * @author  AO Industries, Inc.
 */
public class InputTag extends ElementBufferedBodyTag
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

  /* SimpleTag only:
    public static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, InputTag.class);
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

  /* BodyTag only: */
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
  public void addParam(String name, Object value) {
    if (params == null) {
      params = new URIParametersMap();
    }
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
    if (typeStr != null && !InputTagTEI.isValidType(typeStr)) {
      throw new LocalizedIllegalArgumentException(RESOURCES, "type.invalid", typeStr);
    }
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
   * @see  ParamUtils#addDynamicAttribute(java.lang.String, java.lang.String, java.lang.Object, java.util.List, com.aoapps.taglib.ParamsAttribute)
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
  /* BodyTag only: */
  protected int doEndTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
    /**/
    /* SimpleTag only:
      protected void doTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
        PageContext pageContext = (PageContext)getJspContext();
    /**/
    if (type == null) {
      throw new AttributeRequiredException("type");
    }
    if (value == null) {
      setValue(capturedBody.trim()); // TODO: Distinguish between empty and null, track valueSet boolean for when is set to null?
    }
    if (AnyINPUT.Dynamic.Type.IMAGE.toString().equalsIgnoreCase(type)) {
      if (alt == null) {
        throw new AttributeRequiredException("alt");
      }
    }
    DocumentEE document = new DocumentEE(
        pageContext.getServletContext(),
        (HttpServletRequest) pageContext.getRequest(),
        (HttpServletResponse) pageContext.getResponse(),
        out,
        false, // Do not add extra newlines to JSP
        false  // Do not add extra indentation to JSP
    );
    INPUT.Dynamic<?> input = document.input().dynamic();
    GlobalAttributesUtils.doGlobalAttributes(global, input);
    input.alt(alt);
    // autocomplete is not valid in all doctypes
    // TODO: Allow more autocomplete values as String instead of boolean
    if (!autocomplete) {
      input.autocomplete(AnyINPUT.Autocomplete.OFF);
    }
    input
        .checked(checked)
        .disabled(disabled);
    if (height != null) {
      // TODO: ao-fluent-html attribute as Integer
      out.write(" height=\"");
      Coercion.write(height, textInXhtmlAttributeEncoder, out);
      out.append('"');
    }
    input
        .maxlength(maxlength)
        .name(name)
        .readonly(readonly);
    if (size != null) {
      // TODO: ao-fluent-html attribute as Integer
      out.write(" size=\"");
      Coercion.write(size, textInXhtmlAttributeEncoder, out);
      out.append('"');
    }
    UrlUtils.writeSrc(pageContext, out, src, params, addLastModified, absolute, canonical);
    input
        .tabindex((tabindex >= 1) ? tabindex : null)
        .title(title)
        .type(type);
    if (width != null) {
      // TODO: ao-fluent-html attribute as Integer
      out.write(" width=\"");
      Coercion.write(width, textInXhtmlAttributeEncoder, out);
      out.append('"');
    }
    input
        .value(value == null ? "" : value)
        .onblur(onblur)
        .onchange(onchange)
        .onclick(onclick)
        .onfocus(onfocus)
        .onkeypress(onkeypress)
        .__();
    /* BodyTag only: */
    return EVAL_PAGE;
    /**/
  }

  /* BodyTag only: */
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
