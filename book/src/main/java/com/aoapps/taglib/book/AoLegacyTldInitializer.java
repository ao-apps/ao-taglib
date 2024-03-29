/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2016, 2017, 2019, 2020, 2021, 2022  AO Industries, Inc.
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

package com.aoapps.taglib.book;

import com.semanticcms.tagreference.TagReferenceInitializer;
import javax.servlet.ServletContainerInitializer;

/**
 * Initializes a tag reference during {@linkplain ServletContainerInitializer application start-up}.
 */
public class AoLegacyTldInitializer extends TagReferenceInitializer {

  /**
   * Parses the TLD file.
   */
  public AoLegacyTldInitializer() {
    super(
        Maven.properties.getProperty("documented.name") + " Reference (Legacy)",
        "Taglib Reference (Legacy)",
        "/taglib",
        "/ao-legacy.tld",
        true,
        Maven.properties.getProperty("documented.javadoc.link.javase"),
        Maven.properties.getProperty("documented.javadoc.link.javaee"),
        // Self
        "com.aoapps.taglib", Maven.properties.getProperty("project.url") + "apidocs/com.aoapps.taglib/",
        "com.aoapps.taglib.legacy", Maven.properties.getProperty("project.url") + "apidocs/com.aoapps.taglib/",
        // Dependencies
        "com.aoapps.encoding.taglib", "https://oss.aoapps.com/encoding/taglib/apidocs/com.aoapps.encoding.taglib/",
        "com.aoapps.encoding.taglib.legacy", "https://oss.aoapps.com/encoding/taglib/apidocs/com.aoapps.encoding.taglib/",
        "com.aoapps.lang", "https://oss.aoapps.com/lang/apidocs/com.aoapps.lang/",
        "com.aoapps.net", "https://oss.aoapps.com/net-types/apidocs/com.aoapps.net.types/"
    );
  }
}
