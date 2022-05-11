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

import com.aoapps.collections.MinimalMap;
import com.aoapps.html.any.Attributes;
import com.aoapps.html.any.attributes.enumeration.Dir;
import com.aoapps.html.any.attributes.text.Data;
import com.aoapps.lang.Freezable;
import com.aoapps.lang.Strings;
import java.util.Map;

/**
 * Builder for {@link GlobalAttributes} instances.
 */
public class MutableGlobalAttributes implements GlobalAttributes, Freezable<GlobalAttributes> {

  private String id;
  private String clazz;
  private Map<String, Object> data = MinimalMap.emptyMap();
  private String dir;
  private Object style;

  public MutableGlobalAttributes() {
    // Do nothing
  }

  @SuppressWarnings("OverridableMethodCallInConstructor")
  public MutableGlobalAttributes(GlobalAttributes global) {
    setId(global.getId());
    setClazz(global.getClazz());
    setData(global.getData());
    setDir(global.getDir());
    setStyle(global.getStyle());
  }

  @Override
  public String getId() {
    return id;
  }

  public MutableGlobalAttributes setId(String id) {
    this.id = Strings.trimNullIfEmpty(id); // TODO: normalize and validate
    return this;
  }

  @Override
  public String getClazz() {
    return clazz;
  }

  public MutableGlobalAttributes setClazz(String clazz) {
    this.clazz = Strings.trimNullIfEmpty(clazz);
    return this;
  }

  @Override
  public Map<String, Object> getData() {
    return MinimalMap.unmodifiable(data);
  }

  /**
   * Replaces all the data with the provided HTML attribute names and values.
   * Entries will a {@code null} value are not added.
   *
   * @throws  IllegalArgumentException  When {@code attrName} is not {@linkplain Data.data#validate(java.lang.String) valid}
   *
   * @see  GlobalBufferedAttributes#setData(java.util.Map)
   */
  public MutableGlobalAttributes setData(Map<? extends String, ?> data) throws IllegalArgumentException {
    Map<String, Object> newData = MinimalMap.emptyMap();
    if (data != null) {
      for (Map.Entry<? extends String, ?> entry : data.entrySet()) {
        String attrName = Attributes.validate(entry.getKey(), Data.data::validate);
        Object value = entry.getValue();
        if (value != null) {
          newData = MinimalMap.put(newData, attrName, value);
        }
      }
    }
    this.data = newData;
    return this;
  }

  /**
   * Adds all the data with the provided HTML attribute names and values, replacing any attributes that already exist.
   * Entries with a {@code null} value will remove any existing attribute.
   *
   * @throws  IllegalArgumentException  When {@code attrName} is not {@linkplain Data.data#validate(java.lang.String) valid}
   */
  public MutableGlobalAttributes addData(Map<? extends String, ?> data) throws IllegalArgumentException {
    if (data != null) {
      Map<String, Object> newData = this.data;
      for (Map.Entry<? extends String, ?> entry : data.entrySet()) {
        String attrName = Attributes.validate(entry.getKey(), Data.data::validate);
        Object value = entry.getValue();
        newData =
            (value == null)
                ? MinimalMap.remove(newData, attrName)
                : MinimalMap.put(newData, attrName, value);
      }
      this.data = newData;
    }
    return this;
  }

  /**
   * Adds the data with the provided HTML attribute name and value, replacing any attribute that already exists.
   * When value is {@code null}, will remove an existing attribute.
   *
   * @throws  IllegalArgumentException  When {@code attrName} is not {@linkplain Data.data#validate(java.lang.String) valid}
   *
   * @see  DataAttribute#addData(java.lang.String, java.lang.Object)
   */
  public MutableGlobalAttributes addData(String attrName, Object value) throws IllegalArgumentException {
    Attributes.validate(attrName, Data.data::validate);
    this.data =
        (data == null)
            ? MinimalMap.remove(this.data, attrName)
            : MinimalMap.put(this.data, attrName, value);
    return this;
  }

  /**
   * Removes the data with the provided HTML attribute names.
   */
  public MutableGlobalAttributes removeData(Iterable<? extends String> attrNames) {
    if (attrNames != null) {
      Map<String, Object> newData = this.data;
      // TODO: MinimalMap.removeAll
      for (String key : attrNames) {
        newData = MinimalMap.remove(newData, key);
      }
      this.data = newData;
    }
    return this;
  }

  /**
   * Removes the data with the provided HTML attribute name.
   */
  public MutableGlobalAttributes removeData(String attrName) {
    this.data = MinimalMap.remove(this.data, attrName);
    return this;
  }

  @Override
  public String getDir() {
    return dir;
  }

  public MutableGlobalAttributes setDir(String dir) throws IllegalArgumentException {
    this.dir = Attributes.validate(
        Dir.dir.normalize(dir),
        Dir.dir::validate
    );
    return this;
  }

  @Override
  public Object getStyle() {
    return style;
  }

  public MutableGlobalAttributes setStyle(Object style) throws IllegalArgumentException {
    this.style = AttributeUtils.trimNullIfEmpty(style); // TODO: .normalize()
    return this;
  }

  /**
   * Gets an immutable, thread-safe instance.
   *
   * @return  The instance or {@link ImmutableGlobalAttributes#EMPTY} when empty.
   *
   * @see  ImmutableGlobalAttributes#of(com.aoapps.taglib.GlobalAttributes)
   */
  @Override
  public GlobalAttributes freeze() {
    return ImmutableGlobalAttributes.of(this);
  }
}
