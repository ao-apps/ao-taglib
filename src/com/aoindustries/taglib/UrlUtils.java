/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2014, 2015, 2016  AO Industries, Inc.
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

import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import com.aoindustries.net.HttpParameters;
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
		Appendable out,
		PageContext pageContext,
		String href,
		HttpParameters params,
		boolean hrefAbsolute,
		LastModifiedServlet.AddLastModifiedWhen addLastModified
	) throws JspTagException, IOException {
        if(href!=null) {
            out.append(" href=\"");
			encodeTextInXhtmlAttribute(
				com.aoindustries.net.UrlUtils.buildUrl(pageContext, href, params, hrefAbsolute, addLastModified),
				out
			);
            out.append('"');
        } else {
            if(params != null) throw new LocalizedJspTagException(ApplicationResources.accessor, "UrlUtils.paramsWithoutHref");
        }
	}
	
	/**
	 * @see  #writeHref(java.lang.Appendable, javax.servlet.jsp.PageContext, java.lang.String, com.aoindustries.net.HttpParameters, boolean, com.aoindustries.servlet.http.LastModifiedServlet.AddLastModifiedWhen) 
	 */
	public static void writeHref(
		Appendable out,
		JspContext jspContext,
		String href,
		HttpParameters params,
		boolean hrefAbsolute,
		LastModifiedServlet.AddLastModifiedWhen addLastModified
	) throws JspTagException, IOException {
		writeHref(
			out,
			(PageContext)jspContext,
			href,
			params,
			hrefAbsolute,
			addLastModified
		);
	}

	public static void writeSrc(
		Appendable out,
		PageContext pageContext,
		String src,
		HttpParameters params,
		boolean srcAbsolute,
		LastModifiedServlet.AddLastModifiedWhen addLastModified
	) throws JspTagException, IOException {
		if(src!=null) {
			out.append(" src=\"");
			encodeTextInXhtmlAttribute(
				com.aoindustries.net.UrlUtils.buildUrl(pageContext, src, params, srcAbsolute, addLastModified),
				out
			);
			out.append('"');
        } else {
            if(params != null) throw new LocalizedJspTagException(ApplicationResources.accessor, "UrlUtils.paramsWithoutSrc");
		}
	}

	/**
	 * @see  #writeSrc(java.lang.Appendable, javax.servlet.jsp.PageContext, java.lang.String, com.aoindustries.net.HttpParameters, boolean, com.aoindustries.servlet.http.LastModifiedServlet.AddLastModifiedWhen) 
	 */
	public static void writeSrc(
		Appendable out,
		JspContext jspContext,
		String src,
		HttpParameters params,
		boolean srcAbsolute,
		LastModifiedServlet.AddLastModifiedWhen addLastModified
	) throws JspTagException, IOException {
		writeSrc(
			out,
			(PageContext)jspContext,
			src,
			params,
			srcAbsolute,
			addLastModified
		);
	}

	/**
	 * Make no instances.
	 */
	private UrlUtils() {
	}
}
