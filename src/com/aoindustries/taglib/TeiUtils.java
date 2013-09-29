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
import java.util.ArrayList;
import java.util.List;
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
	 * @param  message  the list of messages to add to, maybe <code>null</code>
	 * 
	 * @return  the list of messages.  A new list will have been created if the <code>message</code> parameter was <code>null</code>
	 * 
	 * @see  MediaType#getMediaType(java.lang.String)
	 */
    public static List<ValidationMessage> validateMediaType(TagData data, List<ValidationMessage> messages) {
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
			} catch(MediaException err) {
				if(messages == null) messages = new ArrayList<ValidationMessage>();
				messages.add(
					new ValidationMessage(
						data.getId(),
						err.getMessage()
					)
				);
			}
        }
		return messages;
    }

	/**
	 * Checks that a scope is a valid.
	 * Must be one of "page", "request", "session", or "application".
	 * 
	 * @param  message  the list of messages to add to, maybe <code>null</code>
	 * 
	 * @return  the list of messages.  A new list will have been created if the <code>message</code> parameter was <code>null</code>
	 * 
	 * @see PropertyUtils#getScope(java.lang.String)
	 */
    public static List<ValidationMessage> validateScope(TagData data, List<ValidationMessage> messages) {
        Object o = data.getAttribute("scope");
        if(
            o != null
            && o != TagData.REQUEST_TIME_VALUE
        ) {
			String scope = Coercion.toString(o);
			try {
				PropertyUtils.getScope(scope);
				// Value is OK
			} catch(JspException err) {
				if(messages == null) messages = new ArrayList<ValidationMessage>();
				messages.add(
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
