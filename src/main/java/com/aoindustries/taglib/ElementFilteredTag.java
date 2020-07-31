/*
 * ao-taglib - Making JSP be what it should have been all along.
 * Copyright (C) 2020  AO Industries, Inc.
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

import com.aoindustries.html.Attributes;
import com.aoindustries.html.Attributes.Global;
import com.aoindustries.lang.Strings;
import javax.servlet.jsp.JspTagException;

/**
 * Implements {@linkplain Global global attributes} on {@link AutoEncodingFilteredTag}.
 *
 * @author  AO Industries, Inc.
 */
abstract public class ElementFilteredTag extends AutoEncodingFilteredTag implements GlobalAttributes {

	protected String id;
	@Override
	public String getId() {
		return id;
	}
	public void setId(String id) throws JspTagException {
		this.id = Strings.trimNullIfEmpty(id);
		// TODO: Validate, and TEI
	}

	protected String clazz;
	@Override
	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) throws JspTagException {
		this.clazz = Strings.trimNullIfEmpty(clazz);
	}

	protected String dir;
	@Override
	public String getDir() {
		return dir;
	}
	public void setDir(String dir) throws JspTagException {
		this.dir = AttributeUtils.validate(
			Attributes.Enum.Dir.normalize(dir),
			Attributes.Enum.Dir::validate
		);
	}

	protected Object style;
	@Override
	public Object getStyle() {
		return style;
	}
	public void setStyle(Object style) throws JspTagException {
		this.style = AttributeUtils.trimNullIfEmpty(style);
	}
}
