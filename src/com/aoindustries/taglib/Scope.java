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

import com.aoindustries.servlet.jsp.LocalizedJspException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * Scope constants.
 *
 * @author  AO Industries, Inc.
 */
final public class Scope {

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
     * @exception  JspException  if invalid scope
     */
    public static int getScopeId(String scope) throws JspException {
        if(scope==null || PAGE.equals(scope)) return PageContext.PAGE_SCOPE;
        else if(REQUEST.equals(scope)) return PageContext.REQUEST_SCOPE;
        else if(SESSION.equals(scope)) return PageContext.SESSION_SCOPE;
        else if(APPLICATION.equals(scope)) return PageContext.APPLICATION_SCOPE;
        else throw new LocalizedJspException(ApplicationResources.accessor, "Scope.scope.invalid", scope);
    }

	/** Make no instances */
	private Scope() {
	}
}
