/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2013, 2016, 2017, 2020, 2021  AO Industries, Inc.
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
 * along with ao-taglib.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoapps.taglib;

import com.aoapps.lang.LocalizedIllegalArgumentException;
import com.aoapps.servlet.attribute.ScopeEE;
import javax.servlet.jsp.PageContext;

/**
 * Scope constants.
 *
 * @author  AO Industries, Inc.
 *
 * @deprecated  Please use {@link com.aoapps.servlet.attribute.ScopeEE.Page} instead.
 */
@Deprecated
public abstract class Scope {

	/** Make no instances. */
	private Scope() {throw new AssertionError();}

	/**
	 * @deprecated  Please use {@link com.aoapps.servlet.attribute.ScopeEE.Page#SCOPE_PAGE} instead.
	 */
	@Deprecated
	public static final String PAGE = ScopeEE.Page.SCOPE_PAGE;

	/**
	 * @deprecated  Please use {@link com.aoapps.servlet.attribute.ScopeEE.Page#SCOPE_REQUEST} instead.
	 */
	@Deprecated
	public static final String REQUEST = ScopeEE.Page.SCOPE_REQUEST;

	/**
	 * @deprecated  Please use {@link com.aoapps.servlet.attribute.ScopeEE.Page#SCOPE_SESSION} instead.
	 */
	@Deprecated
	public static final String SESSION = ScopeEE.Page.SCOPE_SESSION;

	/**
	 * @deprecated  Please use {@link com.aoapps.servlet.attribute.ScopeEE.Page#SCOPE_APPLICATION} instead.
	 */
	@Deprecated
	public static final String APPLICATION = ScopeEE.Page.SCOPE_APPLICATION;

	/**
	 * Gets the {@link PageContext} scope value for the textual scope name.
	 *
	 * @throws  LocalizedIllegalArgumentException  if invalid scope
	 *
	 * @deprecated  Please use {@link com.aoapps.servlet.attribute.ScopeEE.Page#getScopeId(java.lang.String)} instead.
	 */
	@Deprecated
	public static int getScopeId(String scope) throws LocalizedIllegalArgumentException {
		return ScopeEE.Page.getScopeId(scope);
	}
}
