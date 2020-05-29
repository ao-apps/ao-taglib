/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2016, 2017, 2019, 2020  AO Industries, Inc.
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
import java.util.Collections;

public class AoTldInitializer extends TagReferenceInitializer {

	public AoTldInitializer() {
		super(
			"AO Taglib Reference",
			"Taglib Reference",
			"/ao-taglib",
			"/ao.tld",
			Maven.properties.getProperty("documented.javadoc.link.javase"),
			Maven.properties.getProperty("documented.javadoc.link.javaee"),
			// Self
			Collections.singletonMap("com.aoindustries.taglib", Maven.properties.getProperty("project.url") + "apidocs/"),
			// Dependencies
			Collections.singletonMap("com.aoindustries.util", "https://aoindustries.com/aocode-public/apidocs/")
		);
	}
}
