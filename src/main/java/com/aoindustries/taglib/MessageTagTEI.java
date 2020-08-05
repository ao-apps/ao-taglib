/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2013, 2016, 2017, 2018, 2020  AO Industries, Inc.
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

import com.aoindustries.collections.MinimalList;
import static com.aoindustries.taglib.ApplicationResources.accessor;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.ValidationMessage;

/**
 * @author  AO Industries, Inc.
 */
public class MessageTagTEI extends TagExtraInfo {

	@Override
	public ValidationMessage[] validate(TagData data) {
		List<ValidationMessage> messages = MinimalList.emptyList();
		messages = TeiUtils.validateMediaType(data, messages);

		// Currently, in servlet 2.5 Tomcat 7, the dynamic attributes are not given to the TagExtraInfo class for validation.
		// Leaving this logic here just in case a future version of the spec supports this for compile-time validation of attribute names.
		Enumeration<String> attributeNames = data.getAttributes();
		while(attributeNames.hasMoreElements()) {
			String attributeName = attributeNames.nextElement();
			if(
				// Standard attribute names
				!"key".equals(attributeName)
				&& !"type".equals(attributeName)
			) {
				// Dynamic "arg#" attributes (along with arg0 through arg3 that are standard for code assist only)
				boolean isArg = false;
				if(attributeName.startsWith("arg")) {
					try {
						String numSubstring = attributeName.substring(3);
						// Do not allow "arg00" in place of "arg0"
						int index = Integer.parseInt(numSubstring);
						if(numSubstring.equals(Integer.toString(index))) {
							// All OK
							isArg = true;
						}
					} catch(NumberFormatException e) {
						// isArg remains false
					}
				}
				if(!isArg) {
					messages = MinimalList.add(
						messages,
						new ValidationMessage(
							data.getId(),
							accessor.getMessage("error.unexpectedDynamicAttribute1", attributeName, "arg*")
						)
					);
				}
			}
		}
		return messages.isEmpty() ? null : messages.toArray(new ValidationMessage[messages.size()]);
	}
}
