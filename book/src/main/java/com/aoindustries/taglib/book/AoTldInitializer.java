/*
 * ao-taglib - Making JSP what it should have been all along.
 * Copyright (C) 2016  AO Industries, Inc.
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
package com.aoindustries.taglib.book;

import com.semanticcms.tagreference.TagReferenceInitializer;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author  AO Industries, Inc.
 */
public class AoTldInitializer extends TagReferenceInitializer {

	private static final String TLD_BOOK = "/ao-taglib";
	private static final String TLD_PATH = "/ao.tld";
	static final String TLD_SERVLET_PATH = TLD_BOOK + TLD_PATH;

	private static final Map<String,String> additionalApiLinks = new LinkedHashMap<String,String>();
	static {
		additionalApiLinks.put("com.aoindustries.util.", "https://aoindustries.com/aocode-public/apidocs/");
		additionalApiLinks.put("com.aoindustries.taglib.", "https://aoindustries.com/ao-taglib/apidocs/");
	}

	public AoTldInitializer() {
		super(
			"AO Taglib Reference",
			"Taglib Reference",
			TLD_BOOK,
			TLD_PATH,
			"https://docs.oracle.com/javase/6/docs/api/",
			"https://docs.oracle.com/javaee/6/api/",
			additionalApiLinks
		);
	}
}