/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013, 2016, 2017, 2020, 2021, 2022, 2023, 2024  AO Industries, Inc.
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
import com.aoapps.encoding.taglib.EncodingBufferedTag;
import com.aoapps.html.servlet.DocumentEE;
import com.aoapps.io.buffer.BufferResult;
import com.aoapps.servlet.jsp.tagext.JspTagUtils;
import java.io.IOException;
import java.io.Writer;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

/**
 * @author  AO Industries, Inc.
 */
public class StyleTag extends EncodingBufferedTag {

  /* SimpleTag only: */
  public static final String TAG_NAME = "<ao:style>";

  /**/

  public StyleTag() {
    init();
  }

  @Override
  public MediaType getContentType() {
    return MediaType.CSS;
  }

  @Override
  public MediaType getOutputType() {
    Optional<StyleAttribute> styleAttribute = forceElement
        ? Optional.empty()
        : JspTagUtils.findAncestor(this, StyleAttribute.class);
    return styleAttribute.isPresent() ? null : MediaType.XHTML;
  }

  /* BodyTag only:
    private static final long serialVersionUID = 1L;
  /**/

  private boolean forceElement;

  public void setForceElement(boolean forceElement) {
    this.forceElement = forceElement;
  }

  private boolean noscript;

  public void setNoscript(boolean noscript) {
    this.noscript = noscript;
  }

  private void init() {
    forceElement = false;
    noscript = false;
  }

  @Override
  /* BodyTag only:
    protected int doEndTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
  /**/
  /* SimpleTag only: */
  protected void doTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
    PageContext pageContext = (PageContext) getJspContext();
    /**/
    Optional<StyleAttribute> styleAttribute = forceElement
        ? Optional.empty()
        : JspTagUtils.findAncestor(this, StyleAttribute.class);
    if (styleAttribute.isPresent()) {
      if (noscript) {
        throw new JspTagException(TAG_NAME + ": noscript may not be set when providing style to parent tag");
      }
      styleAttribute.get().setStyle(capturedBody.trim());
    } else {
      // Write style tag with src attribute
      DocumentEE document = new DocumentEE(
          pageContext.getServletContext(),
          (HttpServletRequest) pageContext.getRequest(),
          (HttpServletResponse) pageContext.getResponse(),
          out,
          false, // Do not add extra newlines to JSP
          false  // Do not add extra indentation to JSP
      );
      if (noscript) {
        document.autoIndent().unsafe("<noscript>").incDepth();
      }
      document.style()
          .out(capturedBody)
          .__();
      if (noscript) {
        document.decDepth().autoIndent().unsafe("</noscript>").autoNl();
      }
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
