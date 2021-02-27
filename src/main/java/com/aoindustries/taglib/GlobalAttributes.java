/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2020, 2021  AO Industries, Inc.
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

import java.util.Map;

/**
 * {@linkplain com.aoindustries.html.GlobalAttributes Global attributes} when used in a filtered context.
 * These attributes may not be set from nested tags due to the lack of buffering.
 *
 * @author  AO Industries, Inc.
 */
// TODO: Maybe these getters should not be on this interface?  Or not all Element classes must extend this?
// TODO: This just doesn't follow the set-only pattern of other attributes.
public interface GlobalAttributes {

	String getId();

	/**
	 * Getter required because without it, we get the exception:
	 * <pre>Unable to find setter method for attribute: class</pre>
	 */
	String getClazz();

	/**
	 * Gets the HTML data attributes or an empty map when there are none.
	 */
	Map<String,Object> getData();

	String getDir();

	Object getStyle();
}
