/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2013, 2016, 2017, 2019, 2020, 2021, 2022, 2023, 2024  AO Industries, Inc.
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

import com.aoapps.html.any.attributes.text.Hreflang.hreflang;
import com.aoapps.lang.Coercion;
import java.util.Locale;

/**
 * Holds the data for a Link tag that is passed between {@link LinkTag} and any {@link LinksAttribute} parent.
 *
 * @author  AO Industries, Inc.
 */
public class Link {

  private final GlobalAttributes global;
  private final boolean noscript;
  private final String href;
  private final Object hreflang;
  private final String rel;
  private final String type;
  private final Object media;
  private final Object title;
  // Events
  private final Object onerror;
  private final Object onload;

  /**
   * Creates a new link.
   *
   * @param hreflang When is a {@link Locale}, calls {@link Locale#toLanguageTag()}.  Anything else is either
   *                 {@linkplain Coercion#write(java.lang.Object, java.io.Writer) directly streamed}
   *                 or {@linkplain Coercion#toString(java.lang.Object) coerced to String}.
   *
   * @see hreflang#normalize(java.lang.Object)
   */
  public Link(
      GlobalAttributes global,
      boolean noscript,
      String href,
      Object hreflang,
      String rel,
      String type,
      Object media,
      Object title,
      // Events
      Object onerror,
      Object onload
  ) {
    this.global = global;
    this.noscript = noscript;
    this.href = href;
    this.hreflang = hreflang;
    this.rel = rel;
    this.type = type;
    this.media = media;
    this.title = title;
    // Events
    this.onerror = onerror;
    this.onload = onload;
  }

  public GlobalAttributes getGlobal() {
    return global;
  }

  public boolean isNoscript() {
    return noscript;
  }

  public String getHref() {
    return href;
  }

  /**
   * Gets the hreflang.
   *
   * @return When is a {@link Locale}, call {@link Locale#toLanguageTag()}.  Anything else either
   *         {@linkplain Coercion#write(java.lang.Object, java.io.Writer) stream directly}
   *         or {@linkplain Coercion#toString(java.lang.Object) coerce to String}.
   *
   * @see hreflang#normalize(java.lang.Object)
   */
  public Object getHreflang() {
    return hreflang;
  }

  public String getRel() {
    return rel;
  }

  public String getType() {
    return type;
  }

  public Object getMedia() {
    return media;
  }

  // TODO: Move to GlobalAttributes (or AlmostGlobalAttributes)
  public Object getTitle() {
    return title;
  }

  // Events

  public Object getOnerror() {
    return onerror;
  }

  public Object getOnload() {
    return onload;
  }
}
