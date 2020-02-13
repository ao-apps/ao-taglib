/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2011, 2016, 2017, 2019, 2020  AO Industries, Inc.
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

import com.aoindustries.encoding.Doctype;
import com.aoindustries.encoding.Serialization;
import com.aoindustries.util.MinimalList;
import java.util.List;
import java.util.Locale;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.ValidationMessage;

/**
 * @author  AO Industries, Inc.
 */
public class HtmlTagTEI extends TagExtraInfo {

	@Override
	public ValidationMessage[] validate(TagData data) {
		List<ValidationMessage> messages = MinimalList.emptyList();
		Object serializationAttr = data.getAttribute("serialization");
		if(
			serializationAttr != null
			&& serializationAttr != TagData.REQUEST_TIME_VALUE
		) {
			String serialization = ((String)serializationAttr).trim();
			if(!serialization.isEmpty() && !"auto".equalsIgnoreCase(serialization)) {
				try {
					Serialization.valueOf(serialization.toUpperCase(Locale.ROOT));
				} catch(IllegalArgumentException e) {
					messages = MinimalList.add(
						messages,
						new ValidationMessage(data.getId(), ApplicationResources.accessor.getMessage("HtmlTag.serialization.invalid", serialization))
					);
				}
			}
		}
		Object doctypeAttr = data.getAttribute("doctype");
		if(
			doctypeAttr != null
			&& doctypeAttr != TagData.REQUEST_TIME_VALUE
		) {
			String doctype = ((String)doctypeAttr).trim();
			if(!doctype.isEmpty() && !"default".equalsIgnoreCase(doctype)) {
				try {
					Doctype.valueOf(doctype.toUpperCase(Locale.ROOT));
				} catch(IllegalArgumentException e) {
					messages = MinimalList.add(
						messages,
						new ValidationMessage(data.getId(), ApplicationResources.accessor.getMessage("HtmlTag.doctype.invalid", doctype))
					);
				}
			}
		}
		return messages.isEmpty() ? null : messages.toArray(new ValidationMessage[messages.size()]);
	}
}
