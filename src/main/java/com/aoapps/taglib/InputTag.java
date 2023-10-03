/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013, 2014, 2015, 2016, 2017, 2019, 2020, 2021, 2022, 2023  AO Industries, Inc.
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

package com.aoapps.taglib;

import com.aoapps.encoding.MediaType;
import com.aoapps.html.any.AnyINPUT;
import com.aoapps.html.any.attributes.event.Onblur;
import com.aoapps.html.any.attributes.event.Onchange;
import com.aoapps.html.any.attributes.event.Onclick;
import com.aoapps.html.any.attributes.event.Onfocus;
import com.aoapps.html.any.attributes.event.Onkeypress;
import com.aoapps.html.any.attributes.text.Alt;
import com.aoapps.html.any.attributes.text.Name;
import com.aoapps.html.any.attributes.text.Title;
import com.aoapps.html.any.attributes.text.Type;
import com.aoapps.html.any.attributes.url.Src;
import com.aoapps.html.servlet.DocumentEE;
import com.aoapps.html.servlet.INPUT;
import com.aoapps.io.buffer.BufferResult;
import com.aoapps.lang.Coercion;
import com.aoapps.lang.LocalizedIllegalArgumentException;
import com.aoapps.lang.Strings;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.net.MutableURIParameters;
import com.aoapps.net.URIParametersMap;
import com.aoapps.servlet.lastmodified.AddLastModified;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

/**
 * <p>
 * TODO: Have a default value of "true" for input type checkbox and radio?.
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
    OnkeypressAttribute {

  /* SimpleTag only: */
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

  /* BodyTag only:
    private static final long serialVersionUID = 1L;
  /**/

  private Object alt;

  @Override
  public void setAlt(Object alt) throws IOException {
    this.alt = Alt.alt.normalize(alt);
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

  private Integer height;

  @Override
  public void setHeight(Integer height) {
    this.height = height;
  }

  private Integer maxlength;

  @Override
  public void setMaxlength(Integer maxlength) {
    this.maxlength = maxlength;
  }

  private Object name;

  @Override
  public void setName(Object name) throws IOException {
    this.name = Name.name.normalize(name);
  }

  private boolean readonly;

  @Override
  public void setReadonly(boolean readonly) {
    this.readonly = readonly;
  }

  private Integer size;

  @Override
  public void setSize(Integer size) {
    this.size = size;
  }

  private String src;

  @Override
  public void setSrc(String src) {
    this.src = Src.src.normalize(src);
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
    this.addLastModified = AddLastModified.valueOfLowerName(Strings.trim(addLastModified).toLowerCase(Locale.ROOT));
  }

  private int tabindex;

  @Override
  public void setTabindex(int tabindex) {
    this.tabindex = tabindex;
  }

  private Object title;

  @Override
  public void setTitle(Object title) throws IOException {
    this.title = Title.title.normalize(title);
  }

  private String type;

  @Override
  public void setType(Object type) throws IOException {
    type = Type.type.normalize(type);
    String typeStr = (type == null) ? null : Coercion.toString(type);
    if (typeStr != null && !InputTagTEI.isValidType(typeStr)) {
      throw new LocalizedIllegalArgumentException(RESOURCES, "type.invalid", typeStr);
    }
    this.type = typeStr;
  }

  private Integer width;

  @Override
  public void setWidth(Integer width) {
    this.width = width;
  }

  private Object value;

  @Override
  public void setValue(Object value) throws IOException {
    this.value = Coercion.nullIfEmpty(value);
  }

  private Object onblur;

  @Override
  public void setOnblur(Object onblur) throws IOException {
    this.onblur = Onblur.onblur.normalize(onblur);
  }

  private Object onchange;

  @Override
  public void setOnchange(Object onchange) throws IOException {
    this.onchange = Onchange.onchange.normalize(onchange);
  }

  private Object onclick;

  @Override
  public void setOnclick(Object onclick) throws IOException {
    this.onclick = Onclick.onclick.normalize(onclick);
  }

  private Object onfocus;

  @Override
  public void setOnfocus(Object onfocus) throws IOException {
    this.onfocus = Onfocus.onfocus.normalize(onfocus);
  }

  private Object onkeypress;

  @Override
  public void setOnkeypress(Object onkeypress) throws IOException {
    this.onkeypress = Onkeypress.onkeypress.normalize(onkeypress);
  }

  /**
   * {@inheritDoc}
   *
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
  /* BodyTag only:
  protected int doEndTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
  /**/
  /* SimpleTag only: */
  protected void doTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
    final PageContext pageContext = (PageContext) getJspContext();
    /**/
    if (type == null) {
      throw new AttributeRequiredException("type");
    }
    if (value == null) {
      // TODO: Distinguish between empty and null, track valueSet boolean for when is set to null?
      setValue(capturedBody.trim());
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
        .disabled(disabled)
        .height(height)
        .maxlength(maxlength)
        .name(name)
        .readonly(readonly)
        .size(size);
    UrlUtils.writeSrc(pageContext, out, src, params, addLastModified, absolute, canonical);
    input
        .tabindex((tabindex >= 1) ? tabindex : null)
        .title(title)
        .type(type)
        .width(width)
        .value(value == null ? "" : value)
        .onblur(onblur)
        .onchange(onchange)
        .onclick(onclick)
        .onfocus(onfocus)
        .onkeypress(onkeypress)
        .__();
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
