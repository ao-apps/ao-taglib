/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2013, 2016, 2017, 2020, 2021  AO Industries, Inc.
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
package com.aoapps.taglib;

import com.aoapps.lang.attribute.Attribute;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.servlet.attribute.ScopeEE;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
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
	private static final ScopeEE.Request.Attribute<BundleTag> REQUEST_ATTRIBUTE =
		ScopeEE.REQUEST.attribute(BundleTag.class.getName());

	/**
	 * Gets the current BundleTag or <code>null</code> if not set.
	 */
	public static BundleTag getBundleTag(ServletRequest request) {
		return REQUEST_ATTRIBUTE.context(request).get();
	}

	private static final long serialVersionUID = 1L;

	public BundleTag() {
		init();
	}

	private String basename;
	private transient Resources resources; // Set along with basename
	private String prefix;
	private transient Attribute.OldValue oldRequestValue;

	private void init() {
		basename = null;
		resources = null;
		prefix = null;
		oldRequestValue = null;
	}

	@SuppressWarnings("deprecation")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		this.resources = basename == null ? null : Resources.getResources(basename);
	}

	public Resources getResources() {
		return resources;
	}

	@SuppressWarnings("deprecation")
	public void setBasename(String basename) {
		this.basename = basename;
		this.resources = basename == null ? null : Resources.getResources(basename);
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public int doStartTag() throws JspException {
		ServletRequest request = pageContext.getRequest();
		oldRequestValue = REQUEST_ATTRIBUTE.context(request).init(this);
		return EVAL_BODY_INCLUDE;
	}

	@Override
	public void doCatch(Throwable t) throws Throwable {
		throw t;
	}

	@Override
	public void doFinally() {
		try {
			if(oldRequestValue != null) oldRequestValue.close();
		} finally {
			init();
		}
	}
}
