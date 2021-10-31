/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2021  AO Industries, Inc.
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
package com.aoapps.taglib.impl;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Does not encode URL, since that will be done automatically and selectively by tag context.
 *
 * @author  AO Industries, Inc.
 */
public class NoEncodeUrlResponseWrapper extends HttpServletResponseWrapper {

	public NoEncodeUrlResponseWrapper(HttpServletResponse response) {
		super(response);
	}

	/**
	 * Do not encode URL, since that will be done automatically and selectively by tag context.
	 */
	@Override
	public String encodeURL(String url) {
		return url;
	}

	/**
	 * Do not encode URL, since that will be done automatically and selectively by tag context.
	 */
	@Override
	@Deprecated
	public String encodeUrl(String url) {
		return url;
	}
}
