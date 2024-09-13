/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2016, 2017, 2019, 2020, 2021, 2022, 2024  AO Industries, Inc.
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

import com.aoapps.lang.Coercion;

/**
 * Something with a set of parameters.
 *
 * @author  AO Industries, Inc.
 */
public interface ParamsAttribute {

  /**
   * Adds a parameter.
   * <p>
   * The conversion to string may be deferred, or the value may be streamed instead of being
   * converted to a string.  It is incorrect to change the state of the provided value; doing
   * so may or may not affect the value of the resulting parameter.
   * </p>
   * <p>
   * When the value is an {@link Enum}, the parameter value is obtained from {@link Enum#name()} instead of
   * {@link Enum#toString()}.  This is to intuitively use enums as parameters when {@link Enum#toString()} is
   * overridden.
   * </p>
   * <p>
   * Default method is for backward compatibility only.
   * Implementations should override this version.
   * </p>
   */
  // TODO: Remove default in a major release, once no "addParam(String,String)" is completely unused.
  default void addParam(String name, Object value) {
    addParam(name, (value == null) ? null : (value instanceof Enum) ? ((Enum) value).name() : Coercion.toString(value));
  }

  /**
   * @deprecated  Use {@link #addParam(java.lang.String, java.lang.Object)} instead.
   */
  @Deprecated
  default void addParam(String name, String value) {
    addParam(name, (Object) value);
  }
}
