/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013, 2016, 2017, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
import com.aoapps.encoding.taglib.legacy.EncodingBufferedBodyTag;
import com.aoapps.io.buffer.BufferResult;
import com.aoapps.net.MutableURIParameters;
import com.aoapps.net.URIParametersMap;
import com.aoapps.servlet.http.Canonical;
import com.aoapps.servlet.lastmodified.AddLastModified;
import com.aoapps.servlet.lastmodified.LastModifiedUtil;
import com.aoapps.taglib.AttributeUtils;
import com.aoapps.taglib.ParamUtils;
import com.aoapps.taglib.ParamsAttribute;
import com.aoapps.taglib.impl.NoEncodeUrlResponseWrapper;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;

/**
 * TODO: Replace uses of encoding:text with this as it is now more appropriate for sending dynamic parameters to JavaScript since it calls encodeURL.
 *
 * @author  AO Industries, Inc.
 */
public class UrlTag extends EncodingBufferedBodyTag implements ParamsAttribute, DynamicAttributes {

  public UrlTag() {
    init();
  }

  @Override
  public MediaType getContentType() {
    return MediaType.URL;
  }

  @Override
  public MediaType getOutputType() {
    return MediaType.URL;
  }

/* BodyTag only: */
  private static final long serialVersionUID = 1L;
/**/

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

/* BodyTag only: */
  private Canonical c;
/**/

  /**
   * @see  ParamUtils#addDynamicAttribute(java.lang.String, java.lang.String, java.lang.Object, java.util.List, com.aoapps.taglib.ParamsAttribute)
   */
  @Override
  public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
    List<String> expectedPatterns = new ArrayList<>();
    if (!ParamUtils.addDynamicAttribute(uri, localName, value, expectedPatterns, this)) {
      throw AttributeUtils.newDynamicAttributeFailedException(uri, localName, value, expectedPatterns);
    }
  }

  private void init() {
    params = null;
    absolute = false;
    canonical = false;
    addLastModified = AddLastModified.AUTO;
/* BodyTag only: */
    if (c != null) {
      c.close();
      c = null;
    }
/**/
  }

/* BodyTag only: */
  @Override
  protected int doStartTag(Writer out) throws JspException, IOException {
    c = Canonical.set(canonical);
    return super.doStartTag(out);
  }

  @Override
  protected int doEndTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
/**/
/* SimpleTag only:
  @Deprecated
  @Override
  public void doTag() throws JspException, IOException {
    try (Canonical c = Canonical.set(canonical)) {
      super.doTag();
    }
  }

  @Override
  protected void doTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
    PageContext pageContext = (PageContext)getJspContext();
/**/
    assert capturedBody.trim() == capturedBody : "URLs should have already been trimmed";
    out.write(
      LastModifiedUtil.buildURL(
        pageContext.getServletContext(),
        (HttpServletRequest)pageContext.getRequest(),
        new NoEncodeUrlResponseWrapper((HttpServletResponse)pageContext.getResponse()),
        capturedBody.toString(),
        params,
        addLastModified,
        absolute,
        canonical
      )
    );
/* BodyTag only: */
    return SKIP_PAGE;
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
