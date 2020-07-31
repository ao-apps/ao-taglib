/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2013, 2016, 2017, 2020  AO Industries, Inc.
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
import java.io.ObjectInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.TryCatchFinally;

/**
 * @author  AO Industries, Inc.
 */
public class BundleTag
	extends TagSupport
	implements TryCatchFinally
{

	/**
	 * For interaction with nested functions (that have no access to the page context),
	 * the current BundleTag is stored as a this attribute.
	 */
	private static final String REQUEST_ATTRIBUTE = BundleTag.class.getName();

	/**
	 * Gets the current BundleTag or <code>null</code> if not set.
	 */
	public static BundleTag getBundleTag(ServletRequest request) {
		return (BundleTag)request.getAttribute(REQUEST_ATTRIBUTE);
	}

	private static final long serialVersionUID = 1L;

	private String basename;
	private transient ApplicationResourcesAccessor accessor; // Set along with basename
	private String prefix;
	private transient Object oldRequestValue;

	private void init() {
		basename = null;
		accessor = null;
		prefix = null;
		oldRequestValue = null;
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		this.accessor = basename == null ? null : ApplicationResourcesAccessor.getInstance(basename);
	}

	public ApplicationResourcesAccessor getAccessor() {
		return accessor;
	}

	public void setBasename(String basename) {
		this.basename = basename;
		this.accessor = basename == null ? null : ApplicationResourcesAccessor.getInstance(basename);
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public int doStartTag() {
		ServletRequest request = pageContext.getRequest();
		oldRequestValue = request.getAttribute(REQUEST_ATTRIBUTE);
		request.setAttribute(REQUEST_ATTRIBUTE, this);
		return EVAL_BODY_INCLUDE;
	}

	@Override
	public void doCatch(Throwable t) throws Throwable {
		throw t;
	}

	@Override
	public void doFinally() {
		try {
			pageContext.getRequest().setAttribute(REQUEST_ATTRIBUTE, oldRequestValue);
		} finally {
			init();
		}
	}
}
