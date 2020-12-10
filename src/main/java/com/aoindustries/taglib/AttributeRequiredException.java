/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2011, 2013, 2016, 2017, 2020  AO Industries, Inc.
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

import com.aoindustries.lang.Throwables;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import static com.aoindustries.taglib.Resources.PACKAGE_RESOURCES;

/**
 * @author  AO Industries, Inc.
 */
public class AttributeRequiredException extends LocalizedJspTagException {

	private static final long serialVersionUID = 2L;

	private final String attribute;

	public AttributeRequiredException(String attribute) {
		super(PACKAGE_RESOURCES, "AttributeRequiredException.message", attribute);
		this.attribute = attribute;
	}

	public String getAttribute() {
		return attribute;
	}

	static {
		Throwables.registerSurrogateFactory(AttributeRequiredException.class, (template, cause) -> {
			AttributeRequiredException newEx = new AttributeRequiredException(template.attribute);
			newEx.initCause(cause);
			return newEx;
		});
	}
}
