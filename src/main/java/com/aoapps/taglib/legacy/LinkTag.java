/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2013, 2015, 2016, 2017, 2019, 2020, 2021, 2022, 2023  AO Industries, Inc.
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
import com.aoapps.html.any.attributes.enumeration.Rel;
import com.aoapps.html.any.attributes.text.Hreflang;
import com.aoapps.html.any.attributes.text.Media;
import com.aoapps.html.any.attributes.text.Title;
import com.aoapps.html.any.attributes.text.Type;
import com.aoapps.html.any.attributes.url.Href;
import com.aoapps.html.servlet.DocumentEE;
import com.aoapps.html.servlet.LINK;
import com.aoapps.lang.Coercion;
import com.aoapps.lang.Strings;
import com.aoapps.net.MutableURIParameters;
import com.aoapps.net.URIParameters;
import com.aoapps.net.URIParametersMap;
import com.aoapps.servlet.jsp.tagext.JspTagUtils;
import com.aoapps.servlet.lastmodified.AddLastModified;
import com.aoapps.taglib.GlobalAttributesUtils;
import com.aoapps.taglib.HrefAttribute;
import com.aoapps.taglib.HreflangAttribute;
import com.aoapps.taglib.Link;
import com.aoapps.taglib.LinksAttribute;
import com.aoapps.taglib.ParamUtils;
import com.aoapps.taglib.ParamsAttribute;
import com.aoapps.taglib.RelAttribute;
import com.aoapps.taglib.TitleAttribute;
import com.aoapps.taglib.TypeAttribute;
import com.aoapps.taglib.UrlUtils;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

/**
 * @author  AO Industries, Inc.
 */
public class LinkTag extends ElementNullBodyTag
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

  /* BodyTag only: */
  private static final long serialVersionUID = 1L;

  /**/

  /**
   * Copies all values from the provided link.
   */
  public void setLink(Link link) {
    try {
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
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

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
  public void setHreflang(Object hreflang) throws IOException {
    this.hreflang = Hreflang.hreflang.normalize(hreflang);
  }

  private String rel;

  @Override
  public void setRel(String rel) {
    this.rel = Rel.rel.normalize(rel);
  }

  private String type;

  @Override
  public void setType(Object type) throws IOException {
    this.type = Coercion.toString(Type.type.normalize(type));
  }

  private Object media;

  public void setMedia(Object media) throws IOException {
    this.media = Media.media.normalize(media);
  }

  private Object title;

  @Override
  public void setTitle(Object title) throws IOException {
    this.title = Title.title.normalize(title);
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
  /* BodyTag only: */
  protected int doEndTag(Writer out) throws JspException, IOException {
    /**/
    /* SimpleTag only:
      protected void doTag(Writer out) throws JspException, IOException {
        PageContext pageContext = (PageContext)getJspContext();
    /**/
    Optional<LinksAttribute> parent = JspTagUtils.findAncestor(this, LinksAttribute.class);
    if (parent.isPresent()) {
      parent.get().addLink(
          new Link(
              global.freeze(),
              href,
              absolute,
              canonical,
              params,
              addLastModified,
              Coercion.toString(hreflang),
              Strings.trimNullIfEmpty(rel),
              type,
              Coercion.toString(media),
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
      link.href(UrlUtils.getHref(pageContext, href, params, addLastModified, absolute, canonical))
          .hreflang(hreflang)
          .rel(rel)
          .type(type)
          .media(media)
          .title(title)
          .__();
    }
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
