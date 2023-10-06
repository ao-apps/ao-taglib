/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2019, 2020, 2021, 2022, 2023  AO Industries, Inc.
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

import static com.aoapps.taglib.AreaTag.RESOURCES;

import com.aoapps.encoding.MediaType;
import com.aoapps.html.any.attributes.dimension.Coords;
import com.aoapps.html.any.attributes.enumeration.Rel;
import com.aoapps.html.any.attributes.enumeration.Shape;
import com.aoapps.html.any.attributes.enumeration.Target;
import com.aoapps.html.any.attributes.event.Onclick;
import com.aoapps.html.any.attributes.event.Onmouseout;
import com.aoapps.html.any.attributes.event.Onmouseover;
import com.aoapps.html.any.attributes.text.Alt;
import com.aoapps.html.any.attributes.text.Hreflang;
import com.aoapps.html.any.attributes.text.Title;
import com.aoapps.html.any.attributes.text.Type;
import com.aoapps.html.any.attributes.url.Href;
import com.aoapps.html.servlet.AREA;
import com.aoapps.html.servlet.DocumentEE;
import com.aoapps.lang.Coercion;
import com.aoapps.lang.LocalizedIllegalArgumentException;
import com.aoapps.lang.Strings;
import com.aoapps.net.MutableURIParameters;
import com.aoapps.net.URIParametersMap;
import com.aoapps.servlet.lastmodified.AddLastModified;
import com.aoapps.taglib.AltAttribute;
import com.aoapps.taglib.AreaTagTEI;
import com.aoapps.taglib.AttributeRequiredException;
import com.aoapps.taglib.GlobalAttributesUtils;
import com.aoapps.taglib.HrefAttribute;
import com.aoapps.taglib.HreflangAttribute;
import com.aoapps.taglib.OnclickAttribute;
import com.aoapps.taglib.OnmouseoutAttribute;
import com.aoapps.taglib.OnmouseoverAttribute;
import com.aoapps.taglib.ParamUtils;
import com.aoapps.taglib.ParamsAttribute;
import com.aoapps.taglib.RelAttribute;
import com.aoapps.taglib.TargetAttribute;
import com.aoapps.taglib.TitleAttribute;
import com.aoapps.taglib.TypeAttribute;
import com.aoapps.taglib.UrlUtils;
import java.io.IOException;
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
public class AreaTag extends ElementNullBodyTag
    implements
    // Attributes
    HrefAttribute,
    ParamsAttribute,
    HreflangAttribute,
    RelAttribute,
    TypeAttribute,
    TargetAttribute,
    AltAttribute,
    TitleAttribute,
    // Events
    OnclickAttribute,
    OnmouseoverAttribute,
    OnmouseoutAttribute {

  /* SimpleTag only:
    public static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, AreaTag.class);
  /**/

  public AreaTag() {
    init();
  }

  @Override
  public MediaType getOutputType() {
    return MediaType.XHTML;
  }

  /* BodyTag only: */
  private static final long serialVersionUID = 1L;
  /**/

  private String shape;

  public void setShape(String shape) {
    shape = Shape.shape.normalize(shape);
    if (!AreaTagTEI.isValidShape(shape)) {
      throw new LocalizedIllegalArgumentException(RESOURCES, "shape.invalid", shape);
    }
    this.shape = shape;
  }

  private String coords;

  public void setCoords(String coords) {
    this.coords = Coords.coords.normalize(coords);
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
  public void setHreflang(Object hreflang) {
    this.hreflang = Hreflang.hreflang.normalize(hreflang);
  }

  private String rel;

  @Override
  public void setRel(String rel) {
    this.rel = Rel.rel.normalize(rel);
  }

  private Object type;

  @Override
  public void setType(Object type) {
    this.type = Type.type.normalize(type);
  }

  private String target;

  @Override
  public void setTarget(String target) {
    this.target = Target.target.normalize(target);
  }

  private Object alt;

  @Override
  public void setAlt(Object alt) {
    this.alt = Alt.alt.normalize(alt);
  }

  private Object title;

  @Override
  public void setTitle(Object title) {
    this.title = Title.title.normalize(title);
  }

  private Object onclick;

  @Override
  public void setOnclick(Object onclick) {
    this.onclick = Onclick.onclick.normalize(onclick);
  }

  private Object onmouseover;

  @Override
  public void setOnmouseover(Object onmouseover) {
    this.onmouseover = Onmouseover.onmouseover.normalize(onmouseover);
  }

  private Object onmouseout;

  @Override
  public void setOnmouseout(Object onmouseout) {
    this.onmouseout = Onmouseout.onmouseout.normalize(onmouseout);
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
    shape = null;
    coords = null;
    href = null;
    params = null;
    absolute = false;
    canonical = false;
    addLastModified = AddLastModified.AUTO;
    hreflang = null;
    rel = null;
    type = null;
    target = null;
    alt = null;
    title = null;
    onclick = null;
    onmouseover = null;
    onmouseout = null;
  }

  @Override
  /* BodyTag only: */
  protected int doEndTag(Writer out) throws JspException, IOException {
    /**/
    /* SimpleTag only:
  protected void doTag(Writer out) throws JspException, IOException {
    final PageContext pageContext = (PageContext) getJspContext();
    /**/
    if (shape == null) {
      throw new AttributeRequiredException("shape");
    }
    if (!"default".equals(shape)) {
      if (coords == null) {
        throw new AttributeRequiredException("coords");
      }
    }
    if (href != null) {
      if (alt == null) {
        throw new AttributeRequiredException("alt");
      }
    }
    DocumentEE document = new DocumentEE(
        pageContext.getServletContext(),
        (HttpServletRequest) pageContext.getRequest(),
        (HttpServletResponse) pageContext.getResponse(),
        out,
        false, // Do not add extra newlines to JSP
        false  // Do not add extra indentation to JSP
    );
    AREA<?> area = GlobalAttributesUtils.doGlobalAttributes(global, document.area())
        .shape(shape)
        .coords(coords)
        .href(UrlUtils.getHref(pageContext, href, params, addLastModified, absolute, canonical))
        .hreflang(hreflang)
        .rel(rel)
        // TODO: type to Area (or remove entirely since this part of the standard is uncertain and currently unimplemented by all browsers?)
        .attribute("type", type)
        .target(target);
    // non-existing alt is OK when there is no href (with href: "" stays "")
    if (
        alt != null
            && (href != null || !Coercion.isEmpty(alt))
    ) {
      area.alt(alt);
    }
    area
        .title(title)
        .onclick(onclick)
        .onmouseover(onmouseover)
        .onmouseout(onmouseout)
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
