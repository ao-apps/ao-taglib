/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2011, 2012, 2013, 2016, 2017, 2020, 2021  AO Industries, Inc.
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
 * along with ao-taglib.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.aoapps.taglib;

import com.aoapps.encoding.MediaType;
import com.aoapps.encoding.taglib.EncodingBufferedTag;
import com.aoapps.io.buffer.BufferResult;
import com.aoapps.lang.i18n.Resources;
import com.aoapps.servlet.jsp.LocalizedJspTagException;
import java.io.IOException;
import java.io.Writer;
import java.util.ResourceBundle;
import javax.servlet.jsp.JspException;

/**
 * @author  AO Industries, Inc.
 */
public class FrameborderTag extends EncodingBufferedTag {

/* SimpleTag only: */
	public static final Resources RESOURCES = Resources.getResources(ResourceBundle::getBundle, FrameborderTag.class);

	public static final String TAG_NAME = "<ao:frameborder>";
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
		FrameborderAttribute frameborderAttribute = AttributeUtils.requireAttributeParent(TAG_NAME, this, "frameborder", FrameborderAttribute.class);
		String value = capturedBody.trim().toString();
		if(!value.isEmpty()) {
			if("true".equalsIgnoreCase(value)) frameborderAttribute.setFrameborder(true);
			else if("false".equalsIgnoreCase(value)) frameborderAttribute.setFrameborder(false);
			else throw new LocalizedJspTagException(RESOURCES, "invalidValue", value);
		}
/* BodyTag only:
		return EVAL_PAGE;
/**/
	}
}
