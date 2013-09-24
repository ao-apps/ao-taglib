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

import com.aoindustries.encoding.MediaType;
import com.aoindustries.io.AutoTempFileWriter;
import com.aoindustries.io.Coercion;
import com.aoindustries.util.ref.ReferenceUtils;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.jsp.JspException;

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
		this.name = ReferenceUtils.replace(this.name, name);
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
		this.value = ReferenceUtils.replace(this.value, value);
    }

    @Override
    protected void doTag(AutoTempFileWriter capturedBody, Writer out) throws JspException, IOException {
		try {
			ArgsAttribute argsAttribute = AttributeUtils.findAttributeParent("arg", this, "args", ArgsAttribute.class);
			if(name==null) throw new AttributeRequiredException("name");
			if(value==null) setValue(capturedBody.trim());
			argsAttribute.addArg(Coercion.toString(name), value);
		} finally {
			name = ReferenceUtils.release(name);
			value = ReferenceUtils.release(value);
		}
    }
}
