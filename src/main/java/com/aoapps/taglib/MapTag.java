/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2019, 2020, 2021, 2022  AO Industries, Inc.
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
import static com.aoapps.encoding.TextInXhtmlAttributeEncoder.encodeTextInXhtmlAttribute;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspException;

/**
 * @author  AO Industries, Inc.
 */
public class MapTag extends ElementFilteredTag {

	@Override
	public MediaType getContentType() {
		return MediaType.XHTML;
	}

/* BodyTag only:
	private static final long serialVersionUID = 1L;
/**/

	@Override
/* BodyTag only:
	protected int doStartTag(Writer out) throws JspException, IOException {
/**/
/* SimpleTag only: */
	protected void doTag(Writer out) throws JspException, IOException {
/**/
		String _id = global.getId();
		if(_id == null) throw new AttributeRequiredException("id");
		// TODO: Include id/name by doctype
		out.write("<map");
		GlobalAttributesUtils.writeGlobalAttributes(global, out);
		out.write(" name=\"");
		encodeTextInXhtmlAttribute(_id, out);
		out.write("\">");
/* BodyTag only:
		return EVAL_BODY_FILTERED;
	}

	@Override
	protected int doEndTag(Writer out) throws JspException, IOException {
/**/
/* SimpleTag only: */
		super.doTag(out);
/**/
		out.write("</map>");
/* BodyTag only:
		return EVAL_PAGE;
/**/
	}
}
