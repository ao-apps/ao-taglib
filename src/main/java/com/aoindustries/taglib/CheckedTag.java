/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2010, 2011, 2012, 2013, 2016, 2017, 2020  AO Industries, Inc.
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
import com.aoindustries.encoding.taglib.EncodingBufferedTag;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspException;

/**
 * @author  AO Industries, Inc.
 */
public class CheckedTag extends EncodingBufferedTag {

/* SimpleTag only: */
	public static final String TAG_NAME = "<ao:checked>";
/**/

	@Override
	public MediaType getContentType() {
		return MediaType.TEXT;
	}

	@Override
	public MediaType getOutputType() {
		return null;
	}

/* BodyTag only:
	private static final long serialVersionUID = 1L;
/**/

	@Override
/* BodyTag only:
	protected int doEndTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
/**/
/* SimpleTag only: */
	protected void doTag(BufferResult capturedBody, Writer out) throws JspException, IOException {
/**/
		CheckedAttribute checkedAttribute = AttributeUtils.requireAttributeParent(TAG_NAME, this, "checked", CheckedAttribute.class);
		String value = capturedBody.trim().toString();
		if(!value.isEmpty()) {
			if("true".equals(value)) checkedAttribute.setChecked(true);
			else if("false".equals(value)) checkedAttribute.setChecked(false);
			else throw new LocalizedJspTagException(ApplicationResources.accessor, "CheckedTag.invalidValue", value);
		}
/* BodyTag only:
		return EVAL_PAGE;
/**/
	}
}
