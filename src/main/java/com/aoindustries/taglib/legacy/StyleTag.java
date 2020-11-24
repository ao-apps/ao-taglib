/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013, 2016, 2017, 2020  AO Industries, Inc.
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
import com.aoindustries.taglib.AttributeUtils;
import com.aoindustries.taglib.StyleAttribute;
import static com.aoindustries.taglib.StyleTag.TAG_NAME;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspTagException;

/**
 * @author  AO Industries, Inc.
 */
public class StyleTag extends EncodingBufferedBodyTag {

/* SimpleTag only:
	public static final String TAG_NAME = "<ao:style>";
/**/

	@Override
	public MediaType getContentType() {
		return MediaType.TEXT; // TODO: MediaType.CSS
	}

	@Override
	public MediaType getOutputType() {
		return null;
	}

/* BodyTag only: */
	private static final long serialVersionUID = 1L;
/**/

	@Override
/* BodyTag only: */
	protected int doAfterBody(BufferResult capturedBody, Writer out) throws JspTagException, IOException {
/**/
/* SimpleTag only:
	protected void doTag(BufferResult capturedBody, Writer out) throws JspTagException, IOException {
/**/
		// TODO: Support <ao:style> without parent, which would be much like <ao:script> (context for nested <ao:out>, for example)
		// TODO: How to disambiguate, since style is a global attribute, it will often/usually be in a parent?
		// TODO: However, <ao:html> does is not StyleAttribute, so maybe is OK without explicit disambiguation.
		AttributeUtils.requireAttributeParent(TAG_NAME, this, "style", StyleAttribute.class)
			.setStyle(capturedBody.trim());
/* BodyTag only: */
		return SKIP_BODY;
/**/
	}
}
