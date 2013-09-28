/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2013  AO Industries, Inc.
 *     support@aoindustries.com
 *     7262 Bull Pen Cir
 *     Mobile, AL 36695
 *
 * This file is part of aocode-public-taglib.
 *
 * aocode-public-taglib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aocode-public-taglib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with aocode-public-taglib.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aoindustries.taglib;

import com.aoindustries.encoding.MediaException;
import com.aoindustries.encoding.MediaType;
import com.aoindustries.io.Coercion;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspException;

/**
 * @author  AO Industries, Inc.
 */
public class OutTag
	extends AutoEncodingNullTag
	implements
		ValueAttribute,
		TypeAttribute
{

	private Object value;
	private Object def;
	private Object type = MediaType.TEXT;
    private MediaType mediaType = MediaType.TEXT;

	@Override
    public MediaType getOutputType() {
        return mediaType;
    }

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) {
		this.value = value;
	}

	public Object getDefault() {
		return def;
	}
	
	public void setDefault(Object def) {
		this.def = def;
	}

	@Override
    public Object getType() {
        return type;
    }

	@Override
    public void setType(Object type) throws JspException {
		try {
			MediaType newMediaType =
				(type instanceof MediaType)
				? (MediaType)type
				: MediaType.getMediaType(Coercion.toString(type))
			;
			this.type = type;
			this.mediaType = newMediaType;
		} catch(MediaException e) {
			throw new JspException(e);
		}
    }

	@Override
    protected void doTag(Writer out) throws JspException, IOException {
		if(value!=null) {
			Coercion.write(value, out);
		} else if(def!=null) {
			Coercion.write(def, out);
		}
    }
}
