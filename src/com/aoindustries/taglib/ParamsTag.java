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
import com.aoindustries.io.AutoTempFileWriter;
import com.aoindustries.io.Coercion;
import com.aoindustries.servlet.jsp.LocalizedJspException;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.Iterator;
import javax.servlet.jsp.JspException;

/**
 * @author  AO Industries, Inc.
 */
public class ParamsTag extends AutoEncodingBufferedTag implements NameAttribute {

    private String name;
    private Object values;

    @Override
    public MediaType getContentType() {
        return MediaType.TEXT;
    }

    @Override
    public MediaType getOutputType() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public void setValues(Object values) {
        this.values = values;
    }

    @Override
    protected void doTag(AutoTempFileWriter capturedBody, Writer out) throws JspException, IOException {
        ParamsAttribute paramsAttribute = AttributeUtils.findAttributeParent("params", this, "params", ParamsAttribute.class);
        if(name==null) throw new AttributeRequiredException("name");
        if(values!=null) {
            if(values instanceof Iterable<?>) {
                for(Object value : (Iterable<?>)values) {
					paramsAttribute.addParam(
						name,
						Coercion.toString(value)
					);
				}
            } else if(values instanceof Iterator<?>) {
                Iterator<?> iter = (Iterator<?>)values;
                while(iter.hasNext()) {
                    paramsAttribute.addParam(
						name,
						Coercion.toString(iter.next())
					);
                }
            } else if(values.getClass().isArray()) {
                int len = Array.getLength(values);
                for(int c=0; c<len; c++) {
                    paramsAttribute.addParam(
						name,
						Coercion.toString(Array.get(values, c))
					);
                }
            } else {
                throw new LocalizedJspException(ApplicationResources.accessor, "ParamsTag.values.unexpectedType", values.getClass().getName());
            }
        }
    }
}
