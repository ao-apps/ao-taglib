/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2011, 2013, 2016, 2017, 2020, 2021  AO Industries, Inc.
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

import com.aoapps.lang.Throwables;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.servlet.jsp.LocalizedJspTagException;
import java.util.ResourceBundle;

/**
 * @author  AO Industries, Inc.
 */
public class AttributeRequiredException extends LocalizedJspTagException {

	static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, AttributeRequiredException.class);

	private static final long serialVersionUID = 2L;

	private final String attribute;

	public AttributeRequiredException(String attribute) {
		super(RESOURCES, "message", attribute);
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
