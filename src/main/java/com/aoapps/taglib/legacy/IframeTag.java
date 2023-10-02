/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2011, 2012, 2013, 2015, 2016, 2017, 2019, 2020, 2021, 2022, 2023  AO Industries, Inc.
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

import static com.aoapps.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import static com.aoapps.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;

import com.aoapps.encoding.MediaType;
import com.aoapps.hodgepodge.i18n.MarkupCoercion;
import com.aoapps.hodgepodge.i18n.MarkupType;
import com.aoapps.html.any.attributes.url.Src;
import com.aoapps.io.buffer.BufferResult;
import com.aoapps.lang.Coercion;
import com.aoapps.lang.Strings;
import com.aoapps.net.MutableURIParameters;
import com.aoapps.net.URIParametersMap;
import com.aoapps.servlet.lastmodified.AddLastModified;
import com.aoapps.taglib.AttributeRequiredException;
import com.aoapps.taglib.FrameborderAttribute;
import com.aoapps.taglib.GlobalAttributesUtils;
import com.aoapps.taglib.HeightAttribute;
import com.aoapps.taglib.ParamUtils;
import com.aoapps.taglib.ParamsAttribute;
import com.aoapps.taglib.SrcAttribute;
import com.aoapps.taglib.UrlUtils;
import com.aoapps.taglib.WidthAttribute;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Locale;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

/**
 * @author  AO Industries, Inc.
 */
public class IframeTag extends ElementBufferedBodyTag
    implements
    // Attributes
    SrcAttribute,
    ParamsAttribute,
    WidthAttribute,
    HeightAttribute,
    FrameborderAttribute {

  public IframeTag() {
    init();
  }

  @Override
  public MediaType getContentType() {
    return MediaType.XHTML;
  }

  @Override
  public MediaType getOutputType() {
    return MediaType.XHTML;
  }

  /* BodyTag only: */
  private static final long serialVersionUID = 1L;
  /**/

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

  private Integer width;

  @Override
  public void setWidth(Integer width) {
    this.width = width;
  }

  private Integer height;

  @Override
  public void setHeight(Integer height) {
    this.height = height;
  }

  private boolean frameborder;

  @Override
  public void setFrameborder(boolean frameborder) {
    this.frameborder = frameborder;
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
    frameborder = true;
  }

  @Override
  /* BodyTag only: */
  protected int doEndTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
    /**/
    /* SimpleTag only:
  protected void doTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
    final PageContext pageContext = (PageContext) getJspContext();
    /**/
    if (src == null) {
      throw new AttributeRequiredException("src");
    }
    out.write("<iframe");
    GlobalAttributesUtils.writeGlobalAttributes(global, out);
    // TODO: Include id/name by doctype
    String myId = global.getId();
    if (myId != null) {
      out.write(" name=\"");
      encodeTextInXhtmlAttribute(myId, out);
      out.append('"');
    }
    UrlUtils.writeSrc(pageContext, out, src, params, addLastModified, absolute, canonical);
    if (width != null) {
      out.write(" width=\"");
      Coercion.write(width, textInXhtmlAttributeEncoder, out);
      out.append('"');
    }
    if (height != null) {
      out.write(" height=\"");
      Coercion.write(height, textInXhtmlAttributeEncoder, out);
      out.append('"');
    }
    out.write(" frameborder=\"");
    out.append(frameborder ? '1' : '0').write("\">");
    MarkupCoercion.write(
        capturedBody,
        MarkupType.XHTML,
        out,
        true
    );
    out.write("</iframe>");
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
