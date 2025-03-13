/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2020, 2021, 2022, 2023, 2025  AO Industries, Inc.
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

import com.aoapps.encoding.taglib.EncodingBufferedTag;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.DynamicAttributes;

/**
 * Implements {@linkplain com.aoapps.html.any.GlobalAttributes global attributes} on {@link EncodingBufferedTag}.
 *
 * @author  AO Industries, Inc.
 */
public abstract class ElementBufferedTag extends EncodingBufferedTag
    implements
    GlobalBufferedAttributes,
    DynamicAttributes {

  protected final MutableGlobalAttributes global = new MutableGlobalAttributes();

  @Override
  public String getId() {
    return global.getId();
  }

  @Override
  public void setId(String id) {
    global.setId(id);
    // TODO: Validate, and TEI
  }

  @Override
  public Object getClazz() {
    return global.getClazz();
  }

  @Override
  public void setClazz(Object clazz) {
    global.setClazz(clazz);
  }

  @Override
  public Map<String, Object> getData() {
    return global.getData();
  }

  @Override
  public void setData(Map<String, ?> data) {
    global.setData(data);
  }

  @Override
  public void addData(String attrName, Object value) {
    global.addData(attrName, value);
  }

  @Override
  public String getDir() {
    return global.getDir();
  }

  @Override
  public void setDir(String dir) {
    global.setDir(dir);
  }

  @Override
  public Object getStyle() {
    return global.getStyle();
  }

  @Override
  public void setStyle(Object style) {
    global.setStyle(style);
  }

  /**
   * Adds a {@linkplain DynamicAttributes dynamic attribute}.
   *
   * @return  {@code true} when added, or {@code false} when attribute not expected and has not been added.
   *
   * @see  GlobalAttributesUtils#addDynamicAttribute(java.lang.String, java.lang.String, java.lang.Object, java.util.List, com.aoapps.taglib.MutableGlobalAttributes)
   * @see  #setDynamicAttribute(java.lang.String, java.lang.String, java.lang.Object)
   */
  protected boolean addDynamicAttribute(String uri, String localName, Object value, List<String> expectedPatterns) throws JspTagException {
    return GlobalAttributesUtils.addDynamicAttribute(uri, localName, value, expectedPatterns, global);
  }

  /**
   * Sets a {@linkplain DynamicAttributes dynamic attribute}.
   *
   * @deprecated  You should probably be implementing in {@link #addDynamicAttribute(java.lang.String, java.lang.String, java.lang.Object, java.util.List)}
   *
   * @see  #addDynamicAttribute(java.lang.String, java.lang.String, java.lang.Object, java.util.List)
   */
  @Deprecated
  @Override
  public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
    List<String> expectedPatterns = new ArrayList<>();
    if (!addDynamicAttribute(uri, localName, value, expectedPatterns)) {
      throw AttributeUtils.newDynamicAttributeFailedException(uri, localName, value, expectedPatterns);
    }
  }
}
