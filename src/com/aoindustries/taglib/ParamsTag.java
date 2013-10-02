/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2010, 2011, 2012, 2013  AO Industries, Inc.
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
import com.aoindustries.io.Coercion;
import com.aoindustries.servlet.jsp.LocalizedJspException;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import javax.servlet.jsp.JspException;

/**
 * @author  AO Industries, Inc.
 */
public class ParamsTag
	extends AutoEncodingNullTag
	implements NameAttribute
{

    private Object name;
    private Object values;

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

    public void setValues(Object values) {
		this.values = values;
    }

    @Override
    protected void doTag(Writer out) throws JspException, IOException {
		ParamsAttribute paramsAttribute = AttributeUtils.findAttributeParent("params", this, "params", ParamsAttribute.class);
		if(name==null) throw new AttributeRequiredException("name");
		if(values!=null) {
			final String nameStr = Coercion.toString(name);
			if(values instanceof Iterable<?>) {
				ParamUtils.addIterableParams(
					paramsAttribute,
					nameStr,
					(Iterable<?>)values
				);
			} else if(values instanceof Iterator<?>) {
				ParamUtils.addIteratorParams(
					paramsAttribute,
					nameStr,
					(Iterator<?>)values
				);
			} else if(values.getClass().isArray()) {
				ParamUtils.addArrayParams(
					paramsAttribute,
					nameStr,
					values
				);
			} else {
				throw new LocalizedJspException(ApplicationResources.accessor, "ParamsTag.values.unexpectedType", values.getClass().getName());
			}
		}
    }
}
