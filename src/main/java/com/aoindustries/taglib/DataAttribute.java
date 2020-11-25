/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2020  AO Industries, Inc.
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
 * along with ao-taglib.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.taglib;

import com.aoindustries.html.Attributes;

/**
 * Something with data-* attributes.
 *
 * @author  AO Industries, Inc.
 */
public interface DataAttribute {

	/**
	 * Adds the data with the provided HTML attribute name and value, replacing any attribute that already exists.
	 * When value is {@code null}, will remove an existing attribute.
	 *
	 * @throws  IllegalArgumentException  When {@code attrName} is not {@linkplain Attributes.Text.Data.data#validate(java.lang.String) valid}
	 *
	 * @see  MutableGlobalAttributes#addData(java.lang.String, java.lang.Object)
	 */
	void addData(String attrName, Object value) throws IllegalArgumentException;
}
