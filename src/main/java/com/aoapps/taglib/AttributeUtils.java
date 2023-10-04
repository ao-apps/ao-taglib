/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2013, 2016, 2017, 2019, 2020, 2021, 2022, 2023  AO Industries, Inc.
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

import com.aoapps.lang.Strings;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.servlet.el.ElUtils;
import com.aoapps.servlet.jsp.LocalizedJspTagException;
import com.aoapps.servlet.jsp.tagext.JspTagUtils;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.JspTag;

/**
 * @author  AO Industries, Inc.
 */
public final class AttributeUtils  {

  /** Make no instances. */
  private AttributeUtils() {
    throw new AssertionError();
  }

  public static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, AttributeUtils.class);

  /**
   * Finds the attribute parent tag of the provided class (or subclass).
   *
   * @return  the parent tag
   * @exception  NeedAttributeParentException  if parent not found
   *
   * @see  JspTagUtils#findAncestor(javax.servlet.jsp.tagext.JspTag, java.lang.Class)
   */
  public static <T> T requireAttributeParent(String fromTagName, JspTag from, String attribute, Class<? extends T> clazz) throws NeedAttributeParentException {
    return JspTagUtils.findAncestor(from, clazz).orElseThrow(
        () -> new NeedAttributeParentException(fromTagName, attribute)
    );
  }

  /**
   * Evaluates an expression then casts to the provided type.
   *
   * @deprecated Please use {@link ElUtils#resolveValue(javax.el.ValueExpression, java.lang.Class, javax.el.ELContext)} directly.
   */
  @Deprecated
  public static <T> T resolveValue(ValueExpression expression, Class<T> type, ELContext elContext) {
    return ElUtils.resolveValue(expression, type, elContext);
  }

  /**
   * Casts or evaluates an expression then casts to the provided type.
   *
   * @deprecated Please use {@link ElUtils#resolveValue(java.lang.Object, java.lang.Class, javax.el.ELContext)} directly.
   */
  @Deprecated
  public static <T> T resolveValue(Object value, Class<T> type, ELContext elContext) {
    return ElUtils.resolveValue(value, type, elContext);
  }

  private static boolean isAllDigits(String value) {
    for (int i = 0, len = value.length(); i < len; i++) {
      char ch = value.charAt(i);
      if (ch < '0' || ch > '9') {
        return false;
      }
    }
    return true;
  }

  private static boolean isZero(String value) {
    for (int i = 0, len = value.length(); i < len; i++) {
      char ch = value.charAt(i);
      if (ch != '0') {
        return false;
      }
    }
    return true;
  }

  /**
   * Appends a width style, while automatically appending "px" to any non-zero integer.
   * Encoding for XML attribute context is performed.
   *
   * @return  {@code true} when printed the style
   */
  // TODO: Version that takes StyleWriter or StyleDocumentWriter that does not encode for use with streaming style attributes
  public static boolean appendWidthStyle(String width, Appendable out) throws IOException {
    width = Strings.trimNullIfEmpty(width);
    if (width != null) {
      out.append("width:");
      encodeTextInXhtmlAttribute(width, out);
      if (isAllDigits(width) && !isZero(width)) {
        out.append("px");
      }
      return true;
    } else {
      return false;
    }
  }

  /**
   * Gets a width style, while automatically appending "px" to any non-zero integer.
   * No encoding is performed.
   *
   * @return  The style or {@code null} when none
   */
  public static String getWidthStyle(String width) {
    width = Strings.trimNullIfEmpty(width);
    if (width != null) {
      StringBuilder sb = new StringBuilder();
      sb.append("width:").append(width);
      if (isAllDigits(width) && !isZero(width)) {
        sb.append("px");
      }
      return sb.toString();
    } else {
      return null;
    }
  }

  /**
   * Creates the exception for dynamic attribute failed.  Does not throw it.
   */
  public static JspTagException newDynamicAttributeFailedException(String uri, String localName, Object value, List<String> expectedPatterns) {
    if (expectedPatterns == null || expectedPatterns.isEmpty()) {
      return new LocalizedJspTagException(RESOURCES, "unexpectedDynamicAttribute0", localName);
    } else {
      int size = expectedPatterns.size();
      if (size == 1) {
        return new LocalizedJspTagException(
            RESOURCES,
            "unexpectedDynamicAttribute1",
            localName,
            expectedPatterns.get(0)
        );
      } else if (size == 2) {
        return new LocalizedJspTagException(
            RESOURCES,
            "unexpectedDynamicAttribute2",
            localName,
            expectedPatterns.get(0),
            expectedPatterns.get(1)
        );
      } else {
        // Quote and comma-separate arbitrary length list here
        StringBuilder pre = new StringBuilder();
        for (int i = 0; i < (size - 1); i++) {
          if (pre.length() != 0) {
            pre.append(", ");
          }
          pre.append('"').append(expectedPatterns.get(i)).append('"');
        }
        return new LocalizedJspTagException(
            RESOURCES,
            "unexpectedDynamicAttributeN",
            localName,
            pre,
            '"' + expectedPatterns.get(size - 1) + '"'
        );
      }
    }
  }
}
