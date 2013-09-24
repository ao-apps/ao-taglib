/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2011  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aocode-public-taglib.
 *
 * aocode-public-taglib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aocode-public-taglib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aocode-public-taglib.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.taglib;

/**
 * Something with an enctype attribute.
 *
 * @author  AO Industries, Inc.
 */
public interface EnctypeAttribute {

    Object getEnctype();

	/**
	 * Sets the value for this attribute.  If the value implements ReferenceCount,
	 * then incReferenceCount must be called to the object and decReferenceCount
	 * must be called when no longer needed (end of tag, for instance).
	 *
	 * @see  ReferenceUtils
	 */
    void setEnctype(Object enctype);
}
