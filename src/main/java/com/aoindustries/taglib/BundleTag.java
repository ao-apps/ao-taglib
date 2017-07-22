/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2013, 2016, 2017  AO Industries, Inc.
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

import com.aoindustries.util.i18n.ApplicationResourcesAccessor;
import java.io.IOException;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author  AO Industries, Inc.
 */
public class BundleTag
	extends SimpleTagSupport
{

	/**
	 * For interaction with nested functions (that have no access to the page context),
	 * the current BundleTag is stored as a this attribute.
	 */
	private static final String REQUEST_ATTRIBUTE_KEY = BundleTag.class.getName()+".current";

	/**
	 * Gets the current BundleTag or <code>null</code> if not set.
	 */
	public static BundleTag getBundleTag(ServletRequest request) {
		return (BundleTag)request.getAttribute(REQUEST_ATTRIBUTE_KEY);
	}

	private String basename;
	private ApplicationResourcesAccessor accessor; // Set along with basename
	private String prefix;

	public ApplicationResourcesAccessor getAccessor() {
		return accessor;
	}

	public void setBasename(String basename) {
		this.basename = basename;
		this.accessor = ApplicationResourcesAccessor.getInstance(basename);
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public void doTag() throws JspException, IOException {
		JspFragment body = getJspBody();
		if(body!=null) {
			PageContext pageContext = (PageContext)getJspContext();
			ServletRequest request = pageContext.getRequest();
			Object oldRequestValue = request.getAttribute(REQUEST_ATTRIBUTE_KEY);
			try {
				request.setAttribute(REQUEST_ATTRIBUTE_KEY, this);
				body.invoke(null);
			} finally {
				request.setAttribute(REQUEST_ATTRIBUTE_KEY, oldRequestValue);
			}
		}
	}
}
