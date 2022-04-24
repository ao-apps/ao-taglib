/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2013, 2014, 2015, 2016, 2017, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import static com.aoapps.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.net.URIParameters;
import com.aoapps.servlet.jsp.LocalizedJspTagException;
import com.aoapps.servlet.lastmodified.AddLastModified;
import com.aoapps.servlet.lastmodified.LastModifiedUtil;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ResourceBundle;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

/**
 * Helper utility for handling URLs.
 *
 * @author  AO Industries, Inc.
 */
public final class UrlUtils {

  /** Make no instances. */
  private UrlUtils() {
    throw new AssertionError();
  }

  private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, UrlUtils.class);

  /**
   * Gets an href attribute value with parameters.
   * Adds contextPath to URLs that begin with a slash (/).
   * Encodes the URL.
   *
   * @see #writeHref(javax.servlet.jsp.PageContext, java.lang.Appendable, java.lang.String, com.aoapps.net.URIParameters, com.aoapps.servlet.lastmodified.AddLastModified, boolean, boolean)
   *
   * @throws JspTagException when parameters provided with null href
   */
  public static String getHref(
      PageContext pageContext,
      String href,
      URIParameters params,
      AddLastModified addLastModified,
      boolean absolute,
      boolean canonical
  ) throws JspTagException, MalformedURLException {
    if (href != null) {
      return LastModifiedUtil.buildURL(pageContext, href, params, addLastModified, absolute, canonical);
    } else {
      if (params != null) {
        throw new LocalizedJspTagException(RESOURCES, "paramsWithoutHref");
      }
      return null;
    }
  }

  /**
   * Writes an href attribute with parameters.
   * Adds contextPath to URLs that begin with a slash (/).
   * Encodes the URL.
   *
   * @see #getHref(javax.servlet.jsp.PageContext, java.lang.String, com.aoapps.net.URIParameters, com.aoapps.servlet.lastmodified.AddLastModified, boolean, boolean)
   *
   * @throws JspTagException when parameters provided with null href
   */
  // TODO: Still used once converted to ao-fluent-html?
  public static void writeHref(
      PageContext pageContext,
      Appendable out,
      String href,
      URIParameters params,
      AddLastModified addLastModified,
      boolean absolute,
      boolean canonical
  ) throws JspTagException, IOException {
    href = getHref(pageContext, href, params, addLastModified, absolute, canonical);
    if (href != null) {
      out.append(" href=\"");
      encodeTextInXhtmlAttribute(href, out);
      out.append('"');
    }
  }

  /**
   * @see  #writeHref(javax.servlet.jsp.PageContext, java.lang.Appendable, java.lang.String, com.aoapps.net.URIParameters, com.aoapps.servlet.lastmodified.AddLastModified, boolean, boolean)
   */
  // TODO: Still used once converted to ao-fluent-html?
  public static void writeHref(
      JspContext jspContext,
      Appendable out,
      String href,
      URIParameters params,
      AddLastModified addLastModified,
      boolean absolute,
      boolean canonical
  ) throws JspTagException, IOException {
    writeHref(
        (PageContext) jspContext,
        out,
        href,
        params,
        addLastModified,
        absolute,
        canonical
    );
  }

  /**
   * Gets a src attribute value with parameters.
   * Adds contextPath to URLs that begin with a slash (/).
   * Encodes the URL.
   *
   * @see #writeSrc(javax.servlet.jsp.PageContext, java.lang.Appendable, java.lang.String, com.aoapps.net.URIParameters, com.aoapps.servlet.lastmodified.AddLastModified, boolean, boolean)
   *
   * @throws JspTagException when parameters provided with null src
   */
  public static String getSrc(
      PageContext pageContext,
      String src,
      URIParameters params,
      AddLastModified addLastModified,
      boolean absolute,
      boolean canonical
  ) throws JspTagException, MalformedURLException {
    if (src != null) {
      return LastModifiedUtil.buildURL(pageContext, src, params, addLastModified, absolute, canonical);
    } else {
      if (params != null) {
        throw new LocalizedJspTagException(RESOURCES, "paramsWithoutSrc");
      }
      return null;
    }
  }

  /**
   * Writes a src attribute with parameters.
   * Adds contextPath to URLs that begin with a slash (/).
   * Encodes the URL.
   *
   * @see #getSrc(javax.servlet.jsp.PageContext, java.lang.String, com.aoapps.net.URIParameters, com.aoapps.servlet.lastmodified.AddLastModified, boolean, boolean)
   *
   * @throws JspTagException when parameters provided with null src
   */
  // TODO: Still used once converted to ao-fluent-html?
  public static void writeSrc(
      PageContext pageContext,
      Appendable out,
      String src,
      URIParameters params,
      AddLastModified addLastModified,
      boolean absolute,
      boolean canonical
  ) throws JspTagException, IOException {
    src = getSrc(pageContext, src, params, addLastModified, absolute, canonical);
    if (src != null) {
      out.append(" src=\"");
      encodeTextInXhtmlAttribute(src, out);
      out.append('"');
    }
  }

  /**
   * @see  #writeSrc(javax.servlet.jsp.PageContext, java.lang.Appendable, java.lang.String, com.aoapps.net.URIParameters, com.aoapps.servlet.lastmodified.AddLastModified, boolean, boolean)
   */
  // TODO: Still used once converted to ao-fluent-html?
  public static void writeSrc(
      JspContext jspContext,
      Appendable out,
      String src,
      URIParameters params,
      AddLastModified addLastModified,
      boolean absolute,
      boolean canonical
  ) throws JspTagException, IOException {
    writeSrc(
        (PageContext) jspContext,
        out,
        src,
        params,
        addLastModified,
        absolute,
        canonical
    );
  }
}
