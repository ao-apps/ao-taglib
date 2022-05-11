/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2013, 2016, 2017, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.lang.LocalizedIllegalArgumentException;
import com.aoapps.lang.Throwables;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.servlet.attribute.AttributeEE;
import com.aoapps.servlet.attribute.ScopeEE;
import com.aoapps.servlet.jsp.LocalizedJspTagException;
import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

/**
 * Resolves objects from scope, name, and property values.
 *
 * @author  AO Industries, Inc.
 */
public final class PropertyUtils {

  /** Make no instances. */
  private PropertyUtils() {
    throw new AssertionError();
  }

  private static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, PropertyUtils.class);

  /**
   * Sets an attribute in the provided textual scope.
   *
   * @see  com.aoapps.servlet.attribute.ScopeEE.Page#getScopeId(java.lang.String)
   */
  public static <T> void setAttribute(ScopeEE.Page.Attribute<T> attribute, JspContext jspContext, String scope, T value) throws LocalizedIllegalArgumentException {
    attribute.context(jspContext).set(ScopeEE.Page.getScopeId(scope), value);
  }

  /**
   * Sets an attribute in the provided textual scope.
   *
   * @see  com.aoapps.servlet.attribute.ScopeEE.Page#getScopeId(java.lang.String)
   */
  public static void setAttribute(PageContext pageContext, String scope, String name, Object value) throws LocalizedIllegalArgumentException {
    setAttribute(ScopeEE.PAGE.attribute(name), pageContext, scope, value);
  }

  /**
   * Gets the object given its scope, name, and optional property.
   *
   * @param  scope  See {@link com.aoapps.servlet.attribute.ScopeEE.Page#getScopeId(java.lang.String)}
   * @param beanRequired when <code>true</code>, this method will not return <code>null</code>, instead it will
   *                     throw a <code>JspTagException</code> with an appropriate localized message.
   * @param valueRequired when <code>true</code>, this method will not return <code>null</code>, instead it will
   *                      throw a <code>JspTagException</code> with an appropriate localized message.
   *
   * @return  the resolved <code>Object</code> or <code>null</code> if not found.
   */
  @SuppressWarnings({"UseSpecificCatch", "TooBroadCatch"})
  public static <T> T findObject(ScopeEE.Page.Attribute<T> attribute, JspContext jspContext, String scope, String property, boolean beanRequired, boolean valueRequired) throws JspTagException {
    try {
      // Check the name
      if (attribute == null) {
        throw new AttributeRequiredException("name");
      }
      AttributeEE.Page<T> pageAttribute = attribute.context(jspContext);

      // Find the bean
      Object bean;
      if (scope == null) {
        bean = pageAttribute.find();
      } else {
        bean = pageAttribute.get(ScopeEE.Page.getScopeId(scope));
      }

      // Check required
      if (bean == null) {
        if (beanRequired) {
          // null and required
          if (scope == null) {
            throw new LocalizedJspTagException(RESOURCES, "bean.required.nullScope", attribute.getName());
          } else {
            throw new LocalizedJspTagException(RESOURCES, "bean.required.scope", attribute.getName(), scope);
          }
        } else {
          // null and not required
          return null;
        }
      } else {
        if (property == null) {
          // No property lookup, use the bean directly
          @SuppressWarnings("unchecked")
          T t = (T) bean;
          return t;
        } else {
          // Find the property
          try {
            Object value = org.apache.commons.beanutils.PropertyUtils.getProperty(bean, property);
            if (valueRequired && value == null) {
              // null and required
              if (scope == null) {
                throw new LocalizedJspTagException(RESOURCES, "value.required.nullScope", property, attribute.getName());
              } else {
                throw new LocalizedJspTagException(RESOURCES, "value.required.scope", property, attribute.getName(), scope);
              }
            }
            @SuppressWarnings("unchecked")
            T t = (T) value;
            return t;
          } catch (InvocationTargetException e) {
            // Unwrap cause for more direct stack traces
            Throwable cause = e.getCause();
            throw (cause == null) ? e : cause;
          }
        }
      }
    } catch (Throwable t) {
      throw Throwables.wrap(t, JspTagException.class, JspTagException::new);
    }
  }

  /**
   * Gets the object given its scope, name, and optional property.
   *
   * @param  scope  See {@link com.aoapps.servlet.attribute.ScopeEE.Page#getScopeId(java.lang.String)}
   * @param beanRequired when <code>true</code>, this method will not return <code>null</code>, instead it will
   *                     throw a <code>JspTagException</code> with an appropriate localized message.
   * @param valueRequired when <code>true</code>, this method will not return <code>null</code>, instead it will
   *                      throw a <code>JspTagException</code> with an appropriate localized message.
   *
   * @return  the resolved <code>Object</code> or <code>null</code> if not found.
   */
  public static Object findObject(PageContext pageContext, String scope, String name, String property, boolean beanRequired, boolean valueRequired) throws JspTagException {
    return findObject(ScopeEE.PAGE.attribute(name), pageContext, scope, property, beanRequired, valueRequired);
  }
}
