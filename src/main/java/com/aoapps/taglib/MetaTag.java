/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2011, 2013, 2015, 2016, 2017, 2019, 2020, 2021, 2022, 2023  AO Industries, Inc.
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
import com.aoapps.html.any.attributes.enumeration.HttpEquiv;
import com.aoapps.html.any.attributes.text.Content;
import com.aoapps.html.any.attributes.text.Name;
import com.aoapps.html.servlet.DocumentEE;
import com.aoapps.html.servlet.META;
import com.aoapps.io.buffer.BufferResult;
import com.aoapps.lang.Coercion;
import com.aoapps.lang.Strings;
import com.aoapps.servlet.jsp.tagext.JspTagUtils;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * @author  AO Industries, Inc.
 */
public class MetaTag extends ElementBufferedTag
    implements
    NameAttribute,
    ContentAttribute {

  public MetaTag() {
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

  /**
   * Copies all values from the provided meta.
   */
  public void setMeta(Meta meta) {
    GlobalAttributesUtils.copy(meta.getGlobal(), this);
    setName(meta.getName());
    setHttpEquiv(meta.getHttpEquiv());
    setItemprop(meta.getItemprop());
    setCharset(meta.getCharset());
    setContent(meta.getContent());
  }

  /* BodyTag only:
    private static final long serialVersionUID = 1L;
  /**/

  private String name;

  @Override
  public void setName(Object name) {
    name = Name.name.normalize(name);
    this.name = (name == null) ? null : Coercion.toString(name);
  }

  private String httpEquiv;

  public void setHttpEquiv(String httpEquiv) {
    this.httpEquiv = HttpEquiv.httpEquiv.normalize(httpEquiv);
  }

  private String itemprop;

  public void setItemprop(String itemprop) {
    // TODO: Itemprop.itemprop.normalize
    this.itemprop = Strings.trimNullIfEmpty(itemprop);
  }

  private String charset;

  public void setCharset(Object charset) {
    if (charset == null) {
      this.charset = null;
    } else if (charset instanceof Charset) {
      this.charset = ((Charset) charset).name();
    } else {
      this.charset = com.aoapps.html.any.attributes.enumeration.Charset.charset.normalize(Coercion.toString(charset));
    }
  }

  private Object content;

  @Override
  public void setContent(Object content) {
    this.content = Content.content.normalize(content);
  }

  private void init() {
    name = null;
    httpEquiv = null;
    itemprop = null;
    charset = null;
    content = null;
  }

  @Override
  /* BodyTag only:
    protected int doEndTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
  /**/
  /* SimpleTag only: */
  protected void doTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
    PageContext pageContext = (PageContext) getJspContext();
    /**/
    Optional<MetasAttribute> parent = JspTagUtils.findAncestor(this, MetasAttribute.class);
    if (content == null) {
      setContent(capturedBody.trim());
    }
    if (parent.isPresent()) {
      parent.get().addMeta(
          new Meta(
              global.freeze(),
              name,
              httpEquiv,
              itemprop,
              charset,
              (content == null) ? null : Coercion.toString(content)
          )
      );
    } else {
      // Write the meta tag directly here
      DocumentEE document = new DocumentEE(
          pageContext.getServletContext(),
          (HttpServletRequest) pageContext.getRequest(),
          (HttpServletResponse) pageContext.getResponse(),
          out,
          false, // Do not add extra newlines to JSP
          false  // Do not add extra indentation to JSP
      );
      META<?> meta = document.meta();
      GlobalAttributesUtils.doGlobalAttributes(global, meta);
      meta.name(name)
          .httpEquiv(httpEquiv)
          // TODO: Create a global "itemprop" in ao-fluent-html
          .attribute("itemprop", itemprop)
          .charset(charset)
          .content(content)
          .__();
    }
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
