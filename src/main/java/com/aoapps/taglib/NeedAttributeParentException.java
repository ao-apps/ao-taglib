/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2012, 2013, 2016, 2017, 2020, 2021  AO Industries, Inc.
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

/**
 * @author  AO Industries, Inc.
 */
public class NeedAttributeParentException extends LocalizedJspTagException {

	private static final Resources RESOURCES = Resources.getResources(NeedAttributeParentException.class);

	private static final long serialVersionUID = 1L;

	private final String fromTagName;
	private final String attribute;

	public NeedAttributeParentException(String fromTagName, String attribute) {
		super(RESOURCES, "message", fromTagName, attribute);
		this.fromTagName = fromTagName;
		this.attribute = attribute;
	}

	public String getFromTagName() {
		return fromTagName;
	}

	public String getAttribute() {
		return attribute;
	}

	static {
		Throwables.registerSurrogateFactory(NeedAttributeParentException.class, (template, cause) -> {
			NeedAttributeParentException newEx = new NeedAttributeParentException(template.fromTagName, template.attribute);
			newEx.initCause(cause);
			return newEx;
		});
	}
}
