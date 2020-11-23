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
import com.aoindustries.html.Attributes.Global;
import com.aoindustries.validation.ValidationResult;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.ValidationMessage;

/**
 * Validates {@linkplain Global global attributes}.
 *
 * @author  AO Industries, Inc.
 */
public class ElementTagTEI extends TagExtraInfo {

	/**
	 * @deprecated  You should probably be implementing in {@link #validate(javax.servlet.jsp.tagext.TagData, java.util.List)}
	 *
	 * @see  #validate(javax.servlet.jsp.tagext.TagData, java.util.List)
	 */
	@Deprecated
	@Override
	final public ValidationMessage[] validate(TagData data) {
		List<ValidationMessage> messages = new ArrayList<>();
		validate(data, messages);
		int size = messages.size();
		return (size == 0) ? null : messages.toArray(new ValidationMessage[size]);
	}

	/**
	 * Validates the tag, adding all messages to the provided list.
	 */
	protected void validate(TagData data, List<ValidationMessage> messages) {
		Object o = data.getAttribute("dir");
		if(o != TagData.REQUEST_TIME_VALUE) {
			// TODO: Other validation Strings.trimNullIfEmpty (or their normalize* method) for consistency with implementation
			String dir = Attributes.Enum.Dir.dir.normalize((String)o);
			ValidationResult validation = Attributes.Enum.Dir.dir.validate(dir);
			if(!validation.isValid()) {
				messages.add(new ValidationMessage(data.getId(), validation.toString()));
			}
		}
	}
}
