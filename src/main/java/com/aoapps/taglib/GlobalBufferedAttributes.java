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

import com.aoapps.html.any.attributes.Text.Data;
import java.util.Map;

/**
 * {@linkplain com.aoapps.html.any.GlobalAttributes Global attributes} when used in a buffered context.
 * These attributes may be set from nested tags due to the buffering.
 *
 * @author  AO Industries, Inc.
 */
public interface GlobalBufferedAttributes extends
    GlobalAttributes,
    // Allow to be set from within nested tags
    IdAttribute,
    ClassAttribute,
    DataAttribute,
    DirAttribute,
    StyleAttribute
{

  /**
   * Replaces all the data with the provided HTML attribute names and values.
   * Entries will a {@code null} value are not added.
   *
   * @throws  IllegalArgumentException  When {@code attrName} is not {@linkplain Data.data#validate(java.lang.String) valid}
   *
   * @see  MutableGlobalAttributes#setData(java.util.Map)
   */
  void setData(Map<? extends String, ?> data) throws IllegalArgumentException;
}
