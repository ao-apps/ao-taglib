package com.aoindustries.taglib;

/*
 * Copyright 2009 by AO Industries, Inc.,
 * 7262 Bull Pen Cir, Mobile, Alabama, 36695, U.S.A.
 * All rights reserved.
 */
import com.aoindustries.media.MediaException;
import com.aoindustries.media.MediaType;
import com.aoindustries.util.LocalizedToString;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * @author  AO Industries, Inc.
 */
public class WriteTag extends AutoEncodingSimpleTag {

    private static final long serialVersionUID = 1L;

    private String scope;
    private String name;
    private String property;
    private MediaType type = MediaType.TEXT;

    public MediaType getContentType() {
        return type;
    }

    private static final Class[] toStringParamTypes = new Class[] {Locale.class};

    @Override
    public void invokeAutoEncoding(Writer out) throws JspException, IOException {
        try {
            PageContext pageContext = (PageContext)getJspContext();

            // Find the bean to write
            Object value = PropertyUtils.findObject(pageContext, scope, name, property, true, false);

            // Print the value
            if(value!=null) {
                // Avoid reflection when possible by using the aocode-public interface
                if(value instanceof LocalizedToString) {
                    out.write(((LocalizedToString)value).toString(pageContext.getResponse().getLocale()));
                } else {
                    try {
                        Method method = value.getClass().getMethod("toString", toStringParamTypes);
                        if(method.getReturnType()==String.class) {
                            out.write((String)method.invoke(value, pageContext.getResponse().getLocale()));
                        } else {
                            out.write(value.toString());
                        }
                    } catch(NoSuchMethodException err) {
                        out.write(value.toString());
                    } catch(IllegalAccessException err) {
                        out.write(value.toString());
                    }
                }
            }
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

    public String getType() {
        return type.getMediaType();
    }

    public void setType(String type) throws MediaException {
        this.type = MediaType.getMediaType(Locale.getDefault(), type);
    }
}
