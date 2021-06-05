/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2013, 2015, 2016, 2017, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoapps.taglib;

import com.aoapps.collections.MinimalList;
import com.aoapps.lang.Coercion;
import java.util.List;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.ValidationMessage;

/**
 * Utilities common to several {@link TagExtraInfo} implementations.
 *
 * @author  AO Industries, Inc.
 */
// TODO: Merge with attributeUtils?
final public class TeiUtils {

	private TeiUtils() {}

	/**
	 * Checks that a scope is a valid.
	 *
	 * @see  Scope for supported values.
	 * 
	 * @param  messages  the list of messages to add to, maybe <code>null</code>
	 * 
	 * @return  the list of messages.  A new list will have been created if the <code>message</code> parameter was <code>null</code>
	 * 
	 * @see Scope#getScopeId(java.lang.String)
	 */
	public static List<ValidationMessage> validateScope(TagData data, List<ValidationMessage> messages) {
		Object o = data.getAttribute("scope");
		if(
			o != null
			&& o != TagData.REQUEST_TIME_VALUE
		) {
			String scope = Coercion.toString(o).trim(); // TODO: normalizeScope
			try {
				Scope.getScopeId(scope);
				// Value is OK
			} catch(IllegalArgumentException err) {
				messages = MinimalList.add(
					messages,
					new ValidationMessage(
						data.getId(),
						err.getMessage()
					)
				);
			}
		}
		return messages;
	}
}
