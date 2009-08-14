/*
 * aocode-public-taglib - Reusable Java taglib of general tools with minimal external dependencies.
 * Copyright (C) 2009  AO Industries, Inc.
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
import com.aoindustries.util.LocalizedToString;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import javax.servlet.ServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * @author  AO Industries, Inc.
 */
public class WriteTag extends AutoEncodingFilteredTag {

    private static final long serialVersionUID = 1L;

    private String scope;
    private String name;
    private String property;
    private String method = "toString";
    private MediaType type = MediaType.TEXT;

    public MediaType getContentType() {
        return type;
    }

    private static final Class[] toStringParamTypes = new Class[] {Locale.class};
    private static final Class[] toStringEmptyParamTypes = new Class[0];

    @Override
    public void invokeAutoEncoding(Writer out) throws JspException, IOException {
        try {
            PageContext pageContext = (PageContext)getJspContext();

            // Find the bean to write
            Object value = PropertyUtils.findObject(pageContext, scope, name, property, true, false);

            // Print the value
            if(value!=null) {
                ServletResponse response = pageContext.getResponse();
                // Try the localized version
                boolean done;
                // Avoid reflection when possible by using the aocode-public interface
                if("toString".equals(method) && (value instanceof LocalizedToString)) {
                    out.write(((LocalizedToString)value).toString(response.getLocale()));
                    done = true;
                } else {
                    try {
                        Method refMethod = value.getClass().getMethod(method, toStringParamTypes);
                        if(refMethod.getReturnType()==String.class) {
                            out.write((String)refMethod.invoke(value, response.getLocale()));
                            done = true;
                        } else {
                            done = false;
                        }
                    } catch(NoSuchMethodException err) {
                        // Fall-through to next method
                        done = false;
                    }
                }
                if(!done) {
                    // Now the non-localized version
                    if("toString".equals(method)) {
                        out.write(value.toString());
                    } else {
                        try {
                            Method refMethod = value.getClass().getMethod(method, toStringEmptyParamTypes);
                            if(refMethod.getReturnType()==String.class) {
                                out.write((String)refMethod.invoke(value));
                                done = true;
                            }
                        } catch(NoSuchMethodException err) {
                            // Fall-through to failure
                        }
                        if(!done) throw new JspException(ApplicationResourcesAccessor.getMessage(response.getLocale(), "WriteTag.unableToFindMethod", method));
                    }
                }
            }
        } catch(IllegalAccessException err) {
            throw new JspException(err);
        } catch(InvocationTargetException err) {
            throw new JspException(err);
        }
    }

    /**
     * @return the scope
     */
    public String getScope() {
        return scope;
    }

    /**
     * @param scope the scope to set
     */
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the property
     */
    public String getProperty() {
        return property;
    }

    /**
     * @param property the property to set
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     * @param method the method to set
     */
    public void setMethod(String method) {
        this.method = method;
    }

    public String getType() {
        return type.getMediaType();
    }

    public void setType(String type) throws MediaException {
        this.type = MediaType.getMediaType(Locale.getDefault(), type);
    }
}
