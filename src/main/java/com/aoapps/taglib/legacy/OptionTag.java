/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2010, 2011, 2013, 2015, 2016, 2017, 2019, 2020, 2021  AO Industries, Inc.
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
package com.aoapps.taglib.legacy;

import com.aoapps.encoding.MediaType;
import com.aoapps.html.servlet.DocumentEE;
import com.aoapps.io.buffer.BufferResult;
import com.aoapps.taglib.DisabledAttribute;
import com.aoapps.taglib.GlobalAttributesUtils;
import com.aoapps.taglib.SelectedAttribute;
import com.aoapps.taglib.ValueAttribute;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

/**
 * @author  AO Industries, Inc.
 */
public class OptionTag extends ElementBufferedBodyTag
	implements
		// Attributes
		DisabledAttribute,
		SelectedAttribute,
		ValueAttribute
{

	public OptionTag() {
		init();
	}

	@Override
	public MediaType getContentType() {
		return MediaType.TEXT;
	}

	@Override
	public MediaType getOutputType() {
		return MediaType.XHTML;
	}

/* BodyTag only: */
	private static final long serialVersionUID = 1L;
/**/

	private boolean disabled;
	@Override
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	private boolean selected;
	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	private boolean valueSet;
	private Object value;
	@Override
	public void setValue(Object value) {
		this.valueSet = true;
		this.value = value;
	}

	private void init() {
		disabled = false;
		selected = false;
		valueSet = false;
		value = null;
	}

	@Override
/* BodyTag only: */
	protected int doEndTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
/**/
/* SimpleTag only:
	protected void doTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
		PageContext pageContext = (PageContext)getJspContext();
/**/
		capturedBody = capturedBody.trim();
		// TODO: Should we be setting the value always like this?  Duplicates efforts.
		// TODO: If not setting value this way, this does not need to buffer
		// TODO: This has something to do with translator markup added for display, but not value
		if(!valueSet) setValue(capturedBody);
		GlobalAttributesUtils.doGlobalAttributes(
			global,
			new DocumentEE(
				pageContext.getServletContext(),
				(HttpServletRequest)pageContext.getRequest(),
				(HttpServletResponse)pageContext.getResponse(),
				out,
				false, // Do not add extra newlines to JSP
				false  // Do not add extra indentation to JSP
			).option()
		)
			.value(value)
			.selected(selected)
			.disabled(disabled)
			.__(capturedBody);
/* BodyTag only: */
		return EVAL_PAGE;
/**/
	}

/* BodyTag only: */
	@Override
	public void doFinally() {
		try {
			init();
		} finally {
			super.doFinally();
		}
	}
/**/
}
