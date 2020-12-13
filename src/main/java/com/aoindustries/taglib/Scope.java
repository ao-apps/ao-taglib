/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2013, 2016, 2017, 2020  AO Industries, Inc.
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

import com.aoindustries.i18n.Resources;
import com.aoindustries.lang.LocalizedIllegalArgumentException;
import javax.servlet.jsp.PageContext;

/**
 * Scope constants.
 *
 * @author  AO Industries, Inc.
 */
final public class Scope {

	private static final Resources RESOURCES = Resources.getResources(Scope.class);

	/**
	 * The set of allowed scope names.
	 */
	public static final String
		PAGE = "page",
		REQUEST = "request",
		SESSION = "session",
		APPLICATION = "application"
	;

	/**
	 * Gets the PageContext scope value for the textual scope name.
	 *
	 * @exception  IllegalArgumentException  if invalid scope
	 */
	public static int getScopeId(String scope) throws IllegalArgumentException {
		if(scope==null || PAGE.equals(scope)) return PageContext.PAGE_SCOPE;
		else if(REQUEST.equals(scope)) return PageContext.REQUEST_SCOPE;
		else if(SESSION.equals(scope)) return PageContext.SESSION_SCOPE;
		else if(APPLICATION.equals(scope)) return PageContext.APPLICATION_SCOPE;
		else throw new LocalizedIllegalArgumentException(RESOURCES, "scope.invalid", scope);
	}

	/** Make no instances */
	private Scope() {
	}
}
