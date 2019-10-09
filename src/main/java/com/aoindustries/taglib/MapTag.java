/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2019  AO Industries, Inc.
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

import com.aoindustries.encoding.Coercion;
import com.aoindustries.encoding.MediaType;
import static com.aoindustries.encoding.TextInXhtmlAttributeEncoder.textInXhtmlAttributeEncoder;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

/**
 * @author  AO Industries, Inc.
 */
public class MapTag
	extends AutoEncodingFilteredTag
	implements
		IdAttribute
{

	private Object id;

	@Override
	public MediaType getContentType() {
		return MediaType.XHTML;
	}

	@Override
	public void setId(Object id) throws JspTagException {
		this.id = AttributeUtils.trimNullIfEmpty(id);
	}

	@Override
	protected void doTag(Writer out) throws JspException, IOException {
		if(id == null) throw new AttributeRequiredException("id");
		out.write("<map id=\"");
		Coercion.write(id, textInXhtmlAttributeEncoder, out);
		out.write("\" name=\"");
		Coercion.write(id, textInXhtmlAttributeEncoder, out);
		out.write("\">");
		super.doTag(out);
		out.write("</map>");
	}
}
