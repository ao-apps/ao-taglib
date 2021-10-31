/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2013, 2016, 2017, 2019, 2020, 2021  AO Industries, Inc.
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

import com.aoapps.net.URIParameters;
import com.aoapps.servlet.lastmodified.AddLastModified;
import java.util.Locale;

/**
 * Holds the data for a Link tag that is passed between LinkTag and any LinksAttribute parent.
 *
 * @author  AO Industries, Inc.
 */
public class Link {

	private final GlobalAttributes global;
	private final String href;
	private final URIParameters params;
	private final boolean absolute;
	private final boolean canonical;
	private final AddLastModified addLastModified;
	private final String hreflang;
	private final String rel;
	private final String type;
	private final String media;
	private final String title;

	public Link(
		GlobalAttributes global,
		String href,
		boolean absolute,
		boolean canonical,
		URIParameters params,
		AddLastModified addLastModified,
		String hreflang,
		String rel,
		String type,
		String media,
		String title
	) {
		this.global = global;
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

	/**
	 * @deprecated  Please use {@link #Link(com.aoapps.taglib.GlobalAttributes, java.lang.String, boolean, boolean, com.aoapps.net.URIParameters, com.aoapps.servlet.lastmodified.AddLastModified, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
	 */
	@Deprecated
	public Link(
		String href,
		boolean absolute,
		boolean canonical,
		URIParameters params,
		AddLastModified addLastModified,
		String hreflang,
		String rel,
		String type,
		String media,
		String title
	) {
		this(
			ImmutableGlobalAttributes.EMPTY,
			href,
			absolute,
			canonical,
			params,
			addLastModified,
			hreflang,
			rel,
			type,
			media,
			title
		);
	}

	public Link(
		GlobalAttributes global,
		String href,
		boolean absolute,
		boolean canonical,
		URIParameters params,
		AddLastModified addLastModified,
		Locale hreflang,
		String rel,
		String type,
		String media,
		String title
	) {
		this(
			global,
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

	/**
	 * @deprecated  Please use {@link #Link(com.aoapps.taglib.GlobalAttributes, java.lang.String, boolean, boolean, com.aoapps.net.URIParameters, com.aoapps.servlet.lastmodified.AddLastModified, java.util.Locale, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
	 */
	@Deprecated
	public Link(
		String href,
		boolean absolute,
		boolean canonical,
		URIParameters params,
		AddLastModified addLastModified,
		Locale hreflang,
		String rel,
		String type,
		String media,
		String title
	) {
		this(
			ImmutableGlobalAttributes.EMPTY,
			href,
			absolute,
			canonical,
			params,
			addLastModified,
			hreflang,
			rel,
			type,
			media,
			title
		);
	}

	public GlobalAttributes getGlobal() {
		return global;
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

	public AddLastModified getAddLastModified() {
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

	// TODO: Move to GlobalAttributes (or AlmostGlobalAttributes)
	public String getTitle() {
		return title;
	}
}
