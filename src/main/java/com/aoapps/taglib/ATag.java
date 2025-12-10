/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013, 2014, 2015, 2016, 2017, 2019, 2020, 2021, 2022, 2023, 2025  AO Industries, Inc.
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
import static com.aoapps.encoding.TextInXhtmlEncoder.textInXhtmlEncoder;

import com.aoapps.encoding.MediaType;
import com.aoapps.hodgepodge.i18n.MarkupCoercion;
import com.aoapps.hodgepodge.i18n.MarkupType;
import com.aoapps.html.any.attributes.enumeration.Rel;
import com.aoapps.html.any.attributes.enumeration.Target;
import com.aoapps.html.any.attributes.event.Onclick;
import com.aoapps.html.any.attributes.event.Onmouseout;
import com.aoapps.html.any.attributes.event.Onmouseover;
import com.aoapps.html.any.attributes.text.Hreflang;
import com.aoapps.html.any.attributes.text.Title;
import com.aoapps.html.any.attributes.text.Type;
import com.aoapps.html.any.attributes.url.Href;
import com.aoapps.io.buffer.BufferResult;
import com.aoapps.lang.Coercion;
import com.aoapps.lang.Strings;
import com.aoapps.net.MutableURIParameters;
import com.aoapps.net.URIDecoder;
import com.aoapps.net.URIParametersMap;
import com.aoapps.net.URIParser;
import com.aoapps.net.URIResolver;
import com.aoapps.servlet.http.Dispatcher;
import com.aoapps.servlet.lastmodified.AddLastModified;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

/**
 * @author  AO Industries, Inc.
 */
public class ATag extends ElementBufferedTag
    implements
    // Attributes
    HrefAttribute,
    ParamsAttribute,
    HreflangAttribute,
    RelAttribute,
    TargetAttribute,
    TitleAttribute,
    TypeAttribute,
    // Events
    OnclickAttribute,
    OnmouseoutAttribute,
    OnmouseoverAttribute {

  public ATag() {
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

  private String href;

  @Override
  public void setHref(String href) {
    this.href = Href.href.normalize(href);
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

  private Object hreflang;

  @Override
  public void setHreflang(Object hreflang) {
    this.hreflang = Hreflang.hreflang.normalize(hreflang);
  }

  private String rel;

  @Override
  public void setRel(String rel) {
    this.rel = Rel.rel.normalize(rel);
  }

  private String target;

  @Override
  public void setTarget(String target) {
    this.target = Target.target.normalize(target);
  }

  private Object title;

  @Override
  public void setTitle(Object title) {
    this.title = Title.title.normalize(title);
  }

  private Object type;

  @Override
  public void setType(Object type) {
    this.type = Type.type.normalize(type);
  }

  private Object onclick;

  @Override
  public void setOnclick(Object onclick) {
    this.onclick = Onclick.onclick.normalize(onclick);
  }

  private Object onmouseout;

  @Override
  public void setOnmouseout(Object onmouseout) {
    this.onmouseout = Onmouseout.onmouseout.normalize(onmouseout);
  }

  private Object onmouseover;

  @Override
  public void setOnmouseover(Object onmouseover) {
    this.onmouseover = Onmouseover.onmouseover.normalize(onmouseover);
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
    href = null;
    params = null;
    absolute = false;
    canonical = false;
    addLastModified = AddLastModified.AUTO;
    hreflang = null;
    rel = null;
    target = null;
    title = null;
    type = null;
    onclick = null;
    onmouseout = null;
    onmouseover = null;
  }

  @Override
  /* BodyTag only:
  protected int doEndTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
  /**/
  /* SimpleTag only: */
  protected void doTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
    final PageContext pageContext = (PageContext) getJspContext();
    /**/
    // TODO: ao-fluent-html
    out.write("<a");
    GlobalAttributesUtils.writeGlobalAttributes(global, out);
    String transformed;
    if (URIParser.isScheme(href, "tel")) {
      transformed = href.replace(' ', '-');
    } else {
      transformed = href;
    }
    UrlUtils.writeHref(pageContext, out, transformed, params, addLastModified, absolute, canonical);
    if (hreflang != null) {
      out.write(" hreflang=\"");
      Coercion.write(hreflang, textInXhtmlAttributeEncoder, out);
      out.append('"');
    }
    if (rel != null) {
      out.write(" rel=\"");
      encodeTextInXhtmlAttribute(rel, out);
      out.append('"');
    }
    if (target != null) {
      out.write(" target=\"");
      encodeTextInXhtmlAttribute(target, out);
      out.append('"');
    }
    if (title != null) {
      out.write(" title=\"");
      MarkupCoercion.write(
          title,
          MarkupType.TEXT,
          true,
          textInXhtmlAttributeEncoder,
          false,
          out
      );
      out.append('"');
    }
    if (type != null) {
      out.write(" type=\"");
      encodeTextInXhtmlAttribute(type, out);
      out.append('"');
    }
    if (onclick != null) {
      out.write(" onclick=\"");
      MarkupCoercion.write(
          onclick,
          MarkupType.JAVASCRIPT,
          true,
          javascriptInXhtmlAttributeEncoder,
          false,
          out
      );
      out.append('"');
    }
    if (onmouseout != null) {
      out.write(" onmouseout=\"");
      MarkupCoercion.write(
          onmouseout,
          MarkupType.JAVASCRIPT,
          true,
          javascriptInXhtmlAttributeEncoder,
          false,
          out
      );
      out.append('"');
    }
    if (onmouseover != null) {
      out.write(" onmouseover=\"");
      MarkupCoercion.write(
          onmouseover,
          MarkupType.JAVASCRIPT,
          true,
          javascriptInXhtmlAttributeEncoder,
          false,
          out
      );
      out.append('"');
    }
    out.append('>');
    BufferResult trimmedBody = capturedBody.trim();
    // TODO: Make a BufferResult.isEmpty() that defaults to getLength() == 0, but provides a chance at optimizations
    if (trimmedBody.getLength() == 0) {
      // When the body is empty after trimming, display the href itself
      if (href != null) {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        String toDecode;
        if (URIParser.isScheme(href, "mailto")) {
          toDecode = href.substring("mailto:".length());
        } else if (URIParser.isScheme(href, "telnet")) {
          toDecode = href.substring("telnet:".length());
        } else if (URIParser.isScheme(href, "tel")) {
          toDecode = href.substring("tel:".length());
        } else {
          toDecode = URIResolver.getAbsolutePath(Dispatcher.getCurrentPagePath(request), href);
        }
        // Decode (for display onlyh) "Printable characters in US-ASCII that are not allowed in URIs" that were
        // previously considered valid and converted within URIDecoder
        toDecode = toDecode
            .replace("%3C", "<")
            .replace("%3c", "<")
            .replace("%3E", ">")
            .replace("%3E", ">")
            .replace("%22", "\"")
            .replace("%20", " ")
            .replace("%7B", "{")
            .replace("%7b", "{")
            .replace("%7D", "}")
            .replace("%7d", "}")
            .replace("%7C", "|")
            .replace("%7c", "|")
            .replace("%5C", "\\")
            .replace("%5c", "\\")
            .replace("%5E", "^")
            .replace("%5e", "^")
            .replace("%60", "`")
        ;
        // Decode to get a human-readable (but still unambiguous) display
        URIDecoder.decodeURI(toDecode, textInXhtmlEncoder, out);
      }
    } else {
      MarkupCoercion.write(
          trimmedBody,
          MarkupType.XHTML,
          out,
          true
      );
    }
    out.write("</a>");
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
