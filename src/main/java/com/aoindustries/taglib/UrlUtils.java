/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2013, 2014, 2015, 2016, 2017, 2019  AO Industries, Inc.
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

import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import com.aoindustries.net.URIParameters;
import com.aoindustries.servlet.http.HttpServletUtil;
import com.aoindustries.servlet.http.LastModifiedServlet;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import java.io.IOException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

/**
 * Helper utility for handling URLs.
 *
 * @author  AO Industries, Inc.
 */
final public class UrlUtils {

	/**
	 * Writes an href attribute with parameters.
	 * Adds contextPath to URLs that begin with a slash (/).
	 * Encodes the URL.
	 */
	public static void writeHref(
		PageContext pageContext,
		Appendable out,
		String href,
		URIParameters params,
		boolean hrefAbsolute,
		boolean canonical,
		LastModifiedServlet.AddLastModifiedWhen addLastModified
	) throws JspTagException, IOException {
		if(href != null) {
			out.append(" href=\"");
			encodeTextInXhtmlAttribute(
				HttpServletUtil.buildUrl(pageContext, href, params, hrefAbsolute, canonical, addLastModified),
				out
			);
			out.append('"');
		} else {
			if(params != null) throw new LocalizedJspTagException(ApplicationResources.accessor, "UrlUtils.paramsWithoutHref");
		}
	}

	/**
	 * @see  #writeHref(javax.servlet.jsp.PageContext, java.lang.Appendable, java.lang.String, com.aoindustries.net.URIParameters, boolean, boolean, com.aoindustries.servlet.http.LastModifiedServlet.AddLastModifiedWhen)
	 */
	public static void writeHref(
		JspContext jspContext,
		Appendable out,
		String href,
		URIParameters params,
		boolean hrefAbsolute,
		boolean canonical,
		LastModifiedServlet.AddLastModifiedWhen addLastModified
	) throws JspTagException, IOException {
		writeHref(
			(PageContext)jspContext,
			out,
			href,
			params,
			hrefAbsolute,
			canonical,
			addLastModified
		);
	}

	/**
	 * Writes a src attribute with parameters.
	 * Adds contextPath to URLs that begin with a slash (/).
	 * Encodes the URL.
	 */
	public static void writeSrc(
		PageContext pageContext,
		Appendable out,
		String src,
		URIParameters params,
		boolean srcAbsolute,
		boolean canonical,
		LastModifiedServlet.AddLastModifiedWhen addLastModified
	) throws JspTagException, IOException {
		if(src != null) {
			out.append(" src=\"");
			encodeTextInXhtmlAttribute(
				HttpServletUtil.buildUrl(pageContext, src, params, srcAbsolute, canonical, addLastModified),
				out
			);
			out.append('"');
		} else {
			if(params != null) throw new LocalizedJspTagException(ApplicationResources.accessor, "UrlUtils.paramsWithoutSrc");
		}
	}

	/**
	 * @see  #writeSrc(javax.servlet.jsp.PageContext, java.lang.Appendable, java.lang.String, com.aoindustries.net.URIParameters, boolean, boolean, com.aoindustries.servlet.http.LastModifiedServlet.AddLastModifiedWhen)
	 */
	public static void writeSrc(
		JspContext jspContext,
		Appendable out,
		String src,
		URIParameters params,
		boolean srcAbsolute,
		boolean canonical,
		LastModifiedServlet.AddLastModifiedWhen addLastModified
	) throws JspTagException, IOException {
		writeSrc(
			(PageContext)jspContext,
			out,
			src,
			params,
			srcAbsolute,
			canonical,
			addLastModified
		);
	}

	/**
	 * Make no instances.
	 */
	private UrlUtils() {
	}
}
