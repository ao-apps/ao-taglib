/*
 * ao-taglib - Making JSP what it should have been all along.
 * Copyright (C) 2011, 2012, 2013, 2014, 2015, 2016  AO Industries, Inc.
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
import static com.aoindustries.servlet.filter.FunctionContext.getRequest;
import static com.aoindustries.servlet.filter.FunctionContext.getResponse;
import static com.aoindustries.servlet.filter.FunctionContext.getServletContext;
import com.aoindustries.servlet.http.Dispatcher;
import com.aoindustries.servlet.http.LastModifiedServlet;
import com.aoindustries.servlet.http.ServletUtil;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import static com.aoindustries.taglib.ApplicationResources.accessor;
import com.aoindustries.util.StringUtility;
import java.net.MalformedURLException;
import javax.servlet.jsp.JspTagException;

final public class Functions {

    private Functions() {
    }

	public static String addLastModified(String url) throws MalformedURLException {
		return LastModifiedServlet.addLastModified(
			getServletContext(),
			getRequest(),
			Dispatcher.getCurrentPagePath(getRequest()),
			url,
			LastModifiedServlet.AddLastModifiedWhen.TRUE
		);
	}

	public static String encodeURL(String url) {
        return getResponse().encodeURL(url);
    }

	/**
	 * @see  ServletUtil#getAbsoluteURL(javax.servlet.http.HttpServletRequest, java.lang.String)
	 */
	public static String getAbsoluteURL(String relPath) {
        return ServletUtil.getAbsoluteURL(getRequest(), relPath);
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
