/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2013, 2014, 2015, 2016, 2017, 2019, 2020, 2021, 2022, 2023, 2024  AO Industries, Inc.
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
import com.aoapps.html.any.attributes.event.Onerror;
import com.aoapps.html.any.attributes.event.Onload;
import com.aoapps.html.any.attributes.text.Alt;
import com.aoapps.html.any.attributes.text.Title;
import com.aoapps.html.any.attributes.text.Usemap;
import com.aoapps.html.any.attributes.url.Src;
import com.aoapps.html.servlet.DocumentEE;
import com.aoapps.io.buffer.BufferResult;
import com.aoapps.lang.Strings;
import com.aoapps.net.MutableURIParameters;
import com.aoapps.net.URIParametersMap;
import com.aoapps.servlet.lastmodified.AddLastModified;
import com.aoapps.taglib.AltAttribute;
import com.aoapps.taglib.AttributeRequiredException;
import com.aoapps.taglib.GlobalAttributesUtils;
import com.aoapps.taglib.HeightAttribute;
import com.aoapps.taglib.OnerrorAttribute;
import com.aoapps.taglib.OnloadAttribute;
import com.aoapps.taglib.ParamUtils;
import com.aoapps.taglib.ParamsAttribute;
import com.aoapps.taglib.SrcAttribute;
import com.aoapps.taglib.TitleAttribute;
import com.aoapps.taglib.UrlUtils;
import com.aoapps.taglib.WidthAttribute;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Writer;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

/**
 * @author  AO Industries, Inc.
 */
public class ImgTag extends ElementBufferedBodyTag
    implements
    // Attributes
    SrcAttribute,
    ParamsAttribute,
    WidthAttribute,
    HeightAttribute,
    AltAttribute,
    TitleAttribute,
    // Events
    OnerrorAttribute,
    OnloadAttribute {

  public ImgTag() {
    init();
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    init();
  }

  @Override
  public MediaType getContentType() {
    return MediaType.URL;
  }

  @Override
  public MediaType getOutputType() {
    return MediaType.XHTML;
  }

  /* BodyTag only: */
  private static final long serialVersionUID = 1L;
  /**/

  private transient String src;

  @Override
  public void setSrc(String src) {
    this.src = Src.src.normalize(src);
  }

  private transient MutableURIParameters params;

  @Override
  public void addParam(String name, Object value) {
    if (params == null) {
      params = new URIParametersMap();
    }
    params.add(name, value);
  }

  private transient boolean absolute;

  public void setAbsolute(boolean absolute) {
    this.absolute = absolute;
  }

  private transient boolean canonical;

  public void setCanonical(boolean canonical) {
    this.canonical = canonical;
  }

  private transient AddLastModified addLastModified;

  public void setAddLastModified(String addLastModified) {
    this.addLastModified = AddLastModified.valueOfLowerName(Strings.trim(addLastModified).toLowerCase(Locale.ROOT));
  }

  private transient Integer width;

  @Override
  public void setWidth(Integer width) {
    this.width = width;
  }

  private transient Integer height;

  @Override
  public void setHeight(Integer height) {
    this.height = height;
  }

  private transient Object alt;

  @Override
  public void setAlt(Object alt) {
    this.alt = Objects.toString(Alt.alt.normalize(alt), "");
  }

  private transient Object title;

  @Override
  public void setTitle(Object title) {
    this.title = Title.title.normalize(title);
  }

  private transient String usemap;

  public void setUsemap(String usemap) {
    this.usemap = Usemap.usemap.normalize(usemap);
  }

  private transient boolean ismap;

  public void setIsmap(boolean ismap) {
    this.ismap = ismap;
  }

  // Events

  private transient Object onerror;

  @Override
  public void setOnerror(Object onerror) {
    this.onerror = Onerror.onerror.normalize(onerror);
  }

  private transient Object onload;

  @Override
  public void setOnload(Object onload) {
    this.onload = Onload.onload.normalize(onload);
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
    src = null;
    params = null;
    absolute = false;
    canonical = false;
    addLastModified = AddLastModified.AUTO;
    width = null;
    height = null;
    alt = null;
    title = null;
    usemap = null;
    ismap = false;
    // Events
    onerror = null;
    onload = null;
  }

  @Override
  /* BodyTag only: */
  protected int doEndTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
    /**/
    /* SimpleTag only:
  protected void doTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
    final PageContext pageContext = (PageContext) getJspContext();
    /**/
    assert capturedBody.trim() == capturedBody : "URLs should have already been trimmed";
    if (src == null) {
      // TODO: Validate here?
      src = capturedBody.toString();
    }
    if (usemap == null && alt == null) {
      throw new AttributeRequiredException("alt");
    }
    DocumentEE document = new DocumentEE(
        pageContext.getServletContext(),
        (HttpServletRequest) pageContext.getRequest(),
        (HttpServletResponse) pageContext.getResponse(),
        out,
        false, // Do not add extra newlines to JSP
        false  // Do not add extra indentation to JSP
    );
    GlobalAttributesUtils.doGlobalAttributes(global, document.img())
        .src(UrlUtils.getSrc(pageContext, src, params, addLastModified, absolute, canonical))
        .width(width)
        .height(height)
        .alt(alt)
        .title(title)
        .usemap(usemap)
        .ismap(ismap)
        // Events
        .onerror(onerror)
        .onload(onload)
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
