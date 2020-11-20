/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2011, 2012, 2013, 2015, 2016, 2017, 2019, 2020  AO Industries, Inc.
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

import com.aoindustries.encoding.Serialization;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import java.io.IOException;
import java.util.Locale;
import javax.servlet.ServletResponse;

public class HtmlTag {

	private HtmlTag() {}

	/**
	 * The old Struts XHTML mode page attribute.  To avoiding picking-up a big
	 * legacy dependency, we've copied the value here instead of depending on
	 * Globals.  Once we no longer have any code running on old Struts, this
	 * value may be removed.
	 */
	static final String STRUTS_XHTML_KEY = "org.apache.struts.globals.XHTML";

	public static void beginHtmlTag(Locale locale, Appendable out, Serialization serialization, GlobalAttributes global) throws IOException {
		out.append("<html");
		if(serialization == Serialization.XML) {
			out.append(" xmlns=\"http://www.w3.org/1999/xhtml\"");
		}
		if(global != null) GlobalAttributesUtils.appendGlobalAttributes(global, out);
		if(locale != null) {
			String lang = locale.toLanguageTag();
			if(!lang.isEmpty()) {
				out.append(" lang=\"");
				encodeTextInXhtmlAttribute(lang, out);
				out.append('"');
				if(serialization == Serialization.XML) {
					out.append(" xml:lang=\"");
					encodeTextInXhtmlAttribute(lang, out);
					out.append('"');
				}
			}
		}
		out.append('>');
	}

	/**
	 * @deprecated  Please use {@link #beginHtmlTag(java.util.Locale, java.lang.Appendable, com.aoindustries.encoding.Serialization, com.aoindustries.taglib.GlobalAttributes)}
	 */
	@Deprecated
	public static void beginHtmlTag(Locale locale, Appendable out, Serialization serialization, String clazz) throws IOException {
		beginHtmlTag(
			locale,
			out,
			serialization,
			ImmutableGlobalAttributes.of(
				null, // id
				clazz,
				null, // data
				null, // dir
				null  // style
			)
		);
	}

	public static void beginHtmlTag(ServletResponse response, Appendable out, Serialization serialization, GlobalAttributes global) throws IOException {
		beginHtmlTag(response.getLocale(), out, serialization, global);
	}

	/**
	 * @deprecated  Please use {@link #beginHtmlTag(javax.servlet.ServletResponse, java.lang.Appendable, com.aoindustries.encoding.Serialization, com.aoindustries.taglib.GlobalAttributes)}
	 */
	@Deprecated
	public static void beginHtmlTag(ServletResponse response, Appendable out, Serialization serialization, String clazz) throws IOException {
		beginHtmlTag(response.getLocale(), out, serialization, clazz);
	}

	public static void endHtmlTag(Appendable out) throws IOException {
		out.append("</html>");
	}
}
