/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2013, 2015, 2016, 2017, 2019, 2020, 2021, 2022, 2023  AO Industries, Inc.
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

import static com.aoapps.taglib.ScriptTag.RESOURCES;

import com.aoapps.encoding.MediaType;
import com.aoapps.html.any.attributes.text.Type;
import com.aoapps.html.any.attributes.url.Src;
import com.aoapps.html.servlet.DocumentEE;
import com.aoapps.io.buffer.BufferResult;
import com.aoapps.lang.Coercion;
import com.aoapps.lang.Strings;
import com.aoapps.lang.io.LocalizedUnsupportedEncodingException;
import com.aoapps.net.MutableURIParameters;
import com.aoapps.net.URIParametersMap;
import com.aoapps.servlet.lastmodified.AddLastModified;
import com.aoapps.taglib.GlobalAttributesUtils;
import com.aoapps.taglib.ParamUtils;
import com.aoapps.taglib.ParamsAttribute;
import com.aoapps.taglib.SrcAttribute;
import com.aoapps.taglib.TypeAttribute;
import com.aoapps.taglib.UrlUtils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

/**
 * @author  AO Industries, Inc.
 */
public class ScriptTag extends ElementBufferedBodyTag
    implements
    // Attributes
    TypeAttribute,
    SrcAttribute,
    ParamsAttribute {

  /* SimpleTag only:
    public static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, ScriptTag.class);
  /**/

  public ScriptTag() {
    init();
  }

  @Override
  public MediaType getContentType() {
    return mediaType;
  }

  @Override
  public MediaType getOutputType() {
    return MediaType.XHTML;
  }

  /* BodyTag only: */
  private static final long serialVersionUID = 1L;
  /**/

  private MediaType mediaType;

  @Override
  public void setType(Object type) throws IOException {
    try {
      type = Type.type.normalize(type);
      MediaType newMediaType = MediaType.getMediaTypeForContentType((type == null) ? null : Coercion.toString(type));
      if (
          newMediaType != MediaType.JAVASCRIPT
              && newMediaType != MediaType.JSON
              && newMediaType != MediaType.LD_JSON
      ) {
        throw new LocalizedUnsupportedEncodingException(RESOURCES, "unsupportedMediaType", newMediaType);
      }
      this.mediaType = newMediaType;
    } catch (UnsupportedEncodingException e) {
      throw new IllegalArgumentException(e);
    }
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

  // TODO: async, defer, ...

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

  //@Override
  //protected void setMediaEncoderOptions(MediaWriter mediaWriter) {
  //  if (mediaWriter instanceof JavaScriptInXhtmlWriter) {
  //    assert src == null;
  //    ((JavaScriptInXhtmlWriter)mediaWriter).setType(type); // .trim()?
  //  }
  //}

  private void init() {
    mediaType = MediaType.JAVASCRIPT;
    src = null;
    params = null;
    absolute = false;
    canonical = false;
    addLastModified = AddLastModified.AUTO;
  }

  @Override
  /* BodyTag only: */
  protected int doEndTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
    /**/
    /* SimpleTag only:
      protected void doTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
        PageContext pageContext = (PageContext)getJspContext();
    /**/
    // Write script tag with src attribute, discarding any body
    DocumentEE document = new DocumentEE(
        pageContext.getServletContext(),
        (HttpServletRequest) pageContext.getRequest(),
        (HttpServletResponse) pageContext.getResponse(),
        out,
        false, // Do not add extra newlines to JSP
        false  // Do not add extra indentation to JSP
    );
    GlobalAttributesUtils.doGlobalAttributes(global, document.script(mediaType.getContentType()))
        // Call getSrc always, since it validates src versus params
        .src(UrlUtils.getSrc(pageContext, src, params, addLastModified, absolute, canonical))
        // Only write body when there is no source (discard body when src provided)
        .out((src != null) ? null : capturedBody)
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
