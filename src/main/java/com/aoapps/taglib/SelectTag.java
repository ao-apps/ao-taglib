/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2010, 2011, 2012, 2013, 2015, 2016, 2017, 2019, 2020, 2021, 2022, 2023  AO Industries, Inc.
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

import static com.aoapps.encoding.JavaScriptInXhtmlAttributeEncoder.javascriptInXhtmlAttributeEncoder;
import static com.aoapps.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import static com.aoapps.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;

import com.aoapps.encoding.MediaType;
import com.aoapps.encoding.Serialization;
import com.aoapps.encoding.servlet.SerializationEE;
import com.aoapps.hodgepodge.i18n.MarkupCoercion;
import com.aoapps.hodgepodge.i18n.MarkupType;
import com.aoapps.html.any.attributes.event.Onblur;
import com.aoapps.html.any.attributes.event.Onchange;
import com.aoapps.html.any.attributes.event.Onfocus;
import com.aoapps.html.any.attributes.event.Onkeypress;
import com.aoapps.html.any.attributes.text.Name;
import com.aoapps.io.buffer.BufferResult;
import com.aoapps.lang.Coercion;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * @author  AO Industries, Inc.
 */
public class SelectTag extends ElementBufferedTag
    implements
    // Attributes
    DisabledAttribute,
    NameAttribute,
    SizeAttribute,
    // Events
    OnblurAttribute,
    OnchangeAttribute,
    OnfocusAttribute,
    OnkeypressAttribute {

  public SelectTag() {
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

  /* BodyTag only:
    private static final long serialVersionUID = 1L;
  /**/

  private boolean disabled;

  @Override
  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }

  private Object name;

  @Override
  public void setName(Object name) {
    this.name = Name.name.normalize(name);
  }

  private Integer size;

  @Override
  public void setSize(Integer size) {
    this.size = size;
  }

  private Object onblur;

  @Override
  public void setOnblur(Object onblur) {
    this.onblur = Onblur.onblur.normalize(onblur);
  }

  private Object onchange;

  @Override
  public void setOnchange(Object onchange) {
    this.onchange = Onchange.onchange.normalize(onchange);
  }

  private Object onfocus;

  @Override
  public void setOnfocus(Object onfocus) {
    this.onfocus = Onfocus.onfocus.normalize(onfocus);
  }

  private Object onkeypress;

  @Override
  public void setOnkeypress(Object onkeypress) {
    this.onkeypress = Onkeypress.onkeypress.normalize(onkeypress);
  }

  private void init() {
    disabled = false;
    name = null;
    size = null;
    onblur = null;
    onchange = null;
    onfocus = null;
    onkeypress = null;
  }

  @Override
  /* BodyTag only:
    protected int doEndTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
  /**/
  /* SimpleTag only: */
  protected void doTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
    PageContext pageContext = (PageContext) getJspContext();
    /**/
    Serialization serialization = SerializationEE.get(
        pageContext.getServletContext(),
        (HttpServletRequest) pageContext.getRequest()
    );
    // TODO: ao-fluent-html
    out.write("<select");
    GlobalAttributesUtils.writeGlobalAttributes(global, out);
    if (disabled) {
      out.write(" disabled");
      if (serialization == Serialization.XML) {
        out.write("=\"disabled\"");
      }
    }
    if (name != null) {
      out.write(" name=\"");
      encodeTextInXhtmlAttribute(name, out);
      out.append('"');
    }
    if (size != null) {
      out.write(" size=\"");
      Coercion.write(size, textInXhtmlAttributeEncoder, out);
      out.append('"');
    }
    if (onblur != null) {
      out.write(" onblur=\"");
      MarkupCoercion.write(
          onblur,
          MarkupType.JAVASCRIPT,
          true,
          javascriptInXhtmlAttributeEncoder,
          false,
          out
      );
      out.append('"');
    }
    if (onchange != null) {
      out.write(" onchange=\"");
      MarkupCoercion.write(
          onchange,
          MarkupType.JAVASCRIPT,
          true,
          javascriptInXhtmlAttributeEncoder,
          false,
          out
      );
      out.append('"');
    }
    if (onfocus != null) {
      out.write(" onfocus=\"");
      MarkupCoercion.write(
          onfocus,
          MarkupType.JAVASCRIPT,
          true,
          javascriptInXhtmlAttributeEncoder,
          false,
          out
      );
      out.append('"');
    }
    if (onkeypress != null) {
      out.write(" onkeypress=\"");
      MarkupCoercion.write(
          onkeypress,
          MarkupType.JAVASCRIPT,
          true,
          javascriptInXhtmlAttributeEncoder,
          false,
          out
      );
      out.append('"');
    }
    out.append('>');
    MarkupCoercion.write(
        capturedBody,
        MarkupType.XHTML,
        out,
        true
    );
    out.write("</select>");
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
