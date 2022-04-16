/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2011, 2012, 2013, 2016, 2017, 2020, 2021, 2022  AO Industries, Inc.
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

import com.aoapps.lang.NullArgumentException;

/**
 * Holds the data for a Meta tag that is passed between MetaTag and any MetasAttribute parent.
 *
 * @author  AO Industries, Inc.
 */
public class Meta {

	private final GlobalAttributes global;
	private final String name;
	private final String httpEquiv;
	private final String itemprop;
	private final String charset;
	private final String content;

	public Meta(
		GlobalAttributes global,
		String name,
		String httpEquiv,
		String itemprop,
		String charset,
		String content
	) {
		this.global = global;
		this.name = name;
		this.httpEquiv = httpEquiv;
		this.itemprop = itemprop;
		this.charset = charset;
		this.content = NullArgumentException.checkNotNull(content, "content");
	}

	/**
	 * @deprecated  Please use {@link #Meta(com.aoapps.taglib.GlobalAttributes, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
	 */
	@Deprecated
	public Meta(
		String name,
		String httpEquiv,
		String itemprop,
		String charset,
		String content
	) {
		this(
			ImmutableGlobalAttributes.EMPTY,
			name,
			httpEquiv,
			itemprop,
			charset,
			content
		);
	}

	public GlobalAttributes getGlobal() {
		return global;
	}

	public String getName() {
		return name;
	}

	public String getHttpEquiv() {
		return httpEquiv;
	}

	// TODO: Move to GlobalAttributes (or AlmostGlobalAttributes)
	public String getItemprop() {
		return itemprop;
	}

	public String getCharset() {
		return charset;
	}

	public String getContent() {
		return content;
	}
}
