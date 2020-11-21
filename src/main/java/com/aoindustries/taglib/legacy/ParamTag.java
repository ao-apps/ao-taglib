/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013, 2015, 2016, 2017, 2020  AO Industries, Inc.
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
package com.aoindustries.taglib.legacy;

import com.aoindustries.encoding.MediaType;
import com.aoindustries.encoding.taglib.legacy.EncodingBufferedBodyTag;
import com.aoindustries.io.buffer.BufferResult;
import com.aoindustries.taglib.AttributeRequiredException;
import com.aoindustries.taglib.NameAttribute;
import com.aoindustries.taglib.ParamUtils;
import com.aoindustries.taglib.ValueAttribute;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspTagException;

/**
 * @author  AO Industries, Inc.
 */
// TODO: Support output when not inside a containing tag? (or implement applet and object tags to avoid errors of params accidentally passed to client)
public class ParamTag extends EncodingBufferedBodyTag
	implements
		NameAttribute,
		ValueAttribute
{

	public ParamTag() {
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

/* BodyTag only: */
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

/* BodyTag only: */
	private transient BufferResult capturedBody;
/**/

	private void init() {
		name = null;
		value = null;
/* BodyTag only: */
		capturedBody = null;
/**/
	}

/* BodyTag only: */
	@Override
	protected int doAfterBody(BufferResult capturedBody, Writer out) {
		assert this.capturedBody == null;
		assert capturedBody != null;
		this.capturedBody = capturedBody;
		return SKIP_BODY;
	}
/**/

	@Override
/* BodyTag only: */
	protected int doEndTag(Writer out) throws JspTagException, IOException {
/**/
/* SimpleTag only:
	protected void doTag(BufferResult capturedBody, Writer out) throws JspTagException, IOException {
/**/
		if(name == null) throw new AttributeRequiredException("name");
		ParamUtils.addParam(
			"param",
			this,
			name,
			(value != null) ? value : (capturedBody == null) ? "" : capturedBody.trim()
		);
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
