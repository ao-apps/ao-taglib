/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2013  AO Industries, Inc.
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

import com.aoindustries.encoding.MediaException;
import com.aoindustries.encoding.MediaType;
import com.aoindustries.io.Coercion;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.ValidationMessage;

/**
 * Utilities common to several TagExtraInfo implementations.
 *
 * @author  AO Industries, Inc.
 */
final public class TeiUtils {

	private TeiUtils() {}

	/**
	 * Checks that a type is a valid MediaType.
	 * 
	 * @see  MediaType#getMediaType(java.lang.String)
	 */
    public static ValidationMessage validateMediaType(TagData data) {
        Object o = data.getAttribute("type");
        if(
            o != null
            && o != TagData.REQUEST_TIME_VALUE
			&& !(o instanceof MediaType)
        ) {
			String type = Coercion.toString(o);
			try {
				MediaType mediaType = MediaType.getMediaType(type);
				// Value is OK
				return null;
			} catch(MediaException err) {
				return new ValidationMessage(
					data.getId(),
					err.getMessage()
				);
			}
        } else {
            return null;
        }
    }

	/**
	 * Checks that a scope is a valid.
	 * Must be one of "page", "request", "session", or "application".
	 * 
	 * @see PropertyUtils#getScope(java.lang.String)
	 */
    public static ValidationMessage validateScope(TagData data) {
        Object o = data.getAttribute("scope");
        if(
            o != null
            && o != TagData.REQUEST_TIME_VALUE
        ) {
			String type = Coercion.toString(o);
			try {
				PropertyUtils.getScope(type);
				// Value is OK
				return null;
			} catch(JspException err) {
				return new ValidationMessage(
					data.getId(),
					err.getMessage()
				);
			}
        } else {
            return null;
        }
    }
}
