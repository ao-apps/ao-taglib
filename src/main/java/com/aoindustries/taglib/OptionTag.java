/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2010, 2011, 2013, 2015, 2016, 2017, 2019  AO Industries, Inc.
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

import com.aoindustries.encoding.MediaType;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.servlet.http.Html;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

/**
 * @author  AO Industries, Inc.
 */
public class OptionTag
	extends AutoEncodingBufferedTag
	implements
		ValueAttribute,
		SelectedAttribute,
		DisabledAttribute
{

	private boolean valueSet;
	private Object value;
	private boolean selected;
	private boolean disabled;

	@Override
	public MediaType getContentType() {
		return MediaType.TEXT;
	}

	@Override
	public MediaType getOutputType() {
		return MediaType.XHTML;
	}

	@Override
	public void setValue(Object value) {
		this.valueSet = true;
		this.value = value;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	@Override
	protected void doTag(BufferResult capturedBody, Writer out) throws JspTagException, IOException {
		capturedBody = capturedBody.trim();
		// TODO: Should we be setting the value always like this?  Duplicates efforts.
		// TODO: If not setting value this way, this does not need to buffer
		// TODO: This has something to do with translator markup added for display, but not value
		if(!valueSet) setValue(capturedBody);
		PageContext pageContext = (PageContext)getJspContext();
		Html.get(
			pageContext.getServletContext(),
			(HttpServletRequest)pageContext.getRequest(),
			out
		).option()
			.value(value)
			.selected(selected)
			.disabled(disabled)
			.innerText(capturedBody);
	}
}
