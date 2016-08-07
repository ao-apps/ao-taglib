/*
 * ao-taglib - Making JSP what it should have been all along.
 * Copyright (C) 2013, 2015, 2016  AO Industries, Inc.
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
import com.aoindustries.encoding.Coercion;
import com.aoindustries.io.buffer.BufferResult;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspTagException;

/**
 * @author  AO Industries, Inc.
 */
public class ArgTag
	extends AutoEncodingBufferedTag
	implements
		NameAttribute,
		ValueAttribute
{

    private Object name;
    private Object value;

    @Override
    public MediaType getContentType() {
        return MediaType.TEXT;
    }

    @Override
    public MediaType getOutputType() {
        return null;
    }

    @Override
    public Object getName() {
        return name;
    }

    @Override
    public void setName(Object name) {
		this.name = name;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
		this.value = value;
    }

    @Override
    protected void doTag(BufferResult capturedBody, Writer out) throws JspTagException, IOException {
		ArgsAttribute argsAttribute = AttributeUtils.findAttributeParent("arg", this, "args", ArgsAttribute.class);
		if(name==null) throw new AttributeRequiredException("name");
		if(value==null) setValue(capturedBody.trim());
		argsAttribute.addArg(Coercion.toString(name), value);
    }
}
