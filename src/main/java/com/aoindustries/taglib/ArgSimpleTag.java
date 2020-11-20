/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2013, 2015, 2016, 2017, 2020  AO Industries, Inc.
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
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspTagException;

/**
 * @author  AO Industries, Inc.
 */
public class ArgSimpleTag extends com.aoindustries.encoding.taglib.EncodingBufferedSimpleTag
	implements
		NameAttribute,
		ValueAttribute
{

	public ArgSimpleTag() {
		init();
	}

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

	private String name;
	@Override
	public void setName(String name) {
		this.name = name;
	}

	private Object value;
	@Override
	public void setValue(Object value) {
		this.value = value;
	}

	private void init() {
		name = null;
		value = null;
	}

	@Override
/* BodyTag only:
	protected int doAfterBody(BufferResult capturedBody, Writer out) throws JspTagException, IOException {
/**/
/* SimpleTag only: */
	protected void doTag(BufferResult capturedBody, Writer out) throws JspTagException, IOException {
/**/
		ArgsAttribute argsAttribute = AttributeUtils.findAttributeParent("arg", this, "args", ArgsAttribute.class);
		if(name==null) throw new AttributeRequiredException("name");
		if(value==null) setValue(capturedBody.trim());
		argsAttribute.addArg(name, value);
/* BodyTag only:
		return SKIP_BODY;
/**/
	}

/* BodyTag only:
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
