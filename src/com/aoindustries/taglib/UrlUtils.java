/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2013, 2014, 2015  AO Industries, Inc.
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
import com.aoindustries.net.HttpParametersUtils;
import com.aoindustries.servlet.http.Dispatcher;
import com.aoindustries.servlet.http.LastModifiedServlet;
import com.aoindustries.servlet.http.ServletUtil;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
	 * Performs all the proper URL conversions along with optionally adding a lastModified parameter.
	 * This includes:
	 * <ol>
	 *   <li>Converting any page-relative path to a context-relative path starting with a slash (/)</li>
	 *   <li>Adding any additional parameters</li>
	 *   <li>Optionally adding lastModified parameter</li>
	 *   <li>Converting any context-relative path to a site-relative path by prefixing contextPath</li>
	 *   <li>Encoding any non-ASCII characters in the URL path</li>
	 *   <li>Rewrite with response.encodeURL</li>
	 * </ol>
	 */
	public static String buildUrl(
		ServletContext servletContext,
		HttpServletRequest request,
		HttpServletResponse response,
		String href,
		HttpParameters params,
		LastModifiedServlet.AddLastModifiedWhen addLastModified
	) throws MalformedURLException, UnsupportedEncodingException {
		String servletPath = Dispatcher.getCurrentPagePath(request);
        href = ServletUtil.getAbsolutePath(servletPath, href);
		href = HttpParametersUtils.addParams(href, params);
		href = LastModifiedServlet.addLastModified(servletContext, request, servletPath, href, addLastModified);
		if(href.startsWith("/")) {
			String contextPath = request.getContextPath();
			if(contextPath.length()>0) href = contextPath + href;
		}
		href = com.aoindustries.net.UrlUtils.encodeUrlPath(href);
		return response.encodeURL(href);
	}

	/**
	 * @see  #buildUrl(javax.servlet.ServletContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.String, com.aoindustries.net.HttpParameters, boolean) 
	 */
	public static String buildUrl(
		PageContext pageContext,
		String href,
		HttpParameters params,
		LastModifiedServlet.AddLastModifiedWhen addLastModified
	) throws MalformedURLException, UnsupportedEncodingException {
		return buildUrl(
			pageContext.getServletContext(),
			(HttpServletRequest)pageContext.getRequest(),
			(HttpServletResponse)pageContext.getResponse(),
			href,
			params,
			addLastModified
		);
	}
	
	/**
	 * @see  #buildUrl(javax.servlet.ServletContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.String, com.aoindustries.net.HttpParameters, boolean) 
	 */
	public static String buildUrl(
		JspContext jspContext,
		String src,
		HttpParameters params,
		LastModifiedServlet.AddLastModifiedWhen addLastModified
	) throws MalformedURLException, UnsupportedEncodingException {
		return buildUrl(
			(PageContext)jspContext,
			src,
			params,
			addLastModified
		);
	}

	/**
	 * Writes an href attribute with parameters.
	 * Adds contextPath to URLs that begin with a slash (/).
	 * Encodes the URL.
	 */
	public static void writeHref(
		Writer out,
		PageContext pageContext,
		String href,
		HttpParameters params,
		LastModifiedServlet.AddLastModifiedWhen addLastModified
	) throws JspTagException, IOException {
        if(href!=null) {
            out.write(" href=\"");
			encodeTextInXhtmlAttribute(
				buildUrl(pageContext, href, params, addLastModified),
				out
			);
            out.write('"');
        } else {
            if(params!=null) throw new LocalizedJspTagException(ApplicationResources.accessor, "UrlUtils.paramsWithoutHref");
        }
	}
	
	/**
	 * @see  #writeHref(java.io.Writer, javax.servlet.jsp.PageContext, java.lang.String, com.aoindustries.net.MutableHttpParameters, boolean) 
	 */
	public static void writeHref(
		Writer out,
		JspContext jspContext,
		String href,
		HttpParameters params,
		LastModifiedServlet.AddLastModifiedWhen addLastModified
	) throws JspTagException, IOException {
		writeHref(
			out,
			(PageContext)jspContext,
			href,
			params,
			addLastModified
		);
	}

	public static void writeSrc(
		Writer out,
		PageContext pageContext,
		String src,
		HttpParameters params,
		LastModifiedServlet.AddLastModifiedWhen addLastModified
	) throws JspTagException, IOException {
		if(src!=null) {
			out.write(" src=\"");
			encodeTextInXhtmlAttribute(
				buildUrl(pageContext, src, params, addLastModified),
				out
			);
			out.write('"');
        } else {
            if(params!=null) throw new LocalizedJspTagException(ApplicationResources.accessor, "UrlUtils.paramsWithoutSrc");
		}
	}
	
	/**
	 * @see  #writeSrc(java.io.Writer, javax.servlet.jsp.PageContext, java.lang.String, com.aoindustries.net.MutableHttpParameters, boolean) 
	 */
	public static void writeSrc(
		Writer out,
		JspContext jspContext,
		String src,
		HttpParameters params,
		LastModifiedServlet.AddLastModifiedWhen addLastModified
	) throws JspTagException, IOException {
		writeSrc(
			out,
			(PageContext)jspContext,
			src,
			params,
			addLastModified
		);
	}

	/**
	 * Make no instances.
	 */
	private UrlUtils() {
	}
}
