/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2010, 2011, 2016, 2017, 2019, 2020  AO Industries, Inc.
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

import java.util.List;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.ValidationMessage;

/**
 * @author  AO Industries, Inc.
 */
public class FormTagTEI extends ElementTagTEI {

	// TODO: Allow null here while doing normalizeMethod
	// TODO: Change to be validateMethod, like validateDir
	public static boolean isValidMethod(String method) {
		return
			"get".equals(method)
			|| "post".equals(method)
		;
	}

	@Override
	protected void validate(TagData data, List<ValidationMessage> messages) {
		super.validate(data, messages);
		Object o = data.getAttribute("method");
		if(
			o != null
			&& o != TagData.REQUEST_TIME_VALUE
		) {
			String method = ((String)o).trim(); // TODO: normalizeMethod
			if(!isValidMethod(method)) {
				messages.add(
					new ValidationMessage(
						data.getId(),
						ApplicationResources.accessor.getMessage("FormTag.method.invalid", method)
					)
				);
			}
		}
	}
}
