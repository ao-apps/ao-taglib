/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2014  AO Industries, Inc.
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

import javax.servlet.ServletRequest;

/**
 * Routines used to propagate SkipPageException to outermost JSP page.  This
 * allows outer pages to automatically stop processing when an included page
 * throws SkipPageException through this handler.
 *
 * @author  AO Industries, Inc.
 */
class SkipPageHandler {

	/**
	 * The request attribute name set to boolean true when the page should be skipped.
	 * This is used to propagate a SkipPageException through the include chain.
	 * This results in different behavior than a standard jsp:include but should
	 * lead to more intuitive results.  With this change a redirect may be
	 * performed within an include.
	 */
	private static final String SKIP_PAGE_REQUEST_ATTRIBUTE_NAME = SkipPageHandler.class.getName() + ".skipPage";

	/**
	 * Sets the skip page flag.
	 */
	static void setPageSkipped(ServletRequest request) {
		request.setAttribute(SKIP_PAGE_REQUEST_ATTRIBUTE_NAME, true);
	}

	/**
	 * Gets the skip page flag.
	 */
	static boolean isPageSkipped(ServletRequest request) {
		return request.getAttribute(SKIP_PAGE_REQUEST_ATTRIBUTE_NAME) != null;
	}

	private SkipPageHandler() {
	}
}
