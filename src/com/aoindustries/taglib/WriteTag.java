/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2009, 2010, 2011, 2012, 2013  AO Industries, Inc.
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
import com.aoindustries.servlet.jsp.LocalizedJspException;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * @author  AO Industries, Inc.
 */
public class WriteTag
	extends AutoEncodingNullTag
	implements
		NameAttribute,
		TypeAttribute
{

	private String scope;
    private Object name;
    private String property;
    private String method = "toString";
	private Object type = MediaType.TEXT;
    private MediaType mediaType = MediaType.TEXT;

    @Override
    public MediaType getOutputType() {
        return mediaType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

	@Override
    public Object getName() {
        return name;
    }

	@Override
    public void setName(Object name) {
        this.name = name;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
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
        try {
            PageContext pageContext = (PageContext)getJspContext();

			if(name==null) throw new AttributeRequiredException("name");

			// Find the bean to write
            Object value = PropertyUtils.findObject(
				pageContext,
				scope,
				Coercion.toString(name),
				property,
				true,
				false
			);

            // Print the value
            if(value!=null) {
                // Avoid reflection when possible
                if("toString".equals(method)) {
					Coercion.write(value, out);
                    // out.write(value.toString());
                } else {
                    try {
                        Method refMethod = value.getClass().getMethod(method);
						Coercion.write(
							refMethod.invoke(value),
							out
						);
                    } catch(NoSuchMethodException err) {
                        throw new LocalizedJspException(ApplicationResources.accessor, "WriteTag.unableToFindMethod", method);
                    }
                }
            }
        } catch(IllegalAccessException err) {
            throw new JspException(err);
        } catch(InvocationTargetException err) {
            throw new JspException(err);
        }
    }
}
