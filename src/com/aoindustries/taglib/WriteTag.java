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
import com.aoindustries.io.Writable;
import com.aoindustries.servlet.jsp.LocalizedJspTagException;
import com.aoindustries.util.i18n.BundleLookupMarkup;
import com.aoindustries.util.i18n.BundleLookupThreadContext;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.servlet.jsp.JspTagException;
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
    public void setType(Object type) throws JspTagException {
		MediaType newMediaType;
		if(type instanceof MediaType) {
			newMediaType = (MediaType)type;
		} else {
			String typeStr = Coercion.toString(type);
			newMediaType = MediaType.getMediaTypeByName(typeStr);
			if(newMediaType==null) {
				try {
					newMediaType = MediaType.getMediaTypeForContentType(typeStr);
				} catch(MediaException e) {
					throw new JspTagException(e);
				}
			}
		}
		this.type = type;
		this.mediaType = newMediaType;
    }

	// One or neither of these values will be set after writePrefix, but not both
	private String toStringResult;
	private BundleLookupMarkup lookupMarkup;
	private Object value;

	@Override
	protected void writePrefix(MediaType containerType, Writer out) throws JspTagException, IOException {
        try {
			if(name==null) throw new AttributeRequiredException("name");

			// Find the bean to write
            Object bean = PropertyUtils.findObject(
				(PageContext)getJspContext(),
				scope,
				Coercion.toString(name),
				property,
				true,
				false
			);

            // Print the value
            if(bean!=null) {
                // Avoid reflection when possible
                if("toString".equals(method)) {
					if(
						bean instanceof Writable
						&& !((Writable)bean).isFastToString()
					) {
						// Stream with coercion in doTag
						value = bean;
					} else {
						toStringResult = Coercion.toString(bean);
					}
                } else {
                    try {
                        Method refMethod = bean.getClass().getMethod(method);
						Object retVal = refMethod.invoke(bean);
						if(
							retVal instanceof Writable
							&& !((Writable)retVal).isFastToString()
						) {
							// Stream with coercion in doTag
							value = retVal;
						} else {
							toStringResult = Coercion.toString(retVal);
						}
                    } catch(NoSuchMethodException err) {
                        throw new LocalizedJspTagException(ApplicationResources.accessor, "WriteTag.unableToFindMethod", method);
                    }
                }
				if(toStringResult!=null) {
					// Look for any message markup
					BundleLookupThreadContext threadContext = BundleLookupThreadContext.getThreadContext(false);
					if(threadContext!=null) {
						lookupMarkup = threadContext.getLookupMarkup(toStringResult);
					} else {
						lookupMarkup = null;
					}
					if(lookupMarkup!=null) lookupMarkup.appendPrefixTo(containerType.getMarkupType(), out);
				}
            }
        } catch(IllegalAccessException err) {
            throw new JspTagException(err);
        } catch(InvocationTargetException err) {
            throw new JspTagException(err);
        }
	}

	@Override
    protected void doTag(Writer out) throws JspTagException, IOException {
		if(toStringResult!=null) {
			out.write(toStringResult);
		} else if(value!=null) {
			Coercion.write(value, out);
		}
    }

	@Override
	protected void writeSuffix(MediaType containerType, Writer out) throws IOException {
		if(lookupMarkup!=null) lookupMarkup.appendSuffixTo(containerType.getMarkupType(), out);
	}
}
