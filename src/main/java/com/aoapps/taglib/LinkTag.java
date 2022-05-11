/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2013, 2015, 2016, 2017, 2019, 2020, 2021, 2022  AO Industries, Inc.
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
import com.aoapps.html.servlet.DocumentEE;
import com.aoapps.html.servlet.LINK;
import com.aoapps.lang.Coercion;
import com.aoapps.lang.Strings;
import com.aoapps.net.MutableURIParameters;
import com.aoapps.net.URIParameters;
import com.aoapps.net.URIParametersMap;
import com.aoapps.servlet.jsp.tagext.JspTagUtils;
import com.aoapps.servlet.lastmodified.AddLastModified;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

/**
 * @author  AO Industries, Inc.
 */
public class LinkTag extends ElementNullTag
    implements
    // Attributes
    HrefAttribute,
    ParamsAttribute,
    HreflangAttribute,
    RelAttribute,
    TypeAttribute,
    TitleAttribute {

  public LinkTag() {
    init();
  }

  @Override
  public MediaType getOutputType() {
    return MediaType.XHTML;
  }

  /* BodyTag only:
    private static final long serialVersionUID = 1L;
  /**/

  /**
   * Copies all values from the provided link.
   */
  public void setLink(Link link) {
    GlobalAttributesUtils.copy(link.getGlobal(), this);
    setHref(link.getHref());
    setAbsolute(link.getAbsolute());
    URIParameters linkParams = link.getParams();
    if (linkParams != null) {
      for (Map.Entry<String, List<String>> entry : linkParams.getParameterMap().entrySet()) {
        String paramName = entry.getKey();
        for (String paramValue : entry.getValue()) {
          addParam(paramName, (Object) paramValue);
        }
      }
    }
    this.addLastModified = link.getAddLastModified();
    setHreflang(link.getHreflang());
    setRel(link.getRel());
    setType(link.getType());
    setMedia(link.getMedia());
    setTitle(link.getTitle());
  }

  private String href;

  @Override
  public void setHref(String href) {
    this.href = Strings.nullIfEmpty(href);
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

  private Object hreflang;

  @Override
  public void setHreflang(Object hreflang) {
    this.hreflang = hreflang;
  }

  private String rel;

  @Override
  public void setRel(String rel) {
    this.rel = rel;
  }

  private String type;

  @Override
  public void setType(String type) {
    this.type = Strings.trimNullIfEmpty(type);
  }

  private String media; // TODO: media to Object

  public void setMedia(String media) {
    this.media = Strings.trimNullIfEmpty(media);
  }

  private Object title;

  @Override
  public void setTitle(Object title) {
    this.title = AttributeUtils.trimNullIfEmpty(title);
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
    type = null;
    media = null;
    title = null;
  }

  @Override
  /* BodyTag only:
    protected int doEndTag(Writer out) throws JspException, IOException {
  /**/
  /* SimpleTag only: */
  protected void doTag(Writer out) throws JspException, IOException {
    PageContext pageContext = (PageContext) getJspContext();
    /**/
    Optional<LinksAttribute> parent = JspTagUtils.findAncestor(this, LinksAttribute.class);
    if (parent.isPresent()) {
      String hreflangStr;
      if (hreflang instanceof Locale) {
        hreflangStr = ((Locale) hreflang).toLanguageTag();
      } else {
        hreflang = AttributeUtils.trimNullIfEmpty(hreflang);
        hreflangStr = Coercion.toString(hreflang);
      }
      parent.get().addLink(
          new Link(
              global.freeze(),
              href,
              absolute,
              canonical,
              params,
              addLastModified,
              hreflangStr,
              Strings.trimNullIfEmpty(rel),
              type,
              media,
              Coercion.toString(title)
          )
      );
    } else {
      DocumentEE document = new DocumentEE(
          pageContext.getServletContext(),
          (HttpServletRequest) pageContext.getRequest(),
          (HttpServletResponse) pageContext.getResponse(),
          out,
          false, // Do not add extra newlines to JSP
          false  // Do not add extra indentation to JSP
      );
      LINK<?> link = document.link();
      GlobalAttributesUtils.doGlobalAttributes(global, link);
      link.href(UrlUtils.getHref(pageContext, href, params, addLastModified, absolute, canonical));
      if (hreflang instanceof Locale) {
        link.hreflang((Locale) hreflang);
      } else {
        hreflang = AttributeUtils.trimNullIfEmpty(hreflang);
        link.hreflang(Coercion.toString(hreflang));
      }
      link
          .rel(rel)
          .type(type)
          .media(media)
          .title(title)
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
