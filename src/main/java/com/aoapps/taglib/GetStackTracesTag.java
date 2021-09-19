/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013, 2016, 2017, 2019, 2020, 2021  AO Industries, Inc.
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

import com.aoapps.encoding.MediaType;
import com.aoapps.encoding.taglib.EncodingNullTag;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.lang.util.ErrorPrinter;
import com.aoapps.servlet.jsp.LocalizedJspTagException;
import java.io.IOException;
import java.io.Writer;
import java.util.ResourceBundle;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * @author  AO Industries, Inc.
 */
public class GetStackTracesTag extends EncodingNullTag {

	private static final Resources RESOURCES = Resources.getResources(GetStackTracesTag.class, ResourceBundle::getBundle);

	private String scope;
	private String name;
	private String property;

	@Override
	public MediaType getOutputType() {
		return MediaType.TEXT;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	@Override
	protected void doTag(Writer out) throws JspException, IOException {
		PageContext pageContext = (PageContext)getJspContext();
		// Find the Throwable to display
		Object value = PropertyUtils.findObject(pageContext, scope, name, property, true, true);
		if(!(value instanceof Throwable)) throw new LocalizedJspTagException(RESOURCES, "notThrowable", (value == null) ? null : value.getClass().getName());
		Throwable throwable = (Throwable)value;

		// Print the stack traces
		ErrorPrinter.printStackTraces(throwable, out);
	}
}
