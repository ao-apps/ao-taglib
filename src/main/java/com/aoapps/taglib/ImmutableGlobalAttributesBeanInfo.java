/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2020, 2021, 2022  AO Industries, Inc.
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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

/**
 * @author  AO Industries, Inc.
 */
public class ImmutableGlobalAttributesBeanInfo extends SimpleBeanInfo {

  private static final PropertyDescriptor[] properties;

  static {
    try {
      properties = new PropertyDescriptor[]{
          new PropertyDescriptor("id",    ImmutableGlobalAttributesBeanInfo.class, "getId",    null),
          new PropertyDescriptor("class", ImmutableGlobalAttributesBeanInfo.class, "getClazz", null),
          new PropertyDescriptor("data",  ImmutableGlobalAttributesBeanInfo.class, "getData",  null),
          new PropertyDescriptor("dir",   ImmutableGlobalAttributesBeanInfo.class, "getDir",   null),
          new PropertyDescriptor("style", ImmutableGlobalAttributesBeanInfo.class, "getStyle", null),
      };
    } catch (IntrospectionException err) {
      throw new ExceptionInInitializerError(err);
    }
  }

  @Override
  @SuppressWarnings("ReturnOfCollectionOrArrayField") // Not copying array for performance
  public PropertyDescriptor[] getPropertyDescriptors() {
    return properties;
  }

  /**
   * Include base class.
   */
  @Override
  public BeanInfo[] getAdditionalBeanInfo() {
    try {
      return new BeanInfo[]{
          Introspector.getBeanInfo(ImmutableGlobalAttributesBeanInfo.class.getSuperclass())
      };
    } catch (IntrospectionException err) {
      throw new AssertionError(err);
    }
  }
}
