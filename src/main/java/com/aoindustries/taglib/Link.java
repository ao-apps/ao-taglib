/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2013, 2016, 2017, 2019, 2020  AO Industries, Inc.
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

import com.aoindustries.net.URIParameters;
import com.aoindustries.servlet.http.LastModifiedServlet;
import java.util.Locale;

/**
 * Holds the data for a Link tag that is passed between LinkTag and any LinksAttribute parent.
 *
 * @author  AO Industries, Inc.
 */
public class Link {

	private final String href;
	private final URIParameters params;
	private final boolean absolute;
	private final boolean canonical;
	private final LastModifiedServlet.AddLastModifiedWhen addLastModified;
	private final String hreflang;
	private final String rel;
	private final String type;
	private final String media;
	private final String title;

	public Link(
		String href,
		boolean absolute,
		boolean canonical,
		URIParameters params,
		LastModifiedServlet.AddLastModifiedWhen addLastModified,
		String hreflang,
		String rel,
		String type,
		String media,
		String title
	) {
		this.href = href;
		this.params = params;
		this.absolute = absolute;
		this.canonical = canonical;
		this.addLastModified = addLastModified;
		this.hreflang = hreflang;
		this.rel = rel;
		this.type = type;
		this.media = media;
		this.title = title;
	}

	public Link(
		String href,
		boolean absolute,
		boolean canonical,
		URIParameters params,
		LastModifiedServlet.AddLastModifiedWhen addLastModified,
		Locale hreflang,
		String rel,
		String type,
		String media,
		String title
	) {
		this(
			href,
			absolute,
			canonical,
			params,
			addLastModified,
			hreflang == null ? null : hreflang.toLanguageTag(),
			rel,
			type,
			media,
			title
		);
	}

	public String getHref() {
		return href;
	}

	public boolean getAbsolute() {
		return absolute;
	}

	public boolean getCanonical() {
		return canonical;
	}

	public URIParameters getParams() {
		return params;
	}

	public LastModifiedServlet.AddLastModifiedWhen getAddLastModified() {
		return addLastModified;
	}

	/**
	 * @see  Locale#toLanguageTag()
	 */
	public String getHreflang() {
		return hreflang;
	}

	public String getRel() {
		return rel;
	}

	public String getType() {
		return type;
	}

	public String getMedia() {
		return media;
	}

	public String getTitle() {
		return title;
	}
}
