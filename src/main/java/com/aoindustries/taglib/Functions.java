/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2011, 2012, 2013, 2014, 2015, 2016, 2017, 2019  AO Industries, Inc.
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

import com.aoindustries.lang.NullArgumentException;
import com.aoindustries.net.URIResolver;
import com.aoindustries.servlet.ServletUtil;
import com.aoindustries.servlet.URIComponent;
import static com.aoindustries.servlet.filter.FunctionContext.getRequest;
import static com.aoindustries.servlet.filter.FunctionContext.getResponse;
import static com.aoindustries.servlet.filter.FunctionContext.getServletContext;
import com.aoindustries.servlet.http.Dispatcher;
import com.aoindustries.servlet.http.HttpServletUtil;
import com.aoindustries.servlet.http.LastModifiedServlet;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import static com.aoindustries.taglib.ApplicationResources.accessor;
import com.aoindustries.util.StringUtility;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;

final public class Functions {

	private Functions() {
	}

	/**
	 * Gets the lastModified or {@code 0} when not known.
	 *
	 * @see  LastModifiedServlet#getLastModified(javax.servlet.ServletContext, javax.servlet.http.HttpServletRequest, java.lang.String)
	 */
	public static long getLastModified(String url) throws MalformedURLException {
		HttpServletRequest request = getRequest();
		// Get the context-relative path (resolves relative paths)
		String resourcePath = URIResolver.getAbsolutePath(
			Dispatcher.getCurrentPagePath(request),
			url
		);
		if(resourcePath.startsWith("/")) {
			// TODO: url decode path components except '/' to Unicode?
			return LastModifiedServlet.getLastModified(
				getServletContext(),
				request,
				resourcePath
			);
		}
		return 0;
	}

	public static String addLastModified(String url) throws MalformedURLException {
		HttpServletRequest request = getRequest();
		return LastModifiedServlet.addLastModified(
			getServletContext(),
			request,
			Dispatcher.getCurrentPagePath(request),
			url,
			LastModifiedServlet.AddLastModifiedWhen.TRUE
		);
	}

	public static String encodePath(String value) {
		return URIComponent.BASE.encode(value, getResponse());
	}

	public static String decodePath(String value) throws UnsupportedEncodingException {
		return URIComponent.BASE.decode(value, getRequest());
	}

	public static String encodeQuery(String value) {
		return URIComponent.QUERY.encode(value, getResponse());
	}

	public static String decodeQuery(String value) throws UnsupportedEncodingException {
		return URIComponent.QUERY.decode(value, getRequest());
	}

	public static String encodeFragment(String value) {
		return URIComponent.FRAGMENT.encode(value, getResponse());
	}

	public static String decodeFragment(String value) throws UnsupportedEncodingException {
		return URIComponent.FRAGMENT.decode(value, getRequest());
	}

	public static String encodeURI(String value){
		return ServletUtil.encodeURI(value, getResponse());
	}

	public static String decodeURI(String value) throws UnsupportedEncodingException {
		return ServletUtil.decodeURI(value, getRequest());
	}

	public static String encodeURL(String url) {
		HttpServletResponse response = getResponse();
		return response.encodeURL(ServletUtil.encodeURI(url, response));
	}

	/**
	 * @see  HttpServletUtil#getAbsoluteURL(javax.servlet.http.HttpServletRequest, java.lang.String)
	 */
	public static String getAbsoluteURL(String relPath) {
		return HttpServletUtil.getAbsoluteURL(getRequest(), relPath);
	}

	public static String getDecimalTimeLength(Long millis) {
		return millis==null ? null : StringUtility.getDecimalTimeLengthString(millis);
	}

	public static String join(Iterable<?> iter, String separator) {
		if(iter==null) return null;
		return StringUtility.join(iter, separator);
	}

	public static String message(String key) throws JspTagException {
		NullArgumentException.checkNotNull(key, "key");
		BundleTag bundleTag = BundleTag.getBundleTag(getRequest());
		if(bundleTag==null) throw new LocalizedJspTagException(accessor, "error.requiredParentTagNotFound", "bundle");
		String prefix = bundleTag.getPrefix();
		return bundleTag.getAccessor().getMessage(
			prefix==null || prefix.isEmpty() ? key : prefix.concat(key)
		);
	}
}
